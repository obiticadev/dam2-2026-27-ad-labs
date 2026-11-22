package lab;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import lab.Modelo.*;
import java.nio.file.Path;
import java.sql.*;
import java.util.*;
import java.util.function.Function;
import static org.junit.jupiter.api.Assertions.*;

class OrmTest {
    static SessionFactory f;
    @TempDir Path temporal;
    @BeforeAll static void abrir() { f = Arranque.fabrica(); }
    @AfterAll static void cerrar() { f.close(); }

    <T> T tx(Function<Session,T> trabajo) {
        try (Session s = f.openSession()) {
            Transaction tx = s.beginTransaction();
            try { T resultado = trabajo.apply(s); tx.commit(); return resultado; }
            catch (RuntimeException e) { if (tx.isActive()) tx.rollback(); throw e; }
        }
    }
    @BeforeEach void limpiar() {
        tx(s -> {
            s.createNativeQuery("DELETE FROM profesor_modulo").executeUpdate();
            for (String entidad : List.of("Phone","Employee","Company","Ciclo","Correo","Profesor","Direccion","Modulo","Usuario"))
                s.createQuery("delete from " + entidad).executeUpdate();
            return null;
        });
    }
    int profesor(String nombre) {
        return tx(s -> { Profesor p = new Profesor(nombre); s.persist(p); return p.getId(); });
    }
    @Test @Tag("b00") void configuracion() {
        int id = profesor("Configuración lista");
        assertEquals("Configuración lista",tx(s -> s.get(Profesor.class,id).getNombre()));
    }
    @Test @Tag("b01") void mapeoBasico() {
        int id = profesor("Ana");
        tx(s -> { Profesor p = s.get(Profesor.class,id); assertTrue(s.contains(p));
            assertEquals("Ana",p.getNombre()); assertNotNull(p.getCorreos()); return null; });
    }
    @Test @Tag("b02") void crud() {
        int id = tx(s -> Orm.crear(s,"Ana"));
        assertTrue(tx(s -> Orm.renombrar(s,id,"Ada")).booleanValue());
        assertFalse(tx(s -> Orm.renombrar(s,-1,"Nada")).booleanValue());
        assertEquals("Ada",tx(s -> s.get(Profesor.class,id).getNombre()));
        tx(s -> { s.remove(s.get(Profesor.class,id)); return null; });
        assertNull(tx(s -> s.get(Profesor.class,id)));
    }
    @Test @Tag("b03") void relacionesYHuerfano() {
        int id = tx(s -> {
            Modulo m = new Modulo("AD"); s.persist(m);
            int nuevo = Orm.grafo(s,m);
            Profesor otro = new Profesor("Luis"); otro.addModulo(m); s.persist(otro);
            return nuevo;
        });
        tx(s -> {
            Profesor p = s.get(Profesor.class,id);
            assertEquals("Centro",p.getDireccion().getCalle());
            assertSame(p,p.getDireccion().getProfesor());
            assertEquals(List.of("a","b"),p.getCorreos().stream().map(Correo::getTexto).toList());
            assertSame(p,p.getCorreos().get(0).getProfesor());
            assertEquals(2,p.getModulos().iterator().next().getProfesores().size());
            Orm.quitarPrimerCorreo(s,id); return null;
        });
        tx(s -> {
            Profesor p = s.get(Profesor.class,id);
            assertEquals(List.of("b"),p.getCorreos().stream().map(Correo::getTexto).toList());
            assertEquals(1L,s.createQuery("select count(c) from Correo c",Long.class).uniqueResult());
            s.remove(p); return null;
        });
        assertEquals(1L,tx(s -> s.createQuery("select count(m) from Modulo m",Long.class).uniqueResult()).longValue());
    }
    @Test @Tag("b04") void claveCompuesta() {
        assertEquals(new EmployeeId(1,7),new EmployeeId(1,7));
        assertEquals(new EmployeeId(1,7).hashCode(),new EmployeeId(1,7).hashCode());
        tx(s -> { Company c = new Company(1,"Empresa"); s.persist(c); Orm.empleado(s,c); return null; });
        tx(s -> {
            Employee e = s.get(Employee.class,new EmployeeId(1,7));
            assertNotNull(e); assertEquals("Ada",e.getNombre()); assertEquals(1L,e.getCompany().getId());
            assertSame(e,s.get(Phone.class,"555").getEmployee()); return null;
        });
    }
    @Test @Tag("b05") void mergeYEstados() {
        int id = profesor("Ana");
        Profesor separado = tx(s -> s.get(Profesor.class,id));
        separado.setNombre("Ada");
        tx(s -> {
            Profesor gestionado = Orm.fusionar(s,separado);
            assertNotSame(separado,gestionado); assertTrue(s.contains(gestionado));
            assertFalse(s.contains(separado)); return null;
        });
        assertEquals("Ada",tx(s -> s.get(Profesor.class,id).getNombre()));
    }
    @Test @Tag("b05") void versionDetectaEscrituraObsoleta() {
        int id = profesor("Ana");
        Profesor viejo = tx(s -> s.get(Profesor.class,id));
        tx(s -> { s.get(Profesor.class,id).setNombre("Primero"); return null; });
        viejo.setNombre("Obsoleto");
        assertThrows(javax.persistence.OptimisticLockException.class, () -> tx(s -> Orm.fusionar(s,viejo)));
        assertEquals("Primero",tx(s -> s.get(Profesor.class,id).getNombre()));
    }
    @Test @Tag("b06") void parametrosYNombrada() {
        profesor("O'Neil"); profesor("Ana");
        tx(s -> {
            assertEquals(1,Orm.buscar(s,"O'Neil").size());
            assertEquals(Orm.buscar(s,"O'Neil"),Orm.nombrada(s,"O'Neil"));
            assertTrue(Orm.buscar(s,"' OR 1=1 --").isEmpty()); return null;
        });
    }
    @Test @Tag("b07") void paginasYProyecciones() {
        profesor("A"); profesor("B"); profesor("C");
        tx(s -> {
            assertEquals("C",Orm.pagina(s,1,2).get(0).getNombre());
            assertTrue(Orm.pagina(s,4,2).isEmpty());
            assertThrows(IllegalArgumentException.class,() -> Orm.pagina(s,-1,2));
            assertThrows(IllegalArgumentException.class,() -> Orm.pagina(s,0,0));
            assertThrows(ArithmeticException.class,() -> Orm.pagina(s,Integer.MAX_VALUE,2));
            List<Object[]> filas = Orm.proyectar(s);
            assertEquals(3,filas.size()); assertEquals("A",filas.get(0)[1]); return null;
        });
    }
    @Test @Tag("b08") void agregacionSubconsultaYUnion() {
        int id = profesor("Ana"); profesor("Sin ciclos");
        tx(s -> {
            Profesor p = s.get(Profesor.class,id);
            s.persist(new Ciclo("DAM",100,p)); s.persist(new Ciclo("DAW",200,p)); return null;
        });
        tx(s -> {
            assertEquals("DAW",Orm.sobreMedia(s).get(0).getNombre());
            List<Object[]> filas = Orm.resumen(s);
            assertEquals(2,filas.size()); assertEquals(2L,filas.get(0)[1]); assertEquals(0L,filas.get(1)[1]);
            return null;
        });
    }
    @Test @Tag("b09") void fetchFueraDeSesion() {
        int id = tx(s -> { Profesor p = new Profesor("Ana"); p.addCorreo("a"); s.persist(p); return p.getId(); });
        Profesor p = tx(s -> Orm.conCorreos(s,id));
        assertTrue(Hibernate.isInitialized(p.getCorreos()));
        assertEquals("a",p.getCorreos().get(0).getTexto());
    }
    @Test @Tag("b10") void masivaLimpiaContexto() {
        int id = profesor("Ana");
        tx(s -> {
            Profesor anterior = s.get(Profesor.class,id);
            assertEquals(1,Orm.masiva(s,"Ana","Ada"));
            assertFalse(s.contains(anterior));
            assertEquals("Ada",s.get(Profesor.class,id).getNombre());
            assertEquals(0,Orm.masiva(s,"No existe","Nada")); return null;
        });
    }
    @Test @Tag("b11") void criteriaEquivalente() {
        profesor("O'Neil"); profesor("Ana");
        tx(s -> {
            assertEquals(Orm.buscar(s,"O'Neil"),Orm.criteria(s,"O'Neil"));
            assertTrue(Orm.criteria(s,"' OR 1=1 --").isEmpty()); return null;
        });
    }
    @Test @Tag("b12") void sqliteConEsquemaManual() throws Exception {
        String url = "jdbc:sqlite:" + temporal.resolve("orm.db").toAbsolutePath();
        try (Connection c = DriverManager.getConnection(url); Statement st = c.createStatement()) {
            st.executeUpdate("CREATE TABLE usuarios(id INTEGER PRIMARY KEY,nombre TEXT NOT NULL)");
        }
        Configuration cfg = new Configuration().addAnnotatedClass(Usuario.class);
        cfg.setProperty("hibernate.connection.driver_class","org.sqlite.JDBC");
        cfg.setProperty("hibernate.connection.url",url);
        cfg.setProperty("hibernate.dialect","org.sqlite.hibernate.dialect.SQLiteDialect");
        cfg.setProperty("hibernate.hbm2ddl.auto","none");
        try (SessionFactory sqlite = cfg.buildSessionFactory()) {
            try (Session s = sqlite.openSession()) {
                Transaction t = s.beginTransaction();
                try { s.persist(new Usuario(1,"Ana")); t.commit(); }
                catch (RuntimeException e) { if (t.isActive()) t.rollback(); throw e; }
            }
            try (Session s = sqlite.openSession()) {
                Transaction t = s.beginTransaction();
                try { Usuario u = s.get(Usuario.class,1); assertEquals("Ana",u.getNombre());
                    u.setNombre("Ada"); t.commit(); }
                catch (RuntimeException e) { if (t.isActive()) t.rollback(); throw e; }
            }
        }
        try (Connection c = DriverManager.getConnection(url); Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT nombre FROM usuarios WHERE id=1")) {
            assertTrue(rs.next()); assertEquals("Ada",rs.getString(1));
        }
    }
}
