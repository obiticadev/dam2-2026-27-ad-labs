package es.dam.accesodatos.autoestudio.xml.jaxb;

import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import java.io.StringReader;

class TallerJaxbTest {
    @TempDir File carpeta;

    @Test
    void anotacionesProducenWrapperElementosAtributoYOrden() throws Exception {
        StringWriter salida = new StringWriter();
        JAXBContext.newInstance(BibliotecaLibros.class).createMarshaller().marshal(ejemplo(), salida);
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(new InputSource(new StringReader(salida.toString())));
        assertEquals("libreria", doc.getDocumentElement().getTagName());
        assertEquals(1, doc.getElementsByTagName("ListaLibro").getLength());
        Element contenedor = (Element) doc.getElementsByTagName("ListaLibro").item(0);
        NodeList libros = contenedor.getElementsByTagName("Libro");
        assertEquals(2, libros.getLength());
        Element primero = (Element) libros.item(0);
        assertEquals("L1", primero.getAttribute("codigo"));
        List<String> campos = new ArrayList<>();
        for (Node n = primero.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == Node.ELEMENT_NODE) campos.add(n.getNodeName());
        }
        assertEquals(List.of("titulo", "anio"), campos);
    }
    @Test
    void guardaArchivoYLoCompruebaConDomIndependiente() throws Exception {
        File f = new File(carpeta, "libreria.xml");
        TallerJaxb.guardar(ejemplo(), f);
        assertTrue(f.isFile(), "Completa guardar(): debe crear el archivo XML");
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f);
        assertEquals(2, doc.getElementsByTagName("Libro").getLength());
        assertEquals("Sol & Luna", doc.getElementsByTagName("titulo").item(0).getTextContent());
        assertEquals("Barrio", doc.getElementsByTagName("lugar").item(0).getTextContent());
    }
    @Test
    void leeColeccionYPermiteModificarObjetosAntesDeGuardar() throws Exception {
        File f = new File(carpeta, "entrada.xml");
        JAXBContext.newInstance(BibliotecaLibros.class).createMarshaller().marshal(ejemplo(), f);
        BibliotecaLibros copia = TallerJaxb.leer(f);
        assertNotNull(copia);
        assertEquals("Biblioteca", copia.getNombre());
        assertEquals("Barrio", copia.getLugar());
        assertEquals(2, copia.getLibros().size());
        assertEquals("L1", copia.getLibros().get(0).getCodigo());
        assertEquals("Sol & Luna", copia.getLibros().get(0).getTitulo());
        assertEquals(2008, copia.getLibros().get(0).getAnio());
        copia.getLibros().get(0).setAnio(2026);
        File nuevo = new File(carpeta, "modificada.xml");
        TallerJaxb.guardar(copia, nuevo);
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(nuevo);
        assertEquals("2026", doc.getElementsByTagName("anio").item(0).getTextContent());
        Document original = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f);
        assertEquals("2008", original.getElementsByTagName("anio").item(0).getTextContent());
    }
    private BibliotecaLibros ejemplo() {
        BibliotecaLibros b = new BibliotecaLibros();
        b.setNombre("Biblioteca"); b.setLugar("Barrio");
        b.setLibros(new ArrayList<>(List.of(new FichaLibro("L1", "Sol & Luna", 2008),
                new FichaLibro("L2", "XML", 2020))));
        return b;
    }
}
