package es.dam.accesodatos.autoestudio.xml.dom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Consultas y generación XML con DOM")
class ParseoDomTest {

    private static final String XML =
            "<libros><libro id=\"1\">A</libro><libro id=\"2\">B</libro><libro>C</libro></libros>";

    @Test
    void cuentaElementosPorNombre() {
        assertEquals(3, ParseoDom.contarElementos(XML, "libro"));
        assertEquals(1, ParseoDom.contarElementos(XML, "libros"));
        assertEquals(0, ParseoDom.contarElementos(XML, "revista"));
    }

    @Test
    void obtieneElAtributoDelPrimerElementoCoincidente() {
        assertEquals("1", ParseoDom.obtenerAtributo(XML, "libro", "id"));
        assertEquals("", ParseoDom.obtenerAtributo(XML, "libro", "ausente"));
        assertEquals("", ParseoDom.obtenerAtributo(XML, "revista", "id"));
    }

    @Test
    void obtieneTextoDelPrimerElementoEnOrdenDocumental() {
        assertEquals("A", ParseoDom.textoPrimero(XML, "libro"));
        assertEquals("", ParseoDom.textoPrimero(XML, "revista"));
    }

    @Test
    void compruebaRaizYCuentaTodosLosNodosElemento() {
        assertTrue(ParseoDom.raizCoincide(XML, "libros"));
        assertFalse(ParseoDom.raizCoincide(XML, "libro"));
        assertEquals(4, ParseoDom.contarNodosElemento(XML));
    }

    @Test
    void creaUnDocumentoDomYTransformaElTextoEscapandoCaracteres() {
        String generado = ParseoDom.crearDocumento("libros", "libro", "Sol & Luna <mar");

        assertTrue(generado.contains("<libros"));
        assertTrue(generado.contains("<libro>"));
        assertTrue(generado.contains("Sol &amp; Luna &lt;mar"));
    }

    @Test
    void rechazaArgumentosNulosOVacios() {
        assertThrows(IllegalArgumentException.class, () -> ParseoDom.contarElementos(null, "libro"));
        assertThrows(IllegalArgumentException.class, () -> ParseoDom.contarElementos(XML, " "));
        assertThrows(IllegalArgumentException.class, () -> ParseoDom.obtenerAtributo(XML, "libro", null));
        assertThrows(IllegalArgumentException.class, () -> ParseoDom.textoPrimero(" ", "libro"));
        assertThrows(IllegalArgumentException.class, () -> ParseoDom.raizCoincide(XML, ""));
        assertThrows(IllegalArgumentException.class, () -> ParseoDom.contarNodosElemento(null));
        assertThrows(IllegalArgumentException.class, () -> ParseoDom.crearDocumento("raiz", "hijo", " "));
    }

    @Test
    void rechazaXmlMalFormadoYDeclaracionesDoctype() {
        assertThrows(RuntimeException.class, () -> ParseoDom.contarElementos("<libros>", "libro"));
        assertThrows(RuntimeException.class,
                () -> ParseoDom.contarElementos("<!DOCTYPE libros><libros/>", "libro"));
    }
}
