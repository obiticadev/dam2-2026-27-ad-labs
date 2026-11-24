package es.dam.accesodatos.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class StringSerializationDemo {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Path archivo = Path.of("target/demo-data/archivo.x");
        Files.createDirectories(archivo.getParent());

        try (ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(archivo.toFile()))) {
            salida.writeObject("Me van a serializar");
        }
        try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(archivo.toFile()))) {
            String texto = (String) entrada.readObject();
            System.out.println("Objeto recuperado: " + texto);
        }
    }
}
