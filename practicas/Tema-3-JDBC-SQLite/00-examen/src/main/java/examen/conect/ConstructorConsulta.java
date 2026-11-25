package examen.conect;

import examen.conect.ApoyoAsistentes.Campo;
import examen.conect.ApoyoAsistentes.Columna;
import examen.conect.ApoyoAsistentes.Enlace;
import examen.conect.ApoyoAsistentes.Esquema;
import examen.conect.ApoyoAsistentes.Fragmento;
import examen.conect.ApoyoAsistentes.Tabla;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static examen.conect.ApoyoAsistentes.ident;

// Generador estructurado: no interpreta texto libre ni ejecuta SQL introducido.
final class ConstructorConsulta {
    private ConstructorConsulta() {}
    private record Salida(String expresion, String alias, boolean agregada, Columna tipo) {}

    static Fragmento construir(Esquema esquema, TerminalCompacta ui, String prefijo, Set<String> referencias) {
        Tabla primera = ui.elegir(prefijo + ".tabla1", "Tabla principal", esquema.tablas(), Tabla::nombre);
        Tabla segunda = null;
        String relacion = "";
        boolean izquierda = false;
        List<Campo> campos = new ArrayList<>(esquema.campos(primera, "t1"));
        if (ui.si(prefijo + ".dos", "Usar segunda tabla", false)) {
            segunda = ui.elegir(prefijo + ".tabla2", "Segunda tabla (t2)", esquema.tablas(), Tabla::nombre);
            campos.addAll(esquema.campos(segunda, "t2"));
            izquierda = ui.opcion(prefijo + ".join", "Conservar filas",
                    "Solo coincidencias (INNER)", "Todas las de t1 (LEFT)").startsWith("Todas");
            List<Enlace> enlaces = new ArrayList<>(esquema.enlaces(primera, segunda));
            enlaces.add(new Enlace("Elegir columnas manualmente", ""));
            Enlace enlace = ui.elegir(prefijo + ".relacion", "Relacion: confirmar", enlaces, Enlace::descripcion);
            relacion = enlace.sql();
            if (relacion.isEmpty()) {
                List<String> pares = new ArrayList<>();
                int n = 0;
                do {
                    Campo uno = ui.elegir(prefijo + ".par." + n + ".a", "Clave de t1",
                            esquema.campos(primera, "t1"), Campo::etiqueta);
                    Campo dos = ui.elegir(prefijo + ".par." + n + ".b", "Clave de t2",
                            esquema.campos(segunda, "t2"), Campo::etiqueta);
                    pares.add(uno.sql() + " = " + dos.sql());
                    n++;
                } while (ui.si(prefijo + ".par." + n + ".mas", "Relacion compuesta: otro par", false));
                relacion = String.join(" AND ", pares);
            }
        }

        boolean distinct = ui.si(prefijo + ".distinct", "Quitar filas duplicadas (DISTINCT)", false);
        List<Salida> salidas = salidas(ui, prefijo, campos, izquierda);
        Fragmento on = Fragmento.vacio();
        Fragmento where = Fragmento.vacio();
        int n = 0;
        while (ui.si(prefijo + ".filtro." + n + ".mas", "Anadir filtro de filas", false)) {
            String clave = prefijo + ".filtro." + n++;
            Campo campo = ui.elegir(clave + ".campo", "Filtrar columna", campos, Campo::etiqueta);
            boolean enOn = izquierda && campo.alias().equals("t2")
                    && ui.opcion(clave + ".lugar", "Filtro sobre t2",
                            "ON: conservar todas las filas t1", "WHERE: descartar filas sin coincidencia").startsWith("ON");
            Fragmento nuevo = condicion(ui, clave, campo.sql(), campo.columna(), referencias);
            if (enOn) on = anadir(ui, clave + ".union", on, nuevo);
            else where = anadir(ui, clave + ".union", where, nuevo);
        }

        boolean agregada = salidas.stream().anyMatch(Salida::agregada);
        Fragmento having = Fragmento.vacio();
        n = 0;
        if (agregada) {
            List<Salida> agregados = salidas.stream().filter(Salida::agregada).toList();
            while (ui.si(prefijo + ".having." + n + ".mas", "Filtrar grupos (HAVING)", false)) {
                String clave = prefijo + ".having." + n++;
                Salida salida = ui.elegir(clave + ".salida", "Agregado a filtrar", agregados, Salida::alias);
                having = anadir(ui, clave + ".union", having,
                        condicion(ui, clave, salida.expresion(), salida.tipo(), referencias));
            }
        }

        List<String> orden = new ArrayList<>();
        n = 0;
        while (ui.si(prefijo + ".orden." + n + ".mas", "Anadir criterio de orden", false)) {
            String clave = prefijo + ".orden." + n++;
            Salida salida = ui.elegir(clave + ".campo", "Ordenar por salida", salidas, Salida::alias);
            String sentido = ui.opcion(clave + ".sentido", "Sentido", "ASC", "DESC");
            orden.add(ident(salida.alias()) + " " + sentido);
        }
        int limite = ui.entero(prefijo + ".limite", "Limite de filas (0=sin limite)", 0, 0, 1000000);

        List<String> seleccion = salidas.stream()
                .map(s -> s.expresion() + " AS " + ident(s.alias())).toList();
        StringBuilder sql = new StringBuilder("SELECT ").append(distinct ? "DISTINCT " : "")
                .append(String.join(",\n       ", seleccion))
                .append("\nFROM ").append(ident(primera.nombre())).append(" AS \"t1\"");
        List<Object> valores = new ArrayList<>();
        if (segunda != null) {
            sql.append(izquierda ? "\nLEFT JOIN " : "\nINNER JOIN ")
                    .append(ident(segunda.nombre())).append(" AS \"t2\" ON (").append(relacion).append(')');
            if (!on.sql().isEmpty()) {
                sql.append(" AND (").append(on.sql()).append(')');
                valores.addAll(on.valores());
            }
        }
        if (!where.sql().isEmpty()) {
            sql.append("\nWHERE ").append(where.sql());
            valores.addAll(where.valores());
        }
        if (agregada) {
            Set<String> grupos = new LinkedHashSet<>();
            salidas.stream().filter(s -> !s.agregada()).forEach(s -> grupos.add(s.expresion()));
            if (!grupos.isEmpty()) sql.append("\nGROUP BY ").append(String.join(", ", grupos));
            if (!having.sql().isEmpty()) {
                sql.append("\nHAVING ").append(having.sql());
                valores.addAll(having.valores());
            }
        }
        if (!orden.isEmpty()) sql.append("\nORDER BY ").append(String.join(", ", orden));
        if (limite > 0) {
            sql.append("\nLIMIT ?");
            valores.add(limite);
        }
        return new Fragmento(sql.toString(), valores);
    }

