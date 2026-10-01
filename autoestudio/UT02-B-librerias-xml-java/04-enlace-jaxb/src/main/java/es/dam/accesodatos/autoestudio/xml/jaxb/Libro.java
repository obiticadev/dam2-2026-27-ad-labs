package es.dam.accesodatos.autoestudio.xml.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Modelo JAXB del ejercicio, con ISBN como atributo y título/año como elementos.
 */
@XmlRootElement(name = "libro")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"titulo", "anio"})
public class Libro {

    @XmlAttribute
    private String isbn;

    @XmlElement
    private String titulo;

    @XmlElement
    private int anio;

    /**
     * Constructor requerido por JAXB para reconstruir instancias.
     */
    public Libro() {
    }

    /**
     * Construye un libro con todos sus datos de dominio.
     *
     * @param isbn identificador ISBN
     * @param titulo título del libro
     * @param anio año de publicación
     */
    public Libro(String isbn, String titulo, int anio) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.anio = anio;
    }

    /**
     * @return ISBN del libro
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * @return título del libro
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * @return año de publicación
     */
    public int getAnio() {
        return anio;
    }

    @Override
    public String toString() {
        return "Libro{isbn='" + isbn + "', titulo='" + titulo + "', anio=" + anio + "}";
    }
}
