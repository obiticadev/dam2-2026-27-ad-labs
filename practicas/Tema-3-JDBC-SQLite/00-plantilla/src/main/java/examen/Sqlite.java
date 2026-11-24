package examen;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
        // Si falla también el rollback, preservar ambos errores y no llamar a setAutoCommit(true).
        try {
            c.rollback();
        } catch (SQLException rollback) {
            original.addSuppressed(rollback);
        }
    }
}
