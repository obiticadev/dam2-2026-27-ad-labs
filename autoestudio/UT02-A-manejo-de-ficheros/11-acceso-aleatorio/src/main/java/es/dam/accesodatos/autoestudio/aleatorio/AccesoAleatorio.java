package es.dam.accesodatos.autoestudio.aleatorio;

import java.io.File;
import java.io.IOException;

/**
 * UT02-A §2.12 y práctica añadida de registros fijos del repaso.
 * Cada registro contiene int id (4 bytes) + double nota (8 bytes): 12 bytes.
 * Los índices empiezan en cero, no son ids. No mezclar este formato con ObjectOutputStream.
 */
public final class AccesoAleatorio {
    private AccesoAleatorio() {}

    /**
     * Lee la nota del registro de índice indicado con RandomAccessFile en modo r.
     * @throws IndexOutOfBoundsException si indice < 0 o no hay un registro completo en ese índice
     * @throws IOException si falla el acceso
     */
    public static double leerNota(File archivo, int indice) throws IOException {
        // TODO: Abre en modo r y comprueba el índice frente a length()/12.
        // TODO: Calcula el desplazamiento de la nota: indice * 12L + 4.
        // TODO: Usa seek, readDouble y cierra el archivo.
        return 0.0;
    }

    /**
     * Actualiza solo el double del registro en modo rw, sin alterar id ni longitud ni vecinos.
     * El archivo ya existe y contiene registros completos.
     * @throws IndexOutOfBoundsException si indice < 0 o no hay un registro completo en ese índice
     * @throws IOException si falla el acceso
     */
    public static void actualizarNota(File archivo, int indice, double nota) throws IOException {
        // TODO: Abre en modo rw y valida el índice ANTES de escribir.
        // TODO: Posiciónate en el campo nota usando seek.
        // TODO: Escribe solo writeDouble(nota), no reescribas todos los registros.
    }

    /** Consulta sin escribir. */
    public static void main(String[] args) throws IOException {
        if (args.length != 2) { System.out.println("Uso: AccesoAleatorio registros.dat indice"); return; }
        System.out.println(leerNota(new File(args[0]), Integer.parseInt(args[1])));
    }
}
