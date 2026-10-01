package es.dam.accesodatos.autoestudio.file.catalogador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import es.dam.accesodatos.autoestudio.file.catalogador.CatalogadorArchivos.EntradaCatalogo;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@DisplayName("Boss final: catalogador de archivos")
class CatalogadorArchivosTest {

    @TempDir
    File directorioTemporal;

    @Test
    void preparaUnDirectorioHijoYEsIdempotente() throws IOException {
        File preparado = CatalogadorArchivos.prepararDirectorio(directorioTemporal, "catalogo");

        assertNotNull(preparado, "Debe devolver la ruta preparada para que pueda consultarse.");
        assertTrue(preparado.isDirectory());
        assertEquals(new File(directorioTemporal, "catalogo"), preparado);
        assertEquals(preparado, CatalogadorArchivos.prepararDirectorio(directorioTemporal, "catalogo"));
    }

    @Test
    void rechazaPadresONombresQueNoSonValidos() throws IOException {
        File archivoPadre = crearFichero(directorioTemporal, "padre.txt", new byte[0]);

        assertThrows(IllegalArgumentException.class,
                () -> CatalogadorArchivos.prepararDirectorio(null, "catalogo"));
        assertThrows(IllegalArgumentException.class,
                () -> CatalogadorArchivos.prepararDirectorio(archivoPadre, "catalogo"));
        assertThrows(IllegalArgumentException.class,
                () -> CatalogadorArchivos.prepararDirectorio(directorioTemporal, " "));
        assertThrows(IllegalArgumentException.class,
                () -> CatalogadorArchivos.prepararDirectorio(directorioTemporal, ".."));
        assertThrows(IllegalArgumentException.class,
                () -> CatalogadorArchivos.prepararDirectorio(directorioTemporal, "sub/cat"));
        assertThrows(IllegalArgumentException.class,
                () -> CatalogadorArchivos.prepararDirectorio(directorioTemporal, "sub\\cat"));
    }

    @Test
    void noReemplazaUnFicheroQueColisionaConElDirectorioSolicitado() throws IOException {
        crearFichero(directorioTemporal, "catalogo", new byte[] { 1 });

        assertThrows(IllegalArgumentException.class,
                () -> CatalogadorArchivos.prepararDirectorio(directorioTemporal, "catalogo"));
        assertTrue(new File(directorioTemporal, "catalogo").isFile());
    }

    @Test
    void catalogaSoloFicherosDeLaExtensionYConSusMetadatos() throws IOException {
        File carpeta = new File(directorioTemporal, "datos");
        assertTrue(carpeta.mkdir());
        File zeta = crearFichero(carpeta, "zeta.txt", new byte[] { 1, 2, 3, 4 });
        File alfa = crearFichero(carpeta, "alfa.TXT", new byte[] { 5, 6 });
        crearFichero(carpeta, "ignorado.csv", new byte[] { 7 });
        File carpetaConSufijo = new File(carpeta, "subdirectorio.txt");
        assertTrue(carpetaConSufijo.mkdir());

        List<EntradaCatalogo> catalogo = CatalogadorArchivos.catalogar(carpeta, ".txt");

        assertNotNull(catalogo, "catalogar() debe devolver una lista, incluso antes de comprobar sus entradas.");
        assertEquals(2, catalogo.size());
        assertEquals("alfa.TXT", catalogo.get(0).nombre());
        assertEquals(alfa.getPath(), catalogo.get(0).ruta());
        assertEquals(2L, catalogo.get(0).tamanioEnBytes());
        assertTrue(catalogo.get(0).ultimaModificacion() > 0L);
        assertEquals("zeta.txt", catalogo.get(1).nombre());
        assertEquals(zeta.getPath(), catalogo.get(1).ruta());
        assertEquals(4L, catalogo.get(1).tamanioEnBytes());
        assertFalse(catalogo.stream().anyMatch(e -> e.nombre().equals("subdirectorio.txt")));
    }

    @Test
    void catalogoVacioEsUnaListaInmutable() throws IOException {
        File carpeta = new File(directorioTemporal, "vacia");
        assertTrue(carpeta.mkdir());

        List<EntradaCatalogo> catalogo = CatalogadorArchivos.catalogar(carpeta, ".txt");

        assertNotNull(catalogo, "Un catálogo sin coincidencias debe ser una lista vacía, no null.");
        assertTrue(catalogo.isEmpty());
        assertThrows(UnsupportedOperationException.class,
                () -> catalogo.add(new EntradaCatalogo("x.txt", "x.txt", 0L, 0L)));
    }

    @Test
    void rechazaUnDirectorioOUnaExtensionInvalidos() throws IOException {
        File fichero = crearFichero(directorioTemporal, "fichero.txt", new byte[0]);

        assertThrows(IllegalArgumentException.class, () -> CatalogadorArchivos.catalogar(null, ".txt"));
        assertThrows(IllegalArgumentException.class, () -> CatalogadorArchivos.catalogar(fichero, ".txt"));
        assertThrows(IllegalArgumentException.class, () -> CatalogadorArchivos.catalogar(directorioTemporal, "txt"));
    }

    private File crearFichero(File directorio, String nombre, byte[] contenido) throws IOException {
        File fichero = new File(directorio, nombre);
        try (FileOutputStream salida = new FileOutputStream(fichero)) {
            salida.write(contenido);
        }
        return fichero;
    }
}
