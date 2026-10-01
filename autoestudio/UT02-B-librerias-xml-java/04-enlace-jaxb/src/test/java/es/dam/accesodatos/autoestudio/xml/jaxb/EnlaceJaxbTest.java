package es.dam.accesodatos.autoestudio.xml.jaxb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Binding objeto-XML con JAXB 2")
class EnlaceJaxbTest {

    @Test
    void serializaConRaizAtributoYElementos() {
        String xml = EnlaceJaxb.aXml(libroDeEjemplo());

        assertNotNull(xml);
        assertTrue(xml.contains("<libro"));
        assertTrue(xml.contains("isbn=\"978-84\""));
        assertTrue(xml.contains("<titulo>Clean Code</titulo>"));
        assertTrue(xml.contains("<anio>2008</anio>"));
    }

    @Test
    void roundTripPreservaTodosLosDatosDelModelo() {
        Libro original = libroDeEjemplo();
        String xml = EnlaceJaxb.aXml(original);
        assertNotNull(xml, "aXml() debe producir el documento usado por el unmarshalling.");

        Libro reconstruido = EnlaceJaxb.desdeXml(xml);
        assertNotNull(reconstruido, "desdeXml() debe devolver el modelo reconstruido.");

        assertEquals(original.getIsbn(), reconstruido.getIsbn());
        assertEquals(original.getTitulo(), reconstruido.getTitulo());
        assertEquals(original.getAnio(), reconstruido.getAnio());
    }

    @Test
    void rechazaEntradasInvalidasYXmlMalFormado() {
        assertThrows(IllegalArgumentException.class, () -> EnlaceJaxb.aXml(null));
        assertThrows(IllegalArgumentException.class, () -> EnlaceJaxb.desdeXml(null));
        assertThrows(IllegalArgumentException.class, () -> EnlaceJaxb.desdeXml(" "));
        assertThrows(RuntimeException.class, () -> EnlaceJaxb.desdeXml("<libro>"));
    }

    @Test
    void validaFormatoDeIsbn() {
        assertTrue(EnlaceJaxb.validarFormatoIsbn("978-84"));
        assertFalse(EnlaceJaxb.validarFormatoIsbn("invalid-isbn"));
        assertThrows(IllegalArgumentException.class, () -> EnlaceJaxb.validarFormatoIsbn(null));
    }

    @Test
    void compactaTextoXmlSinDejarEspaciosEnLosExtremos() {
        String compacto = EnlaceJaxb.compactarXml("  <libro>   </libro> ");

        assertEquals("<libro> </libro>", compacto);
        assertThrows(IllegalArgumentException.class, () -> EnlaceJaxb.compactarXml(" "));
    }

    @Test
    void extraeAnioUsandoElUnmarshalJaxb() {
        String xml = EnlaceJaxb.aXml(libroDeEjemplo());

        assertNotNull(xml, "Primero debe estar implementada la serialización JAXB.");
        assertFalse(xml.isBlank(), "El documento XML no puede estar vacío.");
        assertEquals(2008, EnlaceJaxb.extraerAnio(xml));
    }

    @Test
    void serializaFragmentoSinDeclaracionXml() {
        String fragmento = EnlaceJaxb.serializarFragmento(libroDeEjemplo());

        assertFalse(fragmento.contains("<?xml"));
        assertTrue(fragmento.contains("<libro"));
        assertThrows(IllegalArgumentException.class, () -> EnlaceJaxb.serializarFragmento(null));
    }

    @Test
    void convierteElModeloEnUnMapaConTiposConservados() {
        Map<String, Object> datos = EnlaceJaxb.libroAMap(libroDeEjemplo());

        assertNotNull(datos, "libroAMap() debe devolver el mapa de datos.");
        assertEquals("978-84", datos.get("isbn"));
        assertEquals("Clean Code", datos.get("titulo"));
        assertEquals(2008, datos.get("anio"));
        assertThrows(IllegalArgumentException.class, () -> EnlaceJaxb.libroAMap(null));
    }

    @Test
    void creaUnLibroPorDefecto() {
        Libro libro = EnlaceJaxb.crearLibroPorDefecto();

        assertNotNull(libro);
        assertEquals("000-00", libro.getIsbn());
        assertEquals("Sin título", libro.getTitulo());
        assertEquals(0, libro.getAnio());
    }

    @Test
    void detectaLaRaizLibroConUnaHeuristicaTextual() {
        assertTrue(EnlaceJaxb.esXmlDeLibro("<libro isbn=\"123\">contenido</libro>"));
        assertFalse(EnlaceJaxb.esXmlDeLibro("<revista></revista>"));
        assertFalse(EnlaceJaxb.esXmlDeLibro(null));
    }

    @Test
    void declaraLaCodificacionIso8859() {
        String xml = EnlaceJaxb.serializarIso8859(libroDeEjemplo());

        assertTrue(xml.contains("encoding=\"ISO-8859-1\""));
    }

    @Test
    void comparaPublicacionConCorteEstricto() {
        Libro libro = libroDeEjemplo();

        assertTrue(EnlaceJaxb.esPublicacionReciente(libro, 2000));
        assertFalse(EnlaceJaxb.esPublicacionReciente(libro, 2008));
        assertFalse(EnlaceJaxb.esPublicacionReciente(libro, 2015));
        assertThrows(IllegalArgumentException.class, () -> EnlaceJaxb.esPublicacionReciente(null, 2000));
    }

    @Test
    void clonaMedianteRoundTripSinCompartirInstancia() {
        Libro original = libroDeEjemplo();

        Libro clon = EnlaceJaxb.clonarLibro(original);

        assertNotNull(clon);
        assertNotSame(original, clon);
        assertEquals(original.getIsbn(), clon.getIsbn());
        assertEquals(original.getTitulo(), clon.getTitulo());
        assertEquals(original.getAnio(), clon.getAnio());
        assertThrows(IllegalArgumentException.class, () -> EnlaceJaxb.clonarLibro(null));
    }

    private Libro libroDeEjemplo() {
        return new Libro("978-84", "Clean Code", 2008);
    }
}
