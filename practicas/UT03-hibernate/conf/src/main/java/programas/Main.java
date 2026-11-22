package programas;

import org.hibernate.Session;
import org.hibernate.Transaction;

import modelo.Usuario;
import utils.HibernateUtils;

public class Main {
    public static void main(String[] args) {
        try (Session sesion = HibernateUtils.getSessionFactory().openSession()) {

            Transaction transaccion = sesion.beginTransaction();

            try {
                Usuario usuario = new Usuario(1, "Ana");
                sesion.persist(usuario);

                sesion.flush();
                sesion.clear();

                Usuario recuperado = sesion.get(Usuario.class, 1);

                if (recuperado != null) {
                    System.out.println("Nombre recuperado: " + recuperado.getNombre());
                }

                transaccion.commit();
                System.out.println("Usuario guardado");
            } catch (RuntimeException e) {
                if (transaccion.isActive()) {
                    transaccion.rollback();
                }
                throw e;
            }
        }
    }
}