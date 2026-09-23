package es.dam.accesodatos.xml.streaming;

import java.io.File;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.helpers.DefaultHandler;

public class Ejemplo1 {
    public static void main(String[] args) throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        saxParser.parse(new File("src/main/resources/menu.xml"), new DefaultHandler());
        System.out.println("Documento menu.xml bien formado.");
    }
}
