package es.dam.accesodatos.autoestudio.objetos;

import static org.junit.jupiter.api.Assertions.*;
import java.io.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SerializacionObjetosTest {
    @TempDir File carpeta;

    @Test
    void elModeloEsSerializableYNoGuardaElCampoTemporal() throws Exception {
        Alumno original = new Alumno("Ángela", 20, "solo-en-memoria");
        assertInstanceOf(Serializable.class, original, "Completa implements Serializable en Alumno");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) { out.writeObject(original); }
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            Alumno copia = (Alumno) in.readObject();
            assertEquals("Ángela", copia.getNombre());
            assertEquals(20, copia.getEdad());
            assertNull(copia.getClaveTemporal(), "Marca claveTemporal como transient");
        }
    }
    @Test
    void guardaUnObjetoQuePuedeLeerJavaSinUsarElLectorDelAlumno() throws Exception {
        File f = new File(carpeta, "alumno.dat");
        SerializacionObjetos.guardar(f, new Alumno("Ana", 21, "temporal"));
        assertTrue(f.isFile(), "Completa guardar(): debe crear el archivo de objetos");
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
            Alumno copia = (Alumno) in.readObject();
            assertEquals("Ana", copia.getNombre());
            assertEquals(21, copia.getEdad());
            assertNull(copia.getClaveTemporal());
        }
    }
    @Test
    void leeUnArchivoPreparadoPorJava() throws Exception {
        File f = new File(carpeta, "alumno.dat");
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f))) {
            out.writeObject(new Alumno("Luis", 22, "temporal"));
        }
        Alumno copia = SerializacionObjetos.leer(f);
        assertNotNull(copia);
        assertEquals("Luis", copia.getNombre());
        assertEquals(22, copia.getEdad());
        assertNull(copia.getClaveTemporal());
    }
    @Test
    void archivoAusenteNoSeConvierteEnNull() {
        assertThrows(IOException.class, () -> SerializacionObjetos.leer(new File(carpeta, "ausente")));
    }
}
