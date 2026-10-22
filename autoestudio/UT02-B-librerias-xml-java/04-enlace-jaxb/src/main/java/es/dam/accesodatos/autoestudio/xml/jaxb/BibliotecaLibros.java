package es.dam.accesodatos.autoestudio.xml.jaxb;

import java.util.ArrayList;
import java.util.List;

/**
 * Completa las anotaciones JAXB 2 como en Libreria del tema (§5.1–5.2).
 * Raíz: libreria. Elementos: ListaLibro (contenedor), nombre y lugar.
 * Cada elemento de la colección se llama Libro (mayúscula).
 * Anota setters como en el ejemplo docente, sin mezclar acceso FIELD y propiedades.
 */
// TODO: @XmlRootElement(name = "libreria") con su import javax.xml.bind.annotation.
public class BibliotecaLibros {
    private List<FichaLibro> libros = new ArrayList<>();
    private String nombre;
    private String lugar;

    public BibliotecaLibros() {}
    public List<FichaLibro> getLibros() { return libros; }
    // TODO: @XmlElementWrapper(name = "ListaLibro") y @XmlElement(name = "Libro").
    public void setLibros(List<FichaLibro> libros) { this.libros = libros; }
    public String getNombre() { return nombre; }
    // TODO: @XmlElement: el nombre por defecto coincide con la propiedad.
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getLugar() { return lugar; }
    // TODO: @XmlElement.
    public void setLugar(String lugar) { this.lugar = lugar; }
}
