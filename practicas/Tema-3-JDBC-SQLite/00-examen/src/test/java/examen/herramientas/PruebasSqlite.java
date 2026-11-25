package examen.herramientas;

import examen.b_recetas.EjemplosSqlite;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// Pruebas sin JUnit ni nuevas dependencias. Ejecutar desde 00-examen.
// NO modifica las bases de datos del usuario. No necesita activar assert.
public class PruebasSqlite {
    private static int comprobaciones;

    public static void main(String[] args) throws Exception {
        cambiosYConsultas();
        transacciones();
        savepointsYCalculos();
        salidaYMetadatos();
        falloAlDeshacer();
        persistencia();
        System.out.println("OK: " + comprobaciones + " comprobaciones");
    }

    private static Connection nuevaBase() throws Exception {
        Connection c = DriverManager.getConnection("jdbc:sqlite::memory:");
        try {
            Sqlite.configurar(c);
            Sqlite.importar(c, Sqlite.leerSql(new File("sql/crear.sql")));
            return c;
        } catch (Exception error) {
            try {
                c.close();
            } catch (SQLException cierre) {
                error.addSuppressed(cierre);
            }
            throw error;
        }
    }

    private static void cambiosYConsultas() throws Exception {
        try (Connection c = nuevaBase()) {
            comprobar(Sqlite.consultarLong(c, "SELECT COUNT(*) FROM empleados") == 4, "Datos iniciales");
            comprobar(Sqlite.insertar(c, "INSERT INTO departamentos(nombre) VALUES (?)", "Compras") == 1,
                    "INSERT");
            long id = EjemplosSqlite.insertarEmpleado(c, "Eva ' ? ; --", 1200, 1);
            comprobar(id == 5, "ID generado");
            List<Map<String, Object>> filas = Sqlite.consultar(c,
                    "SELECT nombre, salario FROM empleados WHERE id = ?", id);
            comprobar(filas.size() == 1 && filas.get(0).get("nombre").equals("Eva ' ? ; --"),
                    "Parámetros sin concatenar");
            comprobar(EjemplosSqlite.cambiarSalario(c, (int) id, 1300) == 1, "UPDATE");
            comprobar(EjemplosSqlite.cambiarSalario(c, 999, 1300) == 0, "UPDATE sin fila");
            comprobar(EjemplosSqlite.borrarEmpleado(c, (int) id) == 1, "DELETE");
            comprobar(Sqlite.consultar(c, "SELECT * FROM empleados WHERE id = ?", id).isEmpty(),
                    "Lista vacía");
            esperarSql(() -> Sqlite.borrar(c, "DELETE FROM departamentos WHERE id = ?", 1));
            esperarSql(() -> Sqlite.insertar(c,
                    "INSERT INTO departamentos(id, nombre) VALUES (?, ?)", 1, "Duplicado"));
            esperarSql(() -> EjemplosSqlite.insertarEmpleado(c, "Mal", -1, 1));
            esperarSql(() -> Sqlite.consultar(c, "SELECT 1 AS repetida, 2 AS repetida"));
            esperarSql(() -> Sqlite.consultarLong(c, "SELECT NULL"));
            esperarSql(() -> Sqlite.consultarLong(c, "SELECT id FROM empleados"));
            esperarSql(() -> Sqlite.consultarLong(c, "SELECT 1, 2"));
            esperarSql(() -> Sqlite.consultarLong(c, "SELECT id FROM empleados WHERE id = -1"));
            esperarSql(() -> Sqlite.exigirUnaFila(0));
            esperarSql(() -> Sqlite.exigirUnaFila(2));
            Sqlite.exigirUnaFila(1);

            try (Statement st = c.createStatement()) {
                st.executeUpdate("CREATE TABLE nulos (entero INTEGER, decimal REAL, texto TEXT)");
            }
            Sqlite.insertar(c, "INSERT INTO nulos VALUES (?, ?, ?)", null, null, "sin valores");
            Sqlite.insertar(c, "INSERT INTO nulos VALUES (?, ?, ?)", 0, 0.0, "ceros");
            filas = Sqlite.consultar(c, "SELECT entero FROM nulos ORDER BY rowid");
            comprobar(filas.get(0).get("entero") == null, "Map conserva NULL");
            try (PreparedStatement ps = Sqlite.preparar(c, "SELECT * FROM nulos ORDER BY rowid");
                    ResultSet rs = ps.executeQuery()) {
                comprobar(rs.next(), "Fila NULL");
                comprobar(Sqlite.leerEnteroNullable(rs, "entero") == null, "Entero NULL");
                comprobar(Sqlite.leerDecimalNullable(rs, "decimal") == null, "Decimal NULL");
                comprobar(rs.next(), "Fila cero");
                comprobar(Sqlite.leerEnteroNullable(rs, "entero") == 0, "Entero cero");
                comprobar(Sqlite.leerDecimalNullable(rs, "decimal") == 0.0, "Decimal cero");
            }
            comprobar(c.getAutoCommit(), "Cambios sueltos no cambian autocommit");
        }
    }

