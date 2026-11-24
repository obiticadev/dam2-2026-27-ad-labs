package es.dam.accesodatos.nio;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileEjemplo1 {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Falta el fichero. Ejemplo: src/main/resources/hola.txt");
            return;
        }

        Path path = Paths.get(args[0]);
        try {
            byte[] contenido = Files.readAllBytes(path);
            System.out.print(new String(contenido, StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.err.println("ERROR: " + e);
        }
    }
}
