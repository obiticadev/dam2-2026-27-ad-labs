/*
Del XML al inventario
Una tienda de material informático recibe un catálogo en XML. Desarrolla una aplicación
Java que lo importe a un fichero binario de acceso aleatorio. Cada registro debe estar en
la posición que indica el código del producto. Asume que el nombre del producto tendrá
como máximo 20 caracteres y cada producto que no esté su registro se marca con -1.


En la entrega:

Explica como has calculado las posiciones de los registros en el fichero de salida.
El código tiene que incluir la declaración de las las librerías necesarias.
El código tiene que controlar errores, y manejarlos.
Tiene que haber una clase por lo menos con el main().
Entrega un fichero pdf con tu nombre_apellido1_apellido2.pdf
*/

import java.io.File;
import java.io.RandomAccessFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Oliver_Bitica {
    public static void main(String[] args) throws Exception {
        // Tamaño de cada registro
        final int TAMANO = 20 + Double.BYTES + Integer.BYTES;

        // Arrays de almacenamiento de datos
        int[] id;
        String[] nombre;
        double[] precio;
        int[] unidades;

        // Primero obtenemos el contenido de xml con DOM
        try {
            // 1. Parsear el archivo XML
            File inputFile = new File("fichero.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);

            // 2. Normalizar el árbol XML (unifica textos adyacentes y elimina nodos vacíos)
            doc.getDocumentElement().normalize();

            System.out.println("Root element: " + doc.getDocumentElement().getNodeName());
            System.out.println("----------------------------");

            // 3. Obtener y recorrer todos los nodos <producto>
            NodeList lista = doc.getElementsByTagName("producto");
            id = new int[lista.getLength()];
            nombre = new String[lista.getLength()];
            precio = new double[lista.getLength()];
            unidades = new int[lista.getLength()];

            for (int i = 0; i < lista.getLength(); i++) {
                Node nodo = lista.item(i);
                System.out.println("\nCurrent Element: " + nodo.getNodeName());

                // 4. Recupera todos los datos
                if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                    Element producto = (Element) nodo;

                    System.out.println("id: " + producto.getAttribute("codigo"));
                    id[i] = Integer.parseInt(producto.getAttribute("codigo"));

                    System.out.println("Nombre: " + texto(producto, "nombre"));
                    nombre[i] = String.valueOf(texto(producto, "nombre"));

                    System.out.println("Precio: " + texto(producto, "precio"));
                    precio[i] = Double.parseDouble(texto(producto, "precio"));

                    System.out.println("Unidades: " + texto(producto, "unidades"));
                    unidades[i] = Integer.parseInt(texto(producto, "unidades"));
                }
            }
            System.out.println("\nComenzamos a montar el fichero de salida con los datos en las posiciones marcadas\n");
            // Comenzamos a montar el fichero de salida con los datos en las posiciones
            // marcadas
            try (RandomAccessFile archivo = new RandomAccessFile("notas.txt", "rw")) {
                archivo.setLength(0);

                // Obtenemos el id más alto para limitar la longitud del archivo al registro con
                // el id más alto
                int maxId = 0;
                for (int i = 0; i < id.length; i++) {
                    if (i == 0) {
                        maxId = id[i];
                    } else {
                        if (id[i] > maxId) {
                            maxId = id[i];
                        }
                    }
                }
                // Rellenamos los campos con -1
                for (int i = 0; i < maxId; i++) {
                    for (int j = 0; j < TAMANO; j++) {
                        archivo.writeByte(-1);
                    }
                }
                // Escribimos con acceso aleatorio los registros en las posiciones indicadas por
                // el id sobreescribiendo los bytes en -1
                for (int i = 0; i < id.length; i++) {
                    System.out.println("ID = " + id[i]);
                    archivo.seek((id[i] - 1) * TAMANO);
                    archivo.writeUTF(nombre[i]);
                    archivo.writeDouble(precio[i]);
                    archivo.writeInt(unidades[i]);

                    archivo.seek((id[i] - 1) * TAMANO);
                    System.out.println("Nombre=" + archivo.readUTF() + ",\nprecio=" + archivo.readDouble()
                            + ",\nunidades=" + archivo.readInt()
                            + ",\nbyte siguiente al registro (debería dar -1 porque ningún registro está junto al otro) = "
                            + archivo.readByte());
                    System.out.println("Puntero: " + archivo.getFilePointer());
                    System.out.println("Longitud: " + archivo.length() + "\n");

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método auxiliar para obtener el texto de un subelemento
    private static String texto(Element elemento, String etiqueta) {
        return elemento.getElementsByTagName(etiqueta).item(0).getTextContent();
    }
}
