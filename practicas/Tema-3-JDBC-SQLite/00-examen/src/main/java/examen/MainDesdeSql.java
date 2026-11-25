package examen;

import examen.herramientas.Sqlite;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

// Te dan un .sql: pégalo en sql/crear.sql y borra MainDesdeSqlite.java.
public class MainDesdeSql {
    public static void main(String[] args) throws Exception {
        File db = new File("datos/examen.sqlite"); // Bórrala si cambia el SQL.
        if (!db.exists()) {
            Sqlite.crear(db, new File("sql/crear.sql")); // Solo la 1.ª vez.
        }
        try (Connection c = Sqlite.abrir(db)) { // FK activadas; se cierra sola.
            Sqlite.b_diagnostico(c, db); // BORRAR antes de entregar.
            // >>> Tus llamadas aquí, pasando c: ejemplo(c);
        }
    }

    // >>> Tus métodos: static void ejemplo(Connection c) throws SQLException
}
