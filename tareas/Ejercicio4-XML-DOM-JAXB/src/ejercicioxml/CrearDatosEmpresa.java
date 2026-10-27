package ejercicioxml;

import java.io.File;
import java.util.ArrayList;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

// Apartado b: leer los datos con DOM y escribir DatosEmpresa.xml con JAXB.
public class CrearDatosEmpresa {

    private static final File FICHERO_ENTRADA = new File("datos/DepartamentosEmpleados.xml");
    private static final File FICHERO_SALIDA = new File("DatosEmpresa.xml");

    public static void main(String[] args) throws Exception {
        // 1. Cargar el fichero de entrada con DOM.
        DocumentBuilderFactory fabrica = DocumentBuilderFactory.newInstance();
        DocumentBuilder constructor = fabrica.newDocumentBuilder();
        Document documento = constructor.parse(FICHERO_ENTRADA);

        // 2. Crear la empresa y obtener las listas del XML original.
        Empresa empresa = new Empresa();
        NodeList filasDepartamentos = documento.getElementsByTagName("DEP_ROW");
        NodeList filasEmpleados = documento.getElementsByTagName("EMP_ROW");

        // 3. Crear cada departamento y añadir sus empleados, igual que en el apartado
        // a.
        for (int i = 0; i < filasDepartamentos.getLength(); i++) {
            Element fila = (Element) filasDepartamentos.item(i);
            Departamento departamento = new Departamento();
            departamento.id = Integer.parseInt(texto(fila, "DEPT_NO"));
            departamento.loc = texto(fila, "LOC");
            departamento.nombre = texto(fila, "DNOMBRE");
            for (int j = 0; j < filasEmpleados.getLength(); j++) {
                Element filaEmpleado = (Element) filasEmpleados.item(j);
                int numeroDepartamento = Integer.parseInt(texto(filaEmpleado, "DEPT_NO"));
                if (departamento.id == numeroDepartamento) {
                    Empleado empleado = new Empleado();
                    empleado.numero = Integer.parseInt(texto(filaEmpleado, "EMP_NO"));
                    empleado.apellido = texto(filaEmpleado, "APELLIDO");
                    empleado.oficio = texto(filaEmpleado, "OFICIO");
                    empleado.dir = texto(filaEmpleado, "DIR");
                    empleado.fechaAlta = texto(filaEmpleado, "FECHA_ALT");
                    empleado.salario = texto(filaEmpleado, "SALARIO");
                    empleado.comision = texto(filaEmpleado, "COMISION");
                    departamento.empleados.empleados.add(empleado);
                }
            }
            empresa.departamentos.add(departamento);
        }

        // 4. Convertir los objetos a XML con JAXB y guardar el resultado.
        JAXBContext contexto = JAXBContext.newInstance(Empresa.class);
        Marshaller marshaller = contexto.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(empresa, FICHERO_SALIDA);
        System.out.println("Fichero creado: " + FICHERO_SALIDA);
    }

    // Obtener el texto de una etiqueta del XML original.
    private static String texto(Element elemento, String nombreEtiqueta) {
        return elemento.getElementsByTagName(nombreEtiqueta).item(0).getTextContent().trim();
    }

    // JAXB lee los campos directamente; propOrder conserva el orden del enunciado.
    @XmlRootElement(name = "EMPRESA")
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(propOrder = { "titulo", "departamentos" })
    public static class Empresa {
        @XmlElement(name = "TITULO")
        private String titulo = "DATOS DE LAS TABLA DEPART y EMPLE";

        @XmlElement(name = "DEPARTAMENTO")
        private ArrayList<Departamento> departamentos = new ArrayList<>();
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(propOrder = { "loc", "nombre", "empleados" })
    public static class Departamento {
        @XmlAttribute(name = "id")
        private int id;

        @XmlElement(name = "LOC")
        private String loc;

        @XmlElement(name = "DNOMBRE")
        private String nombre;

        @XmlElement(name = "EMPLEADOS")
        private Empleados empleados = new Empleados();
    }

    // Esta clase crea el contenedor <EMPLEADOS>, incluso si no hay empleados.
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Empleados {
        @XmlElement(name = "EMPLEADO")
        private ArrayList<Empleado> empleados = new ArrayList<>();
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(propOrder = {
            "apellido", "oficio", "dir", "fechaAlta", "salario", "comision"
    })
    public static class Empleado {
        @XmlAttribute(name = "numero")
        private int numero;

        @XmlElement(name = "APELLIDO")
        private String apellido;

        @XmlElement(name = "OFICIO")
        private String oficio;

        @XmlElement(name = "DIR")
        private String dir;

        @XmlElement(name = "FECHA_ALT")
        private String fechaAlta;

        @XmlElement(name = "SALARIO")
        private String salario;

        @XmlElement(name = "COMISION")
        private String comision;
    }
}
