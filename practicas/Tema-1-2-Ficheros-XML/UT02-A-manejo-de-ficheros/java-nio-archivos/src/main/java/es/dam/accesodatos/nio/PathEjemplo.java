package es.dam.accesodatos.nio;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PathEjemplo {
    public static void main(String[] args) {
        mostrar(Paths.get("C:/Users/alumno/PathEjemplo"));
        mostrar(Paths.get("/home/PathEjemplo"));
    }

    private static void mostrar(Path path) {
        System.out.println("path = " + path);
        System.out.println("is absolute? = " + path.isAbsolute());
        System.out.println("file short name = " + path.getFileName());
        System.out.println("parent = " + path.getParent());
        System.out.println("uri = " + path.toUri());
        System.out.println();
    }
}
