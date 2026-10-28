package es.dam.accesodatos.autoestudio.aleatorio;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * UT02-A §2.12 y práctica añadida de registros fijos del repaso.
 * Cada registro contiene int id (4 bytes) + double nota (8 bytes): 12 bytes.
 * Los índices empiezan en cero, no son ids. No mezclar este formato con
 * ObjectOutputStream.
 */
public final class AccesoAleatorio {
    private AccesoAleatorio() {
    }

    /**
     * Lee la nota del registro de índice indicado con RandomAccessFile en modo r.
     * 
     * @throws IndexOutOfBoundsException si indice < 0 o no hay un registro completo
     *                                   en ese índice
     * @throws IOException               si falla el acceso
     */
    public static double leerNota(File archivo, int indice) throws IOException {
        if (indice < 0) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + indice);
        }
        final int longitudRegistro = Integer.BYTES + Double.BYTES;
        try (RandomAccessFile raf = new RandomAccessFile(archivo, "r")) {
            if (indice >= raf.length() / longitudRegistro) {
                throw new IndexOutOfBoundsException("Índice fuera de rango: " + indice);
            }
            raf.seek(indice * 12L + 4);
            return raf.readDouble();
        }
    }

    /**
     * Actualiza solo el double del registro en modo rw, sin alterar id ni longitud
     * ni vecinos.
     * El archivo ya existe y contiene registros completos.
     * 
     * @throws IndexOutOfBoundsException si indice < 0 o no hay un registro completo
     *                                   en ese índice
     * @throws IOException               si falla el acceso
     */
    public static void actualizarNota(File archivo, int indice, double nota) throws IOException {
        if (indice < 0) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + indice);
        }

        final int longitudRegistro = Integer.BYTES + Double.BYTES;

        try (RandomAccessFile raf = new RandomAccessFile(archivo, "rw")) {
            if (indice >= raf.length() / longitudRegistro) {
                throw new IndexOutOfBoundsException("Índice fuera de rango: " + indice);
            }
            raf.seek(indice * 12L + 4);
            raf.writeDouble(nota);
        }
    }

    /** Consulta sin escribir. */
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Uso: AccesoAleatorio registros.dat indice");
            return;
        }
        System.out.println(leerNota(new File(args[0]), Integer.parseInt(args[1])));
    }
}
