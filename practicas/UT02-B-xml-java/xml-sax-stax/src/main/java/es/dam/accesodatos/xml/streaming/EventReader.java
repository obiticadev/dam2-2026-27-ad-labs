package es.dam.accesodatos.xml.streaming;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class EventReader {
    public static void main(String[] args) {
        try (InputStream input = Files.newInputStream(Path.of("src/main/resources/books.xml"))) {
            XMLEventReader reader = XMLInputFactory.newInstance().createXMLEventReader(input);
            try {
                while (reader.hasNext()) {
                    XMLEvent event = reader.nextEvent();
                    if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
                        StartElement start = event.asStartElement();
                        if ("title".equals(start.getName().getLocalPart())) {
                            Iterator<Attribute> atributos = start.getAttributes();
                            while (atributos.hasNext()) {
                                Attribute atributo = atributos.next();
                                QName nombre = atributo.getName();
                                System.out.println("Atributo name/valor: " + nombre + "/" + atributo.getValue());
                            }
                            System.out.println("Título: " + reader.getElementText());
                        }
                    } else if (event.getEventType() == XMLStreamConstants.END_DOCUMENT) {
                        System.out.println("Fin del documento");
                    }
                }
            } finally {
                reader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
