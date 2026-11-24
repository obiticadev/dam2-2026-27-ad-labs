package es.dam.accesodatos.nio;

import java.nio.file.Files;
import java.nio.file.Path;

public class FileEjemplo {
    public static void main(String[] args) {
        Path path = Path.of("src/main/resources/hola.txt");
        System.out.println("path = " + path);
        System.out.println("exists = " + Files.exists(path));
        System.out.println("readable = " + Files.isReadable(path));
        System.out.println("writable = " + Files.isWritable(path));
        System.out.println("executable = " + Files.isExecutable(path));
    }
}
