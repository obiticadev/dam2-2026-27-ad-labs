package es.dam.accesodatos.xml.dom;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

public class PasosDOM {
    public static void main(String[] args) throws Exception {
        // 1. Inicializar el parser DOM
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        // 2. Cargar y parsear el archivo XML en memoria
        Document document = builder.parse(new File("src/main/resources/clase.xml"));

        // 3. Obtener el elemento raíz (<clase>)
        Element root = document.getDocumentElement();

        // 4. Obtener el primer nodo <alumno>
        Element alumno = (Element) document.getElementsByTagName("alumno").item(0);

        // 5. Leer atributo "numero" y la lista de todos sus atributos
        String numero = alumno.getAttribute("numero");
        NamedNodeMap atributos = alumno.getAttributes();

        // 6. Obtener elementos hijos (<nombre>) y todos los nodos hijos (incluye espacios/saltos de línea)
        NodeList nombres = alumno.getElementsByTagName("nombre");
        NodeList hijos = alumno.getChildNodes();

        // 7. Mostrar resultados
        System.out.println("Raíz: " + root.getNodeName());
        System.out.println("Número del primer alumno: " + numero);
        System.out.println("Número de atributos: " + atributos.getLength());
        System.out.println("Nombre: " + nombres.item(0).getTextContent());
        System.out.println("Número de nodos hijos (incluye espacios): " + hijos.getLength());
    }
}
