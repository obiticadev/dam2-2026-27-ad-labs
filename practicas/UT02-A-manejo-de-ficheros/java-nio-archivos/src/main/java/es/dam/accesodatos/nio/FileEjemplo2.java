package es.dam.accesodatos.nio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileEjemplo2 {
    public static void main(String[] args) {
        Path path = Path.of("target/demo-data/prueba.txt");
        try {
            Files.createDirectories(path.getParent());
            if (Files.exists(path)) {
                Files.delete(path);
                System.out.println("Fichero borrado: " + path);
            } else {
                Files.createFile(path);
                System.out.println("Fichero creado: " + path);
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
