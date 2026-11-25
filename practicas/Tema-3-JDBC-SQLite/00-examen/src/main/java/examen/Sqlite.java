package examen;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/*
 * HERRAMIENTAS PARA CUALQUIER MAIN (consulta EjemplosSqlite.java).
 *
 * Mantén la misma Connection c durante todo el ejercicio.
 * Estos métodos cierran sus sentencias y resultados, pero NO cierran c.
 * El SQL lo adaptas tú al enunciado. Los valores van en ?, en orden.
 * Los ? NO sirven para nombres de tabla/columna ni palabras como ORDER BY.
 *
 * insertar / actualizar / borrar NO hacen commit ni rollback por su cuenta.
 * Sin transacción, JDBC confirma cada sentencia (autocommit).
 * Para "todos los cambios o ninguno", usa iniciarTransaccion + commit/rollback.
 *
 * Esto es SQLite: no hay procedimientos almacenados ni updateRow().
 * Las reglas de negocio y el SQL siguen siendo parte de tu solución.
 */
public final class Sqlite {
    private Sqlite() {}

    public static Connection abrir(File archivo) throws IOException, SQLException {
        File db = archivo.getCanonicalFile();
        if (!db.isFile()) {
            throw new IOException("No existe la base SQLite: " + db);
        }
        // mode=rw impide que un error de ruta cree en silencio otra base vacía.
        // toURI codifica espacios, # y otros caracteres reservados del nombre.
        Connection c = DriverManager.getConnection("jdbc:sqlite:" + db.toURI().toASCIIString() + "?mode=rw");
        try {
            configurar(c);
            return c; // Quien llama es responsable de cerrar la conexión.
        } catch (SQLException original) {
            try {
                c.close();
            } catch (SQLException cierre) {
                original.addSuppressed(cierre);
            }
            throw original;
        }
    }

    public static void configurar(Connection c) throws SQLException {
        // SQLite aplica las FK por conexión. Activarlas antes de setAutoCommit(false).
        try (Statement st = c.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
            try (ResultSet rs = st.executeQuery("PRAGMA foreign_keys")) {
                if (!rs.next() || rs.getInt(1) != 1) {
                    throw new SQLException("Las claves foráneas no están activadas");
                }
            }
        }
    }

    public static String leerSql(File script) throws IOException {
        // El .sql es texto UTF-8. El .sqlite es binario y solo lo abre JDBC.
        try (FileInputStream entrada = new FileInputStream(script)) {
            return new String(entrada.readAllBytes(), "UTF-8");
        }
    }

    public static boolean crearFichero(File archivo) throws IOException {
        File db = archivo.getCanonicalFile();
        File carpeta = db.getParentFile();
        if (db.isDirectory() || carpeta == null) throw new IOException("Indica un fichero: " + db);
        if (!carpeta.isDirectory() && !carpeta.mkdirs() && !carpeta.isDirectory()) {
            throw new IOException("No se pudo crear la carpeta: " + carpeta);
        }
        return db.createNewFile(); // false si existe; nunca lo trunca.
    }

    public static void crear(File archivo, File script) throws IOException, SQLException {
        // Leer primero: si falta el .sql, no se crea ni siquiera el fichero vacío.
        String sql = leerSql(script);
        if (!crearFichero(archivo)) {
            throw new IOException("La base ya existe. Usa abrir o elige otro nombre: " + archivo);
        }
        try (Connection c = abrir(archivo)) {
            importar(c, sql);
        }
    }

    public static void importar(Connection c, String sql) throws SQLException {
        // Este método es dueño de la transacción. Usar en una conexión recién abierta.
        if (!c.getAutoCommit()) {
            throw new SQLException("Importar requiere una conexión con autocommit activado");
        }
        c.setAutoCommit(false);
        try {
            try (Statement st = c.createStatement()) {
                // Con el driver Xerial fijado, executeUpdate(String) ejecuta el script completo.
                // No dividir por ';': puede aparecer dentro de texto, comentarios o triggers.
                st.executeUpdate(sql);
            }
            c.commit();
        } catch (SQLException original) {
            deshacer(c, original);
            throw original;
        }
        c.setAutoCommit(true); // Solo tras el commit; restaurarlo antes podría confirmar cambios.
    }

