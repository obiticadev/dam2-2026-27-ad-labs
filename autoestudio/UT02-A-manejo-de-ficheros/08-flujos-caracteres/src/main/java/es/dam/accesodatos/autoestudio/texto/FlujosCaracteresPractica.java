package es.dam.accesodatos.autoestudio.texto;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

/**
 * Práctica de composición. Consulta los enunciados del README.
 * Para los ejercicios 2-5: archivos no null, entradas existentes, padres del
 * destino existentes y destino distinto de las entradas. Propaga IOException.
 * No se pide validar estas precondiciones ni crear carpetas.
 */
public final class FlujosCaracteresPractica {
    private FlujosCaracteresPractica() {
    }

    /**
     * Ejercicio 1. Copia caracteres por bloques de tamaño tamBuffer.
     * Devuelve el número de unidades char copiadas (no bytes ni puntos Unicode).
     * Conserva exactamente el texto, incluidos sus saltos de línea.
     * No cierra ni hace flush de los recursos: pertenecen a quien llama.
     * tamBuffer <= 0 debe provocar IllegalArgumentException antes de leer o
     * escribir.
     * Los recursos no son null y son distintos; propaga IOException.
     */
    public static long copiar(Reader origen, Writer destino, int tamBuffer) throws IOException {
        if (tamBuffer <= 0) {
            throw new IllegalArgumentException("Parámetro erróneo");
        }
        char[] cache = new char[tamBuffer];
        long total = 0;
        int leido;
        while ((leido = origen.read(cache)) != -1) {
            destino.write(cache, 0, leido);
            total += leido;
        }

        return total;
    }

    /**
     * Ejercicio 2. Lee UTF-8 y escribe en UTF-8 cada línea con el prefijo
     * "1: ", "2: ", etc. También numera las líneas vacías.
     * Sobrescribe el destino y escribe newLine() tras cada línea.
     * Devuelve el número de líneas escritas. Un origen vacío deja el destino vacío.
     * Abre y cierra sus propios recursos; no carga todo el archivo en una lista.
     */
    public static int numerarLineas(File origen, File destino) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(origen, StandardCharsets.UTF_8));
                BufferedWriter bw = new BufferedWriter(new FileWriter(destino, StandardCharsets.UTF_8))) {
            String linea;
            int contador = 0;
            while ((linea = br.readLine()) != null) {
                bw.write(String.format("%d: %s", ++contador, linea));
                bw.newLine();
            }
            return contador;
        }
    }

    /**
     * Ejercicio 3. Copia solo líneas de configuración útiles en UTF-8.
     * Descarta líneas cuyo strip() sea vacío o empiece por #.
     * Las líneas conservadas se escriben EXACTAMENTE como estaban, sin strip().
     * Un # dentro de una línea útil NO inicia un comentario.
     * Sobrescribe, termina cada línea con newLine() y devuelve cuántas conserva.
     * Abre y cierra sus recursos; procesa línea a línea.
     */
    public static int filtrarConfiguracion(File origen, File destino) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(origen, StandardCharsets.UTF_8));
                BufferedWriter bw = new BufferedWriter(new FileWriter(destino, StandardCharsets.UTF_8))) {
            String linea;
            int contador = 0;
            while ((linea = br.readLine()) != null) {
                if (!linea.strip().equals("") && !linea.strip().startsWith("#")) {
                    bw.write(linea);
                    contador++;
                    bw.newLine();
                }
            }
            return contador;
        }

    }

    /**
     * Ejercicio 4. Reduce cada grupo de líneas vacías consecutivas a una sola.
     * Solo "" es una línea vacía: una línea de espacios se conserva y separa
     * grupos.
     * También conserva una vacía al principio o al final si la había.
     * Trabaja en UTF-8, sobrescribe y usa newLine() tras cada línea conservada.
     * Devuelve cuántas líneas ha DESCARTADO, no cuántas ha escrito.
     * Abre y cierra sus recursos; usa estado, no una lista con todo el archivo.
     */
    public static int compactarVacias(File origen, File destino) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(origen, StandardCharsets.UTF_8));
                BufferedWriter bw = new BufferedWriter(new FileWriter(destino, StandardCharsets.UTF_8))) {
            String linea;
            boolean anteriorVacia = false;
            int contadorLineasDescartadas = 0;
            while ((linea = br.readLine()) != null) {
                if (linea.equals("") && !anteriorVacia) {
                    anteriorVacia = true;
                    bw.write(linea);
                    bw.newLine();
                } else if (linea.equals("") && anteriorVacia) {
                    contadorLineasDescartadas++;
                } else {
                    anteriorVacia = false;
                    bw.write(linea);
                    bw.newLine();
                }
            }
            return contadorLineasDescartadas;
        }
    }

    /**
     * Ejercicio 5. Escribe una línea de a, otra de b y repite.
     * Si uno termina antes, escribe todas las líneas restantes del otro.
     * Las líneas vacías también consumen turno. Ambos vacíos: destino vacío.
     * Trabaja en UTF-8, sobrescribe y usa newLine() tras cada línea.
     * Devuelve el total de líneas escritas; no carga los archivos completos.
     * Abre y cierra los tres recursos.
     */
    public static int intercalar(File a, File b, File destino) throws IOException {
        try (BufferedReader br1 = new BufferedReader(new FileReader(a, StandardCharsets.UTF_8));
                BufferedReader br2 = new BufferedReader(new FileReader(b, StandardCharsets.UTF_8));
                BufferedWriter bw = new BufferedWriter(new FileWriter(destino, StandardCharsets.UTF_8))) {
            String lineaA = br1.readLine();
            String lineaB = br2.readLine();
            int contador = 0;

            while (lineaA != null || lineaB != null) {
                if (lineaA != null) {
                    bw.write(lineaA);
                    bw.newLine();
                    contador++;
                    lineaA = br1.readLine();
                }
                if (lineaB != null) {
                    bw.write(lineaB);
                    bw.newLine();
                    contador++;
                    lineaB = br2.readLine();
                }
            }
            return contador;
        }
    }
}
