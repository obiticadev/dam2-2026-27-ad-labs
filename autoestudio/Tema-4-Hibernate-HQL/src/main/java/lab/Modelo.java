package lab;

import java.io.Serializable;
import java.util.*;
import javax.persistence.*;

/** Modelo de apoyo resuelto. B01 y B04: reprodúcelo desde cero según el enunciado. */
public final class Modelo {
    @Entity(name = "Profesor") @Table(name = "profesores")
    @NamedQuery(name = "Profesor.porNombre", query = "from Profesor p where p.nombre=:nombre order by p.id")
    public static class Profesor {
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer id;
        @Column(nullable = false) private String nombre;
        @Version private long version;
        @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
        @JoinColumn(name = "direccion_id", unique = true) private Direccion direccion;
        @OneToMany(mappedBy = "profesor", cascade = CascadeType.ALL, orphanRemoval = true)
        @OrderColumn(name = "posicion") private List<Correo> correos = new ArrayList<>();
        @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
        @JoinTable(name = "profesor_modulo", joinColumns = @JoinColumn(name = "profesor_id"),
                inverseJoinColumns = @JoinColumn(name = "modulo_id"))
        private Set<Modulo> modulos = new HashSet<>();
        public Profesor() {}
        public Profesor(String nombre) { this.nombre = nombre; }
        public Integer getId() { return id; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public long getVersion() { return version; }
        public Direccion getDireccion() { return direccion; }
        public List<Correo> getCorreos() { return correos; }
        public Set<Modulo> getModulos() { return modulos; }
        public void setDireccion(Direccion nueva) {
            if (direccion != null) direccion.profesor = null;
            direccion = nueva;
            if (nueva != null) nueva.profesor = this;
        }
        public void addCorreo(String texto) {
            Correo correo = new Correo(); correo.texto = texto; correo.profesor = this; correos.add(correo);
        }
        public void quitarCorreo(int indice) { Correo correo = correos.remove(indice); correo.profesor = null; }
        public void addModulo(Modulo m) { modulos.add(m); m.profesores.add(this); }
        public void quitarModulo(Modulo m) { modulos.remove(m); m.profesores.remove(this); }
    }

    @Entity(name = "Direccion")
    public static class Direccion {
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer id;
        private String calle;
        @OneToOne(mappedBy = "direccion") private Profesor profesor;
        public Direccion() {}
        public Direccion(String calle) { this.calle = calle; }
        public String getCalle() { return calle; }
        public Profesor getProfesor() { return profesor; }
    }

    @Entity(name = "Correo")
    public static class Correo {
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer id;
        private String texto;
        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "profesor_id", nullable = false) private Profesor profesor;
        public Correo() {}
        public String getTexto() { return texto; }
        public Profesor getProfesor() { return profesor; }
    }

    @Entity(name = "Modulo")
    public static class Modulo {
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer id;
        private String nombre;
        @ManyToMany(mappedBy = "modulos") private Set<Profesor> profesores = new HashSet<>();
        public Modulo() {}
        public Modulo(String nombre) { this.nombre = nombre; }
        public Integer getId() { return id; }
        public Set<Profesor> getProfesores() { return profesores; }
    }

    @Entity(name = "Ciclo")
    public static class Ciclo {
        @Id @SequenceGenerator(name = "ciclo_seq", sequenceName = "ciclo_seq", allocationSize = 1)
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ciclo_seq") private Long id;
        private String nombre;
        private int horas;
        @ManyToOne(fetch = FetchType.LAZY) private Profesor profesor;
        public Ciclo() {}
        public Ciclo(String nombre, int horas, Profesor profesor) {
            this.nombre = nombre; this.horas = horas; this.profesor = profesor;
        }
        public String getNombre() { return nombre; }
        public int getHoras() { return horas; }
    }

    @Entity(name = "Company")
    public static class Company {
        @Id private Long id;
        private String nombre;
        public Company() {}
        public Company(long id, String nombre) { this.id = id; this.nombre = nombre; }
        public Long getId() { return id; }
    }

    @Embeddable
    public static class EmployeeId implements Serializable {
        private static final long serialVersionUID = 1L;
        @Column(name = "company_id") private Long companyId;
        @Column(name = "employee_number") private Long numero;
        public EmployeeId() {}
        public EmployeeId(long companyId, long numero) { this.companyId = companyId; this.numero = numero; }
        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof EmployeeId)) return false;
            EmployeeId otra = (EmployeeId) o;
            return Objects.equals(companyId,otra.companyId) && Objects.equals(numero,otra.numero);
        }
        @Override public int hashCode() { return Objects.hash(companyId,numero); }
    }

    @Entity(name = "Employee")
    public static class Employee {
        @EmbeddedId private EmployeeId id;
        private String nombre;
        @MapsId("companyId") @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "company_id") private Company company;
        public Employee() {}
        public Employee(Company company, long numero, String nombre) {
            this.company = company; this.id = new EmployeeId(company.getId(),numero); this.nombre = nombre;
        }
        public Company getCompany() { return company; }
        public String getNombre() { return nombre; }
    }

    @Entity(name = "Phone")
    public static class Phone {
        @Id private String numero;
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumns({@JoinColumn(name = "company_id", referencedColumnName = "company_id"),
                @JoinColumn(name = "employee_number", referencedColumnName = "employee_number")})
        private Employee employee;
        public Phone() {}
        public Phone(String numero, Employee employee) { this.numero = numero; this.employee = employee; }
        public Employee getEmployee() { return employee; }
    }

    // Entidad de ensayo SQLite: ids asignados, sin secuencias ni creación ORM.
    @Entity(name = "Usuario") @Table(name = "usuarios")
    public static class Usuario {
        @Id private int id;
        @Column(nullable = false) private String nombre;
        public Usuario() {}
        public Usuario(int id, String nombre) { this.id = id; this.nombre = nombre; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
    }
}
