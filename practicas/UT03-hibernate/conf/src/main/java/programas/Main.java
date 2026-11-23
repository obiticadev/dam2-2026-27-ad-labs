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
                // Busca al usuario por ID para no insertarlo dos veces
                Usuario recuperado = sesion.get(Usuario.class, 2);
                boolean nuevo = recuperado == null;
                if (nuevo) {
                    sesion.persist(new Usuario(2, "Mate2"));
                    // Envía el INSERT a SQLite y limpia la caché de la sesión para releer el
                    // usuario
                    sesion.flush();
                    sesion.clear();
                    recuperado = sesion.get(Usuario.class, 2);
                }

                // Confirma la transacción antes de mostrar el resultado
                transaccion.commit();
                System.out.println(nuevo ? "Usuario guardado" : "El usuario ya existía");
                System.out.println("Nombre recuperado: " + recuperado.getNombre());
            } catch (RuntimeException e) {
                // Si algo falla antes de confirmar, deshace los cambios pendientes.
                if (transaccion.isActive()) {
                    transaccion.rollback();
                }
                throw e;
            }
        } finally {
            // Libera los recursos de Hibernate
            HibernateUtils.shutdown();
        }
    }
}
