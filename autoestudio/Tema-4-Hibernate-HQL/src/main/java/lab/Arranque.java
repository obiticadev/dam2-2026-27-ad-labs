package lab;

import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import javax.persistence.Entity;

/** Bloque 00 funcional. Una fábrica por aplicación; sesiones cortas. */
public final class Arranque {
    public static SessionFactory fabrica() {
        Configuration cfg = new Configuration().configure();
        for (Class<?> tipo : Modelo.class.getDeclaredClasses()) {
            if (tipo.isAnnotationPresent(Entity.class)) cfg.addAnnotatedClass(tipo);
        }
        return cfg.buildSessionFactory();
    }
    public static void main(String[] args) {
        try (SessionFactory f = fabrica(); Session s = f.openSession()) {
            Transaction tx = s.beginTransaction();
            try {
                Modelo.Profesor p = new Modelo.Profesor("Arranque listo");
                s.persist(p);
                s.flush(); s.clear();
                System.out.println(s.get(Modelo.Profesor.class,p.getId()).getNombre());
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }
}
