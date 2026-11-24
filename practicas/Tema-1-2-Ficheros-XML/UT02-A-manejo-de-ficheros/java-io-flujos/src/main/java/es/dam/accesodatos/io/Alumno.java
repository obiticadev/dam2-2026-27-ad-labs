package es.dam.accesodatos.io;

import java.io.Serializable;

public class Alumno implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String dni;
    private final String nmatricula;
    private final String nombre;
    private final String ape1;
    private final String ape2;

    public Alumno(String dni, String nmatricula, String nombre, String ape1, String ape2) {
        this.dni = dni;
        this.nmatricula = nmatricula;
        this.nombre = nombre;
        this.ape1 = ape1;
        this.ape2 = ape2;
    }

    @Override
    public String toString() {
        return "Alumno [dni=" + dni + ", nmatricula=" + nmatricula + ", nombre=" + nombre
                + ", ape1=" + ape1 + ", ape2=" + ape2 + "]";
    }
}
