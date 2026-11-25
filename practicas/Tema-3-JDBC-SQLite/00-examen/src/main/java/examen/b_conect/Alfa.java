package examen.b_conect;

import examen.herramientas.Sqlite;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

// Soporte interno de los asistentes. No sustituye las herramientas de Sqlite.
final class Alfa {
    private Alfa() {}

    record Columna(String nombre, String tipo, boolean obligatoria, int primaria,
            String defecto, int oculta) {
        boolean numerica() {
            String t = tipo.toUpperCase(java.util.Locale.ROOT);
            return t.contains("INT") || t.contains("REAL") || t.contains("FLOA")
                    || t.contains("DOUB") || t.contains("NUM") || t.contains("DEC");
        }
        boolean entera() { return tipo.toUpperCase(java.util.Locale.ROOT).contains("INT"); }
        String etiqueta() { return nombre + " " + tipo + (primaria > 0 ? " PK" : "")
                + (obligatoria ? " NOT NULL" : "") + (oculta != 0 ? " [generada/oculta]" : ""); }
    }

    record Tabla(String nombre, String ddl, List<Columna> columnas) {
        List<Columna> claves() {
            return columnas.stream().filter(c -> c.primaria() > 0)
                    .sorted(java.util.Comparator.comparingInt(Columna::primaria)).toList();
        }
        List<Columna> escribibles() { return columnas.stream().filter(c -> c.oculta() == 0).toList(); }
        Columna columna(String nombre) {
            return columnas.stream().filter(c -> c.nombre().equalsIgnoreCase(nombre)).findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Columna desconocida: " + nombre));
        }
    }

    record Campo(Tabla tabla, String alias, Columna columna) {
        String sql() { return (alias.isEmpty() ? "" : ident(alias) + ".") + ident(columna.nombre()); }
        String etiqueta() { return (alias.isEmpty() ? tabla.nombre() : alias) + "." + columna.etiqueta(); }
    }

    record Enlace(String descripcion, String sql) {}
    record ClaveForanea(String hija, String padre, List<String> origen, List<String> destino) {}
    record Referencia(String nombre) {}
    record Fragmento(String sql, List<Object> valores) {
        Fragmento { valores = java.util.Collections.unmodifiableList(new ArrayList<>(valores)); }
        static Fragmento vacio() { return new Fragmento("", List.of()); }
    }

    record Esquema(List<Tabla> tablas, List<ClaveForanea> claves, String huella) {
        List<Campo> campos(Tabla tabla, String alias) {
            return tabla.columnas().stream().filter(c -> c.oculta() != 1)
                    .map(c -> new Campo(tabla, alias, c)).toList();
        }

        List<Enlace> enlaces(Tabla primera, Tabla segunda) {
            List<Enlace> resultado = new ArrayList<>();
            for (ClaveForanea clave : claves) {
                if (clave.hija().equalsIgnoreCase(primera.nombre())
                        && clave.padre().equalsIgnoreCase(segunda.nombre())) {
                    resultado.add(enlace(clave, "t1", "t2"));
                }
                if (clave.hija().equalsIgnoreCase(segunda.nombre())
                        && clave.padre().equalsIgnoreCase(primera.nombre())) {
                    resultado.add(enlace(clave, "t2", "t1"));
                }
            }
            return resultado;
        }

        String informe() {
            StringBuilder texto = new StringBuilder("ESQUEMA (solo lectura)\n");
            for (Tabla tabla : tablas) {
                texto.append("\nTabla: ").append(tabla.nombre()).append('\n');
                for (Columna columna : tabla.columnas()) {
                    texto.append("  ").append(columna.etiqueta()).append(" DEFAULT ")
                            .append(columna.defecto()).append('\n');
                }
                texto.append(tabla.ddl()).append(";\n");
            }
            for (ClaveForanea clave : claves) {
                texto.append("FK: ").append(clave.hija()).append(clave.origen())
                        .append(" -> ").append(clave.padre()).append(clave.destino()).append('\n');
            }
            return texto.toString();
        }
    }

    static final class Contexto implements AutoCloseable {
        final Golf terminal;
        final Path base;
        final Connection conexion;
        final Esquema esquema;
        final Path sesion;

        private Contexto(Golf terminal, Path base, Connection conexion,
                Esquema esquema, Path sesion) {
            this.terminal = terminal;
            this.base = base;
            this.conexion = conexion;
            this.esquema = esquema;
            this.sesion = sesion;
        }

