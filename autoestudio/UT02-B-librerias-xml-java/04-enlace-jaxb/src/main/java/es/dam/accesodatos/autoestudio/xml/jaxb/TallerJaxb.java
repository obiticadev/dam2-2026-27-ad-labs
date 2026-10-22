package es.dam.accesodatos.autoestudio.xml.jaxb;

import java.io.File;
import javax.xml.bind.JAXBException;

/** UT02-B §5.3–5.4: clases anotadas y colección → archivo → objetos. */
public final class TallerJaxb {
    private TallerJaxb() {}

    /** Serializa la biblioteca a archivo, con salida legible. No se pide omitir la declaración XML. */
    public static void guardar(BibliotecaLibros biblioteca, File destino) throws JAXBException {
        // TODO: Crea JAXBContext para BibliotecaLibros y obtiene un Marshaller.
        // TODO: Activa JAXB_FORMATTED_OUTPUT y ejecuta marshal(biblioteca, destino).
    }

    /** Recupera la biblioteca con su colección. Solo archivos locales conocidos del ejercicio. */
    public static BibliotecaLibros leer(File origen) throws JAXBException {
        // TODO: Crea contexto y Unmarshaller; usa unmarshal(origen) y casting.
        return null;
    }

    /** Consulta de un archivo producido por esta práctica. */
    public static void main(String[] args) throws JAXBException {
        if (args.length == 0) { System.out.println("Uso: TallerJaxb libreria.xml"); return; }
        BibliotecaLibros biblioteca = leer(new File(args[0]));
        if (biblioteca != null) System.out.println(biblioteca.getNombre() + ": " + biblioteca.getLibros().size());
    }
}
