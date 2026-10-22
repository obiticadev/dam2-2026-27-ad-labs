package es.dam.accesodatos.autoestudio.objetos;

import java.io.File;
import java.io.IOException;

/** UT02-A §2.8–2.9. Solo se deserializan archivos locales producidos por este ejercicio. */
public final class SerializacionObjetos {
    private SerializacionObjetos() {}

    /** Guarda UN objeto sobrescribiendo el archivo. @throws IOException si falla la serialización */
    public static void guardar(File archivo, Alumno alumno) throws IOException {
        // TODO: Envuelve FileOutputStream con ObjectOutputStream.
        // TODO: Escribe el objeto completo mediante writeObject y cierra el flujo.
        // TODO: No uses append: abrir otro ObjectOutputStream añade otra cabecera.
    }

    /**
     * Recupera el Alumno guardado en el archivo.
     * @throws IOException si falla la lectura o el formato de serialización
     * @throws ClassNotFoundException si no está disponible la clase del objeto
     */
    public static Alumno leer(File archivo) throws IOException, ClassNotFoundException {
        // TODO: Envuelve FileInputStream con ObjectInputStream.
        // TODO: Usa readObject(), aplica el casting a Alumno y cierra el flujo.
        return null;
    }

    /** Lee solo archivos propios del ejercicio; no descarga objetos de Internet. */
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        if (args.length == 0) { System.out.println("Uso: SerializacionObjetos archivo-propio.dat"); return; }
        Alumno alumno = leer(new File(args[0]));
        if (alumno != null) System.out.println(alumno.getNombre() + ": " + alumno.getClaveTemporal());
    }
}
