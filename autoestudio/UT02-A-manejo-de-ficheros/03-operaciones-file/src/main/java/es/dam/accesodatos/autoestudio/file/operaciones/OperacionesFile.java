package es.dam.accesodatos.autoestudio.file.operaciones;

import java.io.File;
import java.io.IOException;

/**
 * Ejercicios de creación, renombrado y borrado con {@link File}.
 *
 * <p>Teoría: {@code teoria/operaciones-file.md}. Los métodos describen
 * operaciones concretas del sistema de archivos y sus resultados booleanos.
 */
public final class OperacionesFile {

    private OperacionesFile() {
    }

    /**
     * Crea un fichero nuevo en la ruta indicada si todavía no existe.
     *
     * @param destino ruta del fichero que se quiere crear
     * @return {@code true} si se creó en esta llamada; {@code false} si ya existía
     * @throws IllegalArgumentException si la ruta es nula
     * @throws IOException si no se puede crear el fichero
     */
    public static boolean crearFichero(File destino) throws IOException {
        // TODO: Rechaza una ruta nula con IllegalArgumentException.
        // TODO: Solicita crear un fichero nuevo sin sobrescribir uno existente.
        // TODO: Conserva la distinción entre creado y ya existente.
        // TODO: Permite que IOException describa un fallo de entrada/salida.
        // TODO: Devuelve el resultado de la operación de creación.
        return false;
    }

    /**
     * Crea un único directorio; no intenta crear sus padres ausentes.
     *
     * @param destino ruta del directorio que se quiere crear
     * @return {@code true} si se creó; {@code false} si ya existe o no se pudo crear
     * @throws IllegalArgumentException si la ruta es nula
     */
    public static boolean crearDirectorio(File destino) {
        // TODO: Rechaza una ruta nula con IllegalArgumentException.
        // TODO: Intenta crear solo el último nivel de directorio solicitado.
        // TODO: No crees automáticamente los directorios padres.
        // TODO: Devuelve el booleano informado por la operación de File.
        return false;
    }

    /**
     * Crea la ruta de directorios y los padres que falten.
     *
     * @param destino ruta de directorio que se quiere preparar
     * @return {@code true} si se crearon directorios; {@code false} si ya existían o falló
     * @throws IllegalArgumentException si la ruta es nula
     */
    public static boolean crearDirectorios(File destino) {
        // TODO: Rechaza una ruta nula con IllegalArgumentException.
        // TODO: Intenta crear el directorio y los niveles padres que falten.
        // TODO: No trates un resultado falso como éxito.
        // TODO: Devuelve el resultado booleano de la operación.
        return false;
    }

    /**
     * Cambia la ruta de una entrada sin sobrescribir un destino existente.
     *
     * @param origen entrada que se desea renombrar
     * @param destino nueva ruta de la entrada
     * @return {@code true} si se completó el cambio; {@code false} en otro caso
     * @throws IllegalArgumentException si algún argumento es nulo
     */
    public static boolean renombrar(File origen, File destino) {
        // TODO: Rechaza argumentos nulos con IllegalArgumentException.
        // TODO: No intentes renombrar si el origen no existe.
        // TODO: No sobrescribas una entrada que ya exista en el destino.
        // TODO: Solicita el cambio de nombre a File.
        // TODO: Devuelve si el sistema informó que el cambio se completó.
        return false;
    }

    /**
     * Borra un fichero o un directorio vacío.
     *
     * @param entrada fichero o directorio que se desea borrar
     * @return {@code true} si se borró; {@code false} si no se pudo borrar
     * @throws IllegalArgumentException si la entrada es nula
     */
    public static boolean borrar(File entrada) {
        // TODO: Rechaza una referencia nula con IllegalArgumentException.
        // TODO: Solicita borrar la entrada, sin implementar borrado recursivo.
        // TODO: Mantén el resultado falso si no existe o no se puede borrar.
        // TODO: Devuelve el booleano informado por File.
        return false;
    }

    /**
     * Playground de las operaciones en una carpeta regenerable de Maven.
     *
     * @param args argumentos de línea de comandos, no utilizados
     * @throws IOException si falla la creación del fichero de demostración
     */
    public static void main(String[] args) throws IOException {
        File directorio = new File("target/demo-data/03-operaciones-file");
        if (!directorio.isDirectory()) {
            crearDirectorios(directorio);
        }
        if (!directorio.isDirectory()) {
            System.out.println("Completa crearDirectorios() para preparar la carpeta de demostración.");
            return;
        }

        File temporal = new File(directorio, "borrador.txt");
        if (temporal.exists() && !borrar(temporal) && temporal.exists()) {
            System.out.println("Completa borrar() para reiniciar el fichero de demostración.");
            return;
        }
        boolean creado = crearFichero(temporal);
        if (!temporal.isFile()) {
            System.out.println("Completa crearFichero() para continuar la demostración.");
            return;
        }
        System.out.println("createNewFile() creó un fichero nuevo: " + creado);
        System.out.println("delete() eliminó el fichero: " + borrar(temporal));
    }
}
