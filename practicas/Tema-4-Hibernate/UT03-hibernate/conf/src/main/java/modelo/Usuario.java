package modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Vincula la clase Usuario con la tabla usuarios
@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    private int id;
    private String nombre;

    // Hibernate usa este constructor al recuperar un usuario
    public Usuario() {
    }

    public Usuario(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
