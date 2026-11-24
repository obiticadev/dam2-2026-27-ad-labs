package es.dam.accesodatos.xml.streaming;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

public class ListaLibrosStAX {
    public static void main(String[] args) throws Exception {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        try (InputStream input = Files.newInputStream(Path.of("src/main/resources/books.xml"))) {
            XMLStreamReader reader = factory.createXMLStreamReader(input);
            try {
                String etiqueta = null;
                System.out.println("Lista de libros");
                while (reader.hasNext()) {
                    int tipoEvento = reader.next();
                    switch (tipoEvento) {
                        case XMLStreamConstants.START_ELEMENT -> {
                            etiqueta = reader.getName().getLocalPart();
                            System.out.println(etiqueta);
                            if (etiqueta.equals("title")) {
                                System.out.println("Idioma: " + reader.getAttributeValue(null, "lang"));
                                System.out.println("Título: " + reader.getElementText());
                            }
                        }
                        case XMLStreamConstants.END_DOCUMENT -> System.out.println("Fin del documento");
                        default -> { }
                    }
                }
            } finally {
                reader.close();
            }
        }
    }
}