        static Contexto abrir(String tipo, String[] args, Golf terminal) throws Exception {
            if (args.length > 2) throw new IllegalArgumentException("Uso: [base.sqlite] [sesion.properties]");
            Properties datos = new Properties();
            boolean cargar = args.length == 2;
            Path sesion = cargar ? Path.of(args[1]).toAbsolutePath().normalize()
                    : Path.of("b_sesiones", tipo + "-" + unico() + ".properties");
            if (cargar) {
                try (java.io.Reader entrada = Files.newBufferedReader(sesion, StandardCharsets.UTF_8)) {
                    datos.load(entrada);
                }
                if (!tipo.equals(datos.getProperty("tipo"))) {
                    throw new IllegalArgumentException("La sesion es de otro asistente");
                }
                if (!"1".equals(datos.getProperty("version"))) {
                    throw new IllegalArgumentException("Version de sesion incompatible");
                }
            }
            String ruta = args.length > 0 ? args[0]
                    : terminal.requerido("base", "Base .sqlite", "datos/examen.sqlite");
            Path base = Path.of(ruta).toRealPath();
            if (!Files.isRegularFile(base)) throw new IOException("La base no es un fichero");
            if (cargar && !base.toString().equals(datos.getProperty("base"))) {
                throw new IllegalArgumentException("La sesion pertenece a otra base");
            }
            Connection c = abrirLectura(base);
            try {
                Esquema esquema = analizar(c);
                if (esquema.tablas().isEmpty()) throw new IllegalArgumentException("La base no tiene tablas");
                if (cargar && !esquema.huella().equals(datos.getProperty("esquema"))) {
                    throw new IllegalArgumentException("El esquema cambio: empieza una sesion nueva");
                }
                datos.setProperty("tipo", tipo);
                datos.setProperty("version", "1");
                datos.setProperty("base", base.toString());
                datos.setProperty("esquema", esquema.huella());
                terminal.conectarSesion(sesion, datos, cargar, esquema::informe);
                terminal.guardar();
                terminal.linea("Solo lectura. ayuda / atras / guardar / salir");
                return new Contexto(terminal, base, c, esquema, sesion);
            } catch (Exception error) {
                try { c.close(); } catch (SQLException cierre) { error.addSuppressed(cierre); }
                throw error;
            }
        }

        @Override public void close() throws SQLException { conexion.close(); }
    }

    static Connection abrirLectura(Path base) throws SQLException {
        Connection c = DriverManager.getConnection("jdbc:sqlite:"
                + base.toUri().toASCIIString() + "?mode=ro");
        try (Statement st = c.createStatement()) {
            st.execute("PRAGMA query_only = ON");
        } catch (SQLException error) {
            try { c.close(); } catch (SQLException cierre) { error.addSuppressed(cierre); }
            throw error;
        }
        return c;
    }

