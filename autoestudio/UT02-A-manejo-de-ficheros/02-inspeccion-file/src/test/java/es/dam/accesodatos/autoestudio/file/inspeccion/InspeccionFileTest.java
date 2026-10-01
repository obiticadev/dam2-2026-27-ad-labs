package es.dam.accesodatos.autoestudio.file.inspeccion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@DisplayName("Inspección y metadatos con java.io.File")
class InspeccionFileTest {

    @TempDir
    File directorioTemporal;

    @Test
    void distingueUnaRutaExistenteDeUnaInexistente() throws IOException {
        File existente = crearFichero("existente.txt", new byte[0]);
        File inexistente = new File(directorioTemporal, "no-existe.txt");

        assertTrue(InspeccionFile.existe(existente));
        assertFalse(InspeccionFile.existe(inexistente));
    }

    @Test
    void distingueFicherosDirectoriosYRutasInexistentes() throws IOException {
        File fichero = crearFichero("datos.txt", new byte[0]);
        File directorio = new File(directorioTemporal, "carpeta");
        File inexistente = new File(directorioTemporal, "ausente");
        assertTrue(directorio.mkdir());

        assertTrue(InspeccionFile.esFichero(fichero));
        assertFalse(InspeccionFile.esDirectorio(fichero));
        assertTrue(InspeccionFile.esDirectorio(directorio));
        assertFalse(InspeccionFile.esFichero(directorio));
        assertFalse(InspeccionFile.esFichero(inexistente));
        assertFalse(InspeccionFile.esDirectorio(inexistente));
    }

    @Test
    void consultaPermisosDeUnaEntradaTemporal() throws IOException {
        File fichero = crearFichero("permisos.txt", new byte[0]);

        assertTrue(InspeccionFile.puedeLeer(fichero));
        assertTrue(InspeccionFile.puedeEscribir(fichero));
    }

    @Test
    void obtieneElTamanioEnBytes() throws IOException {
        File fichero = crearFichero("tamano.bin", new byte[] { 10, 20, 30, 40, 50 });
        File inexistente = new File(directorioTemporal, "ausente.bin");

        assertEquals(5L, InspeccionFile.tamanioEnBytes(fichero));
        assertEquals(0L, InspeccionFile.tamanioEnBytes(inexistente));
    }

    @Test
    void obtieneLaFechaDeModificacionDeUnaEntradaExistente() throws IOException {
        File fichero = crearFichero("fecha.txt", new byte[0]);

        assertTrue(InspeccionFile.ultimaModificacion(fichero) > 0L);
    }

    @Test
    void lasConsultasBooleanasDevuelvenFalseParaUnaRutaInexistente() {
        File inexistente = new File(directorioTemporal, "ausente.txt");

        assertFalse(InspeccionFile.existe(inexistente));
        assertFalse(InspeccionFile.esFichero(inexistente));
        assertFalse(InspeccionFile.esDirectorio(inexistente));
        assertFalse(InspeccionFile.puedeLeer(inexistente));
        assertFalse(InspeccionFile.puedeEscribir(inexistente));
        assertEquals(0L, InspeccionFile.ultimaModificacion(inexistente));
    }

    @Test
    void rechazaUnaReferenciaNulaEnTodasLasConsultas() {
        assertThrows(IllegalArgumentException.class, () -> InspeccionFile.existe(null));
        assertThrows(IllegalArgumentException.class, () -> InspeccionFile.esFichero(null));
        assertThrows(IllegalArgumentException.class, () -> InspeccionFile.esDirectorio(null));
        assertThrows(IllegalArgumentException.class, () -> InspeccionFile.puedeLeer(null));
        assertThrows(IllegalArgumentException.class, () -> InspeccionFile.puedeEscribir(null));
        assertThrows(IllegalArgumentException.class, () -> InspeccionFile.tamanioEnBytes(null));
        assertThrows(IllegalArgumentException.class, () -> InspeccionFile.ultimaModificacion(null));
    }

    private File crearFichero(String nombre, byte[] contenido) throws IOException {
        File fichero = new File(directorioTemporal, nombre);
        try (FileOutputStream salida = new FileOutputStream(fichero)) {
            salida.write(contenido);
        }
        return fichero;
    }
}
