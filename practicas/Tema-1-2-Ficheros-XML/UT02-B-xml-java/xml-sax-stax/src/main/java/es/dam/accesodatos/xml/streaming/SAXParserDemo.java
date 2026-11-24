package es.dam.accesodatos.xml.streaming;

import java.io.File;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SAXParserDemo {
    public static void main(String[] args) {
        try {
            File inputFile = new File("src/main/resources/input.xml");
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(inputFile, new UserHandler());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class UserHandler extends DefaultHandler {
    private String currentElement;
    private final StringBuilder text = new StringBuilder();

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        currentElement = qName;
        text.setLength(0);
        if (qName.equalsIgnoreCase("student")) {
            System.out.println("Roll No: " + attributes.getValue("rollno"));
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        String value = text.toString().trim();
        if (qName.equalsIgnoreCase("firstname")) {
            System.out.println("First Name: " + value);
        } else if (qName.equalsIgnoreCase("lastname")) {
            System.out.println("Last Name: " + value);
        } else if (qName.equalsIgnoreCase("nickname")) {
            System.out.println("Nick Name: " + value);
        } else if (qName.equalsIgnoreCase("marks")) {
            System.out.println("Marks: " + value);
        } else if (qName.equalsIgnoreCase("student")) {
            System.out.println("End Element: " + qName + System.lineSeparator());
        }
        currentElement = null;
        text.setLength(0);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (currentElement != null) {
            text.append(ch, start, length);
        }
    }
}
