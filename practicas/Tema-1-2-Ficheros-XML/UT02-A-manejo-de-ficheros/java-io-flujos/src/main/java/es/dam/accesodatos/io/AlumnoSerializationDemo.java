package es.dam.accesodatos.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class AlumnoSerializationDemo {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Path archivo = Path.of("target/demo-data/alumno.obj");
        Files.createDirectories(archivo.getParent());

        Alumno alumno = new Alumno("12345678A", "2026-001", "Natalia", "García", "López");
        try (ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(archivo.toFile()))) {
            salida.writeObject(alumno);
        }
        try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(archivo.toFile()))) {
            Alumno recuperado = (Alumno) entrada.readObject();
            System.out.println(recuperado);
        }
    }
}
