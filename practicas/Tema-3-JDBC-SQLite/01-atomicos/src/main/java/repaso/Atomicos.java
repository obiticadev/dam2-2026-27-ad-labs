package repaso;

import examen.herramientas.Sqlite;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Scanner;

public class Atomicos {
    public static void main(String[] args) throws Exception {
        // Cada objetivo empieza desde cero. Una base :memory: dura lo que su conexión.
        if (args.length == 1 && args[0].equals("todos")) {
            for (int i = 1; i <= 12; i++) ejecutar(i, "Ventas");
        } else if (args.length >= 1 && args.length <= 2) {
            int numero = Integer.parseInt(args[0]);
            if (numero < 1 || numero > 12 || (args.length == 2 && numero != 1)) {
                throw new IllegalArgumentException("Elige 1–12; solo el 1 admite un departamento");
            }
            String departamento = args.length == 2 ? args[1] : "Ventas";
            if ("--teclado".equals(departamento)) {
                try (Scanner teclado = new Scanner(System.in)) {
                    System.out.print("Departamento: ");
                    departamento = teclado.hasNextLine() ? teclado.nextLine().trim() : "";
                    if (departamento.isBlank()) throw new IllegalArgumentException("Escribe un departamento");
                }
            }
            ejecutar(numero, departamento);
        } else {
            throw new IllegalArgumentException("Uso: Atomicos <1–12|todos> [departamento|--teclado para el 1]");
        }
    }

    private static void ejecutar(int numero, String departamento) throws Exception {
        System.out.printf("%n--- Ejercicio %02d ---%n", numero);
        try (Connection c = DriverManager.getConnection("jdbc:sqlite::memory:")) {
            Sqlite.configurar(c);
            Sqlite.importar(c, Sqlite.leerSql(new File("sql", "datos.sql")));
            switch (numero) {
                case 1 -> buscar(c, departamento);
                case 2 -> contar(c);
                case 3 -> leerNulos(c);
                case 4 -> insertar(c);
                case 5 -> actualizar(c);
                case 6 -> borrar(c);
                case 7 -> confirmar(c);
                case 8 -> revertir(c);
                case 9 -> revertirParte(c);
                case 10 -> metadatos(c);
                case 11 -> revertirPorFk(c);
                case 12 -> recorrerYActualizar(c);
                default -> throw new IllegalArgumentException("Ejercicio desconocido: " + numero);
            }
        }
    }

