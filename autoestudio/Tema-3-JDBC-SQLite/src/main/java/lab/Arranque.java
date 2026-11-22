package lab;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;

/** Bloque 00: referencia mínima funcional, no es la solución de los ejercicios. */
public final class Arranque {
    public static Connection abrir(Path archivo) throws Exception {
        Path absoluta = archivo.toAbsolutePath();
        Files.createDirectories(absoluta.getParent());
        Connection c = DriverManager.getConnection("jdbc:sqlite:" + absoluta);
        try (Statement st = c.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
        } catch (SQLException e) {
            c.close();
            throw e;
        }
        return c;
    }

    public static void main(String[] args) throws Exception {
        Path archivo = Path.of(args.length == 0 ? "datos/arranque.db" : args[0]);
        try (Connection c = abrir(archivo); Statement st = c.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS prueba (id INTEGER PRIMARY KEY, nombre TEXT NOT NULL)");
            try (PreparedStatement ps = c.prepareStatement("INSERT OR IGNORE INTO prueba(id,nombre) VALUES (?,?)")) {
                ps.setInt(1, 1);
                ps.setString(2, "Conexión lista");
                ps.executeUpdate();
            }
            try (ResultSet rs = st.executeQuery("SELECT nombre FROM prueba WHERE id=1")) {
                if (rs.next()) System.out.println(rs.getString("nombre"));
            }
            System.out.println(c.getMetaData().getDatabaseProductName());
            System.out.println(archivo.toAbsolutePath());
        }
    }
}
