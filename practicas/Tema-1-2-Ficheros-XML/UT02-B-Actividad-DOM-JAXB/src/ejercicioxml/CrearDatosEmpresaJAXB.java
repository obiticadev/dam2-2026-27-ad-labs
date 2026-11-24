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

// Actividad pendiente: leer el XML con DOM y generar DatosEmpresa.xml mediante JAXB
public class CrearDatosEmpresaJAXB {

    private static final File FICHERO_ENTRADA = new File("datos/DepartamentosEmpleados.xml");
    private static final File FICHERO_SALIDA = new File("DatosEmpresa.xml");

    public static void main(String[] args) throws Exception {
        // 1. Inicializar el parser DOM
        DocumentBuilderFactory fabrica = DocumentBuilderFactory.newInstance();
        DocumentBuilder constructor = fabrica.newDocumentBuilder();

        // 2. Cargar el archivo XML en memoria y normalizar los nodos de texto
        Document documento = constructor.parse(FICHERO_ENTRADA);
        documento.getDocumentElement().normalize();

        // 3. Crear el objeto raíz de la empresa
        Empresa empresa = new Empresa();

        // 4. TODO: recorrer el DOM y completar los objetos Empresa, Departamento y Empleado

        // 5. TODO: agrupar los empleados en su departamento mediante DEPT_NO

        // 6. TODO: crear JAXBContext y Marshaller para convertir la empresa a FICHERO_SALIDA
        System.out.println("Apartado b pendiente: completa los TODO de DOM y JAXB.");
    }

    // Define la raíz <EMPRESA>; JAXB accede directamente a los campos del modelo
    // TODO: añadir TITULO y la lista de departamentos con sus anotaciones JAXB
    @XmlRootElement(name = "EMPRESA")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Empresa {
        public Empresa() {
        }
    }

    // TODO: añadir el atributo id y los elementos LOC, DNOMBRE y EMPLEADOS
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Departamento {
        public Departamento() {
        }
    }

    // TODO: agrupar la lista de elementos <EMPLEADO> dentro de <EMPLEADOS>
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Empleados {
        public Empleados() {
        }
    }

    // TODO: añadir el atributo numero y los elementos APELLIDO, OFICIO, DIR,
    // FECHA_ALT, SALARIO y COMISION con sus anotaciones JAXB
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Empleado {
        public Empleado() {
        }
    }
}
