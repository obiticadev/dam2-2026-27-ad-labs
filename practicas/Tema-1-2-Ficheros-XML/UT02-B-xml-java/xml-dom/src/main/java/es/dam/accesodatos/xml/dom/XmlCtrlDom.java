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
    final static File ficheroIN = new File("src/main/resources/cd_catalog.xml");
    final static File ficheroOUT = new File("target/demo-data/catalogo.txt");

    public static void main(String[] args)
            throws SAXException, IOException, ParserConfigurationException, TransformerException {
        Document documento = null;
        // Parsear: ficheroIN -> documento DOM
        documento = instanciarDocument(ficheroIN);

        // Transformar: documento DOM -> ficheroOUT
        escribeDocumentATextXml(documento, ficheroOUT);
        System.out.println("DOM transformado a " + ficheroOUT.getAbsolutePath());
    }

    // Crea un documento DOM vacío
    public static Document instanciarDocument() throws ParserConfigurationException {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        return doc;
    }

    // Lee un fichero XML y crea un documento DOM en memoria
    public static Document instanciarDocument(File fXmlFile)
            throws SAXException, IOException, ParserConfigurationException {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(fXmlFile);
        return doc;
    }

    // Transforma un documento DOM en un fichero XML (o a consola)
    public static void escribeDocumentATextXml(Document doc, File file)
            throws TransformerException, IOException {
        if (file.getParentFile() != null) {
            Files.createDirectories(Path.of(file.toURI()).getParent());
        }
        Transformer trans = TransformerFactory.newInstance().newTransformer();
        trans.setOutputProperty(OutputKeys.INDENT, "yes");

        // StreamResult puede tener distintas salidas: a un fichero o por pantalla (System.out)
        StreamResult result = new StreamResult(file);
        DOMSource source = new DOMSource(doc);
        trans.transform(source, result);
    }
}