    private static List<Salida> salidas(TerminalCompacta ui, String prefijo, List<Campo> campos,
            boolean izquierda) {
        List<Salida> resultado = new ArrayList<>();
        Set<String> alias = new HashSet<>();
        int n = 0;
        while (true) {
            String clave = prefijo + ".salida." + n++;
            String tipo = ui.opcion(clave + ".tipo", "Anadir salida",
                    "Columna", "COUNT filas (*)", "COUNT columna", "SUM", "AVG", "MIN", "MAX",
                    "Todas las columnas", "Terminar");
            if (tipo.equals("Terminar")) {
                if (!resultado.isEmpty()) return resultado;
                ui.linea("Selecciona al menos una salida.");
                continue;
            }
            if (tipo.equals("Todas las columnas")) {
                for (Campo campo : campos) {
                    String nombre = campo.alias() + "_" + campo.columna().nombre();
                    String unico = nombre;
                    int sufijo = 2;
                    while (!alias.add(unico.toLowerCase(Locale.ROOT))) unico = nombre + "_" + sufijo++;
                    resultado.add(new Salida(campo.sql(), unico, false, campo.columna()));
                }
                continue;
            }
            if (tipo.equals("COUNT filas (*)")) {
                if (izquierda) ui.linea("Ojo LEFT JOIN: COUNT(*) cuenta tambien filas sin hijo.");
                String nombre = alias(ui, clave, "total_filas", alias);
                resultado.add(new Salida("COUNT(*)", nombre, true,
                        new Columna(nombre, "INTEGER", false, 0, null, 0)));
                continue;
            }
            List<Campo> opciones = campos;
            if (tipo.equals("SUM") || tipo.equals("AVG")) {
                opciones = campos.stream().filter(c -> c.columna().numerica()).toList();
                if (opciones.isEmpty()) {
                    ui.linea("No hay columnas declaradas numericas.");
                    continue;
                }
            }
            Campo campo = ui.elegir(clave + ".campo", "Columna de salida", opciones, Campo::etiqueta);
            boolean agregado = !tipo.equals("Columna");
            String funcion = tipo.startsWith("COUNT") ? "COUNT" : tipo;
            String expresion = agregado ? funcion + "(" + campo.sql() + ")" : campo.sql();
            if (tipo.equals("SUM") && ui.si(clave + ".coalesce", "Suma sin datos: mostrar 0 (COALESCE)", true)) {
                expresion = "COALESCE(" + expresion + ", 0)";
            }
            String defecto = agregado ? funcion.toLowerCase(Locale.ROOT) + "_" + campo.columna().nombre()
                    : campo.alias() + "_" + campo.columna().nombre();
            String nombre = alias(ui, clave, defecto, alias);
            Columna columna = tipo.startsWith("COUNT") ? new Columna(nombre, "INTEGER", false, 0, null, 0)
                    : tipo.equals("AVG") || tipo.equals("SUM") ? ApoyoAsistentes.numero(nombre) : campo.columna();
            resultado.add(new Salida(expresion, nombre, agregado, columna));
        }
    }

