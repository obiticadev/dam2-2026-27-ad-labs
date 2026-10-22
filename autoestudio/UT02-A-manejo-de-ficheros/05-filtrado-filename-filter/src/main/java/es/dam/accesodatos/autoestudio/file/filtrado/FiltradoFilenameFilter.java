package es.dam.accesodatos.autoestudio.file.filtrado;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

/**
 * Filtro reutilizable de ficheros por extensión.
 *
 * <p>Teoría: {@code teoria/filename-filter.md}. Igual que Filtrar en el tema,
 * el criterio básico es name.endsWith(extension), sensible a mayúsculas.
 */
public final class FiltradoFilenameFilter implements FilenameFilter {

    private final String extension;

    /**
     * Prepara un filtro para un sufijo, por ejemplo .txt.
     *
     * @param extension extensión que se desea aceptar, por ejemplo {@code .txt}
     * @throws IllegalArgumentException si la extensión es nula o está en blanco
     */
    public FiltradoFilenameFilter(String extension) {
        // TODO: Rechaza extensiones nulas o en blanco.
        // TODO: Guarda el sufijo tal como se recibe; no lo normalices.
        this.extension = "";
    }

    /**
     * Decide si una entrada del directorio satisface el filtro.
     *
     * @param directorio directorio padre de la entrada candidata
     * @param nombre nombre de la entrada candidata
     * @return {@code true} si el nombre termina con el sufijo (también para directorios)
     */
    @Override
    public boolean accept(File directorio, String nombre) {
        // TODO: Devuelve false si nombre es null.
        // TODO: Comprueba el sufijo del nombre, como en el ejemplo Filtrar del tema.
        // TODO: No inspecciones el disco: accept decide sobre el nombre recibido.
        return false;
    }

    /**
     * Lista las entradas coincidentes del directorio usando FilenameFilter.
     *
     * @param directorio directorio existente que se desea consultar
     * @param extension extensión que se desea aceptar
     * @return entradas sin orden exigido; array vacío si no hay coincidencias
     * @throws IllegalArgumentException si el directorio no es válido
     * @throws IOException si no se pudo obtener el listado
     */
    public static File[] listarPorExtension(File directorio, String extension) throws IOException {
        // TODO: Valida que la ruta recibida sea un directorio existente.
        // TODO: Construye una instancia de este filtro para la extensión solicitada.
        // TODO: Entrega el filtro al listado de objetos File del directorio.
        // TODO: Convierte un resultado nulo del listado en IOException.
        // TODO: Devuelve el array; no se exige ordenar ni excluir directorios.
        return null;
    }

    /**
     * Playground para observar el filtrado sobre la carpeta de ejecución.
     *
     * @param args argumentos de línea de comandos, no utilizados
     * @throws IOException si no se puede consultar el directorio actual
     */
    public static void main(String[] args) throws IOException {
        File[] coincidencias = listarPorExtension(new File("."), ".txt");
        if (coincidencias == null) {
            System.out.println("Implementa listarPorExtension() para mostrar las coincidencias.");
            return;
        }
        for (File coincidencia : coincidencias) {
            System.out.println(coincidencia.getName());
        }
    }
}
