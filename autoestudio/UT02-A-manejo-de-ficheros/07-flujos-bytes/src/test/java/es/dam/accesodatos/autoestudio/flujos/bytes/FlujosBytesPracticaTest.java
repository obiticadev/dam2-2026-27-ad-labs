package es.dam.accesodatos.autoestudio.flujos.bytes;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@DisplayName("Flujos de bytes: práctica progresiva (ejercicios 3 a 7)")
class FlujosBytesPracticaTest {

    @TempDir
    File directorioTemporal;

    @Test
    void guardarCreaUnFicheroConTodoElArray() throws IOException {
        File destino = new File(directorioTemporal, "nuevo.bin");

        FlujosBytes.guardar(destino, new byte[] { 10, 20, 30 });

        assertTrue(destino.isFile(), "guardar debe crear el fichero.");
        assertArrayEquals(new byte[] { 10, 20, 30 }, leer(destino));
    }

    @Test
    void guardarSobrescribeSinDejarRestos() throws IOException {
        File destino = crearFichero("previo.bin", new byte[] { 99, 99, 99, 99 });

        FlujosBytes.guardar(destino, new byte[] { 7 });

        assertArrayEquals(new byte[] { 7 }, leer(destino), "No debe anexar ni conservar bytes antiguos.");
    }

    @Test
    void guardarUnArrayVacioCreaOVaciaElFichero() throws IOException {
        File nuevo = new File(directorioTemporal, "nuevo.bin");
        File previo = crearFichero("previo.bin", new byte[] { 1, 2 });

        FlujosBytes.guardar(nuevo, new byte[0]);
        FlujosBytes.guardar(previo, new byte[0]);

        assertTrue(nuevo.isFile());
        assertArrayEquals(new byte[0], leer(nuevo));
        assertArrayEquals(new byte[0], leer(previo));
    }

    @Test
    void guardarRechazaNulosAntesDeTocarElDestino() throws IOException {
        File previo = crearFichero("previo.bin", new byte[] { 9 });
        File nuevo = new File(directorioTemporal, "nuevo.bin");

        assertThrows(IllegalArgumentException.class, () -> FlujosBytes.guardar(null, new byte[] { 1 }));
        assertThrows(IllegalArgumentException.class, () -> FlujosBytes.guardar(previo, null));
        assertThrows(IllegalArgumentException.class, () -> FlujosBytes.guardar(nuevo, null));
        assertArrayEquals(new byte[] { 9 }, leer(previo));
        assertFalse(nuevo.exists());
    }

    @Test
    void contarBytesSumaLaUltimaLecturaParcial() throws IOException {
        File origen = crearFichero("origen.bin", new byte[] { 1, 2, 3, 4, 5, 6 });

        assertEquals(6L, FlujosBytes.contarBytes(origen, 4), "Se suman 4 + 2, no dos bloques completos.");
        assertEquals(6L, FlujosBytes.contarBytes(origen, 1));
        assertArrayEquals(new byte[] { 1, 2, 3, 4, 5, 6 }, leer(origen));
    }

    @Test
    void contarBytesAceptaVacioYBloqueExacto() throws IOException {
        File vacio = crearFichero("vacio.bin", new byte[0]);
        File exacto = crearFichero("exacto.bin", new byte[] { 0, 1, 2, 3 });

        assertEquals(0L, FlujosBytes.contarBytes(vacio, 4));
        assertEquals(4L, FlujosBytes.contarBytes(exacto, 4), "Un byte de valor 0 también cuenta.");
    }

    @Test
    void contarBytesRechazaArgumentosInvalidos() throws IOException {
        File origen = crearFichero("origen.bin", new byte[] { 1 });
        File inexistente = new File(directorioTemporal, "no-existe.bin");

        assertThrows(IllegalArgumentException.class, () -> FlujosBytes.contarBytes(null, 4));
        assertThrows(IllegalArgumentException.class, () -> FlujosBytes.contarBytes(inexistente, 4));
        assertThrows(IllegalArgumentException.class, () -> FlujosBytes.contarBytes(directorioTemporal, 4));
        assertThrows(IllegalArgumentException.class, () -> FlujosBytes.contarBytes(origen, 0));
        assertThrows(IllegalArgumentException.class, () -> FlujosBytes.contarBytes(origen, -1));
    }

    @Test
    void contarAparicionesNoCuentaRestosDelBufer() throws IOException {
        File origen = crearFichero("origen.bin", new byte[] { 7, 2, 7, 4, 5 });

        assertEquals(2L, FlujosBytes.contarApariciones(origen, (byte) 7, 4),
                "En la última vuelta solo el índice 0 contiene un byte nuevo.");
        assertArrayEquals(new byte[] { 7, 2, 7, 4, 5 }, leer(origen));
    }

