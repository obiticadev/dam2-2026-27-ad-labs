package examen;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main {
    /*
     * GUÍA PARA USAR ESTA PLANTILLA EN EL EXAMEN
     *
     * 1. Abre el pom.xml de 00-examen en el IDE.
     * Usa JDK 17 o superior y 00-examen como directorio de trabajo.
     *
     * 2. Para probar el ejemplo, ejecuta primero MainDesdeSql.
     * Después ejecuta este main SIN argumentos: abre datos/examen.sqlite.
     * Debe listar empleados, mostrar "FK activas" y "Tablas de usuario: 2".
     * Si usas otra base, adapta o quita el SELECT de empleados de abajo.
     *
     * 3. Prepara la base según lo que te entregue el profesor:
     *
     * SI TE DA UN .sql:
     * - Pega su contenido en sql/crear.sql.
     * - En "Argumentos del programa" del IDE escribe:
     * crear datos/examen.sqlite sql/crear.sql
     * - Ejecuta UNA VEZ para construir la base.
     * - Para las siguientes ejecuciones cambia los argumentos a:
     * abrir datos/examen.sqlite
     *
     * SI TE DA UN .sqlite:
     * - Copia el fichero a datos/ (mejor trabajar sobre una copia).
     * - Si se llama examen.sqlite, usa estos argumentos:
     * abrir datos/examen.sqlite
     * - No hace falta importar SQL: las tablas ya están en el fichero.
     *
     * Las rutas datos/ y sql/ son relativas a 00-examen.
     * "crear" rechaza una base que ya exista; "abrir" exige que exista.
     *
     * 4. Cuando hayas comprobado la base, elimina los DOS bloques marcados
     * INICIO BORRAR / FIN BORRAR (incluidas esas líneas).
     * Son solo de diagnóstico. Después ejecuta siempre con argumentos.
     *
     * 5. Escribe las llamadas a tu solución en PASO A y define tus métodos
     * en PASO B. No necesitas modificar Sqlite.java.
     *
     * Usa esta plantilla solo si está permitida en el examen.
     */
    public static void main(String[] args) throws Exception {
        // INICIO BORRAR 1: selección de base por defecto sin argumentos.
        // La base debe existir: usa MainDesdeSql si necesitas crearla.
        boolean pruebaArranque = args.length == 0;
        if (pruebaArranque) {
            File prueba = new File("datos", "examen.sqlite");
            args = new String[] { "abrir", prueba.getPath() };
        }
        // FIN BORRAR 1.

        // CONSERVAR: elegir el fichero de la base según los argumentos.
        // Este selector prepara la base; no resuelve el ejercicio.
        File db;
        if (args.length == 3 && args[0].equals("crear")) {
            // CASO 1: EL PROFESOR TE DA UN .sql.
            // Argumentos: crear datos/examen.sqlite sql/crear.sql
            //
            // args[1] = fichero .sqlite NUEVO que quieres crear.
            // args[2] = fichero .sql que contiene CREATE TABLE, INSERT, etc.
            // new File(...) solo representa una ruta: NO crea la base.
            // La llamada Sqlite.crear(...) de abajo es la que:
            // 1. Crea el fichero .sqlite.
            // 2. Ejecuta el SQL para cargar las tablas y datos.
            //
            // No tienes que llamar además a crearFichero() ni importar().
            // Tampoco tienes que repetir los CREATE TABLE en tus métodos.
            db = new File(args[1]);
            Sqlite.crear(db, new File(args[2]));
        } else if (args.length == 2 && args[0].equals("abrir")) {
            // CASO 2: EL PROFESOR TE DA UN .sqlite, O YA LO HAS CREADO.
            // Argumentos: abrir datos/examen.sqlite
            //
            // Solo guardamos la ruta. La conexión se abre en el try de abajo.
            // NO llames a Sqlite.crear(): el fichero ya contiene la base.
            // Usa también este modo para volver a ejecutar tu solución
            // sin intentar crear otra vez la misma base.
            db = new File(args[1]);
        } else {
            throw new IllegalArgumentException(
                    "Uso: Main crear <base.sqlite> <script.sql> | Main abrir <base.sqlite>");
        }

        // CONSERVAR: abre la conexión y la cierra automáticamente al salir.
        // "c" es la conexión que debes pasar a tus métodos del ejercicio.
        // Sqlite.abrir también activa las claves foráneas.
        // No captura la excepción para permitir ver el error real.
        //
        // PUNTO COMÚN A LOS DOS CASOS:
        // Tanto después de "crear" como al elegir "abrir", llegamos aquí.
        // Sqlite.abrir(db) devuelve la Connection para trabajar con las tablas.
        //
        // Si el profesor exige una ruta fija y ejecutar SIN argumentos,
        // puedes sustituir TODO el selector anterior (desde File db hasta
        // justo antes de estos comentarios) por UNA de estas alternativas:
        //
        // A) Si ya tienes la base:
        // File db = new File("datos/examen.sqlite");
        //
        // B) Si tienes que construirla desde SQL (solo la primera vez):
        // File db = new File("datos/examen.sqlite");
        // Sqlite.crear(db, new File("sql/crear.sql"));
        //
        // En B, tras crearla, quita la llamada a Sqlite.crear para reabrirla.
        // Estas alternativas REEMPLAZAN el selector, no se pegan además.
        // Antes de hacerlo elimina los DOS bloques de diagnóstico.
        try (Connection c = Sqlite.abrir(db)) {
            try (PreparedStatement ps = c.prepareStatement("SELECT * FROM empleados");
                    ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    System.out.println(
                            rs.getInt("id") + " - " +
                                    rs.getString("nombre") + " - " +
                                    rs.getDouble("salario"));
                }
            }
            // INICIO BORRAR 2: salida de diagnóstico y cabecera de la base de prueba.
            System.out.println("Base: " + db.getCanonicalPath());
            if (pruebaArranque && db.length() == 0) {
                // Materializar la cabecera SQLite sin crear tablas en la base de prueba.
                try (Statement st = c.createStatement()) {
                    st.execute("PRAGMA user_version = 0");
                }
            }
            try (Statement st = c.createStatement();
                    ResultSet rs = st.executeQuery("""
                            SELECT sqlite_version(), COUNT(*)
                            FROM sqlite_master WHERE type = 'table' AND name NOT GLOB 'sqlite_*'
                            """)) {
                if (rs.next()) {
                    System.out.printf("SQLite %s | FK activas | Tablas de usuario: %d%n",
                            rs.getString(1), rs.getInt(2));
                }
            }
            // FIN BORRAR 2.

            // PASO A — ESCRIBE AQUÍ LAS LLAMADAS A TU SOLUCIÓN.
            // Herramientas preparadas: Sqlite.insertar / actualizar / borrar,
            // mostrarConsulta, iniciarTransaccion, commit, rollback y savepoints.
            // Consulta EjemplosSqlite.java para copiar las llamadas completas.
            //
            // Por ejemplo, si has definido un método listar(Connection c):
            // listar(c);
            //
            // O si el ejercicio pide un alta y después un listado:
            // darDeAlta(c);
            // listar(c);
            //
            // Estos nombres son ejemplos: los métodos NO vienen hechos.
            // Debes programarlos abajo, en PASO B, según el enunciado.
            //
            // IMPORTANTE: si varios cambios deben hacerse "todos o ninguno",
            // programa su transacción usando ESTA MISMA conexión:
            // setAutoCommit(false), commit() y rollback() si algo falla.
            // La transacción de importación del .sql NO cubre tu solución.
        }
    }

    // PASO B — DEFINE AQUÍ TUS MÉTODOS.
    // Están FUERA de main, pero DENTRO de la clase Main.
    //
    // Ejemplo de estructura (quita // y completa el cuerpo para usarlo):
    //
    // static void listar(Connection c) throws java.sql.SQLException {
    // // 1. Escribe el SELECT que pida el enunciado.
    // // 2. Crea un PreparedStatement con c.prepareStatement(sql).
    // // 3. Rellena los parámetros ? si los hay.
    // // 4. Usa executeQuery() y recorre el ResultSet con while (rs.next()).
    // // Cierra PreparedStatement y ResultSet con try-with-resources.
    // // NO cierres c aquí: la cierra el try de main.
    // }
    //
    // Para INSERT, UPDATE o DELETE usa executeUpdate() y comprueba
    // las filas afectadas. Los valores se pasan con parámetros ?, no
    // concatenando lo que escriba el usuario dentro del SQL.
}
