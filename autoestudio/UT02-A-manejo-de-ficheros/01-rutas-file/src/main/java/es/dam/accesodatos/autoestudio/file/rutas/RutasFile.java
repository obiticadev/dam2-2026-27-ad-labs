package es.dam.accesodatos.autoestudio.file.rutas;

import java.io.File;

/**
 * Ejercicios de construcción y consulta de rutas mediante {@link File}.
 *
 * <p>Teoría: {@code teoria/rutas-file.md}. Crear un {@code File} representa una
 * ruta; no crea por sí mismo el fichero o directorio en el sistema.
 */
public final class RutasFile {

    private RutasFile() {
    }

    /**
     * Crea una representación {@code File} desde una sola cadena de ruta.
     *
     * @param ruta ruta relativa o absoluta que se desea representar
     * @return objeto {@code File} asociado a la ruta indicada
     * @throws IllegalArgumentException si la ruta es nula o está en blanco
     */
    public static File desdeRuta(String ruta) {
        // TODO: Rechaza la ruta nula o en blanco con IllegalArgumentException.
        // TODO: Representa la cadena recibida como una ruta File.
        // TODO: No crees ningún fichero o directorio en el sistema.
        // TODO: Devuelve el objeto que representa exactamente la ruta solicitada.
        return null;
    }

    /**
     * Construye una ruta combinando el nombre de un directorio y el de su hijo.
     *
     * @param directorio ruta del directorio padre
     * @param nombre nombre del hijo dentro del directorio
     * @return ruta combinada como objeto {@code File}
     * @throws IllegalArgumentException si algún argumento es nulo o está en blanco
     */
    public static File desdeDirectorioYNombre(String directorio, String nombre) {
        // TODO: Valida que ambos argumentos tengan contenido.
        // TODO: Combina directorio y nombre mediante la API de File.
        // TODO: Conserva la semántica de ruta del sistema operativo.
        // TODO: Devuelve la representación sin crear la entrada física.
        return null;
    }

    /**
     * Construye una ruta hija a partir de un objeto {@code File} padre.
     *
     * @param directorio padre de la ruta que se quiere representar
     * @param nombre nombre del fichero o directorio hijo
     * @return ruta hija como objeto {@code File}
     * @throws IllegalArgumentException si el padre es nulo o el nombre es nulo o está en blanco
     */
    public static File desdeDirectorioPadre(File directorio, String nombre) {
        // TODO: Valida el objeto padre y el nombre del hijo.
        // TODO: Combina el padre File con el nombre recibido.
        // TODO: No presupongas que el padre existe en el sistema.
        // TODO: Devuelve la ruta representada sin crearla físicamente.
        return null;
    }

    /**
     * Obtiene el último componente de una ruta.
     *
     * @param ruta ruta cuyo nombre se desea consultar
     * @return nombre del fichero o directorio representado
     * @throws IllegalArgumentException si la ruta es nula
     */
    public static String nombre(File ruta) {
        // TODO: Rechaza una referencia nula.
        // TODO: Consulta el último componente de la ruta representada.
        // TODO: No compruebes la existencia física para obtener el nombre.
        // TODO: Devuelve el componente conservando mayúsculas y minúsculas.
        return "";
    }

    /**
     * Obtiene la ruta con la que se construyó el objeto, sin normalizarla.
     *
     * @param ruta ruta cuyo valor representado se desea consultar
     * @return cadena de ruta conservada por el objeto
     * @throws IllegalArgumentException si la ruta es nula
     */
    public static String rutaRepresentada(File ruta) {
        // TODO: Rechaza una referencia nula.
        // TODO: Recupera la ruta conservada por el objeto File.
        // TODO: No la conviertas a ruta absoluta ni canónica.
        // TODO: Devuelve el valor representado, aunque la entrada no exista.
        return "";
    }

    /**
     * Obtiene el padre inmediato de una ruta representada.
     *
     * @param ruta ruta cuyo padre se desea consultar
     * @return objeto {@code File} del padre, o {@code null} si la ruta no tiene padre
     * @throws IllegalArgumentException si la ruta es nula
     */
    public static File directorioPadre(File ruta) {
        // TODO: Rechaza una referencia nula.
        // TODO: Consulta el padre inmediato de la ruta.
        // TODO: Conserva el resultado nulo cuando la ruta no tenga padre.
        // TODO: Devuelve solo la representación del padre, sin inspeccionar el disco.
        return null;
    }

    /**
     * Playground para observar rutas relativas y combinadas desde el IDE.
     *
     * @param args argumentos de línea de comandos, no utilizados
     */
    public static void main(String[] args) {
        File ruta = desdeRuta("datos/informe.txt");
        if (ruta == null) {
            System.out.println("Implementa desdeRuta() para ver la ruta de ejemplo.");
            return;
        }
        System.out.println("Ruta: " + rutaRepresentada(ruta));
        System.out.println("Nombre: " + nombre(ruta));
        System.out.println("Padre: " + directorioPadre(ruta));
    }
}
