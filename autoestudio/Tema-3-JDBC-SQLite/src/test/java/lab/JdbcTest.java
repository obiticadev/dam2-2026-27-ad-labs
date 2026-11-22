package lab;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.sql.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class JdbcTest {
    @TempDir Path temporal;
    Connection c;

    @BeforeEach void abrir() throws Exception {
        c = Arranque.abrir(temporal.resolve("practica.db"));
    }
    @AfterEach void cerrar() throws Exception { c.close(); }

    // Fixture independiente de B01: ningún bloque necesita resolver el anterior.
    void datos() throws Exception {
        sql("CREATE TABLE grupos(id INTEGER PRIMARY KEY,nombre TEXT NOT NULL UNIQUE)");
        sql("CREATE TABLE personas(id INTEGER PRIMARY KEY,nombre TEXT NOT NULL,edad INTEGER CHECK(edad>=0),"
                + "grupo_id INTEGER NOT NULL REFERENCES grupos(id) ON DELETE CASCADE)");
        sql("INSERT INTO grupos VALUES(1,'DAM'),(2,'DAW'),(3,'Vacío')");
        sql("INSERT INTO personas VALUES(1,'Ana',20,1),(2,'Ana',17,1),(3,'Luis',NULL,2)");
    }
    void sql(String sql) throws Exception { try (Statement st = c.createStatement()) { st.execute(sql); } }
    String escalar(String sql) throws Exception {
        try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            assertTrue(rs.next()); return rs.getString(1);
        }
    }

    @Test @Tag("b00") void configuracion() throws Exception {
        assertEquals("SQLite", c.getMetaData().getDatabaseProductName());
        assertEquals("1", escalar("PRAGMA foreign_keys"));
    }
    @Test @Tag("b01") void esquemaYRestricciones() throws Exception {
        Jdbc.crear(c);
        sql("INSERT INTO grupos(nombre) VALUES('DAM')");
        sql("INSERT INTO personas(nombre,grupo_id) VALUES('Ana',1)");
        assertThrows(SQLException.class, () -> sql("INSERT INTO personas(nombre,edad,grupo_id) VALUES('X',-1,1)"));
        assertThrows(SQLException.class, () -> sql("INSERT INTO personas(nombre,grupo_id) VALUES('X',999)"));
        assertThrows(SQLException.class, () -> sql("INSERT INTO grupos(nombre) VALUES('DAM')"));
        assertThrows(SQLException.class, () -> sql("INSERT INTO personas(nombre,grupo_id) VALUES(NULL,1)"));
        sql("DELETE FROM grupos WHERE id=1");
        assertEquals("0", escalar("SELECT COUNT(*) FROM personas"));
    }
    @Test @Tag("b02") void consultaSegura() throws Exception {
        datos();
        assertEquals(List.of(new Jdbc.Persona(1,"Ana",20,1)), Jdbc.buscar(c,"Ana",18));
        assertTrue(Jdbc.buscar(c,"' OR 1=1 --",0).isEmpty());
        assertTrue(Jdbc.buscar(c,"Luis",0).isEmpty()); // NULL no satisface edad>=0.
    }
    @Test @Tag("b03") void crudYNulos() throws Exception {
        datos();
        int id = Jdbc.insertar(c,"O'Neil",null,1);
        assertTrue(id > 3);
        assertNull(escalar("SELECT edad FROM personas WHERE id=" + id));
        assertEquals("O'Neil", escalar("SELECT nombre FROM personas WHERE id=" + id));
        assertEquals(1, Jdbc.renombrar(c,id,"Nuevo"));
        assertEquals("Nuevo", escalar("SELECT nombre FROM personas WHERE id=" + id));
        assertEquals(0, Jdbc.renombrar(c,999,"Nada"));
        assertEquals(1, Jdbc.borrar(c,id));
        assertEquals(0, Jdbc.borrar(c,id));
        assertEquals("3", escalar("SELECT COUNT(*) FROM personas"));
    }
    @Test @Tag("b04") void metadatos() throws Exception {
        datos();
        assertEquals(List.of("grupos","personas"), Jdbc.tablas(c));
        List<String> columnas = Jdbc.columnas(c);
        assertEquals(2,columnas.size());
        assertTrue(columnas.get(0).matches("codigo:.+"));
        assertTrue(columnas.get(1).matches("nombre:.+"));
        assertFalse(c.isClosed());
    }
    @Test @Tag("b05") void commit() throws Exception {
        datos(); Jdbc.trasladar(c,1,2,"Web");
        assertEquals("2",escalar("SELECT grupo_id FROM personas WHERE id=1"));
        assertEquals("Web",escalar("SELECT nombre FROM grupos WHERE id=2"));
        assertTrue(c.getAutoCommit());
    }
    @Test @Tag("b05") void rollbackTrasPrimeraEscritura() throws Exception {
        datos();
        assertThrows(SQLException.class, () -> Jdbc.trasladar(c,1,2,"DAM")); // UNIQUE falla en segundo UPDATE.
        assertEquals("1",escalar("SELECT grupo_id FROM personas WHERE id=1"));
        assertEquals("DAW",escalar("SELECT nombre FROM grupos WHERE id=2"));
        assertTrue(c.getAutoCommit());
        assertThrows(SQLException.class, () -> Jdbc.trasladar(c,999,2,"Web"));
        c.setAutoCommit(false);
        assertThrows(SQLException.class, () -> Jdbc.trasladar(c,1,2,"Web"));
        c.rollback(); c.setAutoCommit(true);
    }
    @Test @Tag("b06") void savepoint() throws Exception {
        datos(); Jdbc.cambioParcial(c,1);
        assertEquals("Ana Maria",escalar("SELECT nombre FROM personas WHERE id=1"));
        assertEquals("20",escalar("SELECT edad FROM personas WHERE id=1"));
        assertTrue(c.getAutoCommit());
    }
    @Test @Tag("b07") void gruposConCeros() throws Exception {
        datos(); assertEquals(List.of("DAM:2","DAW:1","Vacío:0"),Jdbc.resumen(c));
    }
    @Test @Tag("b08") void cursorH2() throws Exception {
        try (Connection h = DriverManager.getConnection("jdbc:h2:mem:cursor" ); Statement st = h.createStatement()) {
            st.execute("CREATE TABLE empleados(id INT PRIMARY KEY,salario DECIMAL(10,2))");
            st.execute("INSERT INTO empleados VALUES(1,100),(2,200)");
            Jdbc.salarios(h);
            try (ResultSet rs = st.executeQuery("SELECT salario FROM empleados ORDER BY id")) {
                assertTrue(rs.next()); assertEquals("110.00",rs.getBigDecimal(1).toPlainString());
                assertTrue(rs.next()); assertEquals("210.00",rs.getBigDecimal(1).toPlainString());
            }
            assertTrue(h.getAutoCommit());
        }
        assertFalse(c.getMetaData().supportsResultSetConcurrency(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE));
    }
    @Test @Tag("b09") void llamadaH2() throws Exception {
        try (Connection h = DriverManager.getConnection("jdbc:h2:mem:rutina"); Statement st = h.createStatement()) {
            st.execute("CREATE ALIAS DOBLE FOR \"lab.JdbcTest.doblar\"");
            assertEquals(14,Jdbc.doble(h,7));
            assertEquals(-4,Jdbc.doble(h,-2));
        }
    }
    public static int doblar(int n) { return n * 2; }
}
