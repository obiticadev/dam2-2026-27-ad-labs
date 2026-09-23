package es.dam.accesodatos.nio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class CopiaFicheroDemo {
    public static void main(String[] args) {
        Path origen = Path.of("src/main/resources/hola.txt");
        Path destino = Path.of("target/demo-data/destino/hola.txt");
        try {
            Files.createDirectories(destino.getParent());
            Files.copy(origen, destino, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Copia guardada en " + destino.toAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
