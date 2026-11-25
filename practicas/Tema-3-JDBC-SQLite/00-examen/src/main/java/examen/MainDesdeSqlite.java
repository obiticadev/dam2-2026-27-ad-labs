package examen;

import java.io.File;
import java.sql.Connection;

// USA ESTE MAIN SI EL PROFESOR TE DA UN .sqlite.
// 1. Guarda una copia de la base en datos/examen.sqlite.
//    Ya tienes ahí una base de ejemplo para probar.
//    Para el examen, reemplaza ese fichero por una copia de la base real
//    con el mismo nombre (con las conexiones y DB Browser cerrados).
// 2. Ejecuta esta clase SIN argumentos.
// Directorio de trabajo del IDE: 00-examen.
public class MainDesdeSqlite {
    public static void main(String[] args) throws Exception {
        File db = new File("datos/examen.sqlite");

        // La base ya existe: NO hay que crearla ni ejecutar un script SQL.
        try (Connection c = Sqlite.abrir(db)) {
            System.out.println("Base abierta: " + db.getCanonicalPath());

            // ESCRIBE AQUÍ las llamadas a tus métodos:
            // listar(c);
            // insertar(c);
            //
            // Ya puedes usar las herramientas de Sqlite directamente:
            // Sqlite.mostrarConsulta(c, "SELECT * FROM empleados");
            // int filas = Sqlite.actualizar(c,
            //         "UPDATE empleados SET salario = ? WHERE id = ?", 1900, 1);
            // Más recetas (INSERT, transacción, rollback...): EjemplosSqlite.java.
        }
    }

    // ESCRIBE AQUÍ tus métodos, fuera de main y dentro de esta clase.
    // Usa la Connection c que reciban; no abras otra ni cierres c aquí.
}
