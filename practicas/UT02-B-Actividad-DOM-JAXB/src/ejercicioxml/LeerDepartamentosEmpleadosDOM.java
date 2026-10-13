/*
 * Actividad: leer un fichero XML con DOM.
 * Lee datos/DepartamentosEmpleados.xml y, para cada departamento, muestra su
 * número, nombre y localidad. A continuación, lista apellido, oficio, salario
 * y comisión de los empleados que pertenecen a ese departamento.
 * Completa los TODO siguiendo el ejercicio.
 */
package ejercicioxml;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class LeerDepartamentosEmpleadosDOM {

    private static final File FICHERO_XML = new File("datos/DepartamentosEmpleados.xml");

    public static void main(String[] args) throws Exception {
        DocumentBuilderFactory fabrica = DocumentBuilderFactory.newInstance();
        DocumentBuilder constructor = fabrica.newDocumentBuilder();
        Document documento = constructor.parse(FICHERO_XML);
        documento.getDocumentElement().normalize();

        // TODO 1: obtener las listas de elementos DEP_ROW y EMP_ROW.
        NodeList listaDepartamentos = documento.getElementsByTagName("DEP_ROW");
        NodeList listaEmpleados = documento.getElementsByTagName("EMP_ROW");

        // TODO 2: recorrer los departamentos y mostrar DEPT_NO, DNOMBRE y LOC.
        // TODO 3: asociar cada empleado por DEPT_NO y mostrar los campos pedidos.

        for (int i = 0; i < listaDepartamentos.getLength(); i++) {
            StringBuilder sb = new StringBuilder();
            Element dep = (Element) listaDepartamentos.item(i);
            String nombre = dep.getElementsByTagName("DNOMBRE").item(0).getTextContent();
            String num = dep.getElementsByTagName("DEPT_NO").item(0).getTextContent();
            String loc = dep.getElementsByTagName("LOC").item(0).getTextContent();
            sb.append(String.format("%nDEPARTAMENTO %s: %s (%s)", num, nombre, loc));
            System.out.println(sb.toString());

            for (int j = 0; j < listaEmpleados.getLength(); j++) {
                Element emp = (Element) listaEmpleados.item(j);
                String depEmp = emp.getElementsByTagName("DEPT_NO").item(0).getTextContent();
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
