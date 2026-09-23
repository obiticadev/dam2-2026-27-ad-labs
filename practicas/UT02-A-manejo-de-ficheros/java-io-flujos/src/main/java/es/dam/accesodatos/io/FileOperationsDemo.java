package es.dam.accesodatos.io;

import java.io.File;
import java.io.IOException;

public class FileOperationsDemo {
    public static void main(String[] args) throws IOException {
        File datos = new File("target/demo-data");
        if (!datos.mkdirs() && !datos.isDirectory()) {
            throw new IOException("No se pudo crear " + datos.getAbsolutePath());
        }

        // Las rutas pueden crearse desde String o combinando un directorio y un nombre.
        File fichero = new File(datos, "fichero-demo.txt");
        System.out.println("Nombre: " + fichero.getName());
        System.out.println("Ruta: " + fichero.getPath());
        System.out.println("Ruta absoluta: " + fichero.getAbsolutePath());
        System.out.println("Existe antes de crear: " + fichero.exists());

        if (fichero.createNewFile()) {
            System.out.println("Fichero creado.");
        } else {
            System.out.println("El fichero ya existía.");
        }
        System.out.println("Es fichero: " + fichero.isFile());
        System.out.println("Se puede leer: " + fichero.canRead());

        File directorio = new File(datos, "directorio-simple");
        System.out.println("Directorio creado: " + directorio.mkdir());

        File anidado = new File(datos, "carpeta1/carpeta2/carpeta3");
        System.out.println("Directorios anidados creados: " + anidado.mkdirs());

        String[] contenido = datos.list();
        if (contenido != null) {
            System.out.println("Contenido de " + datos + ":");
            for (String nombre : contenido) {
                System.out.println(" - " + nombre);
            }
        }
    }
}
