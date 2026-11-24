package es.dam.accesodatos.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ByteCopyDemo {
    public static void main(String[] args) throws IOException {
        Path origen = Path.of("src/main/resources/origen.txt");
        Path destino = Path.of("target/demo-data/destino.txt");
        Files.createDirectories(destino.getParent());

        try (InputStream in = new FileInputStream(origen.toFile());
             OutputStream out = new FileOutputStream(destino.toFile())) {
            byte[] buffer = new byte[1024];
            int leidos;
            while ((leidos = in.read(buffer)) != -1) {
                out.write(buffer, 0, leidos);
            }
        }
        System.out.println("Fichero copiado a " + destino.toAbsolutePath());
    }
}
