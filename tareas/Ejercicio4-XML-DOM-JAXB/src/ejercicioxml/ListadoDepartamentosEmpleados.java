package ejercicioxml;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ListadoDepartamentosEmpleados {

    private static final File FICHERO_XML = new File("datos/DepartamentosEmpleados.xml");

    public static void main(String[] args) {
        try {
            Document documento = leerDocumento();
            listarDepartamentosYEmpleados(documento);
        } catch (IOException | ParserConfigurationException | SAXException e) {
            System.err.println("No se pudo leer el fichero " + FICHERO_XML + ": " + e.getMessage());
        }
    }

    private static Document leerDocumento() throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory fabrica = DocumentBuilderFactory.newInstance();
        DocumentBuilder constructor = fabrica.newDocumentBuilder();
        Document documento = constructor.parse(FICHERO_XML);
        documento.getDocumentElement().normalize();
        return documento;
    }

    private static void listarDepartamentosYEmpleados(Document documento) {
        NodeList departamentos = documento.getElementsByTagName("DEP_ROW");
        NodeList empleados = documento.getElementsByTagName("EMP_ROW");

        System.out.println("DEPARTAMENTOS Y EMPLEADOS");

        for (int i = 0; i < departamentos.getLength(); i++) {
            Element departamento = (Element) departamentos.item(i);
            String numeroDepartamento = texto(departamento, "DEPT_NO");

            System.out.printf("%nDepartamento %s - %s (%s)%n",
                    numeroDepartamento,
                    texto(departamento, "DNOMBRE"),
                    texto(departamento, "LOC"));

            for (int j = 0; j < empleados.getLength(); j++) {
                Element empleado = (Element) empleados.item(j);
                if (numeroDepartamento.equals(texto(empleado, "DEPT_NO"))) {
                    System.out.printf("  Empleado %s | %s | Salario: %s | Comisión: %s%n",
                            texto(empleado, "APELLIDO"),
                            texto(empleado, "OFICIO"),
                            texto(empleado, "SALARIO"),
                            texto(empleado, "COMISION"));
                }
            }
        }
    }

    private static String texto(Element elemento, String nombreEtiqueta) {
        return elemento.getElementsByTagName(nombreEtiqueta).item(0).getTextContent().trim();
    }
}
