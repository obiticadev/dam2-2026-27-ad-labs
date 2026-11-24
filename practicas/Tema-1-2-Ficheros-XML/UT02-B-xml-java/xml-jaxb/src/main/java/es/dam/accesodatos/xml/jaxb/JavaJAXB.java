package es.dam.accesodatos.xml.jaxb;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class JavaJAXB {
    private static final File FICHERO_XML = new File("target/demo-data/libreria-jaxb.xml");

    public static void main(String[] args) throws JAXBException, java.io.IOException {
        ArrayList<Libro> libros = new ArrayList<>();

        Libro libro1 = new Libro();
        libro1.setIsbn("978-0060554736");
        libro1.setNombre("The Game");
        libro1.setAutor("Neil Strauss");
        libro1.setEditorial("HarperCollins");
        libros.add(libro1);

        Libro libro2 = new Libro();
        libro2.setIsbn("978-3832180577");
        libro2.setNombre("Feuchtgebiete");
        libro2.setAutor("Charlotte Roche");
        libro2.setEditorial("Dumont Buchverlag");
        libros.add(libro2);

        Libreria libreria = new Libreria();
        libreria.setNombre("Librería sin límite");
        libreria.setLugar("Barrio Obrero");
        libreria.setListaLibro(libros);

        Files.createDirectories(Path.of(FICHERO_XML.toURI()).getParent());
        JAXBContext context = JAXBContext.newInstance(Libreria.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(libreria, System.out);
        marshaller.marshal(libreria, FICHERO_XML);

        System.out.println("\nSalida deserializada del XML:");
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Libreria recuperada = (Libreria) unmarshaller.unmarshal(FICHERO_XML);
        for (Libro libro : recuperada.getListaLibro()) {
            System.out.println("Libro: " + libro.getNombre() + " de " + libro.getAutor());
        }
    }
}
