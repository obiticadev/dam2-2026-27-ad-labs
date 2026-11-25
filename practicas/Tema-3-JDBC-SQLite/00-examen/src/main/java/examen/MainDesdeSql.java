package examen;

import examen.herramientas.Sqlite;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

// USA ESTE MAIN SI EL PROFESOR TE DA UN .sql (y borra MainDesdeSqlite.java).
// 1. Pega su SQL en sql/crear.sql.
// 2. Ejecuta SIN argumentos, con 00-examen como directorio de trabajo.
public class MainDesdeSql {
    public static void main(String[] args) throws Exception {
        File db = new File("datos/examen.sqlite");

        // Primera ejecución: crea la base y carga el SQL. Las siguientes la reutilizan.
        // Si cambias crear.sql, borra datos/examen.sqlite para que se vuelva a importar.
        if (!db.exists()) {
            Sqlite.crear(db, new File("sql/crear.sql"));
        }

        // abrir() activa las claves foráneas; el try cierra la conexión al terminar.
        try (Connection c = Sqlite.abrir(db)) {
            // ===== DIAGNÓSTICO: borra desde aquí... =====
            System.out.println("Base: " + db.getCanonicalPath());
            Sqlite.mostrarInfoConexion(c);
            Sqlite.mostrarTablas(c); // Sin tablas: el SQL falló; borra la base y corrige crear.sql.
            // ===== ...hasta aquí (ambas líneas incluidas). =====

            // >>> LLAMA AQUÍ A TUS MÉTODOS, pasándoles c. Ejemplo: listar(c);
        }
    }

    // >>> ESCRIBE AQUÍ TUS MÉTODOS. Usan la c que reciben: no abras otra ni la cierres.
    // static void listar(Connection c) throws SQLException { ... }
}
