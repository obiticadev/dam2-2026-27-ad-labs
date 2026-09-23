package es.dam.accesodatos.xml.dom;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class PasosCrearXML {
    public static void main(String[] args) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        Element root = doc.createElement("compania");
        doc.appendChild(root);
        Element empleado = doc.createElement("empleado");
        root.appendChild(empleado);

        Attr atributo = doc.createAttribute("id");
        atributo.setValue("1");
        empleado.setAttributeNode(atributo);
        Element nombre = doc.createElement("nombre");
        nombre.appendChild(doc.createTextNode("Manuel"));
        empleado.appendChild(nombre);

        File salida = new File("target/demo-data/archivo-pasos.xml");
        Files.createDirectories(Path.of(salida.toURI()).getParent());
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(new DOMSource(doc), new StreamResult(salida));
        System.out.println("XML guardado en " + salida.getAbsolutePath());
    }
}
