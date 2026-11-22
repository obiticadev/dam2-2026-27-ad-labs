package lab;

import java.sql.*;
import java.util.List;

/** B10. No usar Jdbc: reproduce el patrón completo desde cero. */
public final class Simulacro {
    // BD ya creada: cursos(id INTEGER PK,nombre TEXT UNIQUE NOT NULL,plazas INTEGER CHECK>=0),
    // matriculas(id INTEGER PK,alumno TEXT NOT NULL,curso_id INTEGER NOT NULL FK cursos(id),
    // UNIQUE(alumno,curso_id)). Exige autocommit al entrar.
    // Descontar una plaza SOLO si queda alguna; insertar matrícula parametrizada.
    // Si curso ausente/sin plazas o alumno duplicado: SQLException y rollback total.
    // Restaurar autocommit, no cerrar conexión. Devuelve id generado de matrícula.
    public static int matricular(Connection c, String alumno, int curso) throws SQLException {
        throw new UnsupportedOperationException("B10: matriculación atómica");
    }

    // Nombre exacto del curso como parámetro. Devuelve alumnos ordenados por nombre.
    public static List<String> alumnos(Connection c, String curso) throws SQLException {
        throw new UnsupportedOperationException("B10: listado con JOIN");
    }
}
