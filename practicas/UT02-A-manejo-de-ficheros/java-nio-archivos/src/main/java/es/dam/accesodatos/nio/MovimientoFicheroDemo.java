package es.dam.accesodatos.nio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class MovimientoFicheroDemo {
    public static void main(String[] args) {
        Path origen = Path.of("target/demo-data/mover/origen.txt");
        Path destino = Path.of("target/demo-data/mover/OtroNombre.txt");
        try {
            Files.createDirectories(origen.getParent());
            Files.writeString(origen, "Fichero de prueba para mover.");
            Files.move(origen, destino, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Fichero movido a " + destino.toAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
