package repaso;

import examen.herramientas.Sqlite;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.nio.file.Files;

public class Personal {
    public static void main(String[] args) throws Exception {
        // Siempre la misma base: se rehace en cada ejecución para partir de los datos iniciales.
        File db = new File("target", "personal.sqlite").getCanonicalFile();
        Files.deleteIfExists(db.toPath());
        Sqlite.crear(db, new File("sql", "datos.sql"));
        System.out.println("Base: " + db);
        try (Connection c = Sqlite.abrir(db)) {
            altaConRevision(c, 4, "Eva", 10, 1);
        }
        // Reabrir prueba que el commit vive en disco y no solo en la conexión.
        try (Connection c = Sqlite.abrir(db)) {
            assert salario(c, 1) == 1100 && salario(c, 4) == 1200;
            try {
                altaConRevision(c, 5, "Hugo", 10, 3);
                throw new AssertionError("No se puede revisar a alguien de otro departamento");
            } catch (SQLException e) {
                if (!"REVISION_SIN_FILA".equals(e.getSQLState()) || e.getSuppressed().length != 0) throw e;
                System.out.println("ROLLBACK: " + e.getMessage());
            }
        }
        try (Connection c = Sqlite.abrir(db)) {
            assert salario(c, 5) == null && salario(c, 3) == 1500;
            listar(c, 10);
        }
    }

    private static void altaConRevision(Connection c, int id, String nombre,
                                         int departamento, int responsable) throws SQLException {
        c.setAutoCommit(false);
        // UPDATE parametrizado y con condición de pertenencia. CASE toma solo el primer WHEN.
        String revisar = """
                UPDATE emp SET salario = salario + CASE
                    WHEN salario <= 1100 THEN 100
                    WHEN dept_id = 10 THEN 50
                    ELSE 0 END
                WHERE id = ? AND dept_id = ? AND id <> ?
                """;
        try {
            try (var alta = c.prepareStatement("INSERT INTO emp VALUES (?, ?, 1200, ?)");
                 var revision = c.prepareStatement(revisar)) {
                alta.setInt(1, id);
                alta.setString(2, nombre);
                alta.setInt(3, departamento);
                if (alta.executeUpdate() != 1) throw new SQLException("El alta no insertó una fila");

                revision.setInt(1, responsable);
                revision.setInt(2, departamento);
                revision.setInt(3, id); // El nuevo empleado no puede ser su propio responsable.
                if (revision.executeUpdate() != 1) {
                    throw new SQLException("El responsable debe ser otro empleado del departamento",
                            "REVISION_SIN_FILA");
                }
            }
            c.commit(); // No confirmar el alta si la segunda parte no cumple el enunciado.
        } catch (SQLException e) {
            Sqlite.deshacer(c, e);
            throw e; // El llamador cierra la conexión al salir de su try-with-resources.
        }
        c.setAutoCommit(true);
        System.out.println("COMMIT: alta de " + nombre);
    }

    private static void listar(Connection c, int departamento) throws SQLException {
        try (var ps = c.prepareStatement("""
                SELECT e.nombre AS empleado, d.nombre AS departamento, e.salario
                FROM emp e JOIN dept d ON d.id = e.dept_id
                WHERE d.id = ? ORDER BY e.id
                """)) {
            ps.setInt(1, departamento);
            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    System.out.printf("%s | %s | %d%n",
                            rs.getString("empleado"), rs.getString("departamento"), rs.getInt("salario"));
                }
            }
        }
    }

    private static Integer salario(Connection c, int id) throws SQLException {
        // Consulta puntual para comprobar resultados: null significa que el id no existe.
        try (var ps = c.prepareStatement("SELECT salario FROM emp WHERE id = ?")) {
            ps.setInt(1, id);
            try (var rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("salario") : null;
            }
        }
    }
}
