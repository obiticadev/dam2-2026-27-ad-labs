package es.dam.accesodatos.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class PersonaSerializationDemo {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Path archivo = Path.of("target/demo-data/persona.dat");
        Files.createDirectories(archivo.getParent());

        Persona persona = new Persona("Juan", 30);
        try (FileOutputStream archivoSalida = new FileOutputStream(archivo.toFile());
             ObjectOutputStream salidaObjeto = new ObjectOutputStream(archivoSalida)) {
            salidaObjeto.writeObject(persona);
            System.out.println("Objeto serializado correctamente.");
        }

        try (FileInputStream archivoEntrada = new FileInputStream(archivo.toFile());
             ObjectInputStream entradaObjeto = new ObjectInputStream(archivoEntrada)) {
            Persona recuperada = (Persona) entradaObjeto.readObject();
            System.out.println("Objeto deserializado: " + recuperada);
        }
    }
}
