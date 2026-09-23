package es.dam.accesodatos.aleatorio;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class RandomLogDemo {
    public static void main(String[] args) throws IOException {
        Path archivo = Path.of("target/demo-data/java.log");
        Files.createDirectories(archivo.getParent());
        String linea = "linea que se anadira al final del fichero" + System.lineSeparator();

        try (RandomAccessFile fichero = new RandomAccessFile(archivo.toFile(), "rw")) {
            fichero.seek(fichero.length());
            fichero.write(linea.getBytes(StandardCharsets.ISO_8859_1));
        }
        System.out.println("Línea añadida a " + archivo.toAbsolutePath());
    }
}