    private static void transacciones() throws Exception {
        try (Connection c = nuevaBase()) {
            long id = EjemplosSqlite.altaConRevision(c, "Eva", 1200, 1, 1);
            comprobar(id == 5 && salario(c, 1) == 1850, "Commit de alta y revisión CASE");
            comprobar(c.getAutoCommit(), "Commit restaura autocommit");
            EjemplosSqlite.probarSinGuardar(c);
            comprobar(salario(c, 1) == 1850 && c.getAutoCommit(), "Rollback voluntario");
        }
        try (Connection c = nuevaBase()) {
            esperarSql(() -> EjemplosSqlite.altaConRevision(c, "Hugo", 1200, 1, 999));
            comprobar(Sqlite.consultarLong(c, "SELECT COUNT(*) FROM empleados") == 4,
                    "Cero filas deshace el alta");
            comprobar(!c.getAutoCommit(), "Deshacer de emergencia no reactiva autocommit");
        }
        try (Connection c = nuevaBase()) {
            Sqlite.iniciarTransaccion(c);
            try {
                Sqlite.exigirUnaFila(EjemplosSqlite.cambiarSalario(c, 1, 9999));
                Sqlite.actualizar(c, "UPDATE empleados SET departamento_id = ? WHERE id = ?", 999, 2);
                throw new AssertionError("La FK debería fallar");
            } catch (SQLException error) {
                Sqlite.deshacer(c, error);
                comprobar(error.getSuppressed().length == 0, "Rollback sin errores secundarios");
            }
            comprobar(salario(c, 1) == 1800, "FK fallida deshace también el primer cambio");
        }
        try (Connection c = nuevaBase()) {
            esperarSql(() -> Sqlite.commit(c));
            esperarSql(() -> Sqlite.rollback(c));
            esperarSql(() -> Sqlite.guardarPunto(c, "sin_transaccion"));
            Sqlite.iniciarTransaccion(c);
            esperarSql(() -> Sqlite.iniciarTransaccion(c));
            Sqlite.actualizar(c, "UPDATE empleados SET salario = ? WHERE id = ?", 9999, 1);
            RuntimeException error = new IllegalArgumentException("Regla calculada en Java");
            Sqlite.deshacer(c, error);
            comprobar(salario(c, 1) == 1800, "También permite deshacer ante excepción Java");
        }
    }

    private static void savepointsYCalculos() throws Exception {
        try (Connection c = nuevaBase()) {
            EjemplosSqlite.guardarSoloPrimerCambio(c);
            comprobar(salario(c, 1) == 1900 && salario(c, 2) == 1650, "Rollback parcial");
            comprobar(c.getAutoCommit(), "Commit tras savepoint");
            Sqlite.iniciarTransaccion(c);
            Savepoint punto = Sqlite.guardarPunto(c, "punto");
            Sqlite.actualizar(c, "UPDATE empleados SET salario = ? WHERE id = ?", 9999, 1);
            Sqlite.liberarPunto(c, punto);
            Sqlite.rollback(c);
            comprobar(salario(c, 1) == 1900, "Liberar savepoint no confirma");
        }
        try (Connection c = nuevaBase()) {
            EjemplosSqlite.revisarSalariosEnJava(c, 1);
            comprobar(salario(c, 1) == 1850 && salario(c, 2) == 1750 && salario(c, 3) == 1700,
                    "Leer, cerrar cursor, calcular y actualizar por PK");
            EjemplosSqlite.revisarSalariosEnJava(c, 999);
            comprobar(c.getAutoCommit(), "Recorrido vacío");
        }
    }

