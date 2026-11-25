package repaso;

import examen.herramientas.Sqlite;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.nio.file.Files;

public class Almacen {
    public static void main(String[] args) throws Exception {
        // Siempre la misma base: se rehace en cada ejecución para partir de los datos iniciales.
        File db = new File("target", "almacen.sqlite").getCanonicalFile();
        Files.deleteIfExists(db.toPath());
        Sqlite.crear(db, new File("sql", "datos.sql"));
        System.out.println("Base: " + db);
        ejecutarSalida(db, "S-01", 3, null);
        ejecutarSalida(db, "S-02", 8, "SIN_STOCK");
        ejecutarSalida(db, "S-01", 2, "REFERENCIA_DUPLICADA");
        try (Connection c = Sqlite.abrir(db)) {
            resumen(c);
        }
    }

    private static void ejecutarSalida(File db, String referencia, int cantidad, String errorEsperado)
            throws Exception {
        // Cada intento se prueba con su propia conexión y la base persistente.
        try (Connection c = Sqlite.abrir(db)) {
            registrar(c, referencia, 1, cantidad);
            if (errorEsperado != null) throw new AssertionError("Se esperaba " + errorEsperado);
            System.out.println("COMMIT: " + referencia);
        } catch (SQLException e) {
            if (errorEsperado == null || !errorEsperado.equals(e.getSQLState())
                    || e.getSuppressed().length != 0) throw e;
            System.out.println("ROLLBACK: " + errorEsperado);
        }
    }

    private static void registrar(Connection c, String referencia, int producto, int cantidad)
            throws SQLException {
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser positiva");
        c.setAutoCommit(false);
        // Validar stock en el WHERE del UPDATE, sin un SELECT previo que pueda quedar obsoleto.
        try {
            try (var descuento = c.prepareStatement("""
                         UPDATE producto SET stock = stock - ? WHERE id = ? AND stock >= ?
                         """);
                 var salida = c.prepareStatement("""
                         INSERT INTO salida VALUES (?, ?, ?) ON CONFLICT(referencia) DO NOTHING
                         """)) {
                descuento.setInt(1, cantidad);
                descuento.setInt(2, producto);
                descuento.setInt(3, cantidad);
                if (descuento.executeUpdate() != 1) {
                    throw new SQLException("Producto inexistente o stock insuficiente", "SIN_STOCK");
                }

                salida.setString(1, referencia);
                salida.setInt(2, producto);
                salida.setInt(3, cantidad);
                // ON CONFLICT apunta solo a la referencia. No se ignoran FK ni CHECK.
                if (salida.executeUpdate() != 1) {
                    throw new SQLException("La referencia ya existe", "REFERENCIA_DUPLICADA");
                }
            }
            c.commit();
        } catch (SQLException e) {
            // Si el INSERT no insertó, también hay que deshacer el descuento anterior.
            Sqlite.deshacer(c, e);
            throw e;
        }
        c.setAutoCommit(true);
    }

    private static void resumen(Connection c) throws SQLException {
        // LEFT JOIN para mostrar también los productos sin salidas.
        try (var st = c.createStatement(); var rs = st.executeQuery("""
                SELECT p.id, p.nombre, p.stock, COUNT(s.referencia) AS operaciones,
                       COALESCE(SUM(s.unidades), 0) AS unidades
                FROM producto p LEFT JOIN salida s ON s.producto_id = p.id
                GROUP BY p.id, p.nombre, p.stock ORDER BY p.id
                """)) {
            int filas = 0;
            while (rs.next()) {
                filas++;
                int stock = rs.getInt("stock");
                int operaciones = rs.getInt("operaciones");
                int unidades = rs.getInt("unidades");
                System.out.printf("%s | stock %d | %d salidas, %d unidades%n",
                        rs.getString("nombre"), stock, operaciones, unidades);
                if (rs.getInt("id") == 1) assert stock == 7 && operaciones == 1 && unidades == 3;
                else assert stock == 5 && operaciones == 0 && unidades == 0;
            }
            assert filas == 2;
        }
    }
}
