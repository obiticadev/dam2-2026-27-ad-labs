package es.dam.accesodatos.autoestudio.aleatorio;

import static org.junit.jupiter.api.Assertions.*;
import java.io.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class AccesoAleatorioTest {
    @TempDir File carpeta;

    @Test
    void leePorIndiceNoPorId() throws IOException {
        File f = preparar();
        assertEquals(8.5, AccesoAleatorio.leerNota(f, 0));
        assertEquals(6.0, AccesoAleatorio.leerNota(f, 1));
        assertEquals(7.5, AccesoAleatorio.leerNota(f, 2));
    }
    @Test
    void actualizaSoloOchoBytesSinCambiarVecinosNiIds() throws IOException {
        File f = preparar();
        AccesoAleatorio.actualizarNota(f, 1, 9.0);
        try (RandomAccessFile in = new RandomAccessFile(f, "r")) {
            assertEquals(36, in.length());
            assertEquals(10, in.readInt()); assertEquals(8.5, in.readDouble());
            assertEquals(20, in.readInt()); assertEquals(9.0, in.readDouble());
            assertEquals(30, in.readInt()); assertEquals(7.5, in.readDouble());
        }
    }
    @Test
    void rechazaIndicesFueraDeRangoSinAmpliarElArchivo() throws IOException {
        File f = preparar();
        for (int indice : new int[] {-1, 3}) {
            assertThrows(IndexOutOfBoundsException.class, () -> AccesoAleatorio.leerNota(f, indice));
            assertThrows(IndexOutOfBoundsException.class, () -> AccesoAleatorio.actualizarNota(f, indice, 5));
        }
        assertEquals(36, f.length());
    }
    private File preparar() throws IOException {
        File f = new File(carpeta, "registros.dat");
        try (RandomAccessFile out = new RandomAccessFile(f, "rw")) {
            out.writeInt(10); out.writeDouble(8.5);
            out.writeInt(20); out.writeDouble(6.0);
            out.writeInt(30); out.writeDouble(7.5);
        }
        return f;
    }
}
