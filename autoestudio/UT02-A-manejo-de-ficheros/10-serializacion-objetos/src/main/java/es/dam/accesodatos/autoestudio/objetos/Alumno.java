package es.dam.accesodatos.autoestudio.objetos;

/**
 * Completa el mapeo de serialización: implements Serializable y transient en claveTemporal.
 * La clave no debe guardarse; tras leer tendrá el valor predeterminado null.
 * transient es una aclaración de repaso de la bóveda; no cifra los datos.
 */
public class Alumno {
    private String nombre;
    private int edad;
    private String claveTemporal;

    public Alumno(String nombre, int edad, String claveTemporal) {
        this.nombre = nombre;
        this.edad = edad;
        this.claveTemporal = claveTemporal;
    }
    public String getNombre() { return nombre; }
    public int getEdad() { return edad; }
    public String getClaveTemporal() { return claveTemporal; }
}
