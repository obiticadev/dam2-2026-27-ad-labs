// OLIVER BITICA
package EjercicioFicheros;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;

public class ArchivoApp2 {

    // Ruta del código fuente de este programa.
    private static final String RUTA_FUENTE_POR_DEFECTO = "src/EjercicioFicheros/ArchivoApp2.java";

    public static void main(String[] args) {
        mostrarCodigoFuente();
    }

    private static void mostrarCodigoFuente() {
        try {
            File ficheroFuente = new File(RUTA_FUENTE_POR_DEFECTO);

            if (!ficheroFuente.exists()) {
                System.out.println("Error: el fichero no existe: " + RUTA_FUENTE_POR_DEFECTO);
                return;
            }

            if (ficheroFuente.isDirectory()) {
                System.out.println("Error: la ruta indicada es un directorio: " + RUTA_FUENTE_POR_DEFECTO);
                return;
            }

            if (!ficheroFuente.isFile() || !ficheroFuente.canRead()) {
                System.out.println("Error: el fichero no es válido o no se puede leer: " + RUTA_FUENTE_POR_DEFECTO);
                return;
            }

            // Guarda el contenido para mostrarlo al finalizar.
            StringBuffer codigoFuente = new StringBuffer();

            try (BufferedReader lector = new BufferedReader(new FileReader(ficheroFuente, StandardCharsets.UTF_8))) {
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
