package es.dam.accesodatos.autoestudio.xml.dom;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.swing.event.DocumentEvent;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Ejercicios de consulta y creación de árboles XML con DOM.
 *
 * <p>
 * Procedencia: adaptación de `b16_xml/Ej145DomSaxParsing` para las consultas
 * DOM, ampliada con la escritura que se trabaja en UT02-B.
 *
 * <p>
 * Teoría: {@code teoria/parseo-dom.md}.
 */
public final class ParseoDom {

    private static final String FEATURE_DISALLOW_DOCTYPE = "http://apache.org/xml/features/disallow-doctype-decl";

    private ParseoDom() {
    }

    /**
     * Cuenta las etiquetas del nombre solicitado en un documento XML.
     *
     * @param xml      documento XML válido
     * @param etiqueta nombre de elemento XML que se desea contar
     * @return cantidad de elementos coincidentes, incluidos los descendientes
     * @throws IllegalArgumentException si el XML o la etiqueta son nulos o están en
     *                                  blanco
     * @throws RuntimeException         si el XML está mal formado o el parser no
     *                                  puede configurarse
     */
    public static int contarElementos(String xml, String etiqueta) {
        // TODO: Valida el contenido XML y el nombre de etiqueta según el contrato.
         // TODO: Parsea el XML mediante DocumentBuilderFactory y DocumentBuilder.
        // TODO: Consulta todos los elementos con el nombre indicado.
        // TODO: Devuelve la cantidad de coincidencias del NodeList.
        if (xml == null || xml.isBlank() || etiqueta == null || etiqueta.isBlank()) {
            throw new IllegalArgumentException("Parámetros no válidos");
        }
        // Document doc = parsearSeguro(xml);
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new InputSource(new StringReader(xml))).getElementsByTagName(etiqueta).getLength();
        } catch (SAXException | IOException | ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Obtiene un atributo del primer elemento coincidente.
     *
     * @param xml      documento XML válido
     * @param etiqueta nombre del elemento que se desea buscar
     * @param atributo nombre del atributo que se desea leer
     * @return valor del atributo, o cadena vacía si no hay elemento o atributo
     * @throws IllegalArgumentException si algún argumento es nulo o está en blanco
     * @throws RuntimeException         si el XML está mal formado o el parser no
     *                                  puede configurarse
     */
    public static String obtenerAtributo(String xml, String etiqueta, String atributo) {
        // TODO: Valida los tres argumentos sin aceptar cadenas en blanco.
         // TODO: Parsea el XML mediante DocumentBuilderFactory y DocumentBuilder.
        // TODO: Selecciona el primer elemento coincidente en orden documental.
        // TODO: Recupera el atributo solicitado y respeta el resultado vacío si falta.
        // TODO: Devuelve el valor de ese atributo como String.

        try {
            NodeList lista = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new InputSource(new StringReader(xml)))
                    .getElementsByTagName(etiqueta);
            if (lista.getLength() == 0) {
                return "";
            }
            Element primerElement = (Element) lista.item(0);
            return primerElement.getAttribute(atributo);
        } catch (SAXException | IOException | ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return atributo;
    }

    /**
     * Recupera el texto del primer elemento que coincide con la etiqueta.
     *
     * @param xml      documento XML válido
     * @param etiqueta nombre del elemento que se desea buscar
     * @return texto del primer elemento, o cadena vacía si no hay coincidencias
     * @throws IllegalArgumentException si el XML o la etiqueta son nulos o están en
     *                                  blanco
     * @throws RuntimeException         si el XML está mal formado o el parser no
     *                                  puede configurarse
     */
    public static String textoPrimero(String xml, String etiqueta) {
        // TODO: Valida el documento y la etiqueta según el contrato.
         // TODO: Convierte el XML en un árbol DOM con DocumentBuilder.
        // TODO: Busca elementos coincidentes y selecciona el primero.
        // TODO: Obtén el contenido textual del elemento, incluidos sus descendientes.
        // TODO: Devuelve cadena vacía cuando no haya coincidencias.
        if (xml == null || xml.isBlank() || etiqueta == null || etiqueta.isBlank()) {
            throw new IllegalArgumentException("Parametros no válidos");
        }

        try {
            NodeList nodeList = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new InputSource(new StringReader(xml))).getElementsByTagName(etiqueta);
            if (nodeList.getLength() == 0) {
                return "";
            }
            return nodeList.item(0).getTextContent();
        } catch (DOMException | SAXException | IOException | ParserConfigurationException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException("Error al parsear XML", e);
        }
    }

    /**
     * Comprueba si el elemento raíz coincide con el nombre esperado.
     *
     * @param xml          documento XML válido
     * @param raizEsperada nombre que se espera para la raíz
     * @return {@code true} si el nombre de la raíz coincide exactamente
     * @throws IllegalArgumentException si algún argumento es nulo o está en blanco
     * @throws RuntimeException         si el XML está mal formado o el parser no
     *                                  puede configurarse
     */
    public static boolean raizCoincide(String xml, String raizEsperada) {
        // TODO: Valida el XML y el nombre de raíz esperado.
         // TODO: Parsea el documento con DocumentBuilder.
        // TODO: Obtén el elemento raíz del Document.
        // TODO: Compara su nombre exacto con el solicitado.
        if (xml == null || xml.isBlank() || raizEsperada == null || raizEsperada.isBlank()) {
            throw new IllegalArgumentException("Argumentos erróneos");
        }

        try {
            Element element = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new InputSource(new StringReader(xml))).getDocumentElement();
            return element.getTagName().equals(raizEsperada);
        } catch (SAXException | IOException | ParserConfigurationException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException("Problemas con el parser", e);
        }
    }

    /**
     * Cuenta los elementos de cualquier nombre, incluida la raíz.
     *
     * @param xml documento XML válido
     * @return número total de nodos de tipo elemento
     * @throws IllegalArgumentException si el XML es nulo o está en blanco
     * @throws RuntimeException         si el XML está mal formado o el parser no
     *                                  puede configurarse
     */
    public static int contarNodosElemento(String xml) {
        // TODO: Valida el documento XML recibido.
         // TODO: Parsea el texto XML con DocumentBuilder.
        // TODO: Selecciona todos los elementos mediante la consulta comodín de DOM.
        // TODO: Devuelve el número total, incluyendo el elemento raíz.
        if (xml == null || xml.isBlank()) {
            throw new IllegalArgumentException("El xml no puede ser nulo o estar en blanco");
        }

        try {
            NodeList nodeList = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new InputSource(new StringReader(xml))).getElementsByTagName("*");
            return nodeList.getLength();
        } catch (SAXException | IOException | ParserConfigurationException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException("Error de parse", e);
        }
    }

    /**
     * Crea un XML con un elemento raíz y un hijo de contenido textual.
     *
     * @param raiz  nombre del elemento raíz
     * @param hijo  nombre del elemento hijo
     * @param texto contenido textual del hijo
     * @return documento XML serializado desde un árbol DOM
     * @throws IllegalArgumentException si algún argumento es nulo o está en blanco
     * @throws RuntimeException         si no se puede crear o transformar el
     *                                  documento
     */
    public static String crearDocumento(String raiz, String hijo, String texto) {
        // TODO: Valida los nombres de elemento y el contenido textual.
        // TODO: Crea un Document nuevo mediante DocumentBuilderFactory.
        // TODO: Construye raíz, hijo y nodo de texto y enlázalos al árbol.
        // TODO: Crea un Transformer y configura una salida XML legible.
        // TODO: Transforma DOMSource a StreamResult respaldado por StringWriter.
        // TODO: Devuelve la cadena XML producida por el Transformer.
        if (raiz == null || raiz.isBlank() || hijo == null || hijo.isBlank() || texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("Alguno de los argumentos es nulo o está en blanco");
        }
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element rootElement = doc.createElement(raiz);
            doc.appendChild(rootElement);

            Element child = doc.createElement(hijo);
            child.appendChild(doc.createTextNode(texto));
            rootElement.appendChild(child);

            TransformerFactory transformerFactory = TransformerFactory.newDefaultInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);

            transformer.transform(source, result);
            return writer.toString();

        } catch (ParserConfigurationException | TransformerException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException("No se puede crear o transformar el documento", e);
        }
    }

    /**
     * Ampliación opcional de seguridad ya trabajada. No se exige usar este helper
     * ni rechazar DOCTYPE en la ruta de examen. Solo se usan entradas locales conocidas.
     * Se conserva la implementación personal, sin convertirla en un requisito docente.
     */
    private static Document parsearSeguro(String xml) {
        // TODO: Rechaza XML nulo o en blanco con IllegalArgumentException.
        // TODO: Configura DocumentBuilderFactory para rechazar DOCTYPE y entidades
        // externas.
        // TODO: Crea DocumentBuilder y parsea el texto mediante
        // InputSource/StringReader.
        // TODO: Instala un ErrorHandler que propague el fallo sin volcar diagnósticos
        // crudos a consola.
        // TODO: Convierte fallos de configuración, sintaxis o entrada en
        // RuntimeException.
        // TODO: Devuelve el Document completo para las consultas del ejercicio.
        if (xml == null || xml.isBlank()) {
            throw new IllegalArgumentException("Parámetros no válidos");
        }
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature(FEATURE_DISALLOW_DOCTYPE, true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new ErrorHandler() {

                @Override
                public void error(SAXParseException exception) throws SAXException {
                    throw exception;
                }

                @Override
                public void fatalError(SAXParseException exception) throws SAXException {
                    throw exception;
                }

                @Override
                public void warning(SAXParseException exception) throws SAXException {
                }

            });

            return builder.parse(new InputSource(new StringReader(xml)));
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new RuntimeException("Error al parsear XML", e);
        }
    }

    /**
     * Playground de consultas DOM y generación XML desde el IDE.
     *
     * @param args argumentos de línea de comandos, no utilizados
     */
    public static void main(String[] args) {
        String xml = "<libros><libro>A</libro><libro>B</libro></libros>";
        int cantidad = contarElementos(xml, "libro");
        String generado = crearDocumento("libros", "libro", "XML con DOM");
        if (cantidad == 0 || generado.isBlank()) {
            System.out.println("Completa contarElementos() y crearDocumento() para ver la salida DOM.");
            return;
        }
        System.out.println("Libros: " + cantidad);
        System.out.println(generado);
    }
}
