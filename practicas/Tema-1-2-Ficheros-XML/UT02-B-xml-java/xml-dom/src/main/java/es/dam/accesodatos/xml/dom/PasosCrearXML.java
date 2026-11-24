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
        // 1. Crear un nuevo documento DOM vacío en memoria
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        // 2. Crear y añadir el elemento raíz <compania>
        Element root = doc.createElement("compania");
        doc.appendChild(root);

        // 3. Crear el elemento <empleado> y añadirlo a la raíz
        Element empleado = doc.createElement("empleado");
        root.appendChild(empleado);

        // 4. Crear el atributo 'id="1"' y asignarlo al empleado
        Attr atributo = doc.createAttribute("id");
        atributo.setValue("1");
        empleado.setAttributeNode(atributo);

        // 5. Crear el elemento <nombre> con texto "Manuel" y añadirlo al empleado
        Element nombre = doc.createElement("nombre");
        nombre.appendChild(doc.createTextNode("Manuel"));
        empleado.appendChild(nombre);

        // 6. Asegurar que existe la carpeta de destino
        File salida = new File("target/demo-data/archivo-pasos.xml");
        Files.createDirectories(Path.of(salida.toURI()).getParent());

        // 7. Volcar el árbol DOM al archivo XML físico mediante Transformer
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(new DOMSource(doc), new StreamResult(salida));
        System.out.println("XML guardado en " + salida.getAbsolutePath());
    }
}
