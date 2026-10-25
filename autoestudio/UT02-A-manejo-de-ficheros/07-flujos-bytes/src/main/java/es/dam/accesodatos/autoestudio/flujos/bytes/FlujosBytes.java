package es.dam.accesodatos.autoestudio.flujos.bytes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Ejercicios de lectura, escritura, copia y anexado con {@link FileInputStream}
 * y
 * {@link FileOutputStream}.
 *
 * <p>
 * Teoría: {@code teoria/flujos-bytes.md}. Corresponde al apartado 2.7 de
 * UT02-A. No se permite {@code Files.copy} ni cargar el archivo completo en
 * memoria: se trabaja por bloques con un búfer de tamaño fijo.
 */
public final class FlujosBytes {

    private FlujosBytes() {
    }

    /**
     * Copia un fichero por bloques y sobrescribe el destino.
     *
     * @param origen    fichero regular existente que se va a leer
     * @param destino   fichero que se crea o se sobrescribe; distinto del origen
     * @param capacidad tamaño del búfer en bytes; mayor que cero
     * @return número total de bytes escritos en el destino
     * @throws IllegalArgumentException si algún argumento no es válido o si
     *                                  origen y destino son el mismo fichero
     * @throws IOException              si falla la lectura o la escritura
     */
    public static long copiar(File origen, File destino, int capacidad) throws IOException {
        // 1. Validar antes de abrir: abrir el destino para escribir lo vacía.
        if (origen == null || !origen.isFile() || destino == null || capacidad <= 0
                || origen.getCanonicalFile().equals(destino.getCanonicalFile())) {
            throw new IllegalArgumentException("Error de parámetros, introduce un parámetro correcto");
        }

        // 2. Abrir los flujos; el try los cierra incluso si ocurre una IOException.
        try (FileInputStream fis = new FileInputStream(origen);
                FileOutputStream fos = new FileOutputStream(destino)) {
            long total = 0;
            byte[] captura = new byte[capacidad];
            int leidos;

            // 3. Leer un bloque, comprobar EOF y procesar solo su parte válida.
            while ((leidos = fis.read(captura)) != -1) {
                fos.write(captura, 0, leidos);
                total += leidos;
            }

            // 4. Devolver lo escrito (0 si el origen estaba vacío).
            return total;
        }
    }

    /**
     * Añade al final de un fichero una porción de un array de bytes.
     *
     * @param destino fichero al que se anexan los bytes; se crea si no existe
     * @param datos   array que contiene los bytes
     * @param off     primer índice del array que se escribe (no es una posición del
     *                fichero)
     * @param len     cantidad de bytes que se escriben desde {@code off}
     * @throws IllegalArgumentException  si {@code destino} o {@code datos} son
     *                                   nulos
     * @throws IndexOutOfBoundsException si {@code off} y {@code len} no caben en
     *                                   {@code datos}; en ese caso el destino no se
     *                                   crea ni se modifica
     * @throws IOException               si falla la escritura
     */
    public static void anexar(File destino, byte[] datos, int off, int len) throws IOException {
        // TODO: Rechaza destino o datos nulos con IllegalArgumentException.
        // TODO: Comprueba off >= 0, len >= 0 y off + len <= datos.length ANTES de abrir
        // el flujo:
        // abrir un FileOutputStream ya crea el fichero aunque luego no se escriba nada.
        // TODO: Abre el flujo en modo append (segundo argumento true) con
        // try-with-resources.
        // TODO: Escribe exactamente len bytes empezando en el índice off del array.
        if (destino == null || datos == null) {
            throw new IllegalArgumentException("Error, parámetros nulos");
        }
        if (off >= 0 && len >= 0 && off <= datos.length - len) {
            try (FileOutputStream fos = new FileOutputStream(destino, true)) {
                fos.write(datos, off, len);
            }
        } else {
            throw new IndexOutOfBoundsException("Parámetros de índice con descuadre");
        }
    }

