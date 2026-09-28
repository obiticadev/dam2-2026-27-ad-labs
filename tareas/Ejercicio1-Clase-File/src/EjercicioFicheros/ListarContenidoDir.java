// OLIVER BITICA
package EjercicioFicheros;

import java.io.File;
import java.io.IOException;
import java.io.FilenameFilter;

public class ListarContenidoDir {
    public static void main(String[] args) {
        // (1) Listar contenido y atributos del directorio de trabajo
        System.out.println("Ejercicio 1");
        File directorioActual = new File(".");

        // Control de errores: comprobar existencia y que sea directorio
        if (!directorioActual.exists() || !directorioActual.isDirectory()) {
            System.err.println("Error: El directorio no existe o no es válido.");
            return;
        }

        // listFiles() obtiene los ficheros; se valida que no devuelva null por permisos
        File[] ficheros = directorioActual.listFiles();
        if (ficheros == null) {
            System.err.println("Error: No se pudo leer el directorio o no se tienen permisos.");
            return;
        }
        for (File file : ficheros) {
            System.out.println(salida(file));
        }

        // (2) Crear dos ficheros .txt en el directorio
        System.out.println("Ejercicio 2");
        try {
            File fichero1 = new File(directorioActual, "fichero1.txt");
            File fichero2 = new File(directorioActual, "fichero2.txt");

            // createNewFile() crea el fichero físicamente en disco si no existe
            fichero1.createNewFile();
            fichero2.createNewFile();
        } catch (IOException e) {
            // IOException es obligatoria de capturar al realizar operaciones de E/S
            System.err.println("Error al crear los ficheros: " + e.getMessage());
        }

        // (3) Filtrar ficheros .txt usando la interfaz FilenameFilter
        System.out.println("Ejercicio 3");
        FilenameFilter filtroTxt = (dir, name) -> name.toLowerCase().endsWith(".txt");
        File[] ficherosFiltrados = directorioActual.listFiles(filtroTxt);
        if (ficherosFiltrados == null) {
            System.err.println("Error: No se pudieron leer los ficheros filtrados.");
            return;
        }
        for (File file : ficherosFiltrados) {
            System.out.println(salida(file));
        }
    }

    public static String nombre(File file) {
        return file.getName();
    }

    public static boolean sePuedeLeer(File file) {
        return file.canRead();
    }

    public static boolean sePuedeEscribir(File file) {
        return file.canWrite();
    }

    public static long tamaño(File file) {
        return file.length();
    }

    public static String rutaAbsoluta(File file) {
        return file.getAbsolutePath();
    }

    // Formatea y concatena los atributos requeridos del fichero usando
    // StringBuilder
    public static String salida(File file) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("nombre: %s%n", nombre(file)))
                .append(String.format("se puede leer: %b%n", sePuedeLeer(file)))
                .append(String.format("se puede escribir: %b%n", sePuedeEscribir(file)))
                .append(String.format("tamaño: %d bytes%n", tamaño(file)))
                .append(String.format("ruta absoluta: %s%n%n", rutaAbsoluta(file)));
        return sb.toString();
    }
}
