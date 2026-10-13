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
        // TODO 2: recorrer los departamentos y mostrar DEPT_NO, DNOMBRE y LOC.
        // TODO 3: asociar cada empleado por DEPT_NO y mostrar los campos pedidos.

        System.out.println("Apartado a pendiente: completa los TODO de DOM.");
    }
}
