package examen;

import examen.herramientas.Sqlite;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

// USA ESTE MAIN SI EL PROFESOR TE DA UN .sqlite (y borra MainDesdeSql.java).
// 1. Copia la base a datos/examen.sqlite (mejor una copia, con DB Browser cerrado).
// 2. Ejecuta SIN argumentos, con 00-examen como directorio de trabajo.
public class MainDesdeSqlite {
    public static void main(String[] args) throws Exception {
        File db = new File("datos/examen.sqlite");

        // abrir() exige que el fichero exista (no crea uno vacío) y activa las claves foráneas.
        try (Connection c = Sqlite.abrir(db)) {
            // ===== DIAGNÓSTICO: borra desde aquí... =====
            System.out.println("Base: " + db.getCanonicalPath());
            Sqlite.mostrarInfoConexion(c);
            Sqlite.mostrarTablas(c); // Si no salen las tablas del enunciado, la ruta es otra.
            // ===== ...hasta aquí (ambas líneas incluidas). =====

            // >>> LLAMA AQUÍ A TUS MÉTODOS, pasándoles c. Ejemplo: listar(c);
        }
    }

    // >>> ESCRIBE AQUÍ TUS MÉTODOS. Usan la c que reciben: no abras otra ni la cierres.
    // static void listar(Connection c) throws SQLException { ... }
}
