package es.dam.accesodatos.autoestudio.flujos.bytes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Ejercicios de copia y anexado con {@link FileInputStream} y
 * {@link FileOutputStream}.
 *
 * <p>
 * Teoría: {@code teoria/flujos-bytes.md}. Corresponde al apartado 2.7 de
 * UT02-A. No se permite {@code Files.copy} ni cargar el archivo completo en
 * memoria: se trabaja por bloques con un búfer de tamaño fijo.
 */
public final class FlujosBytes {

    private FlujosBytes() {
    }

    /**
     * Copia un fichero por bloques y sobrescribe el destino.
     *
     * @param origen    fichero regular existente que se va a leer
     * @param destino   fichero que se crea o se sobrescribe; distinto del origen
     * @param capacidad tamaño del búfer en bytes; mayor que cero
     * @return número total de bytes escritos en el destino
     * @throws IllegalArgumentException si algún argumento no es válido o si
     *                                  origen y destino son el mismo fichero
     * @throws IOException              si falla la lectura o la escritura
     */
    public static long copiar(File origen, File destino, int capacidad) throws IOException {
        // TODO: Rechaza un origen nulo o que no sea un fichero regular existente.
        // TODO: Rechaza un destino nulo, una capacidad <= 0 y un destino que sea el
        // mismo
        // fichero que el origen (compara sus rutas canónicas).
        // TODO: Abre ambos flujos en un try-with-resources; FileOutputStream sin append
        // sobrescribe.
        // TODO: Reserva un búfer de 'capacidad' bytes y lee mientras read(...) no
        // devuelva -1.
        // TODO: Escribe solo los bytes válidos de cada lectura: write(buffer, 0,
        // leidos).
        // TODO: Acumula y devuelve el total escrito.
        if (origen == null || !origen.isFile() || destino == null || capacidad <= 0
                || origen.getCanonicalFile() == destino.getCanonicalFile()) {
            throw new IllegalArgumentException("Error de parámetros, introduce un parámetro correcto");
        }
        try (FileInputStream fis = new FileInputStream(origen);
                FileOutputStream fos = new FileOutputStream(destino)) {
            byte[]  
        } 
        return -1L;
    }

    /**
     * Añade al final de un fichero una porción de un array de bytes.
     *
     * @param destino fichero al que se anexan los bytes; se crea si no existe
     * @param datos   array que contiene los bytes
     * @param off     primer índice del array que se escribe (no es una posición del
     *                fichero)
     * @param len     cantidad de bytes que se escriben desde {@code off}
     * @throws IllegalArgumentException  si {@code destino} o {@code datos} son
     *                                   nulos
     * @throws IndexOutOfBoundsException si {@code off} y {@code len} no caben en
     *                                   {@code datos}; en ese caso el destino no se
     *                                   crea ni se modifica
     * @throws IOException               si falla la escritura
     */
    public static void anexar(File destino, byte[] datos, int off, int len) throws IOException {
        // TODO: Rechaza destino o datos nulos con IllegalArgumentException.
        // TODO: Comprueba off >= 0, len >= 0 y off + len <= datos.length ANTES de abrir
        // el flujo:
        // abrir un FileOutputStream ya crea el fichero aunque luego no se escriba nada.
        // TODO: Abre el flujo en modo append (segundo argumento true) con
        // try-with-resources.
        // TODO: Escribe exactamente len bytes empezando en el índice off del array.
    }

    /**
     * Playground con datos regenerables bajo {@code target/demo-data}.
     *
     * @param args argumentos de línea de comandos, no utilizados
     * @throws IOException si falla la preparación o la copia del ejemplo
     */
    public static void main(String[] args) throws IOException {
        File base = new File("target/demo-data/07-flujos-bytes");
        base.mkdirs();
        File origen = new File(base, "origen.bin");
        try (FileOutputStream salida = new FileOutputStream(origen)) {
            salida.write(new byte[] { 1, 2, 3, 4, 5, 6 });
        }

        File copia = new File(base, "copia.bin");
        long total = copiar(origen, copia, 4);
        System.out.println("copiar() devolvió " + total + " (esperado 6)");
        System.out.println("copia.bin = " + Arrays.toString(leer(copia)));

        File anexo = new File(base, "anexo.bin");
        try (FileOutputStream salida = new FileOutputStream(anexo)) {
            salida.write(9);
        }
        anexar(anexo, new byte[] { 10, 20, 30, 40 }, 1, 2);
        System.out.println("anexo.bin = " + Arrays.toString(leer(anexo)) + " (esperado [9, 20, 30])");
    }

    private static byte[] leer(File fichero) throws IOException {
        if (!fichero.isFile()) {
            return new byte[0];
        }
        try (FileInputStream entrada = new FileInputStream(fichero)) {
            return entrada.readAllBytes();
        }
    }
}