    /**
     * Ejercicio 3: guarda un array completo, sustituyendo el contenido anterior.
     * No necesita bucle: los datos ya están en memoria.
     *
     * @param destino fichero que se crea o se sobrescribe
     * @param datos   bytes que se escriben; puede ser un array vacío
     * @throws IllegalArgumentException si destino o datos son nulos; validar antes
     *                                  de abrir
     * @throws IOException              si falla la escritura
     */
    public static void guardar(File destino, byte[] datos) throws IOException {
        // TODO: Valida las referencias antes de abrir el fichero.
        // TODO: Abre una salida sin append con try-with-resources.
        // TODO: Escribe el array completo. Un array vacío debe dejar el fichero vacío.
        if (destino == null || datos == null) {
            throw new IllegalArgumentException("Error, destino o datos son nulos");
        }
        try (FileOutputStream fos = new FileOutputStream(destino, false)) {
            fos.write(datos);
        }
    }

    /**
     * Ejercicio 4: cuenta bytes leyendo por bloques, sin escribir otro fichero.
     * No uses File.length(): el objetivo es practicar el bucle de lectura.
     *
     * @param origen    fichero regular existente
     * @param capacidad tamaño del búfer; mayor que cero
     * @return total de bytes leídos; cero para un fichero vacío
     * @throws IllegalArgumentException si origen es nulo, no es un fichero o
     *                                  capacidad no es positiva
     * @throws IOException              si falla la lectura
     */
    public static long contarBytes(File origen, int capacidad) throws IOException {
        // TODO: Valida y abre solo un flujo de entrada con try-with-resources.
        // TODO: Lee por bloques hasta -1 y acumula la cantidad realmente leída.
        if (origen == null || !origen.isFile() || capacidad <= 0) {
            throw new IllegalArgumentException(
                    "Error, origen, fichero o capacidad incompatibles con los parámetros esperados");
        }
        long total = 0;
        try (FileInputStream fis = new FileInputStream(origen)) {
            byte[] buffer = new byte[capacidad];
            int leido;
            while ((leido = fis.read(buffer)) != -1) {
                total += leido;
            }
        }
        return total;
    }

    /**
     * Ejercicio 5: cuenta apariciones de un byte dentro de los bloques leídos.
     * Ejemplo: [7, 2, 7, 4, 5], buscado 7, capacidad 4 -> 2 apariciones.
     *
     * @param origen    fichero regular existente
     * @param buscado   byte que se quiere contar
     * @param capacidad tamaño del búfer; mayor que cero
     * @return número de coincidencias; cero para un fichero vacío
     * @throws IllegalArgumentException si origen es nulo, no es un fichero o
     *                                  capacidad no es positiva
     * @throws IOException              si falla la lectura
     */
    public static long contarApariciones(File origen, byte buscado, int capacidad) throws IOException {
        // TODO: Reutiliza el esquema de lectura de contarBytes.
        // TODO: En cada bloque recorre solo los índices 0 hasta leidos - 1.
        // TODO: Incrementa el contador cuando el byte coincida con buscado.
        return -1L;
    }

    /**
     * Ejercicio 6: añade el contenido de un fichero al final de otro por bloques.
     * Es una combinación de copiar y anexar; no carga todo el origen en memoria.
     *
     * @param origen    fichero regular existente
     * @param destino   fichero que se crea o al que se añaden los bytes
     * @param capacidad tamaño del búfer; mayor que cero
     * @return número de bytes añadidos, sin contar los que ya tenía el destino
     * @throws IllegalArgumentException si los argumentos no son válidos según
     *                                  copiar,
     *                                  incluido origen y destino con la misma ruta
     *                                  canónica
     * @throws IOException              si falla la lectura o la escritura
     */
    public static long anexarArchivo(File origen, File destino, int capacidad) throws IOException {
        // TODO: Valida como en copiar, antes de abrir los flujos.
        // TODO: Abre entrada y salida, esta vez con append.
        // TODO: Lee, escribe solo la parte válida y acumula los bytes añadidos.
        return -1L;
    }

