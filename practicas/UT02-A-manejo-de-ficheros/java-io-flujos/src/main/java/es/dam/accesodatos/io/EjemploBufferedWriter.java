package es.dam.accesodatos.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class EjemploBufferedWriter {
    public static void main(String[] args) {
        Path archivo = Path.of("target/demo-data/salida.txt");
        try {
            Files.createDirectories(archivo.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                writer.write("Primera línea del archivo.");
                writer.newLine();
                writer.write("Segunda línea del archivo.");
                writer.newLine();
            }
            System.out.println("Archivo escrito con éxito: " + archivo.toAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