    private static String alias(TerminalCompacta ui, String clave, String defecto, Set<String> usados) {
        String nombre = ui.dato(clave + ".alias", "Alias [" + defecto + "]", s -> {
            String valor = s.isEmpty() ? defecto : s;
            if (valor.isBlank() || usados.contains(valor.toLowerCase(Locale.ROOT))) {
                throw new IllegalArgumentException("El alias debe ser unico y no vacio.");
            }
            return valor;
        });
        usados.add(nombre.toLowerCase(Locale.ROOT));
        return nombre;
    }

    static Fragmento filtros(TerminalCompacta ui, String prefijo, List<Campo> campos,
            Set<String> referencias) {
        Fragmento resultado = Fragmento.vacio();
        int n = 0;
        while (ui.si(prefijo + "." + n + ".mas", "Anadir condicion", false)) {
            String clave = prefijo + "." + n++;
            Campo campo = ui.elegir(clave + ".campo", "Columna de condicion", campos, Campo::etiqueta);
            Fragmento nuevo = condicion(ui, clave, campo.sql(), campo.columna(), referencias);
            resultado = anadir(ui, clave + ".union", resultado, nuevo);
        }
        return resultado;
    }

    static Fragmento anadir(TerminalCompacta ui, String clave, Fragmento anterior, Fragmento nuevo) {
        if (anterior.sql().isEmpty()) return nuevo;
        String union = ui.opcion(clave, "Combinar con TODO lo anterior", "AND", "OR");
        List<Object> valores = new ArrayList<>(anterior.valores());
        valores.addAll(nuevo.valores());
        return new Fragmento("(" + anterior.sql() + " " + union + " " + nuevo.sql() + ")", valores);
    }

    static Fragmento condicion(TerminalCompacta ui, String clave, String expresion, Columna columna,
            Set<String> referencias) {
        String operador = ui.opcion(clave + ".operador", "Operador", "=", "<>", ">", ">=", "<", "<=",
                "LIKE", "IS NULL", "IS NOT NULL", "BETWEEN", "IN", "NOT IN");
        if (operador.startsWith("IS ")) return new Fragmento(expresion + " " + operador, List.of());
        List<Object> valores = new ArrayList<>();
        int cantidad = operador.equals("BETWEEN") ? 2
                : operador.endsWith("IN") ? ui.entero(clave + ".cantidad", "Cantidad de valores", 1, 1, 50) : 1;
        for (int i = 0; i < cantidad; i++) {
            boolean admiteNulo = operador.equals("=") || operador.equals("<>");
            Object valor = ApoyoAsistentes.pedirValor(ui, clave + ".valor." + i,
                    operador.equals("LIKE") ? ApoyoAsistentes.texto(columna.nombre()) : columna,
                    referencias, admiteNulo);
            if (valor == null) {
                return new Fragmento(expresion + (operador.equals("=") ? " IS NULL" : " IS NOT NULL"), List.of());
            }
            valores.add(valor);
        }
        String derecha = operador.equals("BETWEEN") ? "? AND ?"
                : operador.endsWith("IN") ? "(" + String.join(", ", java.util.Collections.nCopies(cantidad, "?")) + ")"
                : "?";
        return new Fragmento(expresion + " " + operador + " " + derecha, valores);
    }
}
