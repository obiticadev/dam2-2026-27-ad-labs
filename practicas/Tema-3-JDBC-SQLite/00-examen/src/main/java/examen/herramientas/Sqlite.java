package examen.herramientas;

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

public final class Sqlite {
    private Sqlite() {
    }

    public static Connection abrir(File archivo) throws IOException, SQLException {
        File db = archivo.getCanonicalFile();
        if (!db.isFile()) {
            throw new IOException("No existe la base SQLite: " + db);
        }

        Connection c = DriverManager.getConnection("jdbc:sqlite:" + db.toURI().toASCIIString() + "?mode=rw");
        try {
            configurar(c);
            return c;
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
        try (FileInputStream entrada = new FileInputStream(script)) {
            return new String(entrada.readAllBytes(), "UTF-8");
        }
    }

    public static boolean crearFichero(File archivo) throws IOException {
        File db = archivo.getCanonicalFile();
        File carpeta = db.getParentFile();
        if (db.isDirectory() || carpeta == null)
            throw new IOException("Indica un fichero: " + db);
        if (!carpeta.isDirectory() && !carpeta.mkdirs() && !carpeta.isDirectory()) {
            throw new IOException("No se pudo crear la carpeta: " + carpeta);
        }
        return db.createNewFile();
    }

    public static void crear(File archivo, File script) throws IOException, SQLException {
        String sql = leerSql(script);
        if (!crearFichero(archivo)) {
            throw new IOException("La base ya existe. Usa abrir o elige otro nombre: " + archivo);
        }
        try (Connection c = abrir(archivo)) {
            importar(c, sql);
        }
    }

    public static void importar(Connection c, String sql) throws SQLException {

        if (!c.getAutoCommit()) {
            throw new SQLException("Importar requiere una conexión con autocommit activado");
        }
        c.setAutoCommit(false);
        try {
            try (Statement st = c.createStatement()) {

                st.executeUpdate(sql);
            }
            c.commit();
        } catch (SQLException original) {
            deshacer(c, original);
            throw original;
        }
        c.setAutoCommit(true);
    }

    public static void deshacer(Connection c, SQLException original) {
        deshacer(c, (Exception) original);
    }

    public static void deshacer(Connection c, Exception original) {

        try {
            c.rollback();
        } catch (SQLException rollback) {
            original.addSuppressed(rollback);
        }
    }

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

    public static int actualizar(Connection c, String sql, Object... valores) throws SQLException {
        return ejecutarCambio(c, sql, valores);
    }

    private static int ejecutarCambio(Connection c, String sql, Object... valores) throws SQLException {
        try (PreparedStatement ps = preparar(c, sql, valores)) {
            return ps.executeUpdate();
        }
    }

    public static void iniciarTransaccion(Connection c) throws SQLException {
        if (!c.getAutoCommit())
            throw new SQLException("Ya hay una transacción en curso");
        c.setAutoCommit(false);
    }

    public static void commit(Connection c) throws SQLException {
        exigirTransaccion(c);
        c.commit();
        c.setAutoCommit(true);
    }

    public static void rollback(Connection c) throws SQLException {
        exigirTransaccion(c);
        c.rollback();
        c.setAutoCommit(true);
    }

    private static void exigirTransaccion(Connection c) throws SQLException {
        if (c.getAutoCommit())
            throw new SQLException("Primero llama a iniciarTransaccion(c)");
    }

}
