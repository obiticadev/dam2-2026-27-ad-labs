package examen.b_conect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.Supplier;

// Interfaz de texto compartida. Cada pregunta usa como máximo tres líneas.
// No depende de ANSI, de un navegador ni de librerías externas.
final class Golf {
    static final class Salir extends RuntimeException {}
    static final class Volver extends RuntimeException {}
    private static final class Repetir extends RuntimeException {}
    private record Respuesta(String clave, String valor) {}

    private final BufferedReader entrada;
    private final PrintStream salida;
    private final int ancho;
    private final List<Respuesta> respuestas = new ArrayList<>();
    private int cursor;
    private Path sesion;
    private Properties contexto;
    private Supplier<String> esquema = () -> "Esquema no disponible.";

    Golf() {
        this(new InputStreamReader(System.in, StandardCharsets.UTF_8), System.out,
                Integer.getInteger("asistente.ancho", 64));
    }

    Golf(Reader entrada, PrintStream salida, int ancho) {
        this.entrada = new BufferedReader(entrada);
        this.salida = salida;
        this.ancho = Math.max(32, Math.min(120, ancho));
    }

    void linea(String texto) {
        String limpio = texto.replace('\n', ' ').replace('\r', ' ').replace('\t', ' ');
        salida.println(limpio.length() <= ancho ? limpio : limpio.substring(0, ancho - 3) + "...");
    }

    void conectarSesion(Path archivo, Properties datos, boolean cargar, Supplier<String> esquema)
            throws IOException {
        respuestas.clear();
        cursor = 0;
        this.sesion = archivo;
        this.contexto = new Properties();
        for (String clave : datos.stringPropertyNames()) {
            if (!clave.startsWith("r.") && !clave.equals("respuestas")) {
                this.contexto.setProperty(clave, datos.getProperty(clave));
            }
        }
        this.esquema = esquema;
        if (cargar) {
            int cantidad = Integer.parseInt(datos.getProperty("respuestas", "0"));
            if (cantidad < 0 || cantidad > 10000) throw new IOException("Sesion demasiado grande");
            for (int i = 0; i < cantidad; i++) {
                String clave = datos.getProperty("r." + i + ".clave");
                String valor = datos.getProperty("r." + i + ".valor");
                if (clave == null || valor == null) throw new IOException("Sesion incompleta");
                respuestas.add(new Respuesta(clave, valor));
            }
        }
    }

    void reiniciar() {
        cursor = 0;
    }

    void nueva() {
        respuestas.clear();
        cursor = 0;
        guardar();
    }

    void retroceder() {
        if (cursor == 0) {
            linea("No hay una pregunta anterior.");
            return;
        }
        respuestas.subList(cursor - 1, respuestas.size()).clear();
        guardar();
        throw new Volver();
    }

    String texto(String clave, String pregunta, String defecto) {
        return pedir(clave, () -> pregunta + (defecto.isEmpty() ? "" : " [" + defecto + "]"),
                null, s -> s.isEmpty() ? defecto : s, Function.identity());
    }

    String requerido(String clave, String pregunta, String defecto) {
        return pedir(clave, () -> pregunta + (defecto.isEmpty() ? "" : " [" + defecto + "]"),
                null, s -> {
                    String valor = s.isEmpty() ? defecto : s;
                    if (valor.isBlank()) throw new IllegalArgumentException("Escribe un valor.");
                    return valor;
                }, Function.identity());
    }

    int entero(String clave, String pregunta, int defecto, int minimo, int maximo) {
        return pedir(clave, () -> pregunta + " [" + defecto + "]", null, s -> {
            int valor = Integer.parseInt(s.isEmpty() ? Integer.toString(defecto) : s);
            if (valor < minimo || valor > maximo) {
                throw new IllegalArgumentException("Rango: " + minimo + ".." + maximo);
            }
            return valor;
        }, Object::toString);
    }

    boolean si(String clave, String pregunta, boolean defecto) {
        return pedir(clave, () -> pregunta + (defecto ? " [s/N: s]" : " [s/N: n]"), null, s -> {
            String valor = s.isEmpty() ? (defecto ? "s" : "n") : s;
            if (valor.equalsIgnoreCase("s") || valor.equalsIgnoreCase("si") || valor.equals("1")) return true;
            if (valor.equalsIgnoreCase("n") || valor.equalsIgnoreCase("no") || valor.equals("0")) return false;
            throw new IllegalArgumentException("Responde s o n.");
        }, b -> b ? "s" : "n");
    }

