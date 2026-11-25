package examen.b_conect;

import examen.herramientas.Sqlite;

import examen.b_conect.Alfa.Esquema;
import examen.b_conect.Alfa.Fragmento;
import examen.b_conect.Echo.Ejercicio;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.tools.ToolProvider;

// Ejecutar desde 00-examen. No toca datos/examen.sqlite ni las propuestas del usuario.
public class Hotel {
    private static int comprobaciones;
    private static Path carpeta;
    private static String datos;

    public static void main(String[] args) throws Exception {
        carpeta = Files.createDirectories(Path.of("target", "pruebas-asistentes-" + Alfa.unico()));
        datos = Sqlite.leerSql(new File("sql/crear.sql"));
        consultas();
        condicionesYEscapes();
        esquemasEspeciales();
        interfazYSesiones();
        generacionJava();
        masOperaciones();
        soloLectura();
        revisarEntrega();
        System.out.println("OK: " + comprobaciones + " comprobaciones de asistentes");
        System.out.println("Archivos aislados: " + carpeta);
    }

    private static Connection base() throws SQLException {
        Connection c = DriverManager.getConnection("jdbc:sqlite::memory:");
        Sqlite.configurar(c);
        Sqlite.importar(c, datos);
        return c;
    }

    private static Golf terminal(String... respuestas) {
        return new Golf(new StringReader(String.join("\n", respuestas) + "\n"),
                new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8), 64);
    }

    private static void consultas() throws Exception {
        try (Connection c = base()) {
            Esquema esquema = Alfa.analizar(c);
            Fragmento simple = Delta.construir(esquema,
                    terminal("2", "n", "n", "8", "9", "n", "n", "0"), "q", Set.of());
            comprobar(Sqlite.consultar(c, simple.sql(), simple.valores().toArray()).size() == 4, "SELECT simple");

            Fragmento resumen = Delta.construir(esquema, terminal(
                    "1", "s", "2", "2", "", "n",
                    "1", "2", "departamento",
                    "3", "3", "empleados",
                    "4", "3", "s", "total", "9",
                    "s", "5", "1", "4", "1700", "n",
                    "s", "1", "4", "0", "n",
                    "s", "1", "1", "n", "0"), "q", Set.of());
            List<Map<String, Object>> filas = Sqlite.consultar(c, resumen.sql(), resumen.valores().toArray());
            comprobar(filas.size() == 3, "LEFT JOIN conserva departamentos");
            Map<String, Object> vacio = filas.stream()
                    .filter(f -> f.get("departamento").equals("Administración")).findFirst().orElseThrow();
            comprobar(((Number) vacio.get("empleados")).intValue() == 0, "COUNT hijo = 0");
            comprobar(((Number) vacio.get("total")).doubleValue() == 0, "COALESCE SUM = 0");
            comprobar(resumen.sql().contains("GROUP BY") && resumen.sql().contains("HAVING"), "GROUP BY/HAVING");
            comprobar(resumen.valores().equals(List.of(1700.0, 0L)), "Parametros ON antes de HAVING");
            comprobar(Sqlite.consultar(c, Alfa.literalizar(resumen)).equals(filas),
                    "SQL literal equivalente al parametrizado");

            Fragmento filtros = Delta.construir(esquema, terminal(
                    "1", "s", "2", "2", "", "n", "8", "9",
                    "s", "2", "1", "Ventas",
                    "s", "5", "1", "4", "1600",
                    "s", "4", "2", "7", "%a%", "1",
                    "n", "n", "10"), "q", Set.of());
            comprobar(filtros.valores().equals(List.of(1600.0, "Ventas", "%a%", 10)),
                    "Orden de parametros aunque se introduzca WHERE antes de ON");
            comprobar(Sqlite.consultar(c, filtros.sql(), filtros.valores().toArray()).size() == 1,
                    "WHERE del LEFT JOIN descarta filas sin coincidencia");
        }
    }

    private static void condicionesYEscapes() throws Exception {
        Fragmento nulo = Delta.condicion(terminal("1", "null"), "f",
                "\"columna\"", Alfa.texto("columna"), Set.of());
        comprobar(nulo.sql().equals("\"columna\" IS NULL") && nulo.valores().isEmpty(), "NULL usa IS NULL");
        Fragmento rango = Delta.condicion(terminal("10", "10", "20"), "f",
                "\"x\"", Alfa.numero("x"), Set.of());
        comprobar(rango.sql().equals("\"x\" BETWEEN ? AND ?")
                && rango.valores().equals(List.of(10.0, 20.0)), "BETWEEN");
        Fragmento lista = Delta.condicion(terminal("12", "2", "1", "2"), "f",
                "\"x\"", Alfa.numero("x"), Set.of());
        comprobar(lista.sql().equals("\"x\" NOT IN (?, ?)"), "NOT IN");
        Fragmento literal = new Fragmento("SELECT ? AS \"pregunta?\", '?' AS literal",
                List.of("O'Brien?\n\\u000a"));
        try (Connection c = base()) {
            comprobar(Sqlite.consultar(c, literal.sql(), literal.valores().toArray())
                    .equals(Sqlite.consultar(c, Alfa.literalizar(literal))),
                    "Apostrofo, salto, barra y ? entrecomillado");
            List<Object> nuloValor = new ArrayList<>();
            nuloValor.add(null);
            comprobar(Alfa.literalizar(new Fragmento("SELECT ?", nuloValor)).equals("SELECT NULL"),
                    "Valor NULL al exportar");
            Fragmento cero = new Fragmento("SELECT ? AS valor", List.of("a\0b"));
            comprobar(Sqlite.consultar(c, cero.sql(), cero.valores().toArray())
                    .equals(Sqlite.consultar(c, Alfa.literalizar(cero))), "Texto con NUL");
        }
        comprobar(Alfa.javaValor(new Alfa.Referencia("id1")).equals("id1"), "Referencia Java");
        try {
            Alfa.valor("NaN", Alfa.numero("n"), Set.of());
            throw new AssertionError("No aceptar NaN");
        } catch (IllegalArgumentException esperado) { comprobaciones++; }
    }

    private static void esquemasEspeciales() throws Exception {
        try (Connection c = DriverManager.getConnection("jdbc:sqlite::memory:")) {
            Sqlite.importar(c, """
                    CREATE TABLE "padre raro?" (a INTEGER, b INTEGER, PRIMARY KEY(a,b));
                    CREATE TABLE "hijo comilla""?" (
                        id INTEGER PRIMARY KEY, a INTEGER, b INTEGER,
                        doble INTEGER GENERATED ALWAYS AS (a * 2) STORED,
                        FOREIGN KEY(a,b) REFERENCES "padre raro?"
                    );
                    CREATE TABLE arbol (id INTEGER PRIMARY KEY, padre INTEGER REFERENCES arbol);
                    """);
            Esquema esquema = Alfa.analizar(c);
            Alfa.Tabla padre = esquema.tablas().stream()
                    .filter(t -> t.nombre().startsWith("padre")).findFirst().orElseThrow();
            Alfa.Tabla hijo = esquema.tablas().stream()
                    .filter(t -> t.nombre().startsWith("hijo")).findFirst().orElseThrow();
            comprobar(esquema.enlaces(padre, hijo).get(0).sql().contains(" AND "), "FK compuesta implicita");
            comprobar(hijo.escribibles().size() == 3, "No escribir columna generada");
            Alfa.Tabla arbol = esquema.tablas().get(0);
            comprobar(esquema.enlaces(arbol, arbol).size() == 2, "Self JOIN ofrece ambas direcciones");
            String sql = "SELECT * FROM " + Alfa.ident(padre.nombre()) + " AS t1 JOIN "
                    + Alfa.ident(hijo.nombre()) + " AS t2 ON "
                    + esquema.enlaces(padre, hijo).get(0).sql();
            try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                comprobar(!rs.next(), "Identificadores raros correctamente citados");
            }
        }
    }

    private static void interfazYSesiones() throws Exception {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        Golf ui = new Golf(new StringReader("n\n\n10\natras\n20\n"),
                new PrintStream(salida, true, StandardCharsets.UTF_8), 40);
        Path sesion = carpeta.resolve("sesion.properties");
        Properties datosSesion = new Properties();
        ui.conectarSesion(sesion, datosSesion, false, () -> "Esquema de prueba");
        comprobar(ui.opcion("menu", "Elegir", "Uno", "Dos").equals("Dos"), "Navegar y elegir con Enter");
        comprobar(ui.entero("primero", "Primero", 0, 0, 100) == 10, "Primer valor");
        try {
            ui.entero("segundo", "Segundo", 0, 0, 100);
            throw new AssertionError("Debe volver");
        } catch (Golf.Volver esperado) { comprobaciones++; }
        ui.reiniciar();
        comprobar(ui.opcion("menu", "Elegir", "Uno", "Dos").equals("Dos"), "Reproducir opcion navegada");
        comprobar(ui.entero("primero", "Primero", 0, 0, 100) == 20, "Atras corrige respuesta anterior");
        Properties guardado = new Properties();
        try (java.io.Reader entrada = Files.newBufferedReader(sesion, StandardCharsets.UTF_8)) {
            guardado.load(entrada);
        }
        Golf reanudado = terminal();
        reanudado.conectarSesion(sesion, guardado, true, () -> "");
        comprobar(reanudado.opcion("menu", "Elegir", "Uno", "Dos").equals("Dos"), "Reanudar menu guardado");
        comprobar(reanudado.entero("primero", "Primero", 0, 0, 100) == 20, "Reanudar entero");
        comprobar(salida.toString(StandardCharsets.UTF_8).lines().allMatch(l -> l.length() <= 40),
                "Salida automatica limitada al ancho configurado");
    }

    private static String[] cambio(String importe, String id) {
        return new String[] {"3", "2", "3", "n", "2", importe, "s", "1", "1", id, "n", "1"};
    }

    private static Golf guion(String[]... partes) {
        List<String> respuestas = new ArrayList<>();
        for (String[] parte : partes) respuestas.addAll(List.of(parte));
        return terminal(respuestas.toArray(String[]::new));
    }

    private static void generacionJava() throws Exception {
        try (Connection c = base()) {
            Esquema esquema = Alfa.analizar(c);
            Ejercicio commit = Echo.construir(esquema,
                    guion(new String[] {"1"}, cambio("100", "1"), new String[] {"10"}));
            ejecutar(commit, "SolucionCommit", c);
            comprobar(salario(c, 1) == 1900 && c.getAutoCommit(), "Java generado: commit");
            Ejercicio rollback = Echo.construir(esquema,
                    guion(new String[] {"2"}, cambio("500", "1"), new String[] {"10"}));
            ejecutar(rollback, "SolucionRollback", c);
            comprobar(salario(c, 1) == 1900 && c.getAutoCommit(), "Java generado: ensayo con rollback");
        }
        try (Connection c = base()) {
            Ejercicio fallo = Echo.construir(Alfa.analizar(c),
                    guion(new String[] {"1"}, cambio("100", "1"), cambio("100", "999"), new String[] {"10"}));
            try {
                ejecutar(fallo, "SolucionCeroFilas", c);
                throw new AssertionError("Debe fallar por cero filas");
            } catch (SQLException esperado) { comprobaciones++; }
            comprobar(salario(c, 1) == 1800, "Java generado: cero filas deshace primer cambio");
        }
        try (Connection c = base()) {
            Ejercicio ids = Echo.construir(Alfa.analizar(c), terminal(
                    "1", "2", "1", "n", "Temporal", "s",
                    "2", "2", "n", "Eva", "1200", "$id1", "s", "10"));
            ejecutar(ids, "SolucionIds", c);
            comprobar(Sqlite.consultarLong(c, "SELECT departamento_id FROM empleados WHERE nombre = ?", "Eva") == 4,
                    "Java generado: ID del alta usado por siguiente operacion");
        }
        try (Connection c = base()) {
            Ejercicio puntos = Echo.construir(Alfa.analizar(c),
                    guion(new String[] {"1"}, cambio("100", "1"), new String[] {"6"},
                            cambio("999", "2"), new String[] {"7", "", "8", "", "10"}));
            ejecutar(puntos, "SolucionPuntos", c);
            comprobar(salario(c, 1) == 1900 && salario(c, 2) == 1650, "Java generado: rollback parcial");
        }
        try (Connection c = base()) {
            Ejercicio recorrido = Echo.construir(Alfa.analizar(c), terminal(
                    "1", "9", "1", "1",
                    "s", "4", "1", "1", "n",
                    "s", "1", "1700", "1", "100", "n", "1", "10"));
            ejecutar(recorrido, "SolucionRecorrido", c);
            comprobar(salario(c, 1) == 1800 && salario(c, 2) == 1750, "Java generado: reglas y calculo Java");
        }
        try (Connection c = base()) {
            Ejercicio reglas = Echo.construir(Alfa.analizar(c), terminal(
                    "1", "3", "2", "3", "s",
                    "3", "6", "1700", "n", "2", "100", "s",
                    "4", "1", "1", "n", "2", "50", "n", "s",
                    "s", "4", "1", "1", "n", "2", "10"));
            ejecutar(reglas, "SolucionCase", c);
            comprobar(salario(c, 1) == 1850 && salario(c, 2) == 1750,
                    "Java generado: CASE solo aplica primera regla");
        }
        try (Connection c = base()) {
            Ejercicio texto = Echo.construir(Alfa.analizar(c), terminal(
                    "3", "2", "1", "n", "O'Brien? \\u000a \" prueba", "s", "10"));
            ejecutar(texto, "SolucionTexto", c);
            comprobar(Sqlite.consultarLong(c, "SELECT COUNT(*) FROM departamentos") == 4,
                    "Java generado: literales especiales y modo independiente");
            comprobar(Sqlite.consultarLong(c, "SELECT COUNT(*) FROM departamentos WHERE nombre = ?",
                    "O'Brien? \\u000a \" prueba") == 1, "El dato conserva su texto exacto");
        }
    }

    private static void ejecutar(Ejercicio ejercicio, String nombre, Connection c) throws Exception {
        Path fuente = Alfa.guardarNuevo(carpeta, nombre + ".java", ejercicio.fuente(nombre));
        Path clases = Files.createDirectories(carpeta.resolve("clases"));
        ByteArrayOutputStream errores = new ByteArrayOutputStream();
        int resultado = ToolProvider.getSystemJavaCompiler().run(null, errores, errores,
                "--release", "17", "-encoding", "UTF-8", "-cp", System.getProperty("java.class.path"),
                "-d", clases.toString(), fuente.toString());
        comprobar(resultado == 0, "Compila " + nombre + ": " + errores.toString(StandardCharsets.UTF_8));
        try (URLClassLoader cargador = new URLClassLoader(new URL[] {clases.toUri().toURL()},
                Hotel.class.getClassLoader())) {
            try {
                cargador.loadClass("examen." + nombre).getMethod("ejecutar", Connection.class).invoke(null, c);
            } catch (InvocationTargetException error) {
                if (error.getCause() instanceof Exception causa) throw causa;
                throw error;
            }
        }
    }

    private static void masOperaciones() throws Exception {
        try (Connection c = base()) {
            Ejercicio baja = Echo.construir(Alfa.analizar(c), terminal(
                    "1", "4", "2", "s", "1", "1", "4", "n", "1", "10"));
            ejecutar(baja, "SolucionBaja", c);
            comprobar(Sqlite.consultarLong(c, "SELECT COUNT(*) FROM empleados") == 3, "DELETE parametrizado");
        }
        try (Connection c = base()) {
            Ejercicio porcentaje = Echo.construir(Alfa.analizar(c), terminal(
                    "1", "3", "2", "3", "n", "4", "10",
                    "s", "1", "1", "1", "n", "1", "10"));
            ejecutar(porcentaje, "SolucionPorcentaje", c);
            comprobar(Math.abs(salario(c, 1) - 1980) < 0.00001, "UPDATE porcentaje");
        }
        try (Connection c = base()) {
            Ejercicio completa = Echo.construir(Alfa.analizar(c), terminal(
                    "1", "3", "2", "3", "n", "3", "100",
                    "n", "s", "3", "10"));
            ejecutar(completa, "SolucionTablaCompleta", c);
            comprobar(salario(c, 1) == 1700 && salario(c, 4) == 1450,
                    "Sin WHERE solo con confirmacion explicita; cantidad libre");
        }
        try (Connection c = base()) {
            Ejercicio reglas = Echo.construir(Alfa.analizar(c), terminal(
                    "1", "3", "2", "3", "s",
                    "3", "6", "1700", "s", "4", "1", "1", "1", "n",
                    "2", "100", "n", "s",
                    "s", "4", "1", "1", "n", "2", "10"));
            ejecutar(reglas, "SolucionCaseCompuesto", c);
            comprobar(salario(c, 1) == 1800 && salario(c, 2) == 1750,
                    "CASE con dos condiciones AND y ELSE previo");
        }
        try (Connection c = base()) {
            Ejercicio fk = Echo.construir(Alfa.analizar(c),
                    guion(new String[] {"1"}, cambio("100", "1"), new String[] {
                            "3", "2", "4", "n", "1", "999",
                            "s", "1", "1", "2", "n", "1", "10"}));
            try {
                ejecutar(fk, "SolucionFalloFk", c);
                throw new AssertionError("Debe fallar por FK");
            } catch (SQLException esperado) { comprobaciones++; }
            comprobar(salario(c, 1) == 1800, "FK invalida revierte cambios anteriores del Java generado");
        }
        try (Connection c = base()) {
            Ejercicio consulta = Echo.construir(Alfa.analizar(c), terminal(
                    "1", "1", "2", "n", "n", "8", "9", "n", "n", "0", "10"));
            // Solo comprobamos compilación; el listado de la propuesta se captura.
            PrintStream anterior = System.out;
            try (PrintStream captura = new PrintStream(new ByteArrayOutputStream())) {
                System.setOut(captura);
                ejecutar(consulta, "SolucionConsulta", c);
            } finally {
                System.setOut(anterior);
            }
            comprobar(c.getAutoCommit(), "Consulta generada termina su transaccion");
        }
    }

    private static void soloLectura() throws Exception {
        Path archivo = carpeta.resolve("lectura.sqlite");
        Sqlite.crear(archivo.toFile(), new File("sql/crear.sql"));
        byte[] antes = Files.readAllBytes(archivo);
        try (Connection c = Alfa.abrirLectura(archivo.toRealPath())) {
            comprobar(Alfa.analizar(c).tablas().size() == 2, "Analizador en modo ro");
            try (Statement st = c.createStatement()) {
                st.executeUpdate("UPDATE empleados SET salario = 0");
                throw new AssertionError("El asistente no puede escribir");
            } catch (SQLException esperado) { comprobaciones++; }
        }
        comprobar(java.util.Arrays.equals(antes, Files.readAllBytes(archivo)), "Base original intacta");
        try {
            Alfa.guardarNuevo(carpeta, "lectura.sqlite", "no");
            throw new AssertionError("No debe sobrescribir");
        } catch (java.nio.file.FileAlreadyExistsException esperado) { comprobaciones++; }
    }

    private static double salario(Connection c, int id) throws SQLException {
        return ((Number) Sqlite.consultar(c, "SELECT salario FROM empleados WHERE id = ?", id)
                .get(0).get("salario")).doubleValue();
    }

    private static void revisarEntrega() throws Exception {
        var metodos = Foxtrot.metodos(Files.readString(Path.of("src/main/java/examen/herramientas/Sqlite.java")));
        // Un comentario o un texto no cuentan como uso; insertar arrastra a preparar y ejecutarCambio.
        var usados = Foxtrot.usados(metodos, List.of("""
                // Sqlite.commit(c);
                String s = "Sqlite.rollback(c)";
                Sqlite.insertar(c, "INSERT INTO t VALUES (?)", 1);
                """));
        comprobar(usados.equals(Set.of("preparar", "insertar", "ejecutarCambio")), "Entrega: " + usados);
        var abrir = Foxtrot.usados(metodos, List.of("Sqlite.abrir(db);"));
        comprobar(abrir.equals(Set.of("abrir", "configurar")), "Entrega: abrir necesita configurar: " + abrir);
        comprobar(Foxtrot.usados(metodos, List.of("import static examen.herramientas.Sqlite.*; commit(c);"))
                .containsAll(Set.of("commit", "exigirTransaccion")), "Entrega: import static");
    }

    private static void comprobar(boolean valido, String mensaje) {
        if (!valido) throw new AssertionError(mensaje);
        comprobaciones++;
    }
}
