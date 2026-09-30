// OLIVER BITICA
package EjercicioFicheros;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ArchivoApp2 {

    // Ruta del código fuente de este programa.
    private static final String RUTA_FUENTE_POR_DEFECTO = "src/EjercicioFicheros/ArchivoApp2.java";

    public static void main(String[] args) {
        mostrarCodigoFuente();
    }

    private static void mostrarCodigoFuente() {
        try {
            Path ficheroFuente = Paths.get(RUTA_FUENTE_POR_DEFECTO);

            if (!Files.exists(ficheroFuente)) {
                System.out.println("Error: el fichero no existe: " + RUTA_FUENTE_POR_DEFECTO);
                return;
            }

            if (Files.isDirectory(ficheroFuente)) {
                System.out.println("Error: la ruta indicada es un directorio: " + RUTA_FUENTE_POR_DEFECTO);
                return;
            }

            if (!Files.isRegularFile(ficheroFuente) || !Files.isReadable(ficheroFuente)) {
                System.out.println("Error: el fichero no es válido o no se puede leer: " + RUTA_FUENTE_POR_DEFECTO);
                return;
            }

            // Guarda el contenido para mostrarlo al finalizar.
            StringBuffer codigoFuente = new StringBuffer();

            try (BufferedReader lector = Files.newBufferedReader(ficheroFuente, StandardCharsets.UTF_8)) {
                int caracterLeido;
                while ((caracterLeido = lector.read()) != -1) {
                    codigoFuente.append((char) caracterLeido);
                }
            }

            System.out.print(codigoFuente.toString());
        } catch (Exception e) {
            // Si ocurre un error, se muestra un mensaje.
            System.out.println("Error al leer el fichero: " + e.getMessage());
        }
    }
}
