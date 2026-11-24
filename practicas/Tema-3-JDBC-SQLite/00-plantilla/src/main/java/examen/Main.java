package examen;

import java.io.File;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) throws Exception {
        // INICIO BORRAR 1: modo de prueba sin argumentos.
        boolean pruebaArranque = args.length == 0;
        if (pruebaArranque) {
            File prueba = new File("target", "arranque.sqlite");
            Sqlite.crearFichero(prueba);
            args = new String[] {"abrir", prueba.getPath()};
        }
        // FIN BORRAR 1.

        File db;
        if (args.length == 3 && args[0].equals("crear")) {
            db = new File(args[1]);
            Sqlite.crear(db, new File(args[2]));
        } else if (args.length == 2 && args[0].equals("abrir")) {
            db = new File(args[1]);
        } else {
            throw new IllegalArgumentException(
                    "Uso: Main crear <base.sqlite> <script.sql> | Main abrir <base.sqlite>");
        }

        // El try cierra JDBC aunque haya una excepción. No captura para permitir ver el error real.
        try (Connection c = Sqlite.abrir(db)) {
            // INICIO BORRAR 2: salida de diagnóstico y cabecera de la base de prueba.
            System.out.println("Base: " + db.getCanonicalPath());
            if (pruebaArranque && db.length() == 0) {
                // Materializar la cabecera SQLite sin crear tablas en la base de prueba.
                try (var st = c.createStatement()) {
                    st.execute("PRAGMA user_version = 0");
                }
            }
            try (var st = c.createStatement(); var rs = st.executeQuery("""
                    SELECT sqlite_version(), COUNT(*)
                    FROM sqlite_master WHERE type = 'table' AND name NOT GLOB 'sqlite_*'
                    """)) {
                if (rs.next()) {
                    System.out.printf("SQLite %s | FK activas | Tablas de usuario: %d%n",
                            rs.getString(1), rs.getInt(2));
                }
            }
            // FIN BORRAR 2.

            // ESCRIBE AQUÍ: llama a tus métodos pasando la misma Connection c.
        }
    }
}
