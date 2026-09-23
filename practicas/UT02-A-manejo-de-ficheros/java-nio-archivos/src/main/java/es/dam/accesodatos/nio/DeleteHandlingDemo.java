package es.dam.accesodatos.nio;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

public class DeleteHandlingDemo {
    public static void main(String[] args) {
        Path path = Path.of(args.length > 0 ? args[0] : "target/demo-data/no-existe.txt");
        try {
            Files.delete(path);
            System.out.println("Borrado: " + path);
        } catch (NoSuchFileException e) {
            System.err.format("%s: no existe el fichero o directorio%n", path);
        } catch (DirectoryNotEmptyException e) {
            System.err.format("%s no está vacío%n", path);
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
