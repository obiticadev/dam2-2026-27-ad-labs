package es.dam.accesodatos.autoestudio.xml.sax;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Eventos y extracción XML con SAX")
class ParseoSaxTest {

    private static final String XML =
            "<libros><libro id=\"1\">A &amp; B</libro><libro id=\"2\">C</libro><libro>D</libro></libros>";

    @Test
    void extraeTextosEnOrdenYDecodificaEntidadesXml() {
        assertEquals(List.of("A & B", "C", "D"), ParseoSax.textosDe(XML, "libro"));
    }

    @Test
    void recuperaAtributosYUsaCadenaVaciaCuandoFaltan() {
        assertEquals(List.of("1", "2", ""), ParseoSax.atributosDe(XML, "libro", "id"));
        assertEquals(List.of("", "", ""), ParseoSax.atributosDe(XML, "libro", "ausente"));
    }

    @Test
    void cuentaEtiquetasEnUnaPasada() {
        assertEquals(3, ParseoSax.contarElementos(XML, "libro"));
        assertEquals(1, ParseoSax.contarElementos(XML, "libros"));
        assertEquals(0, ParseoSax.contarElementos(XML, "revista"));
    }

    @Test
    void rechazaArgumentosNulosOVacios() {
        assertThrows(IllegalArgumentException.class, () -> ParseoSax.textosDe(null, "libro"));
        assertThrows(IllegalArgumentException.class, () -> ParseoSax.textosDe(XML, " "));
        assertThrows(IllegalArgumentException.class, () -> ParseoSax.atributosDe(XML, "libro", null));
        assertThrows(IllegalArgumentException.class, () -> ParseoSax.contarElementos(XML, ""));
    }

    @Test
    void informaXmlMalFormadoComoFallo() {
        assertThrows(RuntimeException.class, () -> ParseoSax.textosDe("<libros>", "libro"));
    }
}
