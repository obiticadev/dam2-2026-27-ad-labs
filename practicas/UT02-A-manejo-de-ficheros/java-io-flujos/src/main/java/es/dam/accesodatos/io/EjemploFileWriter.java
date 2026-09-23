package es.dam.accesodatos.io;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class EjemploFileWriter {
    public static void main(String[] args) {
        Path archivo = Path.of("target/demo-data/HolaMundo-escrito.txt");
        try {
            Files.createDirectories(archivo.getParent());
            try (FileWriter writer = new FileWriter(archivo.toFile(), StandardCharsets.UTF_8, true)) {
                writer.write("Este es un ejemplo de uso de FileWriter.\n");
                writer.write("FileWriter escribe directamente en el archivo.\n");
            }
            System.out.println("Archivo escrito con éxito: " + archivo.toAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
