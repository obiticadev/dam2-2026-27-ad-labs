package es.dam.accesodatos.autoestudio.file.inspeccion;

import java.io.File;

/**
 * Ejercicios de consulta de estado, permisos y metadatos de {@link File}.
 *
 * <p>Teoría: {@code teoria/inspeccion-file.md}. Las consultas describen lo que
 * informa el sistema de archivos en el momento de ejecutarlas.
 */
public final class InspeccionFile {

    private InspeccionFile() {
    }

    /**
     * Indica si existe una entrada para la ruta recibida.
     *
     * @param ruta ruta que se desea comprobar
     * @return {@code true} si la entrada existe; {@code false} en caso contrario
     * @throws IllegalArgumentException si la ruta es nula
     */
    public static boolean existe(File ruta) {
        // TODO: Rechaza una referencia nula.
        // TODO: Consulta la existencia de la entrada representada.
        // TODO: No deduzcas la existencia a partir de su nombre o extensión.
        // TODO: Devuelve el resultado booleano de la consulta.
        return false;
    }

    /**
     * Indica si la ruta existente representa un fichero regular.
     *
     * @param ruta ruta que se desea comprobar
     * @return {@code true} si representa un fichero; {@code false} en otro caso
     * @throws IllegalArgumentException si la ruta es nula
     */
    public static boolean esFichero(File ruta) {
        // TODO: Rechaza una referencia nula.
        // TODO: Consulta el tipo de la entrada usando la API de File.
        // TODO: Una ruta inexistente no debe considerarse fichero.
        // TODO: Devuelve si la entrada es un fichero, no un directorio.
        return false;
    }

    /**
     * Indica si la ruta existente representa un directorio.
     *
     * @param ruta ruta que se desea comprobar
     * @return {@code true} si representa un directorio; {@code false} en otro caso
     * @throws IllegalArgumentException si la ruta es nula
     */
    public static boolean esDirectorio(File ruta) {
        // TODO: Rechaza una referencia nula.
        // TODO: Consulta el tipo de la entrada usando la API de File.
        // TODO: Una ruta inexistente no debe considerarse directorio.
        // TODO: Devuelve si la entrada es un directorio, no un fichero.
        return false;
    }

    /**
     * Consulta si el proceso actual puede leer la ruta.
     *
     * @param ruta ruta que se desea consultar
     * @return {@code true} si el sistema informa de permiso de lectura
     * @throws IllegalArgumentException si la ruta es nula
     */
    public static boolean puedeLeer(File ruta) {
        // TODO: Rechaza una referencia nula.
        // TODO: Consulta el permiso efectivo de lectura informado por File.
        // TODO: No intentes abrir ni leer el contenido del fichero.
        // TODO: Devuelve el resultado de la consulta de permisos.
        return false;
    }

    /**
     * Consulta si el proceso actual puede escribir en la ruta.
     *
     * @param ruta ruta que se desea consultar
     * @return {@code true} si el sistema informa de permiso de escritura
     * @throws IllegalArgumentException si la ruta es nula
     */
    public static boolean puedeEscribir(File ruta) {
        // TODO: Rechaza una referencia nula.
        // TODO: Consulta el permiso efectivo de escritura informado por File.
        // TODO: No modifiques el contenido de la entrada.
        // TODO: Devuelve el resultado de la consulta de permisos.
        return false;
    }

    /**
     * Obtiene el tamaño informado para la ruta, expresado en bytes.
     *
     * @param ruta ruta cuyo tamaño se desea consultar
     * @return tamaño informado por File; cero para una ruta inexistente
     * @throws IllegalArgumentException si la ruta es nula
     */
    public static long tamanioEnBytes(File ruta) {
        // TODO: Rechaza una referencia nula.
        // TODO: Consulta el tamaño informado para la ruta.
        // TODO: Conserva el comportamiento documentado para rutas inexistentes.
        // TODO: Devuelve el tamaño como long, sin convertirlo a caracteres.
        return 0L;
    }

    /**
     * Obtiene la fecha de última modificación como milisegundos desde la época Unix.
     *
     * @param ruta ruta cuya fecha se desea consultar
     * @return fecha en milisegundos; cero si no existe o no se pudo determinar
     * @throws IllegalArgumentException si la ruta es nula
     */
    public static long ultimaModificacion(File ruta) {
        // TODO: Rechaza una referencia nula.
        // TODO: Consulta la fecha de última modificación informada por File.
        // TODO: Conserva el valor cero cuando no se puede determinar la fecha.
        // TODO: Devuelve el instante en milisegundos, sin reformatearlo.
        return 0L;
    }

    /**
     * Playground para observar inspecciones sobre la carpeta de ejecución.
     *
     * @param args argumentos de línea de comandos, no utilizados
     */
    public static void main(String[] args) {
        File ruta = new File(".");
        if (!existe(ruta) || !esDirectorio(ruta)) {
            System.out.println("Completa existe() y esDirectorio() para inspeccionar la carpeta actual.");
            return;
        }
        System.out.println("Existe: true");
        System.out.println("Es directorio: true");
        System.out.println("Se puede leer: " + puedeLeer(ruta));
    }
}
