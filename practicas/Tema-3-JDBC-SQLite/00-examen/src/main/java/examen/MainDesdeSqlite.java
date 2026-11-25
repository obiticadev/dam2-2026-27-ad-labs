package examen;

import examen.herramientas.Sqlite;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

// Te dan un .sqlite: cópialo a datos/examen.sqlite, borra MainDesdeSql.java y ejecuta este.
public class MainDesdeSqlite {
    public static void main(String[] args) throws Exception {
        File db = new File("datos/examen.sqlite");
        try (Connection c = Sqlite.abrir(db)) { // Conexión con FK activadas; se cierra sola (falla si el fichero no existe).
            Sqlite.b_diagnostico(c, db); // BORRAR antes de entregar.
            // >>> Tus llamadas aquí, pasando c: ejemplo(c);
        }
    }

    // >>> Tus métodos aquí: static void ejemplo(Connection c) throws SQLException { ... } (usan c, no la cierran).
}
