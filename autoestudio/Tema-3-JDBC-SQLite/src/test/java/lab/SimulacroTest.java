package lab;

import org.junit.jupiter.api.*;
import java.sql.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@Tag("b10")
class SimulacroTest {
    Connection c;
    @BeforeEach void preparar() throws Exception {
        c = DriverManager.getConnection("jdbc:sqlite::memory:");
        try (Statement st = c.createStatement()) {
            st.execute("PRAGMA foreign_keys=ON");
            st.execute("CREATE TABLE cursos(id INTEGER PRIMARY KEY,nombre TEXT NOT NULL UNIQUE,plazas INTEGER NOT NULL CHECK(plazas>=0))");
            st.execute("CREATE TABLE matriculas(id INTEGER PRIMARY KEY,alumno TEXT NOT NULL,curso_id INTEGER NOT NULL REFERENCES cursos(id),UNIQUE(alumno,curso_id))");
            st.execute("INSERT INTO cursos VALUES(1,'JDBC',2),(2,'XML',0)");
        }
    }
    @AfterEach void cerrar() throws Exception { c.close(); }
    int plazas() throws Exception {
        try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery("SELECT plazas FROM cursos WHERE id=1")) {
            rs.next(); return rs.getInt(1);
        }
    }
    @Test void exitoYListado() throws Exception {
        assertTrue(Simulacro.matricular(c,"O'Neil",1)>0);
        assertTrue(Simulacro.matricular(c,"Ana",1)>0);
        assertEquals(0,plazas());
        assertEquals(List.of("Ana","O'Neil"),Simulacro.alumnos(c,"JDBC"));
        assertTrue(Simulacro.alumnos(c,"' OR 1=1 --").isEmpty());
        assertTrue(c.getAutoCommit());
    }
    @Test void duplicadoNoConsumePlaza() throws Exception {
        Simulacro.matricular(c,"Ana",1);
        assertThrows(SQLException.class,() -> Simulacro.matricular(c,"Ana",1));
        assertEquals(1,plazas());
        assertEquals(List.of("Ana"),Simulacro.alumnos(c,"JDBC"));
        assertTrue(c.getAutoCommit());
    }
    @Test void cursoInexistenteOSinPlazas() throws Exception {
        assertThrows(SQLException.class,() -> Simulacro.matricular(c,"Ana",999));
        assertThrows(SQLException.class,() -> Simulacro.matricular(c,"Ana",2));
        assertEquals(2,plazas());
        assertTrue(Simulacro.alumnos(c,"XML").isEmpty());
        assertTrue(c.getAutoCommit());
    }
}
