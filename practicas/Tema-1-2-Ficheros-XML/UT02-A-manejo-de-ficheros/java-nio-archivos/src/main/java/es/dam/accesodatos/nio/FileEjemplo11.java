package es.dam.accesodatos.nio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileEjemplo11 {
    public static void main(String[] args) {
        Path origen = Path.of("src/main/resources/hola.txt");
        Path destino = Path.of("target/demo-data/bufer/hola.txt");
        try {
            Files.createDirectories(destino.getParent());
            try (BufferedReader reader = Files.newBufferedReader(origen, StandardCharsets.UTF_8);
                 BufferedWriter writer = Files.newBufferedWriter(destino, StandardCharsets.UTF_8,
                         StandardOpenOption.WRITE, StandardOpenOption.CREATE,
                         StandardOpenOption.TRUNCATE_EXISTING)) {
                String linea;
                while ((linea = reader.readLine()) != null) {
                    writer.write(linea);
                    writer.newLine();
                }
            }
            System.out.println("Copia con búfer guardada en " + destino.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("ERROR: " + e);
        }
    }
}
