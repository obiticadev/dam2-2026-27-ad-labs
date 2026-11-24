package es.dam.accesodatos.nio;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileEjemplo4 {
    public static void main(String[] args) {
        Path path = Path.of("target/demo-data/newdir");
        try {
            Files.createDirectories(path.getParent());
            Path newDir = Files.createDirectory(path);
            System.out.println("Directorio creado: " + newDir.toAbsolutePath());
        } catch (FileAlreadyExistsException e) {
            System.out.println("El directorio ya existe: " + path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
