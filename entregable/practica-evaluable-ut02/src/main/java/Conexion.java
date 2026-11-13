import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    private static final String URL = "jdbc:sqlite:empdept.db";

    // Abrimos la base de datos que está en la carpeta del proyecto
    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
