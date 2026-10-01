package es.dam.accesodatos.autoestudio.file.filtrado;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@DisplayName("Filtrado por extensión con FilenameFilter")
class FiltradoFilenameFilterTest {

    @TempDir
    File directorioTemporal;

    @Test
    void aceptaFicherosConExtensionIgnorandoMayusculas() throws IOException {
        crearFichero("notas.txt");
        crearFichero("LEEME.TXT");
        crearFichero("imagen.png");
        crearDirectorio("respaldo.txt");
        FiltradoFilenameFilter filtro = new FiltradoFilenameFilter(".txt");

        assertTrue(filtro.accept(directorioTemporal, "notas.txt"));
        assertTrue(filtro.accept(directorioTemporal, "LEEME.TXT"));
        assertFalse(filtro.accept(directorioTemporal, "imagen.png"));
        assertFalse(filtro.accept(directorioTemporal, "respaldo.txt"));
        assertFalse(filtro.accept(directorioTemporal, "inexistente.txt"));
    }

    @Test
    void rechazaArgumentosNulosYRutasQueNoSeanDirectorio() {
        FiltradoFilenameFilter filtro = new FiltradoFilenameFilter(".txt");

        assertFalse(filtro.accept(null, "notas.txt"));
        assertFalse(filtro.accept(directorioTemporal, null));
        assertFalse(filtro.accept(new File(directorioTemporal, "ausente"), "notas.txt"));
    }

    @Test
    void listaSoloFicherosCoincidentesEnOrdenNatural() throws IOException {
        File zeta = crearFichero("zeta.txt");
        File alfa = crearFichero("alfa.TXT");
        crearFichero("ignorado.csv");
        crearDirectorio("carpeta.txt");

        assertArrayEquals(new File[] { alfa, zeta },
                FiltradoFilenameFilter.listarPorExtension(directorioTemporal, ".txt"));
    }

    @Test
    void devuelveArrayVacioSiNoHayCoincidencias() throws IOException {
        crearFichero("datos.csv");

        assertArrayEquals(new File[0],
                FiltradoFilenameFilter.listarPorExtension(directorioTemporal, ".txt"));
    }

    @Test
    void rechazaExtensionYDirectorioInvalidos() throws IOException {
        File fichero = crearFichero("fichero.txt");
        File inexistente = new File(directorioTemporal, "ausente");

        assertThrows(IllegalArgumentException.class, () -> new FiltradoFilenameFilter(null));
        assertThrows(IllegalArgumentException.class, () -> new FiltradoFilenameFilter(" "));
        assertThrows(IllegalArgumentException.class, () -> new FiltradoFilenameFilter("txt"));
        assertThrows(IllegalArgumentException.class, () -> new FiltradoFilenameFilter("."));
        assertThrows(IllegalArgumentException.class, () -> new FiltradoFilenameFilter(".t/xt"));
        assertThrows(IllegalArgumentException.class,
                () -> FiltradoFilenameFilter.listarPorExtension(fichero, ".txt"));
        assertThrows(IllegalArgumentException.class,
                () -> FiltradoFilenameFilter.listarPorExtension(inexistente, ".txt"));
    }

    private File crearFichero(String nombre) throws IOException {
        File fichero = new File(directorioTemporal, nombre);
        try (FileOutputStream salida = new FileOutputStream(fichero)) {
            salida.write(new byte[] { 1, 2, 3 });
        }
        return fichero;
    }

    private File crearDirectorio(String nombre) {
        File directorio = new File(directorioTemporal, nombre);
        assertTrue(directorio.mkdir());
        return directorio;
    }
}
