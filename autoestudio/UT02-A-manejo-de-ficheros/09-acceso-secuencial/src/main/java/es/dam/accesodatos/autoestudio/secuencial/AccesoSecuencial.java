package es.dam.accesodatos.autoestudio.secuencial;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * UT02-A §2.11. Archivos UTF-8 con líneas id;nombre, sin cabecera ni líneas
 * vacías.
 * Los ids son enteros únicos. No es un ejercicio de CSV ni de validación de
 * datos.
 */
public final class AccesoSecuencial {
    private AccesoSecuencial() {
    }

    /**
     * Devuelve la línea completa del id, o null si no existe. @throws IOException
     * si falla la lectura
     */
    public static String buscarPorId(File archivo, int id) throws IOException {
        // TODO: Lee línea a línea con BufferedReader.
        // TODO: Separa id y nombre por ';' y compara el id completo (1 no es 10).
        // TODO: Devuelve la coincidencia o null al llegar al final.
        try (BufferedReader br = new BufferedReader(new FileReader(archivo, StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] lista = linea.split(";");
                if (lista[0].equals(String.valueOf(id))) {
                    return linea;
                }
            }
        }
        return null;
    }

    /**
     * Reescribe en destino todas las líneas excepto la del id; devuelve cuántas
     * eliminó (0 o 1).
     * No modifica origen. Destino es otro archivo y se sobrescribe; sus padres
     * existen.
     * Escribe newLine() tras cada registro conservado, manteniendo el orden.
     * 
     * @throws IOException si falla la lectura o escritura
     */
    public static int eliminarPorId(File origen, File destino, int id) throws IOException {
        // TODO: Abre lector y escritor UTF-8 sobre archivos DISTINTOS.
        // TODO: Lee cada registro, compara su id y cuenta los descartados.
        // TODO: Escribe solo los demás, conservando orden; no cargues todo el archivo.
        // TODO: Cierra ambos flujos y devuelve el contador.
        int contador = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(origen, StandardCharsets.UTF_8));
                BufferedWriter bw = new BufferedWriter(new FileWriter(destino, StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] listaPalabras = linea.split(";");
                if (listaPalabras[0].equals(String.valueOf(id))) {
                    contador++;
                } else {
                    bw.write(linea);
                    bw.newLine();
                }
            }
        }
        return contador;
    }

    /** Consulta sin modificar el archivo. */
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Uso: AccesoSecuencial archivo.txt id");
            return;
        }
        System.out.println(buscarPorId(new File(args[0]), Integer.parseInt(args[1])));
    }
}
