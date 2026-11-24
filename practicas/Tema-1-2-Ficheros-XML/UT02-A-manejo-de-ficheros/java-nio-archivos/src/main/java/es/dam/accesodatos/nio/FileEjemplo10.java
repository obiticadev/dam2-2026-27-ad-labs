package es.dam.accesodatos.nio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileEjemplo10 {
    public static void main(String[] args) {
        Path origen = Path.of("src/main/resources/hola.txt");
        Path destino = Path.of("target/demo-data/array/hola.txt");
        try {
            Files.createDirectories(destino.getParent());
            byte[] contenido = Files.readAllBytes(origen);
            Files.write(destino, contenido, StandardOpenOption.WRITE,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("Copia por array guardada en " + destino.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("ERROR: " + e);
        }
    }
}