    static Esquema analizar(Connection c) throws Exception {
        List<Tabla> tablas = new ArrayList<>();
        StringBuilder definiciones = new StringBuilder();
        try (Statement st = c.createStatement();
                ResultSet rs = st.executeQuery("""
                        SELECT name, sql FROM sqlite_schema
                        WHERE type = 'table' AND name NOT GLOB 'sqlite_*' ORDER BY name
                        """)) {
            while (rs.next()) {
                String nombre = rs.getString(1);
                String ddl = rs.getString(2);
                List<Columna> columnas = new ArrayList<>();
                try (Statement detalle = c.createStatement();
                        ResultSet cols = detalle.executeQuery("PRAGMA table_xinfo(" + ident(nombre) + ")")) {
                    while (cols.next()) {
                        columnas.add(new Columna(cols.getString("name"), cols.getString("type"),
                                cols.getInt("notnull") != 0, cols.getInt("pk"),
                                cols.getString("dflt_value"), cols.getInt("hidden")));
                    }
                }
                tablas.add(new Tabla(nombre, ddl, List.copyOf(columnas)));
                definiciones.append(nombre).append('\n').append(ddl).append('\n');
            }
        }
        List<ClaveForanea> claves = new ArrayList<>();
        for (Tabla tabla : tablas) {
            Map<Integer, List<String[]>> grupos = new LinkedHashMap<>();
            try (Statement st = c.createStatement();
                    ResultSet rs = st.executeQuery("PRAGMA foreign_key_list(" + ident(tabla.nombre()) + ")")) {
                while (rs.next()) {
                    grupos.computeIfAbsent(rs.getInt("id"), k -> new ArrayList<>()).add(new String[] {
                            rs.getString("table"), rs.getString("from"), rs.getString("to"),
                            Integer.toString(rs.getInt("seq"))});
                }
            }
            for (List<String[]> grupo : grupos.values()) {
                grupo.sort(java.util.Comparator.comparingInt(f -> Integer.parseInt(f[3])));
                String padre = grupo.get(0)[0];
                Tabla destino = tablas.stream().filter(t -> t.nombre().equalsIgnoreCase(padre))
                        .findFirst().orElse(null);
                List<String> origen = new ArrayList<>();
                List<String> columnasDestino = new ArrayList<>();
                for (String[] fila : grupo) {
                    origen.add(fila[1]);
                    String columna = fila[2];
                    int posicion = Integer.parseInt(fila[3]);
                    if (columna == null && destino != null && posicion < destino.claves().size()) {
                        columna = destino.claves().get(posicion).nombre();
                    }
                    if (columna != null) columnasDestino.add(columna);
                }
                if (origen.size() == columnasDestino.size()) {
                    claves.add(new ClaveForanea(tabla.nombre(), padre, origen, columnasDestino));
                }
            }
        }
        // Incluye índices únicos y restricciones externas al CREATE TABLE en la huella.
        try (Statement st = c.createStatement();
                ResultSet rs = st.executeQuery("SELECT name, sql FROM sqlite_schema ORDER BY name")) {
            while (rs.next()) definiciones.append(rs.getString(1)).append(rs.getString(2)).append('\n');
        }
        String huella = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                .digest(definiciones.toString().getBytes(StandardCharsets.UTF_8)));
        return new Esquema(List.copyOf(tablas), List.copyOf(claves), huella);
    }

    private static Enlace enlace(ClaveForanea clave, String hija, String padre) {
        List<String> partes = new ArrayList<>();
        for (int i = 0; i < clave.origen().size(); i++) {
            partes.add(ident(hija) + "." + ident(clave.origen().get(i)) + " = "
                    + ident(padre) + "." + ident(clave.destino().get(i)));
        }
        String sql = String.join(" AND ", partes);
        return new Enlace(sql, sql);
    }

    static String ident(String nombre) {
        return "\"" + nombre.replace("\"", "\"\"") + "\"";
    }

    static Object valor(String texto, Columna columna, Set<String> referencias) {
        if (texto.startsWith("texto:")) return texto.substring(6);
        if (texto.equalsIgnoreCase("null")) return null;
        if (texto.startsWith("$")) {
            String nombre = texto.substring(1);
            if (!referencias.contains(nombre)) throw new IllegalArgumentException("Referencia no disponible");
            return new Referencia(nombre);
        }
        if (texto.startsWith("entero:")) return Long.valueOf(texto.substring(7));
        if (texto.startsWith("decimal:")) return decimal(texto.substring(8));
        if (columna.entera()) return Long.valueOf(texto);
        if (columna.numerica()) return decimal(texto);
        return texto;
    }

    private static Double decimal(String texto) {
        double numero = Double.parseDouble(texto);
        if (!Double.isFinite(numero)) throw new IllegalArgumentException("Numero no finito");
        return numero;
    }

    static Object pedirValor(Golf ui, String clave, Columna columna,
            Set<String> referencias, boolean permiteNulo) {
        return ui.dato(clave, columna.nombre() + ": valor (texto: / null)", s -> {
            Object valor = valor(s, columna, referencias);
            if (valor == null && !permiteNulo) throw new IllegalArgumentException("Aqui NULL no es valido");
            return valor;
        });
    }

    static Columna numero(String nombre) { return new Columna(nombre, "REAL", false, 0, null, 0); }
    static Columna texto(String nombre) { return new Columna(nombre, "TEXT", false, 0, null, 0); }

    static String javaTexto(String texto) {
        StringBuilder salida = new StringBuilder("\"");
        for (char letra : texto.toCharArray()) {
            switch (letra) {
                case '\\' -> salida.append("\\\\");
                case '"' -> salida.append("\\\"");
                case '\n' -> salida.append("\\n");
                case '\r' -> salida.append("\\r");
                case '\t' -> salida.append("\\t");
                default -> {
                    if (letra < 32 || letra == 127) salida.append(String.format("\\%03o", (int) letra));
                    else salida.append(letra);
                }
            }
        }
        return salida.append('"').toString();
    }

    static String javaValor(Object valor) {
        if (valor == null) return "(Object) null";
        if (valor instanceof Referencia referencia) return referencia.nombre();
        if (valor instanceof String cadena) return javaTexto(cadena);
        if (valor instanceof Long numero) return numero + "L";
        if (valor instanceof Integer numero) return numero.toString();
        if (valor instanceof Double numero && Double.isFinite(numero)) return numero + "d";
        throw new IllegalArgumentException("Tipo de parametro no compatible");
    }

    static String llamada(String metodo, Fragmento fragmento) {
        StringBuilder salida = new StringBuilder("Sqlite.").append(metodo).append("(c, ")
                .append(javaTexto(fragmento.sql()));
        for (Object valor : fragmento.valores()) salida.append(", ").append(javaValor(valor));
        return salida.append(')').toString();
    }

    static String literal(Object valor) {
        if (valor == null) return "NULL";
        if (valor instanceof String cadena) {
            if (cadena.indexOf('\0') >= 0) {
                return "CAST(X'" + HexFormat.of().formatHex(cadena.getBytes(StandardCharsets.UTF_8)) + "' AS TEXT)";
            }
            return "'" + cadena.replace("'", "''") + "'";
        }
        if (valor instanceof Referencia) throw new IllegalArgumentException("El valor depende de una variable Java");
        return valor.toString();
    }

    // No reemplaza ? que formen parte de nombres entre comillas o literales.
    static String literalizar(Fragmento fragmento) {
        StringBuilder salida = new StringBuilder();
        char comilla = 0;
        int parametro = 0;
        for (int i = 0; i < fragmento.sql().length(); i++) {
            char actual = fragmento.sql().charAt(i);
            if (comilla != 0) {
                salida.append(actual);
                if (actual == comilla) {
                    if (i + 1 < fragmento.sql().length() && fragmento.sql().charAt(i + 1) == comilla) {
                        salida.append(fragmento.sql().charAt(++i));
                    } else comilla = 0;
                }
            } else if (actual == '"' || actual == '\'') {
                comilla = actual;
                salida.append(actual);
            } else if (actual == '?') {
                if (parametro == fragmento.valores().size()) throw new IllegalArgumentException("Faltan parametros");
                salida.append(literal(fragmento.valores().get(parametro++)));
            } else salida.append(actual);
        }
        if (parametro != fragmento.valores().size()) throw new IllegalArgumentException("Sobran parametros");
        return salida.toString();
    }

    static Path guardarNuevo(Path carpeta, String nombre, String texto) throws IOException {
        Files.createDirectories(carpeta);
        Path archivo = carpeta.resolve(nombre);
        Files.writeString(archivo, texto, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
        return archivo;
    }

    static String unico() {
        return Long.toString(System.currentTimeMillis()) + "_" + UUID.randomUUID().toString().substring(0, 8);
    }

    static void probarConsulta(Connection c, Fragmento consulta, Golf ui) throws SQLException {
        // El límite externo evita volcar una consulta entera a la terminal.
        String sql = "SELECT * FROM (" + consulta.sql() + ") AS vista LIMIT 20";
        try (PreparedStatement ps = Sqlite.preparar(c, sql, consulta.valores().toArray());
                ResultSet rs = ps.executeQuery()) {
            int filas = 0;
            while (rs.next()) {
                filas++;
                StringBuilder fila = new StringBuilder("Fila " + filas + ": ");
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    if (i > 1) fila.append(" | ");
                    Object valor = rs.getObject(i);
                    fila.append(rs.getMetaData().getColumnLabel(i)).append('=')
                            .append(valor instanceof byte[] bytes ? "BLOB(" + bytes.length + ")"
                                    : valor == null ? "NULL" : valor);
                }
                ui.ver(fila.toString());
                if (ui.comando("Enter=otra fila; fin=volver").equalsIgnoreCase("fin")) return;
            }
            ui.linea(filas == 0 ? "Sin resultados." : "Fin de vista previa (maximo 20 filas).");
        }
    }
}
