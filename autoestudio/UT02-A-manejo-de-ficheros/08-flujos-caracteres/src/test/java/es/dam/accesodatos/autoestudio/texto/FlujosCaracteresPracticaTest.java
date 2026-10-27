package es.dam.accesodatos.autoestudio.texto;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FlujosCaracteresPracticaTest {
    @TempDir File carpeta;

    @Test
    void copiarRespetaLecturaParcialSaltosYTildes() throws IOException {
        String texto = "Á\r\n\nJosé🙂!";
        StringWriter destino = new StringWriter();
        assertEquals(texto.length(), FlujosCaracteresPractica.copiar(new StringReader(texto), destino, 4));
        assertEquals(texto, destino.toString());
    }

    @Test
    void copiarAceptaVacioYBloquesDeUnCaracter() throws IOException {
        StringWriter destino = new StringWriter();
        assertEquals(0, FlujosCaracteresPractica.copiar(new StringReader(""), destino, 3));
        assertEquals("", destino.toString());
        assertEquals(3, FlujosCaracteresPractica.copiar(new StringReader("abc"), destino, 1));
        assertEquals("abc", destino.toString());
    }

    @Test
    void copiarNoEsDuenioDeLosRecursos() throws IOException {
        class Lector extends StringReader {
            boolean cerrado;
            Lector() { super("hola"); }
            @Override public void close() { cerrado = true; super.close(); }
        }
        class Escritor extends StringWriter {
            boolean cerrado;
            boolean volcado;
            @Override public void close() { cerrado = true; }
            @Override public void flush() { volcado = true; }
        }
        Lector origen = new Lector();
        Escritor destino = new Escritor();
        FlujosCaracteresPractica.copiar(origen, destino, 2);
        assertEquals("hola", destino.toString());
        assertFalse(origen.cerrado, "El método no ha abierto el lector: no debe cerrarlo.");
        assertFalse(destino.cerrado);
        assertFalse(destino.volcado, "El llamador decide cuándo hacer flush.");
    }

    @Test
    void copiarRechazaBufferInvalidoYPropagaErrores() {
        StringWriter destino = new StringWriter();
        assertThrows(IllegalArgumentException.class,
                () -> FlujosCaracteresPractica.copiar(new StringReader("hola"), destino, 0));
        assertThrows(IllegalArgumentException.class,
                () -> FlujosCaracteresPractica.copiar(new StringReader("hola"), destino, -2));
        assertEquals("", destino.toString());
        Reader roto = new Reader() {
            @Override public int read(char[] b, int off, int len) throws IOException {
                throw new IOException("Lectura fallida");
            }
            @Override public void close() { }
        };
        assertThrows(IOException.class, () -> FlujosCaracteresPractica.copiar(roto, destino, 2));
    }

    @Test
    void numerarCuentaVaciasYUltimaSinSalto() throws IOException {
        File origen = crear("origen.txt", "Ángela\r\n\r\nJosé");
        File destino = crear("destino.txt", "contenido antiguo que debe desaparecer");
        assertEquals(3, FlujosCaracteresPractica.numerarLineas(origen, destino));
        assertEquals(lineas("1: Ángela", "2: ", "3: José"), leer(destino));
        assertEquals("Ángela\r\n\r\nJosé", leer(origen));
    }

    @Test
    void numerarVacioTruncaDestino() throws IOException {
        File origen = crear("vacio.txt", "");
        File destino = crear("destino.txt", "anterior");
        assertEquals(0, FlujosCaracteresPractica.numerarLineas(origen, destino));
        assertEquals("", leer(destino));
    }

    @Test
    void filtrarDistingueComentarDeModificarLaLinea() throws IOException {
        File origen = crear("config.txt", "# título\n  # comentario\n\n \t\n  nombre=José  \ncolor=#fff\nclave=valor");
        File destino = new File(carpeta, "limpio.txt");
        assertEquals(3, FlujosCaracteresPractica.filtrarConfiguracion(origen, destino));
        assertEquals(lineas("  nombre=José  ", "color=#fff", "clave=valor"), leer(destino));
    }

    @Test
    void filtrarSinLineasUtilesDejaDestinoVacio() throws IOException {
        File destino = crear("destino.txt", "viejo");
        assertEquals(0, FlujosCaracteresPractica.filtrarConfiguracion(
                crear("comentarios.txt", "# solo comentarios\n \n"), destino));
        assertEquals("", leer(destino));
        assertEquals(0, FlujosCaracteresPractica.filtrarConfiguracion(crear("vacio.txt", ""), destino));
    }

    @Test
    void compactarRecuerdaElEstadoYLoReiniciaConEspacios() throws IOException {
        File origen = crear("origen.txt", "\n\nÁ\n\n\n \n\n\nB\n\n\n");
        File destino = crear("destino.txt", "viejo");
        assertEquals(4, FlujosCaracteresPractica.compactarVacias(origen, destino));
        assertEquals(lineas("", "Á", "", " ", "", "B", ""), leer(destino));
    }

    @Test
    void compactarDistingueArchivoVacioDeUnaLineaVacia() throws IOException {
        File destino = crear("destino.txt", "viejo");
        assertEquals(0, FlujosCaracteresPractica.compactarVacias(crear("vacio.txt", ""), destino));
        assertEquals("", leer(destino));
        assertEquals(0, FlujosCaracteresPractica.compactarVacias(crear("una.txt", "\n"), destino));
        assertEquals(lineas(""), leer(destino));
        assertEquals(2, FlujosCaracteresPractica.compactarVacias(crear("tres.txt", "\n\n\n"), destino));
        assertEquals(lineas(""), leer(destino));
    }

    @Test
    void intercalarContinuaSiBTieneMasLineas() throws IOException {
        File a = crear("a.txt", "Á\n\nC");
        File b = crear("b.txt", "uno\ndos\ntres\ncuatro\ncinco");
        File destino = crear("destino.txt", "contenido viejo");
        assertEquals(8, FlujosCaracteresPractica.intercalar(a, b, destino));
        assertEquals(lineas("Á", "uno", "", "dos", "C", "tres", "cuatro", "cinco"), leer(destino));
    }

    @Test
    void intercalarContinuaSiATieneMasLineas() throws IOException {
        File a = crear("a.txt", "a1\na2\na3");
        File b = crear("b.txt", "b1");
        File destino = new File(carpeta, "destino.txt");
        assertEquals(4, FlujosCaracteresPractica.intercalar(a, b, destino));
        assertEquals(lineas("a1", "b1", "a2", "a3"), leer(destino));
    }

    @Test
    void intercalarAceptaUnoOAmbosVacios() throws IOException {
        File vacio = crear("vacio.txt", "");
        File otro = crear("otro.txt", "José");
        File destino = crear("destino.txt", "viejo");
        assertEquals(1, FlujosCaracteresPractica.intercalar(vacio, otro, destino));
        assertEquals(lineas("José"), leer(destino));
        assertEquals(1, FlujosCaracteresPractica.intercalar(otro, vacio, destino));
        assertEquals(lineas("José"), leer(destino));
        assertEquals(0, FlujosCaracteresPractica.intercalar(vacio, vacio, destino));
        assertEquals("", leer(destino));
    }

    @Test
    void operacionesConArchivosPropaganEntradaAusente() {
        File ausente = new File(carpeta, "ausente.txt");
        File destino = new File(carpeta, "destino.txt");
        assertThrows(IOException.class, () -> FlujosCaracteresPractica.numerarLineas(ausente, destino));
        assertThrows(IOException.class, () -> FlujosCaracteresPractica.filtrarConfiguracion(ausente, destino));
        assertThrows(IOException.class, () -> FlujosCaracteresPractica.compactarVacias(ausente, destino));
        assertThrows(IOException.class, () -> FlujosCaracteresPractica.intercalar(ausente, ausente, destino));
    }

    private File crear(String nombre, String texto) throws IOException {
        File archivo = new File(carpeta, nombre);
        try (Writer writer = new FileWriter(archivo, StandardCharsets.UTF_8)) {
            writer.write(texto);
        }
        return archivo;
    }

    private String leer(File archivo) throws IOException {
        StringBuilder texto = new StringBuilder();
        try (Reader reader = new FileReader(archivo, StandardCharsets.UTF_8)) {
            int c;
            while ((c = reader.read()) != -1) {
                texto.append((char) c);
            }
        }
        return texto.toString();
    }

    private String lineas(String... valores) {
        return String.join(System.lineSeparator(), valores) + System.lineSeparator();
    }
}
