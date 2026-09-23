package es.dam.accesodatos.nio;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileEjemplo5 {
    public static void main(String[] args) {
        Path origen = Path.of("src/main/resources/origen");
        Path destino = Path.of("target/demo-data/copia-directorio");
        try {
            Files.createDirectories(destino.getParent());
            // Files.copy copia el directorio, pero no copia recursivamente su contenido.
            Files.deleteIfExists(destino);
            Files.copy(origen, destino);
            System.out.println("Directorio vacío creado en " + destino.toAbsolutePath());
        } catch (FileAlreadyExistsException e) {
            System.out.println("El destino ya existe.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
