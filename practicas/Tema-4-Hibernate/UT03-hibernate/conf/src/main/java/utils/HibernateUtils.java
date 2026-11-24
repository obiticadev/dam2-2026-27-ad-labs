package utils;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtils {
    // Carga hibernate.cfg.xml y crea una fábrica compartida para todas las sesiones
    private static final SessionFactory FACTORY = new Configuration().configure().buildSessionFactory();

    public static SessionFactory getSessionFactory() {
        return FACTORY;
    }

    // Cierra la fábrica y libera las conexiones al terminar el programa
    public static void shutdown() {
        FACTORY.close();
    }
}
