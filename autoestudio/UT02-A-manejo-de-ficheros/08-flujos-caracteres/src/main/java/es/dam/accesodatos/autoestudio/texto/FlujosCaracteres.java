package es.dam.accesodatos.autoestudio.texto;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * UT02-A §2.10. Usa Reader/Writer y sus buffers. Charset no implica estudiar
 * Path/Files.
 */
public final class FlujosCaracteres {
    private FlujosCaracteres() {
    }

    /**
     * Lee UTF-8 con BufferedReader y devuelve las líneas, sin sus separadores.
     * Conserva líneas vacías. Un fichero vacío produce una lista vacía, nunca null.
     * No se exige una lista inmutable.
     * 
     * @throws IOException si falla la lectura
     */
    public static List<String> leerLineas(File archivo) throws IOException {
        // TODO: Abre FileReader(archivo, StandardCharsets.UTF_8) y envuélvelo en
        // BufferedReader.
        // TODO: Acumula readLine() hasta null, no hasta "".
        // TODO: Cierra el reader y devuelve la lista.
        List<String> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(archivo, StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                lista.add(linea);
            }
        }
        return lista;
    }

    /**
     * Escribe las líneas en UTF-8 con BufferedWriter y newLine() tras cada línea.
     * append=false sobrescribe; append=true añade. En modo append, la entrada
     * previa
     * está vacía o termina en salto de línea. Los padres ya existen; las líneas no
     * son null.
     * 
     * @throws IOException si falla la escritura
     */
    public static void escribirLineas(File archivo, List<String> lineas, boolean append) throws IOException {
        // TODO: Abre FileWriter(archivo, StandardCharsets.UTF_8, append) y
        // BufferedWriter.
        // TODO: Recorre las líneas; escribe cada String y un separador con newLine().
        // TODO: Cierra el writer; no confundas flush con close.
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo, StandardCharsets.UTF_8, append))) {
            for (String lineaString : lineas) {
                bw.write(lineaString);
                bw.newLine();
            }
        }
    }

    /** Lee un archivo UTF-8 sin modificarlo. */
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Uso: FlujosCaracteres archivo.txt");
            return;
        }
        System.out.println(leerLineas(new File(args[0])));
    }
}