    @Test
    void contarAparicionesAceptaVacioAusenciasYCeros() throws IOException {
        File vacio = crearFichero("vacio.bin", new byte[0]);
        File origen = crearFichero("origen.bin", new byte[] { 0, 2, 0, 0 });

        assertEquals(0L, FlujosBytes.contarApariciones(vacio, (byte) 0, 4));
        assertEquals(0L, FlujosBytes.contarApariciones(origen, (byte) 9, 3));
        assertEquals(3L, FlujosBytes.contarApariciones(origen, (byte) 0, 3));
    }

    @Test
    void contarAparicionesFuncionaConBuferDeUnoYBloqueExacto() throws IOException {
        File origen = crearFichero("origen.bin", new byte[] { 7, 7, 7, 7 });

        assertEquals(4L, FlujosBytes.contarApariciones(origen, (byte) 7, 1));
        assertEquals(4L, FlujosBytes.contarApariciones(origen, (byte) 7, 4));
    }

    @Test
    void contarAparicionesRechazaArgumentosInvalidos() throws IOException {
        File origen = crearFichero("origen.bin", new byte[] { 1 });
        File inexistente = new File(directorioTemporal, "no-existe.bin");

        assertThrows(IllegalArgumentException.class, () -> FlujosBytes.contarApariciones(null, (byte) 1, 4));
        assertThrows(IllegalArgumentException.class, () -> FlujosBytes.contarApariciones(inexistente, (byte) 1, 4));
        assertThrows(IllegalArgumentException.class, () -> FlujosBytes.contarApariciones(directorioTemporal, (byte) 1, 4));
        assertThrows(IllegalArgumentException.class, () -> FlujosBytes.contarApariciones(origen, (byte) 1, 0));
        assertThrows(IllegalArgumentException.class, () -> FlujosBytes.contarApariciones(origen, (byte) 1, -1));
    }

    @Test
    void anexarArchivoConservaLoAnteriorYEscribeLaParteValida() throws IOException {
        File origen = crearFichero("origen.bin", new byte[] { 1, 2, 3, 4, 5, 6 });
        File destino = crearFichero("destino.bin", new byte[] { 9 });

        assertEquals(6L, FlujosBytes.anexarArchivo(origen, destino, 4), "No se cuenta el 9 que ya existía.");
        assertArrayEquals(new byte[] { 9, 1, 2, 3, 4, 5, 6 }, leer(destino));
        assertArrayEquals(new byte[] { 1, 2, 3, 4, 5, 6 }, leer(origen));
    }

    @Test
    void anexarArchivoCreaDestinoYPermiteAnexarVariasVeces() throws IOException {
        File origen = crearFichero("origen.bin", new byte[] { 1, 2 });
        File destino = new File(directorioTemporal, "destino.bin");

        assertEquals(2L, FlujosBytes.anexarArchivo(origen, destino, 2));
        assertEquals(2L, FlujosBytes.anexarArchivo(origen, destino, 1));
        assertArrayEquals(new byte[] { 1, 2, 1, 2 }, leer(destino));
    }

    @Test
    void anexarArchivoVacioCreaDestinoOConservaElAnterior() throws IOException {
        File origen = crearFichero("vacio.bin", new byte[0]);
        File nuevo = new File(directorioTemporal, "nuevo.bin");
        File previo = crearFichero("previo.bin", new byte[] { 9 });

        assertEquals(0L, FlujosBytes.anexarArchivo(origen, nuevo, 4));
        assertTrue(nuevo.isFile());
        assertArrayEquals(new byte[0], leer(nuevo));
        assertEquals(0L, FlujosBytes.anexarArchivo(origen, previo, 4));
        assertArrayEquals(new byte[] { 9 }, leer(previo));
    }

    @Test
    void anexarArchivoRechazaElMismoFicheroSinModificarlo() throws IOException {
        File origen = crearFichero("origen.bin", new byte[] { 1, 2 });
        File otraRuta = new File(new File(directorioTemporal, "."), "origen.bin");

        assertThrows(IllegalArgumentException.class, () -> FlujosBytes.anexarArchivo(origen, origen, 4));
        assertThrows(IllegalArgumentException.class, () -> FlujosBytes.anexarArchivo(origen, otraRuta, 4));
        assertArrayEquals(new byte[] { 1, 2 }, leer(origen));
    }

    @Test
    void anexarArchivoRechazaArgumentosInvalidosSinCrearDestino() throws IOException {
        File origen = crearFichero("origen.bin", new byte[] { 1 });
        File destino = new File(directorioTemporal, "destino.bin");
        File inexistente = new File(directorioTemporal, "no-existe.bin");

        assertThrows(IllegalArgumentException.class, () -> FlujosBytes.anexarArchivo(null, destino, 4));
        assertThrows(IllegalArgumentException.class, () -> FlujosBytes.anexarArchivo(inexistente, destino, 4));
        assertThrows(IllegalArgumentException.class, () -> FlujosBytes.anexarArchivo(directorioTemporal, destino, 4));
        assertThrows(IllegalArgumentException.class, () -> FlujosBytes.anexarArchivo(origen, null, 4));
        assertThrows(IllegalArgumentException.class, () -> FlujosBytes.anexarArchivo(origen, destino, 0));
        assertThrows(IllegalArgumentException.class, () -> FlujosBytes.anexarArchivo(origen, destino, -1));
        assertFalse(destino.exists());
    }

