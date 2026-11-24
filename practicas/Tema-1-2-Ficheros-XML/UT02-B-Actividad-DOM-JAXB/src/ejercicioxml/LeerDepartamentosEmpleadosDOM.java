package ejercicioxml;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

// Lee departamentos y muestra sus empleados a partir de un fichero XML con DOM
public class LeerDepartamentosEmpleadosDOM {

    private static final File FICHERO_XML = new File("datos/DepartamentosEmpleados.xml");

    public static void main(String[] args) throws Exception {
        // 1. Inicializar el parser DOM
        DocumentBuilderFactory fabrica = DocumentBuilderFactory.newInstance();
        DocumentBuilder constructor = fabrica.newDocumentBuilder();

        // 2. Cargar el archivo XML en memoria y normalizar los nodos de texto
        Document documento = constructor.parse(FICHERO_XML);
        documento.getDocumentElement().normalize();

        // 3. Obtener las listas de departamentos (<DEP_ROW>) y empleados (<EMP_ROW>)
        NodeList listaDepartamentos = documento.getElementsByTagName("DEP_ROW");
        NodeList listaEmpleados = documento.getElementsByTagName("EMP_ROW");

        // 4. Recorrer los departamentos y mostrar su número, nombre y localidad
        for (int i = 0; i < listaDepartamentos.getLength(); i++) {
            StringBuilder sb = new StringBuilder();
            Element dep = (Element) listaDepartamentos.item(i);
            String nombre = dep.getElementsByTagName("DNOMBRE").item(0).getTextContent();
            String num = dep.getElementsByTagName("DEPT_NO").item(0).getTextContent();
            String loc = dep.getElementsByTagName("LOC").item(0).getTextContent();
            sb.append(String.format("%nDEPARTAMENTO %s: %s (%s)", num, nombre, loc));
            System.out.println(sb.toString());

            // 5. Recorrer los empleados y leer el número de su departamento
            for (int j = 0; j < listaEmpleados.getLength(); j++) {
                Element emp = (Element) listaEmpleados.item(j);
                String depEmp = emp.getElementsByTagName("DEPT_NO").item(0).getTextContent();

                // 6. Mostrar los datos del empleado solo si pertenece al departamento actual
                if (num.equals(depEmp)) {
                    String apellido = emp.getElementsByTagName("APELLIDO").item(0).getTextContent();
                    String oficio = emp.getElementsByTagName("OFICIO").item(0).getTextContent();
                    String salario = emp.getElementsByTagName("SALARIO").item(0).getTextContent();
                    String comision = emp.getElementsByTagName("COMISION").item(0).getTextContent();
                    System.out.println(
                            String.format(" - %-10s | Oficio: %-10s | Salario: %-10s | Comisión: %5s €", apellido,
                                    oficio, salario, comision));
                }
            }
        }

    }
}