    public static void deshacer(Connection c, SQLException original) {
        deshacer(c, (Exception) original);
    }

    // También admite errores de reglas/calculaciones Java (RuntimeException).
    public static void deshacer(Connection c, Exception original) {
        // Si falla también el rollback, preservar ambos errores y no llamar a setAutoCommit(true).
        try {
            c.rollback();
        } catch (SQLException rollback) {
            original.addSuppressed(rollback);
        }
    }

    // ==================== 1. PARÁMETROS Y CAMBIOS ====================

    // Ejemplo: preparar(c, "SELECT * FROM empleados WHERE id = ?", 1).
    // Devuelve una sentencia ABIERTA: úsala dentro de try-with-resources.
    // Normalmente basta con insertar/actualizar/borrar/consultar de abajo.
    public static PreparedStatement preparar(Connection c, String sql, Object... valores)
            throws SQLException {
        PreparedStatement ps = c.prepareStatement(sql);
        try {
            for (int i = 0; i < valores.length; i++) {
                if (valores[i] == null) {
                    ps.setNull(i + 1, Types.NULL);
                } else {
                    ps.setObject(i + 1, valores[i]);
                }
            }
            return ps;
        } catch (SQLException | RuntimeException original) {
            try {
                ps.close();
            } catch (SQLException cierre) {
                original.addSuppressed(cierre);
            }
            throw original;
        }
    }

    // Una sola sentencia SIN RETURNING. Devuelve cuántas filas insertó.
    // insertar(c, "INSERT INTO departamentos(nombre) VALUES (?)", "Compras");
    public static int insertar(Connection c, String sql, Object... valores) throws SQLException {
        return ejecutarCambio(c, sql, valores);
    }

    // actualizar(c, "UPDATE empleados SET salario = ? WHERE id = ?", 1900.0, 1);
    public static int actualizar(Connection c, String sql, Object... valores) throws SQLException {
        return ejecutarCambio(c, sql, valores);
    }

    // borrar(c, "DELETE FROM empleados WHERE id = ?", 4);
    public static int borrar(Connection c, String sql, Object... valores) throws SQLException {
        return ejecutarCambio(c, sql, valores);
    }

    private static int ejecutarCambio(Connection c, String sql, Object... valores) throws SQLException {
        try (PreparedStatement ps = preparar(c, sql, valores)) {
            return ps.executeUpdate();
        }
    }

