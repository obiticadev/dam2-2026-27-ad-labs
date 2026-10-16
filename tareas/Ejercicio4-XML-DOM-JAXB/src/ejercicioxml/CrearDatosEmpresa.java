package ejercicioxml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class CrearDatosEmpresa {

    private static final File FICHERO_ENTRADA = new File("datos/DepartamentosEmpleados.xml");
    private static final File FICHERO_SALIDA = new File("DatosEmpresa.xml");
    private static final String TITULO = "DATOS DE LAS TABLA DEPART y EMPLE";

    public static void main(String[] args) {
        try {
            Document documento = leerDocumento();
            Empresa empresa = crearEmpresa(documento);
            escribirEmpresa(empresa);
            System.out.println("Fichero creado: " + FICHERO_SALIDA);
        } catch (IOException | ParserConfigurationException | SAXException | JAXBException e) {
            System.err.println("No se pudo crear " + FICHERO_SALIDA + ": " + e.getMessage());
        }
    }

    private static Document leerDocumento() throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory fabrica = DocumentBuilderFactory.newInstance();
        DocumentBuilder constructor = fabrica.newDocumentBuilder();
        Document documento = constructor.parse(FICHERO_ENTRADA);
        documento.getDocumentElement().normalize();
        return documento;
    }

    private static Empresa crearEmpresa(Document documento) {
        Map<Integer, Departamento> departamentos = new LinkedHashMap<>();
        NodeList filasDepartamentos = documento.getElementsByTagName("DEP_ROW");

        for (int i = 0; i < filasDepartamentos.getLength(); i++) {
            Element fila = (Element) filasDepartamentos.item(i);
            Departamento departamento = new Departamento();
            departamento.id = Integer.parseInt(texto(fila, "DEPT_NO"));
            departamento.loc = texto(fila, "LOC");
            departamento.nombre = texto(fila, "DNOMBRE");
            departamento.empleados = new Empleados();
            departamentos.put(departamento.id, departamento);
        }

        NodeList filasEmpleados = documento.getElementsByTagName("EMP_ROW");
        for (int i = 0; i < filasEmpleados.getLength(); i++) {
            Element fila = (Element) filasEmpleados.item(i);
            int numeroDepartamento = Integer.parseInt(texto(fila, "DEPT_NO"));
            Departamento departamento = departamentos.get(numeroDepartamento);
            if (departamento == null) {
                throw new IllegalArgumentException("No existe el departamento " + numeroDepartamento);
            }

            Empleado empleado = new Empleado();
            empleado.numero = Integer.parseInt(texto(fila, "EMP_NO"));
            empleado.apellido = texto(fila, "APELLIDO");
            empleado.oficio = texto(fila, "OFICIO");
            empleado.dir = texto(fila, "DIR");
            empleado.fechaAlta = texto(fila, "FECHA_ALT");
            empleado.salario = texto(fila, "SALARIO");
            empleado.comision = texto(fila, "COMISION");
            departamento.empleados.empleados.add(empleado);
        }

        Empresa empresa = new Empresa();
        empresa.titulo = TITULO;
        empresa.departamentos.addAll(departamentos.values());
        return empresa;
    }

    private static void escribirEmpresa(Empresa empresa) throws JAXBException {
        JAXBContext contexto = JAXBContext.newInstance(Empresa.class);
        Marshaller marshaller = contexto.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        marshaller.marshal(empresa, FICHERO_SALIDA);
    }

    private static String texto(Element elemento, String nombreEtiqueta) {
        return elemento.getElementsByTagName(nombreEtiqueta).item(0).getTextContent().trim();
    }

    @XmlRootElement(name = "EMPRESA")
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "EmpresaType", propOrder = { "titulo", "departamentos" })
    public static class Empresa {
        @XmlElement(name = "TITULO")
        private String titulo;

        @XmlElement(name = "DEPARTAMENTO")
        private ArrayList<Departamento> departamentos = new ArrayList<>();

        public Empresa() {
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "DepartamentoType", propOrder = { "loc", "nombre", "empleados" })
    public static class Departamento {
        @XmlAttribute(name = "id")
        private int id;

        @XmlElement(name = "LOC")
        private String loc;

        @XmlElement(name = "DNOMBRE")
        private String nombre;

        @XmlElement(name = "EMPLEADOS")
        private Empleados empleados;

        public Departamento() {
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "EmpleadosType", propOrder = { "empleados" })
    public static class Empleados {
        @XmlElement(name = "EMPLEADO")
        private ArrayList<Empleado> empleados = new ArrayList<>();

        public Empleados() {
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "EmpleadoType", propOrder = {
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

        public Empleado() {
        }
    }
}
