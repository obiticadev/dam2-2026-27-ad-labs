// OLIVER BITICA
package EjercicioFicheros;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class CursosAleatorioApp {

    private static final String NOMBRE_FICHERO = "CursosAleatorio.dat";
    private static final int TOTAL_SLOTS = 15;
    private static final int LONGITUD_NOMBRE = 15;
    private static final int TAMANIO_REGISTRO = 4 + (LONGITUD_NOMBRE * 2) + 8; // 42 bytes
    private static final int CURSO_VACIO = -1;

    private static final int[] NUMEROS = { 10, 2, 1, 5 };
    private static final String[] NOMBRES = { "Curso10", "Curso2", "Curso1", "Curso5" };
    private static final double[] COSTES = { 4000.0, 5000.0, 6000.0, 3000.0 };

    public static void main(String[] args) {
        File archivo = new File(NOMBRE_FICHERO);
        escribirCursos(archivo);
        leerCursos(archivo);
    }

    private static void escribirCursos(File archivo) {
        try (RandomAccessFile raf = new RandomAccessFile(archivo, "rw")) {
            // Inicializar los 15 slots con -1
            for (int i = 0; i < TOTAL_SLOTS; i++) {
                raf.writeInt(CURSO_VACIO);
                for (int j = 0; j < LONGITUD_NOMBRE; j++) {
                    raf.writeChar('\0');
                }
                raf.writeDouble(0.0);
            }

            // Escribir cada curso en su posición lógica
            for (int i = 0; i < NUMEROS.length; i++) {
                long pos = (long) (NUMEROS[i] - 1) * TAMANIO_REGISTRO;
                raf.seek(pos);

                raf.writeInt(NUMEROS[i]);

                // Rellenar hasta 15 caracteres con StringBuilder
                StringBuilder sb = new StringBuilder(NOMBRES[i]);
                sb.setLength(LONGITUD_NOMBRE);
                raf.writeChars(sb.toString());

                raf.writeDouble(COSTES[i]);
                System.out.println("Insertado curso " + NUMEROS[i] + " en byte " + pos);
            }
        } catch (IOException e) {
            System.err.println("Error al escribir: " + e.getMessage());
        }
    }

    private static void leerCursos(File archivo) {
        System.out.printf("%n%-18s %-8s %-15s %-10s%n", "Posición (bytes)", "Número", "Nombre", "Coste");

        try (RandomAccessFile raf = new RandomAccessFile(archivo, "r")) {
            for (int i = 1; i <= TOTAL_SLOTS; i++) {
                long pos = raf.getFilePointer();
                int num = raf.readInt();

                char[] chars = new char[LONGITUD_NOMBRE];
                for (int j = 0; j < LONGITUD_NOMBRE; j++) {
                    chars[j] = raf.readChar();
                }
                String nombre = new String(chars).replace("\0", "").trim();
                double coste = raf.readDouble();

                if (num == CURSO_VACIO) {
                    System.out.printf("%-18d %-8d %-15s %-10s%n", pos, num, "(vacío)", "—");
                } else {
                    System.out.printf("%-18d %-8d %-15s %-10.2f%n", pos, num, nombre, coste);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer: " + e.getMessage());
        }
    }
}
