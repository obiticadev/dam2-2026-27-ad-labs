package lab;

import java.util.List;
import javax.persistence.criteria.*;
import org.hibernate.Session;
import lab.Modelo.*;

public final class Orm {
    public static int crear(Session s, String nombre) {
        Profesor p = new Profesor(nombre); s.persist(p); return p.getId();
    }
    public static boolean renombrar(Session s, int id, String nombre) {
        Profesor p = s.get(Profesor.class,id);
        if (p == null) return false;
        p.setNombre(nombre); return true;
    }
    public static int grafo(Session s, Modulo modulo) {
        Profesor p = new Profesor("Ana"); p.setDireccion(new Direccion("Centro"));
        p.addCorreo("a"); p.addCorreo("b"); p.addModulo(modulo);
        s.persist(p); return p.getId();
    }
    public static void quitarPrimerCorreo(Session s, int id) {
        Profesor p = s.get(Profesor.class,id);
        if (p == null) throw new IllegalArgumentException("Profesor inexistente");
        p.quitarCorreo(0);
    }
    public static void empleado(Session s, Company company) {
        Employee e = new Employee(company,7,"Ada"); s.persist(e); s.persist(new Phone("555",e));
    }
    public static Profesor fusionar(Session s, Profesor separado) {
        return (Profesor) s.merge(separado);
    }
    public static List<Profesor> buscar(Session s, String nombre) {
        return s.createQuery("from Profesor p where p.nombre=:nombre order by p.id",Profesor.class)
                .setParameter("nombre",nombre).list();
    }
    public static List<Profesor> nombrada(Session s, String nombre) {
        return s.createNamedQuery("Profesor.porNombre",Profesor.class).setParameter("nombre",nombre).list();
    }
    public static List<Profesor> pagina(Session s, int pagina, int tamanyo) {
        if (pagina < 0 || tamanyo <= 0) throw new IllegalArgumentException("Página >=0 y tamaño >0");
        int inicio = Math.multiplyExact(pagina,tamanyo);
        return s.createQuery("from Profesor p order by p.id",Profesor.class)
                .setFirstResult(inicio).setMaxResults(tamanyo).list();
    }
    public static List<Object[]> proyectar(Session s) {
        return s.createQuery("select p.id,p.nombre from Profesor p order by p.id",Object[].class).list();
    }
    public static List<Ciclo> sobreMedia(Session s) {
        return s.createQuery("from Ciclo c where c.horas > (select avg(c2.horas) from Ciclo c2) order by c.nombre",Ciclo.class).list();
    }
    public static List<Object[]> resumen(Session s) {
        return s.createQuery("select p.nombre,count(c) from Profesor p left join Ciclo c on c.profesor=p "
                + "group by p.id,p.nombre order by p.id",Object[].class).list();
    }
    public static Profesor conCorreos(Session s, int id) {
        return s.createQuery("select distinct p from Profesor p left join fetch p.correos where p.id=:id",Profesor.class)
                .setParameter("id",id).uniqueResult();
    }
    public static int masiva(Session s, String anterior, String nuevo) {
        s.flush();
        int n = s.createQuery("update Profesor p set p.nombre=:nuevo where p.nombre=:anterior")
                .setParameter("nuevo",nuevo).setParameter("anterior",anterior).executeUpdate();
        s.clear(); return n;
    }
    public static List<Profesor> criteria(Session s, String nombre) {
        CriteriaBuilder cb = s.getCriteriaBuilder();
        CriteriaQuery<Profesor> cq = cb.createQuery(Profesor.class);
        Root<Profesor> p = cq.from(Profesor.class);
        cq.select(p).where(cb.equal(p.get("nombre"),nombre)).orderBy(cb.asc(p.get("id")));
        return s.createQuery(cq).list();
    }
}
