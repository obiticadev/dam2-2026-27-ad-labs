package ejercicioxml;

import java.io.File;
import java.util.ArrayList;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

// Leer DatosEmpresa.xml con JAXB (unmarshal) y mostrar los departamentos y empleados.
public class LeerDatosEmpresa {

    private static final File FICHERO_XML = new File("DatosEmpresa.xml");

    public static void main(String[] args) throws Exception {
        // 1. Cargar el fichero XML con JAXB y deserializarlo a objetos.
        JAXBContext contexto = JAXBContext.newInstance(Empresa.class);
        Unmarshaller unmarshaller = contexto.createUnmarshaller();
        Empresa empresa = (Empresa) unmarshaller.unmarshal(FICHERO_XML);

        // 2. Mostrar el título y los departamentos con sus empleados.
        System.out.println(empresa.titulo);
        for (Departamento departamento : empresa.departamentos) {
            System.out.printf("%nDepartamento %d - %s (%s)%n",
                    departamento.id,
                    departamento.nombre,
                    departamento.loc);

            for (Empleado empleado : departamento.empleados.empleados) {
                System.out.printf("  Empleado %s | %s | Salario: %s | Comisión: %s%n",
                        empleado.apellido,
                        empleado.oficio,
                        empleado.salario,
                        empleado.comision);
            }
        }
    }

    // JAXB lee los campos directamente; propOrder conserva el orden del enunciado.
    @XmlRootElement(name = "EMPRESA")
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(propOrder = { "titulo", "departamentos" })
    public static class Empresa {
        @XmlElement(name = "TITULO")
        private String titulo;

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
