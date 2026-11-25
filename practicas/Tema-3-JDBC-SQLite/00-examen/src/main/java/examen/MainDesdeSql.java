package examen;

import java.io.File;
import java.sql.Connection;

// USA ESTE MAIN SI EL PROFESOR TE DA UN .sql.
// 1. El ejemplo ya está en sql/crear.sql. Sustitúyelo por el SQL del profesor.
// 2. Ejecuta esta clase SIN argumentos.
// Directorio de trabajo del IDE: 00-examen.
// Para el examen, cambia examen.sqlite por examen-real.sqlite:
// así no reutilizarás por error la base de ejemplo ya creada.
public class MainDesdeSql {
    public static void main(String[] args) throws Exception {
        File db = new File("datos/examen.sqlite");
        File script = new File("sql/crear.sql");

        // Solo crea la base la primera vez. Después reutiliza la que ya existe.
        // Si cambias el SQL, NO se vuelve a importar sobre esa base:
        // cambia el nombre examen.sqlite por otro nuevo para empezar de cero.
        if (!db.exists()) {
            Sqlite.crear(db, script);
        }

        try (Connection c = Sqlite.abrir(db)) {
            System.out.println("Base abierta: " + db.getCanonicalPath());
            Sqlite.exigirUnaFila(1);
            // ESCRIBE AQUÍ las llamadas a tus métodos:
            // listar(c);
            // insertar(c);
            //
            // Ya puedes usar las herramientas de Sqlite directamente:
            // Sqlite.mostrarConsulta(c, "SELECT * FROM empleados");
            // int filas = Sqlite.actualizar(c,
            // "UPDATE empleados SET salario = ? WHERE id = ?", 1900, 1);
            // Más recetas (INSERT, transacción, rollback...): EjemplosSqlite.java.
        }
    }

    // ESCRIBE AQUÍ tus métodos, fuera de main y dentro de esta clase.
    // Usa la Connection c que reciban; no abras otra ni cierres c aquí.
}
