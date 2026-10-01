package es.dam.accesodatos.autoestudio.file.operaciones;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@DisplayName("Creación, renombrado y borrado con java.io.File")
class OperacionesFileTest {

    @TempDir
    File directorioTemporal;

    @Test
    void creaFicheroNuevoYNoSobrescribeElExistente() throws IOException {
        File fichero = new File(directorioTemporal, "nuevo.txt");

        assertTrue(OperacionesFile.crearFichero(fichero));
        assertTrue(fichero.isFile());
        assertFalse(OperacionesFile.crearFichero(fichero));
    }

    @Test
    void propagaElFalloSiFaltaElDirectorioPadre() {
        File fichero = new File(new File(directorioTemporal, "ausente"), "nuevo.txt");

        assertThrows(IOException.class, () -> OperacionesFile.crearFichero(fichero));
    }

    @Test
    void mkdirCreaUnSoloNivelYNoCreaPadres() {
        File unNivel = new File(directorioTemporal, "un-nivel");
        File padreAusente = new File(directorioTemporal, "ausente");
        File hijo = new File(padreAusente, "hijo");

        assertTrue(OperacionesFile.crearDirectorio(unNivel));
        assertFalse(OperacionesFile.crearDirectorio(unNivel));
        assertFalse(OperacionesFile.crearDirectorio(hijo));
        assertFalse(padreAusente.exists());
    }

    @Test
    void mkdirsCreaLosNivelesPadresQueFaltan() {
        File anidado = new File(new File(directorioTemporal, "nivel-a"), "nivel-b");

        assertTrue(OperacionesFile.crearDirectorios(anidado));
        assertTrue(anidado.isDirectory());
        assertTrue(anidado.getParentFile().isDirectory());
        assertFalse(OperacionesFile.crearDirectorios(anidado));
    }

    @Test
    void renombraUnaEntradaDentroDelMismoDirectorio() throws IOException {
        File origen = new File(directorioTemporal, "origen.txt");
        File destino = new File(directorioTemporal, "destino.txt");
        assertTrue(origen.createNewFile());

        assertTrue(OperacionesFile.renombrar(origen, destino));
        assertFalse(origen.exists());
        assertTrue(destino.isFile());
    }

    @Test
    void noRenombraSiElOrigenFaltaOSiElDestinoYaExiste() throws IOException {
        File inexistente = new File(directorioTemporal, "ausente.txt");
        File origen = new File(directorioTemporal, "origen.txt");
        File destinoExistente = new File(directorioTemporal, "destino.txt");
        assertTrue(origen.createNewFile());
        assertTrue(destinoExistente.createNewFile());

        assertFalse(OperacionesFile.renombrar(inexistente, destinoExistente));
        assertFalse(OperacionesFile.renombrar(origen, destinoExistente));
        assertTrue(origen.exists());
        assertTrue(destinoExistente.exists());
    }

    @Test
    void borraFicherosPeroNoDirectoriosNoVacios() throws IOException {
        File fichero = new File(directorioTemporal, "borrar.txt");
        File directorio = new File(directorioTemporal, "con-contenido");
        assertTrue(fichero.createNewFile());
        assertTrue(directorio.mkdir());
        assertTrue(new File(directorio, "dentro.txt").createNewFile());

        assertTrue(OperacionesFile.borrar(fichero));
        assertFalse(fichero.exists());
        assertFalse(OperacionesFile.borrar(directorio));
        assertTrue(directorio.isDirectory());
    }

    @Test
    void rechazaArgumentosNulos() {
        assertThrows(IllegalArgumentException.class, () -> OperacionesFile.crearFichero(null));
        assertThrows(IllegalArgumentException.class, () -> OperacionesFile.crearDirectorio(null));
        assertThrows(IllegalArgumentException.class, () -> OperacionesFile.crearDirectorios(null));
        assertThrows(IllegalArgumentException.class, () -> OperacionesFile.renombrar(null, new File("destino")));
        assertThrows(IllegalArgumentException.class, () -> OperacionesFile.renombrar(new File("origen"), null));
        assertThrows(IllegalArgumentException.class, () -> OperacionesFile.borrar(null));
    }
}
