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

@DisplayName("Flujos de bytes: copiar y anexar")
class FlujosBytesTest {

    @TempDir
    File directorioTemporal;

    @Test
    void copiaSeisBytesConBuferDeCuatroSinBytesSobrantes() throws IOException {
        File origen = crearFichero("origen.bin", new byte[] { 1, 2, 3, 4, 5, 6 });
        File destino = new File(directorioTemporal, "destino.bin");

        long total = FlujosBytes.copiar(origen, destino, 4);

        assertEquals(6L, total, "Dos lecturas: 4 + 2 bytes. La tercera devuelve -1.");
        assertArrayEquals(new byte[] { 1, 2, 3, 4, 5, 6 }, leer(destino),
                "Si aparecen 8 bytes, la última vuelta escribió el búfer completo con write(buffer).");
    }

    @Test
    void copiaUnArchivoVacioSinEscribirNada() throws IOException {
        File origen = crearFichero("vacio.bin", new byte[0]);
        File destino = new File(directorioTemporal, "copia-vacia.bin");

        long total = FlujosBytes.copiar(origen, destino, 4);

        assertEquals(0L, total);
        assertTrue(destino.isFile(), "El destino debe existir aunque el origen esté vacío.");
        assertEquals(0L, destino.length());
    }

    @Test
    void copiaUnBloqueExacto() throws IOException {
        File origen = crearFichero("exacto.bin", new byte[] { 7, 8, 9, 10 });
        File destino = new File(directorioTemporal, "copia-exacta.bin");

        assertEquals(4L, FlujosBytes.copiar(origen, destino, 4));
        assertArrayEquals(new byte[] { 7, 8, 9, 10 }, leer(destino));
    }

    @Test
    void copiaUnArchivoMuchoMayorQueElBufer() throws IOException {
        byte[] contenido = new byte[1000];
        for (int i = 0; i < contenido.length; i++) {
            contenido[i] = (byte) i;
        }
        File origen = crearFichero("grande.bin", contenido);
        File destino = new File(directorioTemporal, "copia-grande.bin");

        assertEquals(1000L, FlujosBytes.copiar(origen, destino, 3));
        assertArrayEquals(contenido, leer(destino));
    }

    @Test
    void sobrescribeUnDestinoPrevioEnLugarDeAnexar() throws IOException {
        File origen = crearFichero("origen.bin", new byte[] { 1, 2, 3 });
        File destino = crearFichero("previo.bin", new byte[] { 99, 99, 99, 99, 99, 99, 99, 99 });

        assertEquals(3L, FlujosBytes.copiar(origen, destino, 4));
        assertArrayEquals(new byte[] { 1, 2, 3 }, leer(destino),
                "FileOutputStream sin append debe truncar el contenido anterior.");
    }

    @Test
    void copiarRechazaArgumentosInvalidos() throws IOException {
        File origen = crearFichero("origen.bin", new byte[] { 1, 2 });
        File destino = new File(directorioTemporal, "destino.bin");
        File inexistente = new File(directorioTemporal, "no-existe.bin");

        assertThrows(IllegalArgumentException.class, () -> FlujosBytes.copiar(null, destino, 4));
        assertThrows(IllegalArgumentException.class, () -> FlujosBytes.copiar(inexistente, destino, 4));
        assertThrows(IllegalArgumentException.class, () -> FlujosBytes.copiar(directorioTemporal, destino, 4));
        assertThrows(IllegalArgumentException.class, () -> FlujosBytes.copiar(origen, null, 4));
        assertThrows(IllegalArgumentException.class, () -> FlujosBytes.copiar(origen, destino, 0));
        assertThrows(IllegalArgumentException.class, () -> FlujosBytes.copiar(origen, destino, -1));
    }

    @Test
    void copiarRechazaQueElDestinoSeaElOrigenYNoLoDestruye() throws IOException {
        File origen = crearFichero("mismo.bin", new byte[] { 1, 2, 3 });
        File mismoConOtraRuta = new File(new File(directorioTemporal, "."), "mismo.bin");

        assertThrows(IllegalArgumentException.class, () -> FlujosBytes.copiar(origen, origen, 4));
        assertThrows(IllegalArgumentException.class, () -> FlujosBytes.copiar(origen, mismoConOtraRuta, 4));
        assertArrayEquals(new byte[] { 1, 2, 3 }, leer(origen),
                "Abrir el destino para escribir antes de validar habría vaciado el origen.");
    }

