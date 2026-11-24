package es.dam.accesodatos.aleatorio;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class RandomEjemplo {
    public static void main(String[] args) throws IOException {
        Path archivo = Path.of("target/demo-data/HolaMundo.txt");
        Files.createDirectories(archivo.getParent());
        if (Files.notExists(archivo)) {
            Files.writeString(archivo, "Contenido inicial del fichero." + System.lineSeparator(),
                    StandardCharsets.ISO_8859_1);
        }

        String texto = "linea que se anadira al final del fichero" + System.lineSeparator();
        try (RandomAccessFile fichero = new RandomAccessFile(archivo.toFile(), "rw")) {
            System.out.println("Posición del puntero en la apertura: " + fichero.getFilePointer());
            fichero.seek(fichero.length());
            fichero.writeBytes(texto);

            fichero.seek(0);
            String linea;
            while ((linea = fichero.readLine()) != null) {
                System.out.println(linea);
            }

            System.out.println("Bytes leídos: " + fichero.length());
            fichero.seek(0);
            byte[] contenido = new byte[(int) fichero.length()];
            fichero.readFully(contenido);
            System.out.println("Contenido completo: " + new String(contenido, StandardCharsets.ISO_8859_1));
        }
    }
}
