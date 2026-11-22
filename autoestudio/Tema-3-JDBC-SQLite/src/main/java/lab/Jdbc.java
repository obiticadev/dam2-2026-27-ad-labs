package lab;

import java.sql.*;
import java.util.List;

/** Completa un método por bloque. La conexión es del llamante: NO la cierres. */
public final class Jdbc {
    public record Persona(int id, String nombre, Integer edad, int grupoId) {}

    // B01: crear grupos(id,nombre UNIQUE NOT NULL) y personas(id,nombre NOT NULL,
    // edad nullable CHECK>=0, grupo_id NOT NULL FK grupos ON DELETE CASCADE).
    // INTEGER PRIMARY KEY en ambos ids. No insertar datos ni borrar tablas.
    public static void crear(Connection c) throws SQLException {
        throw new UnsupportedOperationException("B01: crear esquema");
    }

    // B02: edad mínima inclusiva, orden id. SQL NULL se representa como null.
    public static List<Persona> buscar(Connection c, String nombre, int edadMinima) throws SQLException {
        throw new UnsupportedOperationException("B02: SELECT parametrizado");
    }

    // B03: devuelve id generado, sin MAX(id). nombre puede contener apóstrofos.
    public static int insertar(Connection c, String nombre, Integer edad, int grupo) throws SQLException {
        throw new UnsupportedOperationException("B03: INSERT y clave");
    }

    public static int renombrar(Connection c, int id, String nombre) throws SQLException {
        throw new UnsupportedOperationException("B03: UPDATE por PK y recuento");
    }

    public static int borrar(Connection c, int id) throws SQLException {
        throw new UnsupportedOperationException("B03: DELETE por PK y recuento");
    }

    // B04: tablas de usuario ordenadas; columnas de SELECT id AS codigo,nombre
    // en formato etiqueta:tipo (solo se exige etiqueta y tipo no vacío).
    public static List<String> tablas(Connection c) throws SQLException {
        throw new UnsupportedOperationException("B04: DatabaseMetaData");
    }

    public static List<String> columnas(Connection c) throws SQLException {
        throw new UnsupportedOperationException("B04: ResultSetMetaData");
    }

    // B05: exige autocommit al entrar; mover persona a grupo y renombrar ese grupo.
    // Si cualquiera afecta 0 filas: SQLException, rollback total. Restaurar autocommit.
    public static void trasladar(Connection c, int persona, int grupo, String nuevoNombre) throws SQLException {
        throw new UnsupportedOperationException("B05: transacción atómica");
    }

    // B06: exige autocommit; cambiar nombre a Ana Maria, savepoint, cambiar edad
    // a 99, deshacer SOLO la edad y confirmar nombre. Fallo: rollback completo.
    public static void cambioParcial(Connection c, int persona) throws SQLException {
        throw new UnsupportedOperationException("B06: Savepoint");
    }

    // B07: nombres de grupos y número de personas; incluir grupos vacíos.
    // Formato nombre:cuenta, orden g.id. Cuenta filas hijas, no COUNT(*).
    public static List<String> resumen(Connection c) throws SQLException {
        throw new UnsupportedOperationException("B07: LEFT JOIN y GROUP BY");
    }

    // B08 H2, no SQLite: cursor desplazable actualizable, empleados(id,salario).
    // Sumar 10 a cada salario usando updateBigDecimal/updateRow, una transacción.
    public static void salarios(Connection c) throws SQLException {
        throw new UnsupportedOperationException("B08: cursor actualizable H2");
    }

    // B09 H2: función alias DOBLE, retorno entero posición 1 y entrada posición 2.
    public static int doble(Connection c, int numero) throws SQLException {
        throw new UnsupportedOperationException("B09: CallableStatement");
    }
}
