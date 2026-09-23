package es.dam.accesodatos.xml.dom;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Ejemplo2 {
    public static void main(String[] args) {
        try {
            File inputFile = new File("src/main/resources/clase.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            System.out.println("Root element: " + doc.getDocumentElement().getNodeName());
            NodeList lista = doc.getElementsByTagName("alumno");
            System.out.println("----------------------------");

            for (int i = 0; i < lista.getLength(); i++) {
                Node nodo = lista.item(i);
                System.out.println("\nCurrent Element: " + nodo.getNodeName());
                if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                    Element alumno = (Element) nodo;
                    System.out.println("Número de alumno: " + alumno.getAttribute("numero"));
                    System.out.println("Nombre: " + texto(alumno, "nombre"));
                    System.out.println("Apellido: " + texto(alumno, "apellido"));
                    System.out.println("Apodo: " + texto(alumno, "apodo"));
                    System.out.println("Marcas: " + texto(alumno, "marcas"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String texto(Element elemento, String etiqueta) {
        return elemento.getElementsByTagName(etiqueta).item(0).getTextContent();
    }
}
