package es.dam.accesodatos.autoestudio.xml.sax;

import static org.junit.jupiter.api.Assertions.*;
import java.io.StringReader;
import java.util.List;
import javax.xml.parsers.SAXParserFactory;
import org.junit.jupiter.api.Test;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.AttributesImpl;

class ManejadorAlumnosTest {
    @Test
    void acumulaFragmentosRespetandoStartYLength() {
        ManejadorAlumnos h = new ManejadorAlumnos();
        h.startDocument();
        h.characters("\n  ".toCharArray(), 0, 3);
        h.startElement("", "", "nombre", new AttributesImpl());
        h.characters("xxAna yy".toCharArray(), 2, 4);
        h.characters("María".toCharArray(), 0, 5);
        assertTrue(h.getNombres().isEmpty(), "El nombre se guarda al cerrar la etiqueta");
        h.endElement("", "", "nombre");
        h.characters("\n".toCharArray(), 0, 1);
        assertEquals(List.of("Ana María"), h.getNombres());
    }
    @Test
    void extraeAtributosYTextoConElParserRealYReiniciaElEstado() throws Exception {
        ManejadorAlumnos h = new ManejadorAlumnos();
        String xml = "<alumnos><alumno id=\"1\"><nombre>Ana &amp; José</nombre><nota>8.5</nota></alumno>"
                + "<alumno id=\"2\"><nombre>Luis</nombre><nota>6.0</nota></alumno></alumnos>";
        for (int intento = 0; intento < 2; intento++) {
            SAXParserFactory.newInstance().newSAXParser().parse(new InputSource(new StringReader(xml)), h);
            assertEquals(List.of("1", "2"), h.getIds());
            assertEquals(List.of("Ana & José", "Luis"), h.getNombres());
        }
    }
}
