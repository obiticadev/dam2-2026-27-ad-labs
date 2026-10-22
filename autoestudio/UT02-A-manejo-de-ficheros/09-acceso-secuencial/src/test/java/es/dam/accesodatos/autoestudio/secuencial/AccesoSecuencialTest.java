package es.dam.accesodatos.autoestudio.secuencial;

import static org.junit.jupiter.api.Assertions.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class AccesoSecuencialTest {
    @TempDir File carpeta;

    @Test
    void buscaIdCompletoYDevuelveNullSiNoExiste() throws IOException {
        File origen = preparar("10;Luis\n1;Ana\n2;José");
        assertEquals("1;Ana", AccesoSecuencial.buscarPorId(origen, 1));
        assertEquals("2;José", AccesoSecuencial.buscarPorId(origen, 2));
        assertNull(AccesoSecuencial.buscarPorId(origen, 8));
    }
    @Test
    void eliminaPorReescrituraSinCambiarElOriginal() throws IOException {
        File origen = preparar("1;Ana\n2;José\n3;Luis");
        File destino = new File(carpeta, "salida.txt");
        for (int id : new int[] {1, 2, 3}) {
            assertEquals(1, AccesoSecuencial.eliminarPorId(origen, destino, id));
            List<String> esperado = new ArrayList<>(List.of("1;Ana", "2;José", "3;Luis"));
            esperado.remove(id - 1);
            assertEquals(esperado, leer(destino));
            assertEquals(List.of("1;Ana", "2;José", "3;Luis"), leer(origen));
        }
        assertEquals(0, AccesoSecuencial.eliminarPorId(origen, destino, 99));
        assertEquals(leer(origen), leer(destino));
    }
    @Test
    void trataArchivoVacioYRegistroUnico() throws IOException {
        File origen = preparar("1;Ana");
        File destino = new File(carpeta, "salida.txt");
        assertEquals(1, AccesoSecuencial.eliminarPorId(origen, destino, 1));
        assertEquals(List.of(), leer(destino));
        origen = preparar("");
        assertNull(AccesoSecuencial.buscarPorId(origen, 1));
        assertEquals(0, AccesoSecuencial.eliminarPorId(origen, destino, 1));
        assertEquals(List.of(), leer(destino));
    }
    private File preparar(String texto) throws IOException {
        File f = new File(carpeta, "entrada.txt");
        try (Writer out = new FileWriter(f, StandardCharsets.UTF_8)) { out.write(texto); }
        return f;
    }
    private List<String> leer(File f) throws IOException {
        List<String> lineas = new ArrayList<>();
        try (BufferedReader in = new BufferedReader(new FileReader(f, StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = in.readLine()) != null) lineas.add(linea);
        }
        return lineas;
    }
}
