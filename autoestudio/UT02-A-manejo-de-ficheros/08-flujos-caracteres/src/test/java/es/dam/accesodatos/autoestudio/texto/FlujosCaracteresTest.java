package es.dam.accesodatos.autoestudio.texto;

import static org.junit.jupiter.api.Assertions.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FlujosCaracteresTest {
    @TempDir File carpeta;

    @Test
    void leeTildesLineaVaciaYUltimaLineaSinSalto() throws IOException {
        File f = preparar("Ángela\n\nJosé");
        assertEquals(List.of("Ángela", "", "José"), FlujosCaracteres.leerLineas(f));
    }
    @Test
    void vacioNoEsErrorYArchivoAusenteSiLoEs() throws IOException {
        assertEquals(List.of(), FlujosCaracteres.leerLineas(preparar("")));
        assertThrows(IOException.class, () -> FlujosCaracteres.leerLineas(new File(carpeta, "ausente")));
    }
    @Test
    void escribeSobrescribeYAnexaSinDependerDelLectorDelAlumno() throws IOException {
        File f = preparar("contenido antiguo largo\n");
        FlujosCaracteres.escribirLineas(f, List.of("Ana", ""), false);
        assertEquals("Ana" + System.lineSeparator() + System.lineSeparator(), contenido(f));
        FlujosCaracteres.escribirLineas(f, List.of("José"), true);
        assertEquals("Ana" + System.lineSeparator() + System.lineSeparator()
                + "José" + System.lineSeparator(), contenido(f));
        FlujosCaracteres.escribirLineas(f, List.of(), false);
        assertEquals("", contenido(f));
    }
    private File preparar(String texto) throws IOException {
        File f = new File(carpeta, "texto.txt");
        try (Writer out = new FileWriter(f, StandardCharsets.UTF_8)) { out.write(texto); }
        return f;
    }
    private String contenido(File f) throws IOException {
        try (Reader in = new FileReader(f, StandardCharsets.UTF_8)) {
            StringBuilder texto = new StringBuilder();
            int c;
            while ((c = in.read()) != -1) texto.append((char) c);
            return texto.toString();
        }
    }
}
