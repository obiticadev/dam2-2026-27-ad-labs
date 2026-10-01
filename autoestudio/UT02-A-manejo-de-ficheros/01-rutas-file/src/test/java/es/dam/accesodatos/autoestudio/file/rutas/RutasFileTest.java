package es.dam.accesodatos.autoestudio.file.rutas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@DisplayName("Rutas y componentes de java.io.File")
class RutasFileTest {

    @TempDir
    File directorioTemporal;

    @Test
    void representaUnaRutaSinCrearElFichero() {
        File esperada = new File(new File(directorioTemporal, "no-creado"), "informe.txt");

        File resultado = RutasFile.desdeRuta(esperada.getPath());

        assertEquals(esperada, resultado);
        assertFalse(resultado.exists());
    }

    @Test
    void combinaUnDirectorioYUnNombre() {
        File esperada = new File(directorioTemporal, "informe.txt");

        File resultado = RutasFile.desdeDirectorioYNombre(directorioTemporal.getPath(), "informe.txt");

        assertEquals(esperada, resultado);
        assertFalse(resultado.exists());
    }

    @Test
    void combinaUnObjetoPadreConElNombreDelHijo() {
        File esperada = new File(directorioTemporal, "informe.txt");

        File resultado = RutasFile.desdeDirectorioPadre(directorioTemporal, "informe.txt");

        assertEquals(esperada, resultado);
        assertFalse(resultado.exists());
    }

    @Test
    void obtieneElNombreDeLaRutaAunqueNoExista() {
        File ruta = new File(directorioTemporal, "Informe.TXT");

        assertEquals("Informe.TXT", RutasFile.nombre(ruta));
    }

    @Test
    void devuelveLaCadenaDeRutaOriginal() {
        File ruta = new File("datos", ".." + File.separator + "informe.txt");

        assertEquals(ruta.getPath(), RutasFile.rutaRepresentada(ruta));
    }

    @Test
    void obtieneElPadreInmediatoOElResultadoNuloDeFile() {
        File rutaConPadre = new File(directorioTemporal, "informe.txt");
        File rutaSinPadre = new File("informe.txt");

        assertEquals(directorioTemporal, RutasFile.directorioPadre(rutaConPadre));
        assertNull(RutasFile.directorioPadre(rutaSinPadre));
    }

    @Test
    void rechazaArgumentosInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> RutasFile.desdeRuta(null));
        assertThrows(IllegalArgumentException.class, () -> RutasFile.desdeRuta("  "));
        assertThrows(IllegalArgumentException.class,
                () -> RutasFile.desdeDirectorioYNombre("datos", " "));
        assertThrows(IllegalArgumentException.class,
                () -> RutasFile.desdeDirectorioPadre(null, "informe.txt"));
        assertThrows(IllegalArgumentException.class, () -> RutasFile.nombre(null));
        assertThrows(IllegalArgumentException.class, () -> RutasFile.rutaRepresentada(null));
        assertThrows(IllegalArgumentException.class, () -> RutasFile.directorioPadre(null));
    }
}
