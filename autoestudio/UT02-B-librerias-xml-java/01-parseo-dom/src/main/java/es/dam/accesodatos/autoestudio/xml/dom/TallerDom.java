package es.dam.accesodatos.autoestudio.xml.dom;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * UT02-B §2–2.5. Práctica añadida: alumnos.xml → árbol → cambios →
 * resultado.xml.
 * Entradas locales conocidas: raíz alumnos, alumno con atributo id y campos
 * nombre/nota.
 * No se pide filtrar DOCTYPE, borrar cabeceras, concatenar XML ni programar
 * seguridad.
 * Consulta README y teoria/taller-dom.md antes de empezar. Tu ParseoDom se
 * conserva.
 */
public final class TallerDom {
    private TallerDom() {
    }

    /**
     * Parsea el archivo con DocumentBuilder. normalize() es pertinente, no elimina
     * cabeceras.
     */
    public static Document cargar(File archivo)
            throws ParserConfigurationException, SAXException, IOException {
        // TODO: Crea factoría, builder y Document usando parse(archivo).
        // TODO: Normaliza la raíz como en Ejemplo2 del tema y devuelve el documento.
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(archivo);
        doc.getDocumentElement().normalize();
        return doc;
    }

    /**
     * Extrae todos los nombres de alumnos en orden; devuelve lista vacía si no hay
     * alumnos.
     */
    public static List<String> nombres(Document doc) {
        // TODO: Obtén NodeList de alumno y recórrelo por índice con item(i).
        // TODO: Convierte a Element; obtiene nombre con getElementsByTagName y
        // getTextContent.
        // TODO: Devuelve los nombres. Cada alumno de esta entrada tiene exactamente un
        // nombre.
        return null;
    }

    /**
     * Cuenta SOLO hijos directos de tipo Element, no textos de indentación ni
     * descendientes.
     */
    public static int contarHijosElemento(Element padre) {
        // TODO: Recorre getChildNodes() y comprueba getNodeType() == Node.ELEMENT_NODE.
        return 0;
    }

    /**
     * Cambia la nota por id exacto; devuelve false si no existe. No guarda todavía
     * el archivo.
     */
    public static boolean actualizarNota(Document doc, String id, double nota) {
        // TODO: Localiza alumno por getAttribute("id"), no por índice.
        // TODO: Cambia el campo nota mediante setTextContent(Double.toString(nota)).
        return false;
    }

    /**
     * Añade un alumno a la raíz; id es nuevo. Deja el escape de texto al
     * árbol/Transformer.
     */
    public static void agregarAlumno(Document doc, String id, String nombre, double nota) {
        // TODO: Crea alumno, atributo id y campos nombre y nota con el Document.
        // TODO: Crea el texto y enlaza los nodos con appendChild.
        // TODO: Inserta el alumno bajo la raíz; crear un nodo no lo inserta.
    }

    /** Elimina el alumno por id exacto y devuelve true; false si no existe. */
    public static boolean eliminarAlumno(Document doc, String id) {
        // TODO: Localiza el elemento y usa su padre para removeChild; termina tras
        // eliminarlo.
        return false;
    }

    /**
     * Serializa a archivo usando Transformer, DOMSource y StreamResult. No se exige
     * formato exacto.
     */
    public static void guardar(Document doc, File destino) throws TransformerException {
        // TODO: Crea Transformer y activa OutputKeys.INDENT="yes", como en el tema.
        // TODO: Transforma DOMSource(doc) a StreamResult(destino).
    }

    /** Consulta inicial sin modificar el archivo de entrada. */
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Uso: TallerDom alumnos.xml");
            return;
        }
        Document doc = cargar(new File(args[0]));
        if (doc != null)
            System.out.println(nombres(doc));
    }
}
