package es.dam.accesodatos.io;

import java.io.File;
import java.io.FilenameFilter;

public class FilenameFilterDemo {
    public static void main(String[] args) {
        File directorio = new File(args.length > 0 ? args[0] : "src/main/resources/filtrar");
        FilenameFilter filtroTxt = (dir, nombre) -> nombre.endsWith(".txt");
        String[] archivos = directorio.list(filtroTxt);

        if (archivos == null) {
            System.err.println("No se pudo listar el directorio: " + directorio.getAbsolutePath());
            return;
        }
        if (archivos.length == 0) {
            System.out.println("No hay archivos .txt que listar.");
            return;
        }
        for (String archivo : archivos) {
            System.out.println(archivo);
        }
    }
}
