package examen;

import examen.herramientas.Sqlite;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

// Te dan un .sqlite: cópialo a datos/examen.sqlite y borra MainDesdeSql.java.
public class MainDesdeSqlite {
    public static void main(String[] args) throws Exception {
        File db = new File("datos/examen.sqlite");
        try (Connection c = Sqlite.abrir(db)) { // FK activadas; debe existir.
            Sqlite.b_diagnostico(c, db); // BORRAR antes de entregar.
            // >>> Tus llamadas aquí, pasando c: ejemplo(c);
        }
    }

    // >>> Tus métodos: static void ejemplo(Connection c) throws SQLException
}
