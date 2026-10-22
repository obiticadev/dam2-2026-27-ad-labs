package es.dam.accesodatos.autoestudio.bytes;

import java.io.File;
import java.io.IOException;

/** UT02-A §2.7. Completa los métodos con FileInputStream y FileOutputStream, no Files.copy. */
public final class FlujosBytes {
    private FlujosBytes() {}

    /**
     * Copia todos los bytes y sobrescribe el destino. Devuelve el número de bytes copiados.
     * Origen y destino son archivos distintos; sus padres ya existen.
     * @param capacidad tamaño positivo del buffer; rechaza cero o negativos con IllegalArgumentException
     * @throws IOException si falla la apertura, lectura o escritura (no ocultar el error)
     */
    public static long copiar(File origen, File destino, int capacidad) throws IOException {
        // TODO: Valida capacidad y abre ambos flujos con try-with-resources.
        // TODO: Crea byte[] y lee hasta -1; el resultado de read es una cantidad.
        // TODO: Escribe solo buffer[0..leidos), no todo el array.
        // TODO: Acumula los bytes escritos y devuelve el total.
        return 0L;
    }

    /**
     * Añade al final exactamente len bytes del array, empezando en off.
     * El rango es válido: 0 <= off, 0 <= len y off + len <= datos.length.
     * @throws IOException si falla la escritura
     */
    public static void anexar(File destino, byte[] datos, int off, int len) throws IOException {
        // TODO: Abre FileOutputStream en modo append=true.
        // TODO: Usa write(datos, off, len); off es índice del array, no posición del archivo.
        // TODO: Cierra el flujo automáticamente.
    }

    /** Ejecuta desde el IDE con argumentos: origen destino (archivos distintos). */
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Uso: FlujosBytes origen destino; el destino se sobrescribe.");
            return;
        }
        System.out.println("Bytes copiados: " + copiar(new File(args[0]), new File(args[1]), 4));
    }
}