    private static void buscar(Connection c, String departamento) throws SQLException {
        String sql = """
                SELECT e.nombre AS empleado, d.nombre AS departamento
                FROM emp e JOIN dept d ON d.id = e.dept_id
                WHERE d.nombre = ? ORDER BY e.id
                """;
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            // El ? representa un valor. Los índices JDBC empiezan en 1, no en 0.
            ps.setString(1, departamento);
            try (ResultSet rs = ps.executeQuery()) {
                int filas = 0;
                while (rs.next()) {
                    filas++;
                    System.out.println(rs.getString("empleado") + " | " + rs.getString("departamento"));
                }
                if (filas == 0) System.out.println("Sin resultados");
                assert !departamento.equals("Ventas") || filas == 2;
            }
        }
    }

    private static void contar(Connection c) throws SQLException {
        // LEFT JOIN conserva Archivo; COUNT(e.id) ignora el NULL de la fila sin empleado.
        try (var st = c.createStatement(); var rs = st.executeQuery("""
                SELECT d.nombre, COUNT(e.id) AS empleados, COALESCE(SUM(e.salario), 0) AS total
                FROM dept d LEFT JOIN emp e ON e.dept_id = d.id
                GROUP BY d.id, d.nombre ORDER BY d.id
                """)) {
            while (rs.next()) {
                System.out.printf("%s: %d empleados, total %d%n",
                        rs.getString(1), rs.getInt("empleados"), rs.getInt("total"));
                assert !rs.getString(1).equals("Archivo") || rs.getInt("empleados") == 0;
            }
        }
    }

    private static void leerNulos(Connection c) throws SQLException {
        try (var st = c.createStatement(); var rs = st.executeQuery("""
                SELECT e.id, e.nombre, e.comision, d.nombre AS departamento
                FROM emp e JOIN dept d ON d.id = e.dept_id ORDER BY e.id
                """)) {
            while (rs.next()) {
                int importe = rs.getInt("comision");
                boolean nula = rs.wasNull(); // Inmediatamente después del getter del campo.
                System.out.printf("%s (%s): %s%n", rs.getString("nombre"),
                        rs.getString("departamento"), nula ? "NULL" : importe);
                assert nula == (rs.getInt("id") == 1);
            }
        }
    }

    private static void insertar(Connection c) throws SQLException {
        // INTEGER PRIMARY KEY sin valor asigna un id. RETURNING produce filas, no un recuento.
        try (var ps = c.prepareStatement("""
                INSERT INTO emp(nombre, salario, comision, dept_id) VALUES (?, ?, ?, ?) RETURNING id
                """)) {
            ps.setString(1, "Eva");
            ps.setInt(2, 1300);
            ps.setNull(3, Types.INTEGER);
            ps.setInt(4, 20);
            try (var rs = ps.executeQuery()) {
                if (!rs.next()) throw new SQLException("El INSERT no devolvió su id");
                int id = rs.getInt("id");
                System.out.println("Id generado: " + id);
                assert id == 4;
            }
        }
    }

    private static void actualizar(Connection c) throws SQLException {
        int correcta = cambiarSalario(c, 1, 1100);
        int ausente = cambiarSalario(c, 999, 1100);
        System.out.printf("Id 1: %d fila; id 999: %d filas%n", correcta, ausente);
        assert correcta == 1 && ausente == 0 && salario(c, 1) == 1100;
    }

    private static void borrar(Connection c) throws SQLException {
        // No confundir 0 filas con error de FK. Archivo no tiene hijos; Ventas sí.
        try (var ps = c.prepareStatement("DELETE FROM dept WHERE id = ?")) {
            ps.setInt(1, 10);
            try {
                ps.executeUpdate();
                throw new AssertionError("Se ha borrado un departamento con empleados");
            } catch (SQLException e) {
                if (!esErrorFk(e)) throw e;
                System.out.println("Ventas: borrado rechazado por FK");
            }
            ps.setInt(1, 30);
            int filas = ps.executeUpdate();
            System.out.println("Archivo: filas borradas = " + filas);
            assert filas == 1;
        }
    }

    private static void confirmar(Connection c) throws SQLException {
        c.setAutoCommit(false);
        try {
            exigirUna(cambiarSalario(c, 1, 1100));
            exigirUna(cambiarSalario(c, 2, 1100));
            c.commit(); // Se confirman ambos cambios en la misma conexión.
        } catch (SQLException e) {
            Sqlite.deshacer(c, e);
            throw e;
        }
        c.setAutoCommit(true);
        System.out.printf("COMMIT: %d y %d%n", salario(c, 1), salario(c, 2));
        assert salario(c, 1) == 1100 && salario(c, 2) == 1100;
    }

    private static void revertir(Connection c) throws SQLException {
        c.setAutoCommit(false);
        try {
            exigirUna(cambiarSalario(c, 1, 1100));
            int filas = cambiarSalario(c, 999, 1100);
            if (filas != 0) throw new SQLException("La prueba esperaba un id inexistente");
            c.rollback(); // Fallo de negocio: SQL no lanzó nada, pero no se completó el cambio.
        } catch (SQLException e) {
            Sqlite.deshacer(c, e);
            throw e;
        }
        c.setAutoCommit(true);
        System.out.println("ROLLBACK: salario original = " + salario(c, 1));
        assert salario(c, 1) == 1000;
    }

    private static void revertirParte(Connection c) throws SQLException {
        c.setAutoCommit(false);
        try {
            exigirUna(cambiarSalario(c, 1, 1100));
            Savepoint punto = c.setSavepoint("antes_del_segundo");
            exigirUna(cambiarSalario(c, 2, 1300));
            c.rollback(punto); // Deshace solo el segundo UPDATE, no la transacción completa.
            c.releaseSavepoint(punto);
            c.commit();
        } catch (SQLException e) {
            Sqlite.deshacer(c, e);
            throw e;
        }
        c.setAutoCommit(true);
        System.out.printf("SAVEPOINT: %d y %d%n", salario(c, 1), salario(c, 2));
        assert salario(c, 1) == 1100 && salario(c, 2) == 1200;
    }

    private static void metadatos(Connection c) throws SQLException {
        // DatabaseMetaData describe el esquema/driver; ResultSetMetaData describe el SELECT.
        var meta = c.getMetaData();
        System.out.printf("%s | %s%n", meta.getDatabaseProductName(), meta.getDriverVersion());
        try (var tablas = meta.getTables(null, null, "%", new String[] {"TABLE"})) {
            while (tablas.next()) System.out.println("Tabla: " + tablas.getString("TABLE_NAME"));
        }
        try (var fk = meta.getImportedKeys(null, null, "emp")) {
            if (!fk.next()) throw new SQLException("Se esperaba una FK de emp a dept");
            System.out.println("FK: " + fk.getString("FKCOLUMN_NAME") + " → " + fk.getString("PKTABLE_NAME"));
        }
        try (var st = c.createStatement(); var rs = st.executeQuery("""
                SELECT e.nombre AS empleado, d.nombre AS departamento FROM emp e JOIN dept d ON e.dept_id = d.id
                """)) {
            var columnas = rs.getMetaData();
            for (int i = 1; i <= columnas.getColumnCount(); i++) {
                System.out.printf("%s: %s%n", columnas.getColumnLabel(i), columnas.getColumnTypeName(i));
            }
            assert columnas.getColumnCount() == 2;
        }
    }

    private static void revertirPorFk(Connection c) throws SQLException {
        c.setAutoCommit(false);
        try {
            exigirUna(cambiarSalario(c, 1, 1100));
            // La segunda sentencia falla por FK. Eso no revierte por sí solo el primer UPDATE.
            try (var ps = c.prepareStatement("UPDATE emp SET dept_id = ? WHERE id = ?")) {
                ps.setInt(1, 999);
                ps.setInt(2, 1);
                ps.executeUpdate();
            }
            c.rollback();
            throw new AssertionError("SQLite debería rechazar un departamento que no existe");
        } catch (SQLException e) {
            Sqlite.deshacer(c, e);
            if (!esErrorFk(e) || e.getSuppressed().length != 0) throw e;
        }
        c.setAutoCommit(true);
        System.out.println("ROLLBACK por FK: salario original = " + salario(c, 1));
        assert salario(c, 1) == 1000;
    }

    private static boolean esErrorFk(SQLException e) {
        // Criterio diagnóstico del driver SQLite incluido, no un código JDBC portable.
        return e.getErrorCode() == 19 && e.getMessage().contains("FOREIGN KEY");
    }

    private static void recorrerYActualizar(Connection c) throws SQLException {
        record Cambio(int id, int salario) {}

        // SQLite JDBC no permite updateRow(). Leer primero, cerrar el cursor y aplicar DML por PK.
        c.setAutoCommit(false);
        try {
            var cambios = new ArrayList<Cambio>();
            try (var st = c.createStatement(); var rs = st.executeQuery("""
                    SELECT e.id, e.salario FROM emp e JOIN dept d ON d.id = e.dept_id WHERE d.nombre = 'Ventas'
                    """)) {
                while (rs.next()) {
                    int anterior = rs.getInt("salario");
                    int nuevo = anterior + (anterior <= 1000 ? 100 : 50);
                    cambios.add(new Cambio(rs.getInt("id"), nuevo));
                }
            }
            for (Cambio cambio : cambios) exigirUna(cambiarSalario(c, cambio.id(), cambio.salario()));
            c.commit();
        } catch (SQLException e) {
            Sqlite.deshacer(c, e);
            throw e;
        }
        c.setAutoCommit(true);
        System.out.printf("Salarios tras recorrer y actualizar: %d, %d, %d%n",
                salario(c, 1), salario(c, 2), salario(c, 3));
        assert salario(c, 1) == 1100 && salario(c, 2) == 1250 && salario(c, 3) == 1500;
    }

    private static void exigirUna(int filas) throws SQLException {
        if (filas != 1) throw new SQLException("Esperaba 1 fila, actualicé " + filas);
    }

    private static int cambiarSalario(Connection c, int id, int nuevo) throws SQLException {
        try (var ps = c.prepareStatement("UPDATE emp SET salario = ? WHERE id = ?")) {
            ps.setInt(1, nuevo);
            ps.setInt(2, id);
            return ps.executeUpdate();
        }
    }

    private static int salario(Connection c, int id) throws SQLException {
        try (var ps = c.prepareStatement("SELECT salario FROM emp WHERE id = ?")) {
            ps.setInt(1, id);
            try (var rs = ps.executeQuery()) {
                if (!rs.next()) throw new SQLException("No existe empleado " + id);
                return rs.getInt(1);
            }
        }
    }
}