    <T> T elegir(String clave, String pregunta, List<T> opciones, Function<T, String> etiqueta) {
        if (opciones.isEmpty()) throw new IllegalArgumentException("No hay opciones para " + pregunta);
        int[] visible = {0};
        int indice = pedir(clave,
                () -> pregunta + " [" + (visible[0] + 1) + "/" + opciones.size() + "]: "
                        + etiqueta.apply(opciones.get(visible[0])),
                "n/p=otra; numero/Enter=elegir; detalle=ver",
                s -> {
                    if (s.equalsIgnoreCase("n") || s.equalsIgnoreCase("p")) {
                        visible[0] = Math.floorMod(visible[0] + (s.equalsIgnoreCase("n") ? 1 : -1),
                                opciones.size());
                        throw new Repetir();
                    }
                    if (s.equalsIgnoreCase("detalle")) {
                        ver(etiqueta.apply(opciones.get(visible[0])));
                        throw new Repetir();
                    }
                    int elegido = s.isEmpty() ? visible[0] : Integer.parseInt(s) - 1;
                    if (elegido < 0 || elegido >= opciones.size()) {
                        throw new IllegalArgumentException("Elige 1.." + opciones.size());
                    }
                    return elegido;
                }, i -> Integer.toString(i + 1));
        return opciones.get(indice);
    }

    String opcion(String clave, String pregunta, String... opciones) {
        return elegir(clave, pregunta, List.of(opciones), Function.identity());
    }

    <T> T dato(String clave, String pregunta, Function<String, T> parser) {
        return pedir(clave, () -> pregunta, null, parser, valor -> ultimoTexto);
    }

    private String ultimoTexto;

    private <T> T pedir(String clave, Supplier<String> pregunta, String ayuda,
            Function<String, T> parser, Function<T, String> normalizar) {
        if (cursor < respuestas.size()) {
            Respuesta anterior = respuestas.get(cursor);
            if (anterior.clave().equals(clave)) {
                try {
                    ultimoTexto = anterior.valor();
                    T resultado = parser.apply(anterior.valor());
                    cursor++;
                    return resultado;
                } catch (IllegalArgumentException | Repetir cambio) {
                    linea("La respuesta guardada ya no es valida.");
                }
            }
            respuestas.subList(cursor, respuestas.size()).clear();
        }
        while (true) {
            linea(pregunta.get());
            if (ayuda != null) linea(ayuda);
            String texto = leer();
            switch (texto.toLowerCase()) {
                case "salir": throw new Salir();
                case "atras":
                    retroceder();
                    continue;
                case "guardar":
                    guardar();
                    ver("Sesion: " + sesion);
                    continue;
                case "ayuda":
                case "?":
                    ver("Enter=defecto; atras=corregir; guardar=sesion; esquema=ver estructura; "
                            + "salir=terminar. En menus: n/p o numero. Para texto reservado usa texto:valor.");
                    continue;
                case "esquema":
                    ver(esquema.get());
                    continue;
                default:
                    try {
                        ultimoTexto = texto;
                        T resultado = parser.apply(texto);
                        respuestas.add(new Respuesta(clave, normalizar.apply(resultado)));
                        cursor++;
                        guardar();
                        return resultado;
                    } catch (Repetir siguiente) {
                        // Navegación por opciones: no guarda n/p como respuesta.
                    } catch (IllegalArgumentException error) {
                        linea(error instanceof NumberFormatException ? "Numero no valido." : error.getMessage());
                    }
            }
        }
    }

    // Para menús finales y paginación: no modifica las respuestas del asistente.
    String comando(String pregunta) {
        linea(pregunta);
        String valor = leer();
        if (valor.equalsIgnoreCase("salir")) throw new Salir();
        return valor;
    }

    void ver(String texto) {
        List<String> trozos = new ArrayList<>();
        for (String renglon : texto.split("\\R", -1)) {
            String limpio = renglon.replace('\t', ' ');
            if (limpio.isEmpty()) trozos.add("");
            for (int i = 0; i < limpio.length(); i += ancho) {
                trozos.add(limpio.substring(i, Math.min(limpio.length(), i + ancho)));
            }
        }
        for (int i = 0; i < trozos.size(); i++) {
            linea(trozos.get(i));
            if (i + 1 < trozos.size()) {
                String comando = comando("Enter=siguiente; fin=volver");
                if (comando.equalsIgnoreCase("fin")) break;
            }
        }
    }

    void guardar() {
        if (sesion == null) return;
        Properties datos = new Properties();
        datos.putAll(contexto);
        datos.setProperty("respuestas", Integer.toString(respuestas.size()));
        for (int i = 0; i < respuestas.size(); i++) {
            datos.setProperty("r." + i + ".clave", respuestas.get(i).clave());
            datos.setProperty("r." + i + ".valor", respuestas.get(i).valor());
        }
        try {
            Files.createDirectories(sesion.toAbsolutePath().getParent());
            Path provisional = Files.createTempFile(sesion.toAbsolutePath().getParent(), "sesion-", ".tmp");
            try {
                try (java.io.Writer archivo = Files.newBufferedWriter(provisional, StandardCharsets.UTF_8)) {
                    datos.store(archivo, "Asistente local. Contiene los valores introducidos.");
                }
                Files.move(provisional, sesion, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            } finally {
                Files.deleteIfExists(provisional);
            }
        } catch (IOException error) {
            throw new java.io.UncheckedIOException("No se pudo guardar la sesion", error);
        }
    }

    private String leer() {
        salida.print("> ");
        salida.flush();
        try {
            String texto = entrada.readLine();
            if (texto == null) throw new Salir();
            return texto.trim();
        } catch (IOException error) {
            throw new java.io.UncheckedIOException(error);
        }
    }
}
