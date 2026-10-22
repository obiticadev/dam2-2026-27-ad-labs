package es.dam.accesodatos.autoestudio.xml.dom;

import static org.junit.jupiter.api.Assertions.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

class TallerDomTest {
    private static final String XML = "<alumnos>\n"
            + "<alumno id=\"1\"><nombre>Ana</nombre><nota>8.5</nota></alumno>\n"
            + "<alumno id=\"10\"><nombre>José</nombre><nota>6.0</nota></alumno>\n</alumnos>";
    @TempDir File carpeta;

    @Test
    void cargaArchivoLocalConDeclaracionXml() throws Exception {
        File f = new File(carpeta, "alumnos.xml");
        try (Writer out = new FileWriter(f, StandardCharsets.UTF_8)) {
            out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + XML);
        }
        Document doc = TallerDom.cargar(f);
        assertNotNull(doc);
        assertEquals("alumnos", doc.getDocumentElement().getTagName());
        assertEquals(2, doc.getElementsByTagName("alumno").getLength());
    }
    @Test
    void extraeTodosLosNombresConTildes() throws Exception {
        assertEquals(List.of("Ana", "José"), TallerDom.nombres(parsear(XML)));
        assertEquals(List.of(), TallerDom.nombres(parsear("<alumnos/>")));
    }
    @Test
    void diferenciaHijosDeDescendientesYTextoDeIndentacion() throws Exception {
        Document doc = parsear(XML);
        assertEquals(2, TallerDom.contarHijosElemento(doc.getDocumentElement()));
        assertEquals(2, TallerDom.contarHijosElemento((Element) doc.getElementsByTagName("alumno").item(0)));
        assertEquals(0, TallerDom.contarHijosElemento((Element) doc.getElementsByTagName("nombre").item(0)));
    }
    @Test
    void modificaPorIdSinConfundirUnoConDiez() throws Exception {
        Document doc = parsear(XML);
        assertTrue(TallerDom.actualizarNota(doc, "1", 9));
        assertEquals("9.0", doc.getElementsByTagName("nota").item(0).getTextContent());
        assertEquals("6.0", doc.getElementsByTagName("nota").item(1).getTextContent());
        assertFalse(TallerDom.actualizarNota(doc, "99", 5));
    }
    @Test
    void agregaNodosYTextoSinConcatenarMarcas() throws Exception {
        Document doc = parsear(XML);
        TallerDom.agregarAlumno(doc, "3", "Sol & Luna <mar", 7.5);
        NodeList alumnos = doc.getElementsByTagName("alumno");
        assertEquals(3, alumnos.getLength());
        Element nuevo = (Element) alumnos.item(2);
        assertEquals("3", nuevo.getAttribute("id"));
        assertEquals("Sol & Luna <mar", nuevo.getElementsByTagName("nombre").item(0).getTextContent());
        assertEquals("7.5", nuevo.getElementsByTagName("nota").item(0).getTextContent());
    }
    @Test
    void eliminaElElementoCorrecto() throws Exception {
        Document doc = parsear(XML);
        assertTrue(TallerDom.eliminarAlumno(doc, "1"));
        assertFalse(TallerDom.eliminarAlumno(doc, "1"));
        assertEquals(1, doc.getElementsByTagName("alumno").getLength());
        assertEquals("10", ((Element) doc.getElementsByTagName("alumno").item(0)).getAttribute("id"));
    }
    @Test
    void guardaYReabreSinCompararEspaciosNiCabeceraExacta() throws Exception {
        Document doc = parsear(XML);
        doc.getElementsByTagName("nombre").item(0).setTextContent("Sol & Luna <mar");
        File salida = new File(carpeta, "resultado.xml");
        TallerDom.guardar(doc, salida);
        assertTrue(salida.isFile(), "Completa guardar(): debe crear el archivo de salida");
        Document reabierto = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(salida);
        assertEquals("Sol & Luna <mar", reabierto.getElementsByTagName("nombre").item(0).getTextContent());
        assertEquals(2, reabierto.getElementsByTagName("alumno").getLength());
    }
    private Document parsear(String xml) throws Exception {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(new InputSource(new StringReader(xml)));
    }
}
