package lab;

import java.sql.*;
import java.util.*;

public final class Simulacro {
    public static int matricular(Connection c, String alumno, int curso) throws SQLException {
        if (!c.getAutoCommit()) throw new SQLException("Transacción ajena activa");
        c.setAutoCommit(false);
        try {
            try (PreparedStatement ps = c.prepareStatement("UPDATE cursos SET plazas=plazas-1 WHERE id=? AND plazas>0")) {
                ps.setInt(1,curso);
                if (ps.executeUpdate() != 1) throw new SQLException("Curso ausente o sin plazas");
            }
            int id;
            try (PreparedStatement ps = c.prepareStatement("INSERT INTO matriculas(alumno,curso_id) VALUES(?,?)",Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1,alumno); ps.setInt(2,curso); ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (!rs.next()) throw new SQLException("Sin clave");
                    id = rs.getInt(1);
                }
            }
            c.commit(); return id;
        } catch (SQLException | RuntimeException e) {
            try { c.rollback(); } catch (SQLException otro) { e.addSuppressed(otro); }
            throw e;
        } finally { c.setAutoCommit(true); }
    }

    public static List<String> alumnos(Connection c, String curso) throws SQLException {
        List<String> alumnos = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement("SELECT m.alumno FROM matriculas m JOIN cursos c ON c.id=m.curso_id WHERE c.nombre=? ORDER BY m.alumno")) {
            ps.setString(1,curso);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) alumnos.add(rs.getString(1)); }
        }
        return alumnos;
    }
}
