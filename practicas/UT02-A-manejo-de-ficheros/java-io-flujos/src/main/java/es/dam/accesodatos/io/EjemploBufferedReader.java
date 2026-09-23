package es.dam.accesodatos.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class EjemploBufferedReader {
    public static void main(String[] args) {
        Path origen = Path.of("src/main/resources/origen.txt");
        try (BufferedReader reader = Files.newBufferedReader(origen, StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                System.out.println(linea);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
