package ejercicioxml;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

// Apartado a: leer el XML con DOM y mostrar cada departamento con sus empleados.
public class ListadoDepartamentosEmpleados {

    private static final File FICHERO_XML = new File("datos/DepartamentosEmpleados.xml");

    public static void main(String[] args) throws Exception {
        // 1. Cargar el fichero XML con DOM.
        DocumentBuilderFactory fabrica = DocumentBuilderFactory.newInstance();
        DocumentBuilder constructor = fabrica.newDocumentBuilder();
        Document documento = constructor.parse(FICHERO_XML);

        // 2. Obtener las listas de departamentos y empleados.
        NodeList departamentos = documento.getElementsByTagName("DEP_ROW");
        NodeList empleados = documento.getElementsByTagName("EMP_ROW");

        System.out.println("DEPARTAMENTOS Y EMPLEADOS");

        // 3. Mostrar los datos de cada departamento.
        for (int i = 0; i < departamentos.getLength(); i++) {
            Element departamento = (Element) departamentos.item(i);
            String numeroDepartamento = texto(departamento, "DEPT_NO");

            System.out.printf("%nDepartamento %s - %s (%s)%n",
                    numeroDepartamento,
                    texto(departamento, "DNOMBRE"),
                    texto(departamento, "LOC"));

            // 4. Mostrar solo los empleados que pertenecen a este departamento.
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

    // Obtener el texto de una etiqueta sin repetir la misma expresión DOM.
    private static String texto(Element elemento, String nombreEtiqueta) {
        return elemento.getElementsByTagName(nombreEtiqueta).item(0).getTextContent().trim();
    }
}