    /**
     * Ejercicio 7: copia por bloques sustituyendo un byte por otro en el destino.
     * El origen no se modifica. Ejemplo: [1, 2, 1], cambiar 1 por 9 -> [9, 2, 9].
     *
     * @param origen    fichero regular existente
     * @param destino   fichero que se crea o se sobrescribe, distinto del origen
     * @param buscado   byte que se sustituye
     * @param reemplazo byte que se escribe en su lugar
     * @param capacidad tamaño del búfer; mayor que cero
     * @return número total de bytes escritos, no el número de sustituciones
     * @throws IllegalArgumentException si los argumentos no son válidos según
     *                                  copiar
     * @throws IOException              si falla la lectura o la escritura
     */
    public static long copiarSustituyendo(File origen, File destino, byte buscado,
            byte reemplazo, int capacidad) throws IOException {
        // TODO: Parte del esquema de copiar, con las mismas validaciones.
        // TODO: Antes de escribir cada bloque, recorre su parte válida y sustituye las
        // coincidencias.
        // TODO: Escribe solo los bytes leídos y acumula el total.
        return -1L;
    }

    /**
     * Playground con datos regenerables bajo {@code target/demo-data}.
     * Los ejercicios pendientes muestran resultados distintos de los esperados.
     *
     * @param args argumentos de línea de comandos, no utilizados
     * @throws IOException si falla la preparación o la copia del ejemplo
     */
    public static void main(String[] args) throws IOException {
        File base = new File("target/demo-data/07-flujos-bytes");
        base.mkdirs();
        File origen = new File(base, "origen.bin");
        try (FileOutputStream salida = new FileOutputStream(origen)) {
            salida.write(new byte[] { 1, 2, 3, 4, 5, 6 });
        }

        File copia = new File(base, "copia.bin");
        long total = copiar(origen, copia, 4);
        System.out.println("copiar() devolvió " + total + " (esperado 6)");
        System.out.println("copia.bin = " + Arrays.toString(leer(copia)));

        File anexo = new File(base, "anexo.bin");
        try (FileOutputStream salida = new FileOutputStream(anexo)) {
            salida.write(9);
        }
        anexar(anexo, new byte[] { 10, 20, 30, 40 }, 1, 2);
        System.out.println("anexo.bin = " + Arrays.toString(leer(anexo)) + " (esperado [9, 20, 30])");

        System.out.println("\n--- Ejercicios nuevos: completa los TODO en orden ---");
        File guardado = new File(base, "guardado.bin");
        // Preparación independiente de guardar: permite ver si realmente sobrescribe.
        try (FileOutputStream salida = new FileOutputStream(guardado)) {
            salida.write(new byte[] { 99, 99, 99, 99 });
        }
        guardar(guardado, new byte[] { 10, 20, 30 });
        System.out.println("3. guardar: " + Arrays.toString(leer(guardado)) + " (esperado [10, 20, 30])");

        System.out.println("4. contarBytes: " + contarBytes(origen, 4) + " (esperado 6)");

        File repetidos = new File(base, "repetidos.bin");
        try (FileOutputStream salida = new FileOutputStream(repetidos)) {
            salida.write(new byte[] { 7, 2, 7, 4, 5 });
        }
        System.out.println("5. contarApariciones: " + contarApariciones(repetidos, (byte) 7, 4)
                + " (esperado 2)");

        File unido = new File(base, "unido.bin");
        try (FileOutputStream salida = new FileOutputStream(unido)) {
            salida.write(9);
        }
        long añadidos = anexarArchivo(origen, unido, 4);
        System.out.println("6. anexarArchivo: " + añadidos + " bytes (esperado 6), "
                + Arrays.toString(leer(unido)) + " (esperado [9, 1, 2, 3, 4, 5, 6])");

        File sustituido = new File(base, "sustituido.bin");
        try (FileOutputStream salida = new FileOutputStream(sustituido)) {
            salida.write(99);
        }
        long escritos = copiarSustituyendo(repetidos, sustituido, (byte) 7, (byte) 9, 4);
        System.out.println("7. copiarSustituyendo: " + escritos + " bytes (esperado 5), "
                + Arrays.toString(leer(sustituido)) + " (esperado [9, 2, 9, 4, 5])");
    }

    private static byte[] leer(File fichero) throws IOException {
        if (!fichero.isFile()) {
            return new byte[0];
        }
        try (FileInputStream entrada = new FileInputStream(fichero)) {
            return entrada.readAllBytes();
        }
    }
}
