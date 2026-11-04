package es.dam.accesodatos.autoestudio.xml.sax;

import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Práctica añadida de UT02-B §3.2, con el alumnos.xml del taller DOM.
 * Recoge id en la apertura de alumno y texto de nombre al cerrarlo.
 * Los nombres son texto simple, sin elementos anidados. Usa
 * SAXParser.parse(File, manejador).
 * Los tests llaman también a characters directamente para garantizar la
 * fragmentación.
 */
public class ManejadorAlumnos extends DefaultHandler {
    private final List<String> ids = new ArrayList<>();
    private final List<String> nombres = new ArrayList<>();
    // TODO: Declara un StringBuilder y el estado de captura que necesites.
    private final StringBuilder TEXTO_NOMBRE = new StringBuilder();
    private boolean leyendoNombre;

    @Override
    public void startDocument() {
        // TODO: Reinicia las listas y el estado para poder reutilizar el manejador.
        ids.clear();
        nombres.clear();
        TEXTO_NOMBRE.setLength(0);
        leyendoNombre = true;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        // TODO: Si qName es alumno, recoge su atributo id con Attributes.getValue.
        // TODO: Si qName es nombre, inicia captura y vacía el acumulador.
        if (qName.equals("alumno")) {
            ids.add(attributes.getValue("id"));
        } else if (qName.equals("nombre")) {
            TEXTO_NOMBRE.setLength(0);
            leyendoNombre = true;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        // TODO: Si capturas nombre, añade SOLO el rango ch/start/length.
        // TODO: No guardes todavía el nombre: pueden llegar más fragmentos.
        if (leyendoNombre) {
            TEXTO_NOMBRE.append(ch, start, length);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        // TODO: Si termina nombre, guarda el acumulado y desactiva la captura.
        // TODO: No recortes espacios internos ni los confundas con la indentación
        // exterior.
        if (qName.equals("nombre")) {
            nombres.add(TEXTO_NOMBRE.toString());
            leyendoNombre = false;
        }
    }

    public List<String> getIds() {
        return ids;
    }

    public List<String> getNombres() {
        return nombres;
    }
}