    private static void salidaYMetadatos() throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream original = System.out;
        try (Connection c = nuevaBase();
                PrintStream captura = new PrintStream(buffer, true, StandardCharsets.UTF_8)) {
            System.setOut(captura);
            Sqlite.mostrarInfoConexion(c);
            Sqlite.mostrarTablas(c);
            Sqlite.mostrarColumnas(c, "empleados");
            Sqlite.mostrarClavesForaneas(c, "empleados");
            Sqlite.mostrarColumnasConsulta(c, "SELECT nombre AS empleado FROM empleados WHERE id = ?", 1);
            EjemplosSqlite.listarPorDepartamento(c, "Ventas");
            EjemplosSqlite.resumen(c);
            EjemplosSqlite.distinguirNuloDeCero(c);
            Sqlite.mostrarConsulta(c, "SELECT NULL AS prueba");
            Sqlite.mostrarConsulta(c, "SELECT * FROM empleados WHERE id = ?", 999);
        } finally {
            System.setOut(original);
        }
        String salida = buffer.toString(StandardCharsets.UTF_8);
        comprobar(salida.contains("Tabla: empleados") && salida.contains("Tabla: departamentos"), "Tablas");
        comprobar(salida.contains("departamento_id -> departamentos.id"), "FK en metadatos");
        comprobar(salida.contains("admite NULL:") && salida.contains("empleado |"), "Columnas y alias");
        comprobar(salida.contains("Administración | 0 | 0"), "LEFT JOIN conserva el departamento vacío");
        comprobar(salida.contains("NULL") && salida.contains("Sin resultados"), "Listado nulos/vacío");
    }

    private static void falloAlDeshacer() throws Exception {
        // Simula un fallo del driver: no debemos activar autocommit si falla
        // commit o rollback, y no debemos perder la excepción original.
        boolean[] intentoAutocommit = {false};
        Connection c = (Connection) Proxy.newProxyInstance(
                PruebasSqlite.class.getClassLoader(), new Class<?>[] {Connection.class},
                (proxy, metodo, argumentos) -> {
                    switch (metodo.getName()) {
                        case "getAutoCommit": return false;
                        case "commit": throw new SQLException("Fallo de commit simulado");
                        case "rollback": throw new SQLException("Fallo de rollback simulado");
                        case "setAutoCommit":
                            intentoAutocommit[0] = true;
                            return null;
                        default: throw new UnsupportedOperationException(metodo.getName());
                    }
                });
        esperarSql(() -> Sqlite.commit(c));
        comprobar(!intentoAutocommit[0], "Commit fallido no reactiva autocommit");
        esperarSql(() -> Sqlite.rollback(c));
        comprobar(!intentoAutocommit[0], "Rollback fallido no reactiva autocommit");
        SQLException original = new SQLException("Error original");
        Sqlite.deshacer(c, original);
        comprobar(original.getSuppressed().length == 1, "Conserva el fallo secundario de rollback");
        comprobar(original.getMessage().equals("Error original"), "Conserva el error original");
        comprobar(!intentoAutocommit[0], "Deshacer fallido no reactiva autocommit");
    }

    private static void persistencia() throws Exception {
        File archivo = new File("target", "prueba-herramientas-" + UUID.randomUUID() + ".sqlite");
        Sqlite.crear(archivo, new File("sql/crear.sql"));
        try (Connection c = Sqlite.abrir(archivo)) {
            Sqlite.iniciarTransaccion(c);
            Sqlite.exigirUnaFila(EjemplosSqlite.cambiarSalario(c, 1, 2000));
            Sqlite.commit(c);
        }
        try (Connection c = Sqlite.abrir(archivo)) {
            comprobar(salario(c, 1) == 2000, "Commit persiste al reabrir");
            Sqlite.iniciarTransaccion(c);
            Sqlite.actualizar(c, "UPDATE empleados SET salario = ? WHERE id = ?", 9999, 1);
            Sqlite.rollback(c);
        }
        try (Connection c = Sqlite.abrir(archivo)) {
            comprobar(salario(c, 1) == 2000, "Rollback persiste al reabrir");
        }
        try {
            Sqlite.crear(archivo, new File("sql/crear.sql"));
            throw new AssertionError("No debe sobrescribir una base existente");
        } catch (IOException esperado) {
            comprobaciones++;
        }
        File ausente = new File("target", "no-existe-" + UUID.randomUUID() + ".sqlite");
        try (Connection ignorada = Sqlite.abrir(ausente)) {
            throw new AssertionError("No debe crear una base al abrir");
        } catch (IOException esperado) {
            comprobar(!ausente.exists(), "Abrir no crea ficheros");
        }
        System.out.println("Base aislada de prueba: " + archivo.getCanonicalPath());
    }

    private static long salario(Connection c, int id) throws SQLException {
        return Sqlite.consultarLong(c, "SELECT salario FROM empleados WHERE id = ?", id);
    }

    private static void comprobar(boolean correcto, String mensaje) {
        if (!correcto) throw new AssertionError(mensaje);
        comprobaciones++;
    }

    @FunctionalInterface
    private interface AccionSql {
        void ejecutar() throws SQLException;
    }

    private static void esperarSql(AccionSql accion) throws SQLException {
        try {
            accion.ejecutar();
            throw new AssertionError("Se esperaba SQLException");
        } catch (SQLException esperado) {
            comprobaciones++;
        }
    }
}
