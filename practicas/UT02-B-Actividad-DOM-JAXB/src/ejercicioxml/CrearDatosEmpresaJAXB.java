/*
 * Actividad: leer con DOM DepartamentosEmpleados.xml y crear DatosEmpresa.xml
 * con JAXB. El resultado debe tener raíz EMPRESA, título, departamentos con
 * atributo id y empleados agrupados en EMPLEADOS; cada empleado incluye el
 * atributo numero y sus datos. Completa los TODO siguiendo el esquema pedido.
 */
package ejercicioxml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

public class CrearDatosEmpresaJAXB {

    private static final File FICHERO_ENTRADA = new File("datos/DepartamentosEmpleados.xml");
    private static final File FICHERO_SALIDA = new File("DatosEmpresa.xml");

    public static void main(String[] args) throws Exception {
        DocumentBuilderFactory fabrica = DocumentBuilderFactory.newInstance();
        DocumentBuilder constructor = fabrica.newDocumentBuilder();
        Document documento = constructor.parse(FICHERO_ENTRADA);
        documento.getDocumentElement().normalize();

        // TODO 1: recorrer el DOM y crear Empresa, Departamento y Empleado.
        Empresa empresa = new Empresa();

        // TODO 2: agrupar los empleados dentro del departamento correspondiente.

        // TODO 3: crear JAXBContext y Marshaller y escribir FICHERO_SALIDA.
        System.out.println("Apartado b pendiente: completa los TODO de DOM y JAXB.");
    }

    /*
     * TODO 4: completa este modelo JAXB con los campos y anotaciones del esquema:
     * Empresa (TITULO y departamentos), Departamento (id, LOC, DNOMBRE, EMPLEADOS),
     * Empleados (lista de EMPLEADO) y Empleado (numero, APELLIDO, OFICIO, DIR,
     * FECHA_ALT, SALARIO y COMISION).
     */
    @XmlRootElement(name = "EMPRESA")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Empresa {
        public Empresa() {
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Departamento {
        public Departamento() {
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Empleados {
        public Empleados() {
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Empleado {
        public Empleado() {
        }
    }
}