    // Para UN alta con ID entero generado. El SQL debe acabar en RETURNING id.
    // insertarConId(c, "INSERT INTO departamentos(nombre) VALUES (?) RETURNING id", "Compras");
    // Con RETURNING se usa executeQuery(), NO executeUpdate().
    public static long insertarConId(Connection c, String sql, Object... valores) throws SQLException {
        try (PreparedStatement ps = preparar(c, sql, valores);
                ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) throw new SQLException("El INSERT no devolvió su ID");
            long id = rs.getLong(1);
            if (rs.wasNull()) throw new SQLException("El ID devuelto es NULL");
            if (rs.next()) throw new SQLException("Se esperaba el ID de una sola fila");
            return id;
        }
    }

    // Cero filas NO provoca una excepción JDBC. Esta comprobación sí.
    // Úsala dentro de la transacción para poder deshacer cambios anteriores.
    public static void exigirUnaFila(int filas) throws SQLException {
        if (filas != 1) {
            throw new SQLException("Se esperaba 1 fila afectada, pero fueron " + filas);
        }
    }

    // ==================== 2. CONSULTAS ====================

    // Devuelve las filas ya leídas: puedes recorrerlas cuando el cursor ya está cerrado.
    // Cada Map es una fila: fila.get("nombre"), fila.get("salario"), etc.
    // Lista vacía = sin resultados. SQL NULL se conserva como null de Java.
    // En un JOIN usa alias distintos (e.nombre AS empleado, d.nombre AS departamento).
    // Materializa todo en memoria: para resultados grandes, usa preparar + ResultSet.
    public static List<Map<String, Object>> consultar(Connection c, String sql, Object... valores)
            throws SQLException {
        List<Map<String, Object>> filas = new ArrayList<>();
        try (PreparedStatement ps = preparar(c, sql, valores);
                ResultSet rs = ps.executeQuery()) {
            ResultSetMetaData columnas = rs.getMetaData();
            while (rs.next()) {
                Map<String, Object> fila = new LinkedHashMap<>();
                for (int i = 1; i <= columnas.getColumnCount(); i++) {
                    String etiqueta = columnas.getColumnLabel(i);
                    if (fila.containsKey(etiqueta)) {
                        throw new SQLException("Columna repetida: " + etiqueta + ". Usa alias AS distintos");
                    }
                    fila.put(etiqueta, rs.getObject(i));
                }
                filas.add(fila);
            }
        }
        return filas;
    }

    // Para ver un SELECT directamente en la consola, sin programar un listado.
    // mostrarConsulta(c, "SELECT * FROM empleados WHERE salario >= ?", 1700);
    public static void mostrarConsulta(Connection c, String sql, Object... valores) throws SQLException {
        try (PreparedStatement ps = preparar(c, sql, valores);
                ResultSet rs = ps.executeQuery()) {
            ResultSetMetaData columnas = rs.getMetaData();
            for (int i = 1; i <= columnas.getColumnCount(); i++) {
                if (i > 1) System.out.print(" | ");
                System.out.print(columnas.getColumnLabel(i));
            }
            System.out.println();
            int filas = 0;
            while (rs.next()) {
                filas++;
                for (int i = 1; i <= columnas.getColumnCount(); i++) {
                    if (i > 1) System.out.print(" | ");
                    Object valor = rs.getObject(i);
                    System.out.print(valor == null ? "NULL" : valor);
                }
                System.out.println();
            }
            if (filas == 0) System.out.println("Sin resultados");
        }
    }

    // Para COUNT(*) o un único entero. Exige una sola fila, columna y valor no nulo.
    // long total = Sqlite.consultarLong(c, "SELECT COUNT(*) FROM empleados");
    public static long consultarLong(Connection c, String sql, Object... valores) throws SQLException {
        try (PreparedStatement ps = preparar(c, sql, valores);
                ResultSet rs = ps.executeQuery()) {
            if (rs.getMetaData().getColumnCount() != 1 || !rs.next()) {
                throw new SQLException("Se esperaba una fila con una sola columna");
            }
            long valor = rs.getLong(1);
            if (rs.wasNull()) throw new SQLException("La consulta devolvió NULL");
            if (rs.next()) throw new SQLException("La consulta devolvió más de una fila");
            return valor;
        }
    }

    // Para distinguir SQL NULL de un 0 real cuando recorres tu propio ResultSet.
    public static Integer leerEnteroNullable(ResultSet rs, String columna) throws SQLException {
        int valor = rs.getInt(columna);
        return rs.wasNull() ? null : Integer.valueOf(valor);
    }

    public static Double leerDecimalNullable(ResultSet rs, String columna) throws SQLException {
        double valor = rs.getDouble(columna);
        return rs.wasNull() ? null : Double.valueOf(valor);
    }

    // ==================== 3. TRANSACCIONES Y SAVEPOINT ====================

    public static void iniciarTransaccion(Connection c) throws SQLException {
        if (!c.getAutoCommit()) throw new SQLException("Ya hay una transacción en curso");
        c.setAutoCommit(false);
    }

    // Confirma TODO. Solo vuelve a autocommit si commit() ha terminado bien.
    public static void commit(Connection c) throws SQLException {
        exigirTransaccion(c);
        c.commit();
        c.setAutoCommit(true);
    }

    // Deshace TODO. Solo vuelve a autocommit si rollback() ha terminado bien.
    public static void rollback(Connection c) throws SQLException {
        exigirTransaccion(c);
        c.rollback();
        c.setAutoCommit(true);
    }

    // En un catch usa deshacer(c, error) y relanza el error.
    // Conserva el error original si también falla el rollback.
    // deshacer NO reactiva autocommit: sal del try de la conexión para cerrarla.
    // Nunca pongas setAutoCommit(true) en un finally: podría confirmar pendientes.

    public static Savepoint guardarPunto(Connection c, String nombre) throws SQLException {
        exigirTransaccion(c);
        return c.setSavepoint(nombre);
    }

    // Deshace lo posterior al punto; la transacción sigue abierta.
    public static void rollbackHasta(Connection c, Savepoint punto) throws SQLException {
        exigirTransaccion(c);
        c.rollback(punto);
    }

    // Liberar un punto NO es commit: aún hay que confirmar o deshacer.
    public static void liberarPunto(Connection c, Savepoint punto) throws SQLException {
        exigirTransaccion(c);
        c.releaseSavepoint(punto);
    }

    private static void exigirTransaccion(Connection c) throws SQLException {
        if (c.getAutoCommit()) throw new SQLException("Primero llama a iniciarTransaccion(c)");
    }

    // ==================== 4. METADATOS ====================

    public static void mostrarInfoConexion(Connection c) throws SQLException {
        DatabaseMetaData meta = c.getMetaData();
        System.out.println("Motor: " + meta.getDatabaseProductName() + " " + meta.getDatabaseProductVersion());
        System.out.println("Driver: " + meta.getDriverName() + " " + meta.getDriverVersion());
    }

    public static void mostrarTablas(Connection c) throws SQLException {
        DatabaseMetaData meta = c.getMetaData();
        try (ResultSet tablas = meta.getTables(null, null, "%", new String[] {"TABLE"})) {
            while (tablas.next()) {
                String nombre = tablas.getString("TABLE_NAME");
                if (!nombre.startsWith("sqlite_")) System.out.println("Tabla: " + nombre);
            }
        }
    }

    public static void mostrarColumnas(Connection c, String tabla) throws SQLException {
        DatabaseMetaData meta = c.getMetaData();
        try (ResultSet columnas = meta.getColumns(null, null, tabla, "%")) {
            while (columnas.next()) {
                // getColumns recibe un patrón; este filtro exige el nombre exacto.
                if (tabla.equals(columnas.getString("TABLE_NAME"))) {
                    System.out.println(columnas.getString("COLUMN_NAME") + " | "
                            + columnas.getString("TYPE_NAME") + " | admite NULL: "
                            + columnas.getString("IS_NULLABLE"));
                }
            }
        }
    }

    public static void mostrarClavesForaneas(Connection c, String tabla) throws SQLException {
        DatabaseMetaData meta = c.getMetaData();
        try (ResultSet claves = meta.getImportedKeys(null, null, tabla)) {
            while (claves.next()) {
                System.out.println(claves.getString("FKCOLUMN_NAME") + " -> "
                        + claves.getString("PKTABLE_NAME") + "." + claves.getString("PKCOLUMN_NAME"));
            }
        }
    }

    // ResultSetMetaData: columnas del SELECT (incluidos alias), no de toda la tabla.
    public static void mostrarColumnasConsulta(Connection c, String sql, Object... valores) throws SQLException {
        try (PreparedStatement ps = preparar(c, sql, valores);
                ResultSet rs = ps.executeQuery()) {
            ResultSetMetaData columnas = rs.getMetaData();
            for (int i = 1; i <= columnas.getColumnCount(); i++) {
                System.out.println(columnas.getColumnLabel(i) + " | " + columnas.getColumnTypeName(i));
            }
        }
    }
}
