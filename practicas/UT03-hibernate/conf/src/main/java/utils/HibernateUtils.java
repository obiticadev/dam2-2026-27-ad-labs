package utils;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtils {
    private static final SessionFactory FACTORY = new Configuration().configure().buildSessionFactory();

    public static SessionFactory getSessionFactory() {
        return FACTORY;
    }

    public static void shutdown() {
        FACTORY.close();
    }
}