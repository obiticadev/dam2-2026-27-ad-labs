package es.dam.accesodatos.autoestudio.file.filtrado;

import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FiltradoFilenameFilterTest {
    @TempDir File carpeta;

    @Test
    void aplicaElSufijoTalComoHaceElTemario() {
        FiltradoFilenameFilter filtro = new FiltradoFilenameFilter(".txt");
        assertTrue(filtro.accept(carpeta, "notas.txt"));
        assertFalse(filtro.accept(carpeta, "NOTAS.TXT"));
        assertFalse(filtro.accept(carpeta, "imagen.png"));
        assertFalse(filtro.accept(carpeta, null));
        // No hace falta que notas.txt exista: FilenameFilter recibe nombres.
    }

    @Test
    void listaNombresCoincidentesIncluidoUnDirectorioConEseSufijo() throws IOException {
        assertTrue(new File(carpeta, "notas.txt").createNewFile());
        assertTrue(new File(carpeta, "imagen.png").createNewFile());
        assertTrue(new File(carpeta, "respaldo.txt").mkdir());
        File[] resultado = FiltradoFilenameFilter.listarPorExtension(carpeta, ".txt");
        assertNotNull(resultado);
        String[] nombres = Arrays.stream(resultado).map(File::getName).sorted().toArray(String[]::new);
        assertArrayEquals(new String[] {"notas.txt", "respaldo.txt"}, nombres);
    }

    @Test
    void distingueSinCoincidenciasDeDirectorioInvalido() throws IOException {
        assertArrayEquals(new File[0], FiltradoFilenameFilter.listarPorExtension(carpeta, ".txt"));
        assertThrows(IllegalArgumentException.class,
                () -> FiltradoFilenameFilter.listarPorExtension(new File(carpeta, "ausente"), ".txt"));
        assertThrows(IllegalArgumentException.class, () -> new FiltradoFilenameFilter(null));
        assertThrows(IllegalArgumentException.class, () -> new FiltradoFilenameFilter(" "));
    }
}
