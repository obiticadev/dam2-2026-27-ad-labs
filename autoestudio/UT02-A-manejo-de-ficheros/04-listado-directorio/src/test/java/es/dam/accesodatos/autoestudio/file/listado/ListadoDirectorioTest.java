package es.dam.accesodatos.autoestudio.file.listado;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@DisplayName("Listados de directorio con java.io.File")
class ListadoDirectorioTest {

    @TempDir
    File directorioTemporal;

    @Test
    void listaNombresInmediatosEnOrdenNatural() throws IOException {
        crearEntrada("c.txt");
        crearEntrada("a.txt");
        crearEntrada("b.txt");

        assertArrayEquals(new String[] { "a.txt", "b.txt", "c.txt" },
                ListadoDirectorio.nombres(directorioTemporal));
    }

    @Test
    void listaObjetosFileEnOrdenPorNombre() throws IOException {
        File c = crearEntrada("c.txt");
        File a = crearEntrada("a.txt");
        File b = crearEntrada("b.txt");

        assertArrayEquals(new File[] { a, b, c }, ListadoDirectorio.entradas(directorioTemporal));
    }

    @Test
    void devuelveArraysVaciosParaUnDirectorioVacio() throws IOException {
        assertArrayEquals(new String[0], ListadoDirectorio.nombres(directorioTemporal));
        assertArrayEquals(new File[0], ListadoDirectorio.entradas(directorioTemporal));
        assertTrue(ListadoDirectorio.estaVacio(directorioTemporal));
    }

    @Test
    void soloListaLasEntradasInmediatas() throws IOException {
        File subdirectorio = new File(directorioTemporal, "subdirectorio");
        assertTrue(subdirectorio.mkdir());
        crearEntrada("visible.txt");
        File nieto = new File(subdirectorio, "interior.txt");
        assertTrue(nieto.createNewFile());

        assertArrayEquals(new String[] { "subdirectorio", "visible.txt" },
                ListadoDirectorio.nombres(directorioTemporal));
        assertEquals(2, ListadoDirectorio.entradas(directorioTemporal).length);
    }

    @Test
    void detectaCuandoUnDirectorioContieneEntradas() throws IOException {
        crearEntrada("presente.txt");

        assertFalse(ListadoDirectorio.estaVacio(directorioTemporal));
    }

    @Test
    void rechazaRutasNulasInexistentesYQueNoSeanDirectorios() throws IOException {
        File fichero = crearEntrada("fichero.txt");
        File inexistente = new File(directorioTemporal, "ausente");

        assertThrows(IllegalArgumentException.class, () -> ListadoDirectorio.nombres(null));
        assertThrows(IllegalArgumentException.class, () -> ListadoDirectorio.entradas(null));
        assertThrows(IllegalArgumentException.class, () -> ListadoDirectorio.estaVacio(null));
        assertThrows(IllegalArgumentException.class, () -> ListadoDirectorio.nombres(fichero));
        assertThrows(IllegalArgumentException.class, () -> ListadoDirectorio.entradas(inexistente));
        assertThrows(IllegalArgumentException.class, () -> ListadoDirectorio.estaVacio(inexistente));
    }

    private File crearEntrada(String nombre) throws IOException {
        File entrada = new File(directorioTemporal, nombre);
        assertTrue(entrada.createNewFile());
        return entrada;
    }
}
