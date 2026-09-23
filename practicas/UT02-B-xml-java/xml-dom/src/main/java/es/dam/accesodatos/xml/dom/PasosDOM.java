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
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File("src/main/resources/clase.xml"));

        Element root = document.getDocumentElement();
        Element alumno = (Element) document.getElementsByTagName("alumno").item(0);
        String numero = alumno.getAttribute("numero");
        NamedNodeMap atributos = alumno.getAttributes();
        NodeList nombres = alumno.getElementsByTagName("nombre");
        NodeList hijos = alumno.getChildNodes();

        System.out.println("Raíz: " + root.getNodeName());
        System.out.println("Número del primer alumno: " + numero);
        System.out.println("Número de atributos: " + atributos.getLength());
        System.out.println("Nombre: " + nombres.item(0).getTextContent());
        System.out.println("Número de nodos hijos (incluye espacios): " + hijos.getLength());
    }
}
