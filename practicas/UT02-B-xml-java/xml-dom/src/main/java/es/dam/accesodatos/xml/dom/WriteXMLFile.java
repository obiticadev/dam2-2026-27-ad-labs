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
            // 1. Crear documento DOM vacío
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            // 2. Crear raíz <compania> y añadir elemento <empleado> con atributo 'id'
            Element raiz = doc.createElement("compania");
            doc.appendChild(raiz);
            Element empleado = doc.createElement("empleado");
            empleado.setAttribute("id", "1");
            raiz.appendChild(empleado);

            // 3. Añadir datos del empleado con el método auxiliar
            agregarTexto(doc, empleado, "nombre", "Manuel");
            agregarTexto(doc, empleado, "apellidos", "González");
            agregarTexto(doc, empleado, "seccion", "almacén");
            agregarTexto(doc, empleado, "salario", "1000");

            // 4. Preparar archivo de destino
            File salida = new File("target/demo-data/archivo.xml");
            Files.createDirectories(Path.of(salida.toURI()).getParent());

            // 5. Configurar transformer con formato legible (indentado) y UTF-8
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            // 6. Escribir el árbol DOM en el archivo
            transformer.transform(new DOMSource(doc), new StreamResult(salida));
            System.out.println("Fichero guardado correctamente en " + salida.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Crea un elemento con texto y lo añade al nodo padre
    private static void agregarTexto(Document doc, Element padre, String etiqueta, String valor) {
        Element elemento = doc.createElement(etiqueta);
        elemento.appendChild(doc.createTextNode(valor));
        padre.appendChild(elemento);
    }
}
