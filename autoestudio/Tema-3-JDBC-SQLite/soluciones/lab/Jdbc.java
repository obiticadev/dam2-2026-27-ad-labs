package lab;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

/** Solución añadida para practicar. No se compila en la ruta normal. */
public final class Jdbc {
    public record Persona(int id, String nombre, Integer edad, int grupoId) {}

    public static void crear(Connection c) throws SQLException {
        try (Statement st = c.createStatement()) {
            st.executeUpdate("CREATE TABLE grupos(id INTEGER PRIMARY KEY,nombre TEXT NOT NULL UNIQUE)");
            st.executeUpdate("CREATE TABLE personas(id INTEGER PRIMARY KEY,nombre TEXT NOT NULL,"
                    + "edad INTEGER CHECK(edad>=0),grupo_id INTEGER NOT NULL REFERENCES grupos(id) ON DELETE CASCADE)");
        }
    }

    public static List<Persona> buscar(Connection c, String nombre, int edadMinima) throws SQLException {
        List<Persona> resultado = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT id,nombre,edad,grupo_id FROM personas WHERE nombre=? AND edad>=? ORDER BY id")) {
            ps.setString(1, nombre);
            ps.setInt(2, edadMinima);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int edad = rs.getInt("edad");
                    Integer valor = rs.wasNull() ? null : edad;
                    resultado.add(new Persona(rs.getInt("id"), rs.getString("nombre"), valor, rs.getInt("grupo_id")));
                }
            }
        }
        return resultado;
    }

    public static int insertar(Connection c, String nombre, Integer edad, int grupo) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO personas(nombre,edad,grupo_id) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nombre);
            if (edad == null) ps.setNull(2, Types.INTEGER); else ps.setInt(2, edad);
            ps.setInt(3, grupo);
            if (ps.executeUpdate() != 1) throw new SQLException("No se insertó una fila");
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (!rs.next()) throw new SQLException("No hay clave generada");
                return rs.getInt(1);
            }
        }
    }

    public static int renombrar(Connection c, int id, String nombre) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("UPDATE personas SET nombre=? WHERE id=?")) {
            ps.setString(1, nombre); ps.setInt(2, id);
            return ps.executeUpdate();
        }
    }

    public static int borrar(Connection c, int id) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("DELETE FROM personas WHERE id=?")) {
            ps.setInt(1, id); return ps.executeUpdate();
        }
    }

    public static List<String> tablas(Connection c) throws SQLException {
        List<String> resultado = new ArrayList<>();
        try (ResultSet rs = c.getMetaData().getTables(null, null, "%", new String[]{"TABLE"})) {
            while (rs.next()) {
                String nombre = rs.getString("TABLE_NAME");
                if (!nombre.startsWith("sqlite_")) resultado.add(nombre);
            }
        }
        Collections.sort(resultado);
        return resultado;
    }

    public static List<String> columnas(Connection c) throws SQLException {
        List<String> resultado = new ArrayList<>();
        try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery("SELECT id AS codigo,nombre FROM personas")) {
            ResultSetMetaData m = rs.getMetaData();
            for (int i = 1; i <= m.getColumnCount(); i++) resultado.add(m.getColumnLabel(i) + ":" + m.getColumnTypeName(i));
        }
        return resultado;
    }

    @FunctionalInterface
    private interface Trabajo { void ejecutar() throws SQLException; }

    private static void transaccion(Connection c, Trabajo trabajo) throws SQLException {
        if (!c.getAutoCommit()) throw new SQLException("No se admite una transacción ajena activa");
        c.setAutoCommit(false);
        try {
            trabajo.ejecutar();
            c.commit();
        } catch (SQLException | RuntimeException e) {
            try { c.rollback(); } catch (SQLException fallo) { e.addSuppressed(fallo); }
            throw e;
        } finally {
            c.setAutoCommit(true);
        }
    }

    public static void trasladar(Connection c, int persona, int grupo, String nuevoNombre) throws SQLException {
        transaccion(c, () -> {
            try (PreparedStatement uno = c.prepareStatement("UPDATE personas SET grupo_id=? WHERE id=?");
                 PreparedStatement dos = c.prepareStatement("UPDATE grupos SET nombre=? WHERE id=?")) {
                uno.setInt(1, grupo); uno.setInt(2, persona);
                if (uno.executeUpdate() != 1) throw new SQLException("Persona inexistente");
                dos.setString(1, nuevoNombre); dos.setInt(2, grupo);
                if (dos.executeUpdate() != 1) throw new SQLException("Grupo inexistente");
            }
        });
    }

    public static void cambioParcial(Connection c, int persona) throws SQLException {
        transaccion(c, () -> {
            if (renombrar(c, persona, "Ana Maria") != 1) throw new SQLException("Persona inexistente");
            Savepoint punto = c.setSavepoint();
            try (PreparedStatement ps = c.prepareStatement("UPDATE personas SET edad=99 WHERE id=?")) {
                ps.setInt(1, persona); ps.executeUpdate();
            }
            c.rollback(punto);
            c.releaseSavepoint(punto);
        });
    }

    public static List<String> resumen(Connection c) throws SQLException {
        List<String> resultado = new ArrayList<>();
        try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery(
                "SELECT g.nombre,COUNT(p.id) AS total FROM grupos g LEFT JOIN personas p ON p.grupo_id=g.id "
                        + "GROUP BY g.id,g.nombre ORDER BY g.id")) {
            while (rs.next()) resultado.add(rs.getString("nombre") + ":" + rs.getInt("total"));
        }
        return resultado;
    }

    public static void salarios(Connection c) throws SQLException {
        if (!c.getMetaData().supportsResultSetConcurrency(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE))
            throw new SQLFeatureNotSupportedException("No hay cursor actualizable");
        transaccion(c, () -> {
            try (Statement st = c.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                 ResultSet rs = st.executeQuery("SELECT id,salario FROM empleados ORDER BY id")) {
                while (rs.next()) {
                    rs.updateBigDecimal("salario", rs.getBigDecimal("salario").add(new BigDecimal("10.00")));
                    rs.updateRow();
                }
            }
        });
    }

    public static int doble(Connection c, int numero) throws SQLException {
        try (CallableStatement cs = c.prepareCall("{? = call DOBLE(?)}")) {
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setInt(2, numero);
            cs.execute();
            return cs.getInt(1);
        }
    }
}