    @Test
    void copiarSustituyendoTransformaSoloLosBytesValidos() throws IOException {
        File origen = crearFichero("origen.bin", new byte[] { 7, 2, 7, 4, 5 });
        File destino = crearFichero("destino.bin", new byte[] { 99, 99, 99, 99, 99, 99, 99 });

        assertEquals(5L, FlujosBytes.copiarSustituyendo(origen, destino, (byte) 7, (byte) 9, 4),
                "Se devuelven los 5 bytes escritos, no las 2 sustituciones.");
        assertArrayEquals(new byte[] { 9, 2, 9, 4, 5 }, leer(destino));
        assertArrayEquals(new byte[] { 7, 2, 7, 4, 5 }, leer(origen), "El origen no se modifica.");
    }

    @Test
    void copiarSustituyendoSinCoincidenciasOConElMismoByteCopiaIgual() throws IOException {
        File origen = crearFichero("origen.bin", new byte[] { 1, 2, 1 });
        File destino = new File(directorioTemporal, "destino.bin");

        assertEquals(3L, FlujosBytes.copiarSustituyendo(origen, destino, (byte) 8, (byte) 9, 1));
        assertArrayEquals(new byte[] { 1, 2, 1 }, leer(destino));
        assertEquals(3L, FlujosBytes.copiarSustituyendo(origen, destino, (byte) 1, (byte) 1, 3));
        assertArrayEquals(new byte[] { 1, 2, 1 }, leer(destino));
    }

    @Test
    void copiarSustituyendoVacioCreaDestinoOVaciaElAnterior() throws IOException {
        File origen = crearFichero("vacio.bin", new byte[0]);
        File nuevo = new File(directorioTemporal, "nuevo.bin");
        File previo = crearFichero("previo.bin", new byte[] { 9 });

        assertEquals(0L, FlujosBytes.copiarSustituyendo(origen, nuevo, (byte) 1, (byte) 2, 4));
        assertTrue(nuevo.isFile());
        assertArrayEquals(new byte[0], leer(nuevo));
        assertEquals(0L, FlujosBytes.copiarSustituyendo(origen, previo, (byte) 1, (byte) 2, 4));
        assertArrayEquals(new byte[0], leer(previo));
    }

    @Test
    void copiarSustituyendoRechazaElMismoFicheroSinModificarlo() throws IOException {
        File origen = crearFichero("origen.bin", new byte[] { 1, 2 });
        File otraRuta = new File(new File(directorioTemporal, "."), "origen.bin");

        assertThrows(IllegalArgumentException.class,
                () -> FlujosBytes.copiarSustituyendo(origen, origen, (byte) 1, (byte) 9, 4));
        assertThrows(IllegalArgumentException.class,
                () -> FlujosBytes.copiarSustituyendo(origen, otraRuta, (byte) 1, (byte) 9, 4));
        assertArrayEquals(new byte[] { 1, 2 }, leer(origen));
    }

    @Test
    void copiarSustituyendoRechazaArgumentosInvalidosSinTocarDestino() throws IOException {
        File origen = crearFichero("origen.bin", new byte[] { 1 });
        File destino = crearFichero("destino.bin", new byte[] { 9 });
        File inexistente = new File(directorioTemporal, "no-existe.bin");

        assertThrows(IllegalArgumentException.class,
                () -> FlujosBytes.copiarSustituyendo(null, destino, (byte) 1, (byte) 2, 4));
        assertThrows(IllegalArgumentException.class,
                () -> FlujosBytes.copiarSustituyendo(inexistente, destino, (byte) 1, (byte) 2, 4));
        assertThrows(IllegalArgumentException.class,
                () -> FlujosBytes.copiarSustituyendo(directorioTemporal, destino, (byte) 1, (byte) 2, 4));
        assertThrows(IllegalArgumentException.class,
                () -> FlujosBytes.copiarSustituyendo(origen, null, (byte) 1, (byte) 2, 4));
        assertThrows(IllegalArgumentException.class,
                () -> FlujosBytes.copiarSustituyendo(origen, destino, (byte) 1, (byte) 2, 0));
        assertThrows(IllegalArgumentException.class,
                () -> FlujosBytes.copiarSustituyendo(origen, destino, (byte) 1, (byte) 2, -1));
        assertArrayEquals(new byte[] { 9 }, leer(destino));
    }

    private File crearFichero(String nombre, byte[] datos) throws IOException {
        File fichero = new File(directorioTemporal, nombre);
        try (FileOutputStream salida = new FileOutputStream(fichero)) {
            salida.write(datos);
        }
        return fichero;
    }

    private byte[] leer(File fichero) throws IOException {
        try (FileInputStream entrada = new FileInputStream(fichero)) {
            return entrada.readAllBytes();
        }
    }
}
