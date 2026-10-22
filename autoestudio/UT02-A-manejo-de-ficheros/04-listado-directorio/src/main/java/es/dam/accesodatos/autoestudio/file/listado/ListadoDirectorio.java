package es.dam.accesodatos.autoestudio.file.listado;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Ejercicios de listado inmediato con {@link File#list()} y
 * {@link File#listFiles()}.
 *
 * <p>Teoría: {@code teoria/listado-directorio.md}. El orden del sistema no está
 * garantizado; ordenar no es requisito del ejercicio.
 */
public final class ListadoDirectorio {

    private ListadoDirectorio() {
    }

    /**
     * Obtiene los nombres de las entradas inmediatas de un directorio.
     *
     * @param directorio directorio existente que se desea listar
     * @return nombres sin orden exigido; array vacío si no contiene entradas
     * @throws IllegalArgumentException si la ruta no representa un directorio existente
     * @throws IOException si el sistema no puede obtener el listado
     */
    public static String[] nombres(File directorio) throws IOException {
        // TODO: Comprueba que la referencia representa un directorio existente.
        // TODO: Obtén el listado de nombres mediante la operación correspondiente de File.
        // TODO: Distingue un array nulo de un array vacío y comunica el fallo como IOException.
        // TODO: Devuelve todos los nombres inmediatos, sin recorrer subdirectorios.
        return null;
    }

    /**
     * Obtiene las entradas inmediatas como objetos {@code File}.
     *
     * @param directorio directorio existente que se desea listar
     * @return objetos {@code File} sin orden exigido; array vacío si no hay entradas
     * @throws IllegalArgumentException si la ruta no representa un directorio existente
     * @throws IOException si el sistema no puede obtener el listado
     */
    public static File[] entradas(File directorio) throws IOException {
        // TODO: Comprueba que la referencia representa un directorio existente.
        // TODO: Obtén los objetos File de las entradas inmediatas.
        // TODO: Distingue un array nulo de un array vacío y comunica el fallo como IOException.
        // TODO: Devuelve las rutas sin recorrer sus posibles directorios hijos.
        return null;
    }

    /**
     * Indica si el directorio existente no contiene entradas inmediatas.
     *
     * @param directorio directorio que se desea comprobar
     * @return {@code true} si está vacío; {@code false} si contiene al menos una entrada
     * @throws IllegalArgumentException si la ruta no representa un directorio existente
     * @throws IOException si el sistema no puede obtener el listado
     */
    public static boolean estaVacio(File directorio) throws IOException {
        // TODO: Valida que la ruta represente un directorio existente.
        // TODO: Consulta sus entradas inmediatas, sin recorrerlas recursivamente.
        // TODO: Trata un error de listado como IOException, no como directorio vacío.
        // TODO: Devuelve si el listado válido contiene cero entradas.
        return false;
    }

    /**
     * Playground para mostrar un listado sencillo de la carpeta de ejecución.
     *
     * @param args argumentos de línea de comandos, no utilizados
     * @throws IOException si no se puede consultar el directorio actual
     */
    public static void main(String[] args) throws IOException {
        File directorioActual = new File(".");
        String[] nombres = nombres(directorioActual);
        if (nombres == null) {
            System.out.println("Implementa nombres() para obtener la salida del listado.");
            return;
        }
        System.out.println("Nombres: " + Arrays.toString(nombres));
        System.out.println("Entradas vacías: " + estaVacio(directorioActual));
    }
}
