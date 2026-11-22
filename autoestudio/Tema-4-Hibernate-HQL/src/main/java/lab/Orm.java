package lab;

import java.util.List;
import org.hibernate.Session;
import lab.Modelo.*;

/** Recibe sesión y transacción del llamante. No cerrar ni confirmar aquí. */
public final class Orm {
    // B02: persistir nuevo Profesor y devolver su id generado.
    public static int crear(Session s, String nombre) {
        throw new UnsupportedOperationException("B02: persist");
    }
    // B02: buscar; false si no existe; cambiar nombre mediante dirty checking.
    public static boolean renombrar(Session s, int id, String nombre) {
        throw new UnsupportedOperationException("B02: get y cambio gestionado");
    }
    // B03: nuevo Profesor con Dirección 'Centro', dos correos ('a','b') y módulo
    // compartido recibido ya gestionado. Mantener ambos lados y persistir grafo.
    public static int grafo(Session s, Modulo modulo) {
        throw new UnsupportedOperationException("B03: relaciones y cascada");
    }
    // B03: cargar Profesor y quitar SOLO el primer correo (orphanRemoval).
    public static void quitarPrimerCorreo(Session s, int id) {
        throw new UnsupportedOperationException("B03: huérfano");
    }
    // B04: Company recibida gestionada; Employee con número 7 y nombre 'Ada';
    // Phone '555' apunta a esa clave compuesta. Persistir ambos.
    public static void empleado(Session s, Company company) {
        throw new UnsupportedOperationException("B04: EmbeddedId y MapsId");
    }
    // B05: no asociar el original detached: devolver instancia gestionada de merge.
    public static Profesor fusionar(Session s, Profesor separado) {
        throw new UnsupportedOperationException("B05: merge");
    }
    // B06: filtro exacto parametrizado, orden id.
    public static List<Profesor> buscar(Session s, String nombre) {
        throw new UnsupportedOperationException("B06: HQL");
    }
    public static List<Profesor> nombrada(Session s, String nombre) {
        throw new UnsupportedOperationException("B06: NamedQuery");
    }
    // B07: página desde 0; tamaño>0; detectar overflow; orden id.
    public static List<Profesor> pagina(Session s, int pagina, int tamanyo) {
        throw new UnsupportedOperationException("B07: paginación");
    }
    public static List<Object[]> proyectar(Session s) {
        throw new UnsupportedOperationException("B07: id,nombre orden id");
    }
    // B08: Ciclos con horas > media, orden nombre.
    public static List<Ciclo> sobreMedia(Session s) {
        throw new UnsupportedOperationException("B08: subconsulta");
    }
    // B08: nombre profesor y número de ciclos, incluyendo cero; orden p.id.
    public static List<Object[]> resumen(Session s) {
        throw new UnsupportedOperationException("B08: agrupación LEFT JOIN");
    }
    // B09: recuperar con correos inicializados, sin cambiar fetch global a EAGER.
    public static Profesor conCorreos(Session s, int id) {
        throw new UnsupportedOperationException("B09: JOIN FETCH");
    }
    // B10: UPDATE masivo con parámetros, flush antes y clear después; recuento.
    public static int masiva(Session s, String anterior, String nuevo) {
        throw new UnsupportedOperationException("B10: DML masiva");
    }
    // B11: mismo filtro de buscar con Criteria (ampliación editorial).
    public static List<Profesor> criteria(Session s, String nombre) {
        throw new UnsupportedOperationException("B11: Criteria");
    }
}
