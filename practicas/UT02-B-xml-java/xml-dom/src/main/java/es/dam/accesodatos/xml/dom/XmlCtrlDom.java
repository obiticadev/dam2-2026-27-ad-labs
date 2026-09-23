package es.dam.accesodatos.xml.dom;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XmlCtrlDom {
    private static final File FICHERO_ENTRADA = new File("src/main/resources/cd_catalog.xml");
    private static final File FICHERO_SALIDA = new File("target/demo-data/catalogo.xml");

    public static void main(String[] args)
            throws SAXException, IOException, ParserConfigurationException, TransformerException {
        Document documento = instanciarDocument(FICHERO_ENTRADA);
        escribeDocumentATextXml(documento, FICHERO_SALIDA);
        System.out.println("DOM transformado a " + FICHERO_SALIDA.getAbsolutePath());
    }

    public static Document instanciarDocument() throws ParserConfigurationException {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    }

    public static Document instanciarDocument(File ficheroXml)
            throws SAXException, IOException, ParserConfigurationException {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(ficheroXml);
    }

    public static void escribeDocumentATextXml(Document doc, File fichero)
            throws TransformerException, IOException {
        Files.createDirectories(Path.of(fichero.toURI()).getParent());
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.transform(new DOMSource(doc), new StreamResult(fichero));
    }
}
