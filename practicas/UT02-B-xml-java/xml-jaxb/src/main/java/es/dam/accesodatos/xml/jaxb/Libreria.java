package es.dam.accesodatos.xml.jaxb;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Libreria {
    private ArrayList<Libro> listaLibro = new ArrayList<>();
    private String nombre;
    private String lugar;

    public Libreria() {
    }

    public ArrayList<Libro> getListaLibro() {
        return listaLibro;
    }

    @XmlElementWrapper(name = "ListaLibro")
    @XmlElement(name = "Libro")
    public void setListaLibro(ArrayList<Libro> listaLibro) {
        this.listaLibro = listaLibro;
    }

    public String getNombre() {
        return nombre;
    }

    @XmlElement
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getLugar() {
        return lugar;
    }

    @XmlElement
    public void setLugar(String lugar) {
        this.lugar = lugar;
    }
}
