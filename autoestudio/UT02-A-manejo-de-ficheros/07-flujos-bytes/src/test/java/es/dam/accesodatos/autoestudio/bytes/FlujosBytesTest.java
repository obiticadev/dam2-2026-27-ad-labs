package es.dam.accesodatos.autoestudio.bytes;

import static org.junit.jupiter.api.Assertions.*;
import java.io.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FlujosBytesTest {
    @TempDir File carpeta;

    @Test
    void copiaUltimoBloqueParcialSinBytesSobrantesYSobrescribe() throws IOException {
        File origen = preparar("origen.bin", new byte[] {0, 1, 2, 3, 4, (byte) 255});
        File destino = preparar("destino.bin", new byte[20]);
        assertEquals(6, FlujosBytes.copiar(origen, destino, 4));
        assertArrayEquals(leer(origen), leer(destino));
    }

    @Test
    void copiaArchivoVacioYBloqueExacto() throws IOException {
        File origen = preparar("vacio.bin", new byte[0]);
        File destino = preparar("salida.bin", new byte[] {9});
        assertEquals(0, FlujosBytes.copiar(origen, destino, 4));
        assertEquals(0, destino.length());
        origen = preparar("exacto.bin", new byte[] {1, 2, 3, 4});
        assertEquals(4, FlujosBytes.copiar(origen, destino, 4));
        assertArrayEquals(leer(origen), leer(destino));
    }

    @Test
    void anexaSoloElRangoDelArray() throws IOException {
        File destino = preparar("anexo.bin", new byte[] {9});
        FlujosBytes.anexar(destino, new byte[] {10, 20, 30, 40}, 1, 2);
        FlujosBytes.anexar(destino, new byte[] {99}, 0, 0);
        assertArrayEquals(new byte[] {9, 20, 30}, leer(destino));
    }

    @Test
    void rechazaBufferInvalidoYNoOcultaOrigenAusente() {
        File origen = new File(carpeta, "ausente");
        File destino = new File(carpeta, "salida");
        assertThrows(IllegalArgumentException.class, () -> FlujosBytes.copiar(origen, destino, 0));
        assertThrows(IllegalArgumentException.class, () -> FlujosBytes.copiar(origen, destino, -1));
        assertThrows(IOException.class, () -> FlujosBytes.copiar(origen, destino, 4));
    }

    private File preparar(String nombre, byte[] datos) throws IOException {
        File f = new File(carpeta, nombre);
        try (FileOutputStream out = new FileOutputStream(f)) { out.write(datos); }
        return f;
    }
    private byte[] leer(File f) throws IOException {
        try (FileInputStream in = new FileInputStream(f)) { return in.readAllBytes(); }
    }
}
