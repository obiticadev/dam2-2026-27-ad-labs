package examen;

import examen.herramientas.Sqlite;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

// Te dan un .sql: pégalo en sql/crear.sql, borra MainDesdeSqlite.java y ejecuta este (si cambias el SQL, borra datos/examen.sqlite).
public class MainDesdeSql {
    public static void main(String[] args) throws Exception {
        File db = new File("datos/examen.sqlite");
        if (!db.exists())
            Sqlite.crear(db, new File("sql/crear.sql")); // Solo la primera vez: crea la base desde el SQL.
        try (Connection c = Sqlite.abrir(db)) { // Conexión con FK activadas; se cierra sola.
            Sqlite.b_diagnostico(c, db); // BORRAR antes de entregar.
            // >>> Tus llamadas aquí, pasando c: ejemplo(c);
        }
    }

    // >>> Tus métodos aquí: static void ejemplo(Connection c) throws SQLException {
    // ... } (usan c, no la cierran).
}
