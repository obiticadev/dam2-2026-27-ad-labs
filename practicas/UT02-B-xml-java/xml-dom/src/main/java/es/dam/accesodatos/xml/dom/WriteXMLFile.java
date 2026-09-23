package es.dam.accesodatos.xml.dom;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class WriteXMLFile {
    public static void main(String[] args) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element raiz = doc.createElement("compania");
            doc.appendChild(raiz);
            Element empleado = doc.createElement("empleado");
            empleado.setAttribute("id", "1");
            raiz.appendChild(empleado);
            agregarTexto(doc, empleado, "nombre", "Manuel");
            agregarTexto(doc, empleado, "apellidos", "González");
            agregarTexto(doc, empleado, "seccion", "almacén");
            agregarTexto(doc, empleado, "salario", "1000");

            File salida = new File("target/demo-data/archivo.xml");
            Files.createDirectories(Path.of(salida.toURI()).getParent());
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.transform(new DOMSource(doc), new StreamResult(salida));
            System.out.println("Fichero guardado correctamente en " + salida.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void agregarTexto(Document doc, Element padre, String etiqueta, String valor) {
        Element elemento = doc.createElement(etiqueta);
        elemento.appendChild(doc.createTextNode(valor));
        padre.appendChild(elemento);
    }
}
