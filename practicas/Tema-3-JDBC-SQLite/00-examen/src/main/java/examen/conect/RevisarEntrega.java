package examen.conect;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Antes de entregar: qué métodos de Sqlite usa tu código y qué carpetas sobran.
// Solo lee ficheros; no borra nada. Ejecutar desde 00-examen.
// Uso: RevisarEntrega [Fichero.java ...]   (por defecto, los .java sueltos de src/main/java/examen)
public final class RevisarEntrega {
    private static final Path SQLITE = Path.of("src/main/java/examen/herramientas/Sqlite.java");
    private static final Pattern METODO = Pattern.compile(
            "\\b(?:public|private)\\s+static\\s+[\\w<>\\[\\], ?]+?\\s+(\\w+)\\s*\\(");
    // Llamada propia: Sqlite.x( o x( sin objeto delante; c.commit() es de JDBC, no de Sqlite.
    private static final Pattern LLAMADA = Pattern.compile("(?:\\bSqlite\\s*\\.\\s*|(?<![\\w.]))(\\w+)\\s*\\(");

    private RevisarEntrega() {}

    public static void main(String[] args) throws IOException {
        List<String> rutas = new ArrayList<>(List.of(args));
        if (rutas.isEmpty()) {
            File[] mains = new File("src/main/java/examen").listFiles((d, n) -> n.endsWith(".java"));
            if (mains != null) for (File f : mains) rutas.add(f.getPath());
            rutas.sort(null);
        }
        if (rutas.size() > 1 && args.length == 0)
            System.out.println("AVISO: hay " + rutas.size() + " Main. Borra primero el que no uses y vuelve a ejecutar.\n");
        List<String> codigo = new ArrayList<>();
        for (String ruta : rutas) codigo.add(Files.readString(Path.of(ruta), StandardCharsets.UTF_8));
        Map<String, String> metodos = metodos(Files.readString(SQLITE, StandardCharsets.UTF_8));
        Set<String> usados = usados(metodos, codigo);

        System.out.println("Código revisado: " + String.join(", ", rutas));
        if (usados.isEmpty()) {
            System.out.println("\nNo usas Sqlite: puedes borrar herramientas/ y el import de examen.herramientas.Sqlite.");
        } else {
            System.out.println("\nCONSERVA en Sqlite.java (tú los llamas o los necesitan los que llamas):");
            System.out.println("  " + String.join(", ", usados));
            List<String> sobran = new ArrayList<>(metodos.keySet());
            sobran.removeAll(usados);
            System.out.println("\nPUEDES BORRAR de Sqlite.java (nadie los usa):");
            System.out.println(sobran.isEmpty() ? "  (ninguno)" : "  " + String.join(", ", sobran));
            System.out.println("  Si borras uno, borra también todas sus variantes con el mismo nombre.");
        }

        for (String texto : codigo) {
            String limpio = limpiar(texto);
            if (limpio.contains("examen.recetas") || limpio.contains("EjemplosSqlite"))
                System.out.println("\nAVISO: tu código usa EjemplosSqlite; copia lo que necesites antes de borrar recetas/.");
            if (limpio.contains("examen.conect"))
                System.out.println("\nAVISO: tu código usa examen.conect; quítalo antes de borrar conect/.");
        }

        System.out.println("\nBORRA para entregar (generación y comprobación, no forman parte de tu solución):");
        for (String ruta : List.of("src/main/java/examen/conect", "src/main/java/examen/recetas",
                "src/test", "asistente.sh", "asistente.cmd", "consultas", "propuestas", "sesiones", "target")) {
            if (new File(ruta).exists()) System.out.println("  " + ruta);
        }
        System.out.println("\nDespués, compila de nuevo para confirmar que no falta nada.");
    }

    // Nombre -> cuerpo de todas sus variantes (las sobrecargas se tratan como una).
    static Map<String, String> metodos(String fuente) {
        String limpio = limpiar(fuente);
        Map<String, String> metodos = new LinkedHashMap<>();
        Matcher m = METODO.matcher(limpio);
        while (m.find()) {
            int inicio = limpio.indexOf('{', m.end());
            int fin = inicio, nivel = 0;
            do {
                char ch = limpio.charAt(fin++);
                if (ch == '{') nivel++;
                else if (ch == '}') nivel--;
            } while (nivel > 0);
            metodos.merge(m.group(1), limpio.substring(inicio, fin), String::concat);
        }
        return metodos;
    }

    static Set<String> usados(Map<String, String> metodos, List<String> codigo) {
        Set<String> usados = new LinkedHashSet<>();
        Deque<String> pendientes = new ArrayDeque<>();
        for (String texto : codigo) {
            String limpio = limpiar(texto);
            boolean importEstatico = limpio.contains("import static examen.herramientas.Sqlite.");
            Matcher m = (importEstatico ? LLAMADA : Pattern.compile("\\bSqlite\\s*\\.\\s*(\\w+)\\s*\\(")).matcher(limpio);
            while (m.find()) if (metodos.containsKey(m.group(1))) pendientes.add(m.group(1));
        }
        while (!pendientes.isEmpty()) {
            String nombre = pendientes.pop();
            if (!usados.add(nombre)) continue;
            Matcher m = LLAMADA.matcher(metodos.get(nombre));
            while (m.find()) if (metodos.containsKey(m.group(1))) pendientes.add(m.group(1));
        }
        Set<String> enOrden = new LinkedHashSet<>(metodos.keySet());
        enOrden.retainAll(usados);
        return enOrden;
    }

    // Quita comentarios y vacía textos: un nombre citado en un comentario o un String no cuenta como uso.
    static String limpiar(String java) {
        StringBuilder salida = new StringBuilder(java.length());
        int i = 0;
        while (i < java.length()) {
            if (java.startsWith("//", i)) {
                while (i < java.length() && java.charAt(i) != '\n') i++;
            } else if (java.startsWith("/*", i)) {
                int fin = java.indexOf("*/", i + 2);
                i = fin < 0 ? java.length() : fin + 2;
                salida.append(' ');
            } else if (java.startsWith("\"\"\"", i)) {
                int fin = java.indexOf("\"\"\"", i + 3);
                i = fin < 0 ? java.length() : fin + 3;
                salida.append("\"\"");
            } else if (java.charAt(i) == '"' || java.charAt(i) == '\'') {
                char cierre = java.charAt(i++);
                while (i < java.length() && java.charAt(i) != cierre) i += java.charAt(i) == '\\' ? 2 : 1;
                i++;
                salida.append("\"\"");
            } else {
                salida.append(java.charAt(i++));
            }
        }
        return salida.toString();
    }
}
