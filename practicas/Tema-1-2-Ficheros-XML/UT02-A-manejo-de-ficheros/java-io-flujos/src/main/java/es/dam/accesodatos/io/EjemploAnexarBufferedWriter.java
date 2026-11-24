package es.dam.accesodatos.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class EjemploAnexarBufferedWriter {
    public static void main(String[] args) {
        Path archivo = Path.of("target/demo-data/salida.txt");
        try {
            Files.createDirectories(archivo.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                writer.newLine();
                writer.write("Esta es una nueva línea anexada al final.");
            }
            System.out.println("Línea anexada con éxito.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