    @Test
    void anexaUnaPorcionDelArrayConOffYLen() throws IOException {
        File destino = crearFichero("anexo.bin", new byte[] { 9 });

        FlujosBytes.anexar(destino, new byte[] { 10, 20, 30, 40 }, 1, 2);

        assertArrayEquals(new byte[] { 9, 20, 30 }, leer(destino),
                "off=1 es el índice del array y len=2 la cantidad: se añaden 20 y 30.");
    }

    @Test
    void anexaUnBloqueCompletoAUnArchivoVacio() throws IOException {
        File destino = crearFichero("vacio.bin", new byte[0]);

        FlujosBytes.anexar(destino, new byte[] { 10, 20, 30, 40 }, 0, 4);

        assertArrayEquals(new byte[] { 10, 20, 30, 40 }, leer(destino));
    }

    @Test
    void anexarVariasVecesConservaLoAnterior() throws IOException {
        File destino = crearFichero("acumulado.bin", new byte[] { 1 });

        FlujosBytes.anexar(destino, new byte[] { 2, 3 }, 0, 2);
        FlujosBytes.anexar(destino, new byte[] { 4, 5, 6 }, 2, 1);
        FlujosBytes.anexar(destino, new byte[] { 7 }, 0, 0);

        assertArrayEquals(new byte[] { 1, 2, 3, 6 }, leer(destino));
    }

    @Test
    void anexarCreaElDestinoSiNoExiste() throws IOException {
        File destino = new File(directorioTemporal, "nuevo.bin");

        FlujosBytes.anexar(destino, new byte[] { 5, 6 }, 0, 2);

        assertArrayEquals(new byte[] { 5, 6 }, leer(destino));
    }

    @Test
    void anexarRechazaRangosFueraDelArraySinTocarElDestino() throws IOException {
        File destino = crearFichero("intacto.bin", new byte[] { 9 });
        File inexistente = new File(directorioTemporal, "no-creado.bin");
        byte[] datos = { 10, 20, 30, 40 };

        assertThrows(IndexOutOfBoundsException.class, () -> FlujosBytes.anexar(destino, datos, -1, 2));
        assertThrows(IndexOutOfBoundsException.class, () -> FlujosBytes.anexar(destino, datos, 1, -1));
        assertThrows(IndexOutOfBoundsException.class, () -> FlujosBytes.anexar(destino, datos, 3, 2));
        assertThrows(IndexOutOfBoundsException.class, () -> FlujosBytes.anexar(inexistente, datos, 0, 5));
        assertArrayEquals(new byte[] { 9 }, leer(destino));
        assertFalse(inexistente.exists(), "Valida off y len antes de abrir el flujo.");
    }

    @Test
    void anexarRechazaArgumentosNulos() throws IOException {
        File destino = crearFichero("destino.bin", new byte[0]);

        assertThrows(IllegalArgumentException.class, () -> FlujosBytes.anexar(null, new byte[] { 1 }, 0, 1));
        assertThrows(IllegalArgumentException.class, () -> FlujosBytes.anexar(destino, null, 0, 0));
    }

    @Test
    void copiaBytesCeroY255ConDistintasCapacidades() throws IOException {
        byte[] contenido = { 0, 1, 2, 3, 4, (byte) 255 };
        File origen = crearFichero("binario.bin", contenido);

        for (int capacidad : new int[] { 1, 3, 4 }) {
            File destino = crearFichero("binario-copia-" + capacidad + ".bin", new byte[20]);

            assertEquals(6L, FlujosBytes.copiar(origen, destino, capacidad));
            assertArrayEquals(contenido, leer(destino),
                    "La copia debe conservar 0 y 255 y sobrescribir el destino con cualquier búfer.");
        }
    }

    @Test
    void copiarPropagaErrorDeAperturaDelDestino() throws IOException {
        File origen = crearFichero("origen.bin", new byte[] { 1, 2 });

        assertThrows(IOException.class, () -> FlujosBytes.copiar(origen, directorioTemporal, 4),
                "Un directorio no puede abrirse como fichero de salida; no ocultes la IOException.");
    }

    private File crearFichero(String nombre, byte[] contenido) throws IOException {
        File fichero = new File(directorioTemporal, nombre);
        try (FileOutputStream salida = new FileOutputStream(fichero)) {
            salida.write(contenido);
        }
        return fichero;
    }

    private byte[] leer(File fichero) throws IOException {
        try (FileInputStream entrada = new FileInputStream(fichero)) {
            return entrada.readAllBytes();
        }
    }
}
