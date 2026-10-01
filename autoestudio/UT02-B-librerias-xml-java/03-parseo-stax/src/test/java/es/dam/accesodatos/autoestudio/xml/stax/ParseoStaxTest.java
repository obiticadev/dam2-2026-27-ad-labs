package es.dam.accesodatos.autoestudio.xml.stax;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Lectura y escritura XML con StAX")
class ParseoStaxTest {

    private static final String XML = "<bookstore>"
            + "<book><title lang=\"en\">Everyday Italian</title></book>"
            + "<book><title lang=\"es\">Sol &amp; Luna</title></book>"
            + "<book><title>Learning XML</title></book>"
            + "</bookstore>";

    @Test
    void cursorExtraeTextoEnOrdenYDecodificaEntidades() {
        assertEquals(List.of("Everyday Italian", "Sol & Luna", "Learning XML"),
                ParseoStax.titulosConCursor(XML, "title"));
    }

    @Test
    void cursorLeeAtributosDelEventoActual() {
        assertEquals(List.of("en", "es", ""), ParseoStax.atributosConCursor(XML, "title", "lang"));
    }

    @Test
    void iteradorExtraeTextoEnOrden() {
        assertEquals(List.of("Everyday Italian", "Sol & Luna", "Learning XML"),
                ParseoStax.titulosConIterador(XML, "title"));
    }

    @Test
    void iteradorLeeAtributosDesdeStartElement() {
        assertEquals(List.of("en", "es", ""), ParseoStax.atributosConIterador(XML, "title", "lang"));
    }

    @Test
    void iteradorCuentaElementosYDevuelveCeroSiNoHayCoincidencias() {
        assertEquals(3, ParseoStax.contarElementosConIterador(XML, "title"));
        assertEquals(3, ParseoStax.contarElementosConIterador(XML, "book"));
        assertEquals(0, ParseoStax.contarElementosConIterador(XML, "magazine"));
    }

    @Test
    void cursorEscribeXMLQuePuedeVolverALeer() {
        String generado = ParseoStax.crearDocumentoConCursor("catalog", "item", "Sol & Luna");

        assertTrue(generado.contains("&amp;"));
        assertEquals(List.of("Sol & Luna"), ParseoStax.titulosConCursor(generado, "item"));
    }

    @Test
    void eventWriterEscribeXMLQuePuedeVolverALeer() {
        String generado = ParseoStax.crearDocumentoConIterador("catalog", "item", "A < B");

        assertTrue(generado.contains("&lt;"));
        assertEquals(List.of("A < B"), ParseoStax.titulosConIterador(generado, "item"));
    }

    @Test
    void lectoresRechazanEntradaInvalidaYXmlMalFormado() {
        assertThrows(IllegalArgumentException.class, () -> ParseoStax.titulosConCursor(null, "title"));
        assertThrows(IllegalArgumentException.class, () -> ParseoStax.titulosConIterador(XML, " "));
        assertThrows(IllegalArgumentException.class,
                () -> ParseoStax.atributosConCursor(XML, "title", ""));
        assertThrows(RuntimeException.class, () -> ParseoStax.contarElementosConIterador("<books>", "book"));
    }

    @Test
    void escritoresRechazanNombresOVariablesInvalidas() {
        assertThrows(IllegalArgumentException.class,
                () -> ParseoStax.crearDocumentoConCursor(" ", "item", "value"));
        assertThrows(IllegalArgumentException.class,
                () -> ParseoStax.crearDocumentoConIterador("root", "item", null));
    }
}
