package examen.b_conect;

import examen.herramientas.Sqlite;

import examen.b_conect.Alfa.Campo;
import examen.b_conect.Alfa.Columna;
import examen.b_conect.Alfa.Esquema;
import examen.b_conect.Alfa.Fragmento;
import examen.b_conect.Alfa.Tabla;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static examen.b_conect.Alfa.ident;

final class Echo {
    private Echo() {}

    record Ejercicio(String modo, List<String> lineas, List<String> avisos) {
        boolean transaccion() { return !modo.startsWith("Independientes"); }

        String fuente(String clase) {
            if (!clase.matches("[A-Za-z][A-Za-z0-9_]*")) throw new IllegalArgumentException("Clase no valida");
            StringBuilder java = new StringBuilder("""
                    package examen;

                    import examen.herramientas.Sqlite;
                    import java.io.File;
                    import java.sql.Connection;
                    import java.sql.SQLException;
                    import java.sql.Savepoint;
                    import java.util.List;
                    import java.util.Map;

                    // PROPUESTA LOCAL: revisa las reglas antes de ejecutar.
                    // No se ha probado automaticamente contra los requisitos del enunciado.
                    // Usa UNA COPIA de la base. Tambien puedes copiar ejecutar(c) a tu Main.
                    """);
            java.append("public class ").append(clase).append(" {\n")
                    .append("    public static void main(String[] args) throws Exception {\n")
                    .append("        if (args.length != 1) throw new IllegalArgumentException(\"Indica la ruta de una COPIA .sqlite\");\n")
                    .append("        try (Connection c = Sqlite.abrir(new File(args[0]))) {\n")
                    .append("            ejecutar(c);\n        }\n    }\n\n")
                    .append("    public static void ejecutar(Connection c) throws SQLException {\n");
            if (transaccion()) {
                java.append("        Sqlite.iniciarTransaccion(c);\n        try {\n");
            } else {
                java.append("        // AUTOCOMMIT: los cambios anteriores no se deshacen si falla uno posterior.\n");
            }
            String sangria = transaccion() ? "            " : "        ";
            for (String linea : lineas) java.append(sangria).append(linea).append('\n');
            if (transaccion()) {
                java.append(sangria).append(modo.startsWith("Ensayo") ? "Sqlite.rollback(c);" : "Sqlite.commit(c);")
                        .append("\n        } catch (SQLException | RuntimeException error) {\n")
                        .append("            Sqlite.deshacer(c, error);\n            throw error;\n        }\n");
            }
            return java.append("    }\n}\n").toString();
        }
    }

    static Ejercicio construir(Esquema esquema, Golf ui) {
        String modo = ui.opcion("ej.modo", "Transaccion del ejercicio",
                "Todo o nada (commit)", "Ensayo (rollback al final)", "Independientes (sin transaccion)");
        boolean transaccion = !modo.startsWith("Independientes");
        List<String> lineas = new ArrayList<>();
        List<String> avisos = new ArrayList<>();
        avisos.add("Revisa SQL, filtros, cantidades, prioridades y redondeos con el enunciado.");
        avisos.add("Los valores son constantes de ejemplo; cambialos o conviertelos en parametros de tu metodo.");
        if (!transaccion) avisos.add("AUTOCOMMIT: no puedes deshacer cambios anteriores ya confirmados.");
        Set<String> referencias = new LinkedHashSet<>();
        LinkedHashMap<String, String> puntos = new LinkedHashMap<>();
        int n = 0;
        while (true) {
            String clave = "ej.op." + ++n;
            String tipo = ui.opcion(clave + ".tipo", "Operacion " + n,
                    "Consulta", "Insertar", "Modificar", "Borrar", "Metadatos", "Guardar punto",
                    "Rollback hasta punto", "Liberar punto", "Calculo en Java", "Terminar");
            if (tipo.equals("Terminar")) {
                if (!lineas.isEmpty()) break;
                ui.linea("Anade al menos una operacion.");
                continue;
            }
            if ((tipo.contains("punto") || tipo.equals("Calculo en Java")) && !transaccion) {
                ui.linea("Esta receta requiere transaccion. Usa atras.");
                continue;
            }
            if ((tipo.equals("Rollback hasta punto") || tipo.equals("Liberar punto")) && puntos.isEmpty()) {
                ui.linea("No hay puntos activos.");
                continue;
            }
            List<String> operacion = new ArrayList<>();
            switch (tipo) {
                case "Consulta" -> operacion.add(Alfa.llamada("mostrarConsulta",
                        Delta.construir(esquema, ui, clave, referencias)) + ";");
                case "Insertar" -> insertar(esquema, ui, clave, n, referencias, operacion);
                case "Modificar" -> modificar(esquema, ui, clave, n, referencias, operacion);
                case "Borrar" -> borrar(esquema, ui, clave, n, referencias, operacion);
                case "Metadatos" -> metadatos(esquema, ui, clave, referencias, operacion);
                case "Guardar punto" -> {
                    String variable = "punto" + n;
                    operacion.add("Savepoint " + variable + " = Sqlite.guardarPunto(c, "
                            + Alfa.javaTexto(variable) + ");");
                    puntos.put(variable, variable);
                }
                case "Rollback hasta punto", "Liberar punto" -> {
                    String punto = ui.elegir(clave + ".punto", "Punto activo",
                            new ArrayList<>(puntos.keySet()), s -> s);
                    boolean rollback = tipo.startsWith("Rollback");
                    operacion.add("Sqlite." + (rollback ? "rollbackHasta" : "liberarPunto") + "(c, " + punto + ");");
                    // SQLite invalida los puntos posteriores al punto deshecho/liberado.
                    boolean despues = false;
                    for (String nombre : new ArrayList<>(puntos.keySet())) {
                        if (nombre.equals(punto)) {
                            despues = true;
                            if (!rollback) puntos.remove(nombre);
                        } else if (despues) puntos.remove(nombre);
                    }
                }
                case "Calculo en Java" -> recorrer(esquema, ui, clave, n, referencias, operacion);
                default -> throw new IllegalArgumentException("Operacion no soportada");
            }
            if (!operacion.isEmpty()) {
                lineas.add("// Operacion " + n + ": " + tipo);
                lineas.addAll(operacion);
            }
        }
        return new Ejercicio(modo, List.copyOf(lineas), List.copyOf(avisos));
    }

    private static Tabla tabla(Esquema esquema, Golf ui, String clave) {
        return ui.elegir(clave + ".tabla", "Tabla de la operacion", esquema.tablas(), Tabla::nombre);
    }

    private static void insertar(Esquema esquema, Golf ui, String clave, int n,
            Set<String> referencias, List<String> codigo) {
        Tabla tabla = tabla(esquema, ui, clave);
        List<String> columnas = new ArrayList<>();
        List<Object> valores = new ArrayList<>();
        int i = 0;
        for (Columna columna : tabla.escribibles()) {
            String paso = clave + ".col." + i++;
            boolean auto = tabla.claves().size() == 1 && columna.primaria() == 1
                    && columna.tipo().equalsIgnoreCase("INTEGER")
                    && !tabla.ddl().toUpperCase(java.util.Locale.ROOT).contains("WITHOUT ROWID");
            boolean requerida = columna.obligatoria() && columna.defecto() == null && !auto;
            boolean incluir = requerida || ui.si(paso + ".incluir",
                    "Incluir " + columna.etiqueta(), !auto && columna.defecto() == null);
            if (incluir) {
                columnas.add(ident(columna.nombre()));
                valores.add(Alfa.pedirValor(ui, paso + ".valor", columna, referencias,
                        !columna.obligatoria()));
            }
        }
        String sql = "INSERT INTO " + ident(tabla.nombre()) + (columnas.isEmpty() ? " DEFAULT VALUES"
                : " (" + String.join(", ", columnas) + ") VALUES ("
                        + String.join(", ", java.util.Collections.nCopies(columnas.size(), "?")) + ")");
        boolean claveEntera = tabla.claves().size() == 1 && tabla.claves().get(0).entera();
        if (claveEntera && ui.si(clave + ".id", "Recuperar clave como $id" + n, true)) {
            String variable = "id" + n;
            codigo.add("long " + variable + " = " + Alfa.llamada("insertarConId",
                    new Fragmento(sql + " RETURNING " + ident(tabla.claves().get(0).nombre()), valores)) + ";");
            referencias.add(variable);
        } else {
            codigo.add("int filas" + n + " = " + Alfa.llamada("insertar", new Fragmento(sql, valores)) + ";");
            codigo.add("Sqlite.exigirUnaFila(filas" + n + ");");
        }
    }

    private static void modificar(Esquema esquema, Golf ui, String clave, int n,
            Set<String> referencias, List<String> codigo) {
        Tabla tabla = tabla(esquema, ui, clave);
        Columna columna = ui.elegir(clave + ".columna", "Columna a modificar",
                tabla.escribibles(), Columna::etiqueta);
        if (columna.primaria() > 0 && !ui.si(clave + ".pk",
                "Cambiar CLAVE PRIMARIA: confirmar", false)) return;
        Fragmento expresion;
        if (ui.si(clave + ".case", "Usar reglas ordenadas (CASE)", false)) {
            StringBuilder sql = new StringBuilder("CASE");
            List<Object> valores = new ArrayList<>();
            int regla = 0;
            do {
                String paso = clave + ".regla." + regla++;
                Campo campo = ui.elegir(paso + ".campo", "Condicion de la regla",
                        esquema.campos(tabla, ""), Campo::etiqueta);
                Fragmento condicion = Delta.condicion(ui, paso,
                        campo.sql(), campo.columna(), referencias);
                int extra = 0;
                while (ui.si(paso + ".extra." + extra + ".mas", "Otra condicion en esta regla", false)) {
                    String subpaso = paso + ".extra." + extra++;
                    Campo otro = ui.elegir(subpaso + ".campo", "Columna de condicion",
                            esquema.campos(tabla, ""), Campo::etiqueta);
                    condicion = Delta.anadir(ui, subpaso + ".union", condicion,
                            Delta.condicion(ui, subpaso,
                                    otro.sql(), otro.columna(), referencias));
                }
                Fragmento valor = expresion(ui, paso + ".entonces", columna, referencias);
                sql.append(" WHEN ").append(condicion.sql()).append(" THEN ").append(valor.sql());
                valores.addAll(condicion.valores());
                valores.addAll(valor.valores());
            } while (ui.si(clave + ".regla." + regla + ".mas", "Otra regla (solo gana la primera)", false));
            if (ui.si(clave + ".else", "Si no cumple: conservar valor previo", true)) {
                sql.append(" ELSE ").append(ident(columna.nombre()));
            } else {
                Fragmento otro = expresion(ui, clave + ".alternativa", columna, referencias);
                sql.append(" ELSE ").append(otro.sql());
                valores.addAll(otro.valores());
            }
            expresion = new Fragmento(sql.append(" END").toString(), valores);
        } else expresion = expresion(ui, clave + ".valor", columna, referencias);
        Fragmento filtro = filtroEscritura(esquema, ui, clave, tabla, referencias);
        List<Object> valores = new ArrayList<>(expresion.valores());
        valores.addAll(filtro.valores());
        String sql = "UPDATE " + ident(tabla.nombre()) + " SET " + ident(columna.nombre()) + " = "
                + expresion.sql() + (filtro.sql().isEmpty() ? "" : " WHERE " + filtro.sql());
        codigo.add("int filas" + n + " = " + Alfa.llamada("actualizar", new Fragmento(sql, valores)) + ";");
        comprobarFilas(ui, clave, n, codigo);
    }

    private static Fragmento expresion(Golf ui, String clave, Columna columna,
            Set<String> referencias) {
        String tipo = columna.numerica() ? ui.opcion(clave + ".tipo", "Como cambiar " + columna.nombre(),
                "Valor fijo", "Sumar", "Restar", "Porcentaje") : "Valor fijo";
        Object valor = tipo.equals("Valor fijo")
                ? Alfa.pedirValor(ui, clave + ".dato", columna, referencias, !columna.obligatoria())
                : pedirNumero(ui, clave + ".dato", "Cantidad (porcentaje admite negativos)", referencias);
        String sql = switch (tipo) {
            case "Sumar" -> ident(columna.nombre()) + " + ?";
            case "Restar" -> ident(columna.nombre()) + " - ?";
            case "Porcentaje" -> ident(columna.nombre()) + " * (1 + ? / 100.0)";
            default -> "?";
        };
        List<Object> valores = new ArrayList<>();
        valores.add(valor);
        return new Fragmento(sql, valores);
    }

    private static Object pedirNumero(Golf ui, String clave, String pregunta,
            Set<String> referencias) {
        return ui.dato(clave, pregunta, s -> {
            Object valor = Alfa.valor(s, Alfa.numero("numero"), referencias);
            if (!(valor instanceof Number) && !(valor instanceof Alfa.Referencia)) {
                throw new IllegalArgumentException("Se necesita un numero o $id disponible");
            }
            return valor;
        });
    }

    private static Fragmento filtroEscritura(Esquema esquema, Golf ui, String clave,
            Tabla tabla, Set<String> referencias) {
        int intento = 0;
        while (true) {
            String paso = clave + ".where." + intento++;
            Fragmento filtro = Delta.filtros(ui, paso, esquema.campos(tabla, ""), referencias);
            if (!filtro.sql().isEmpty() || ui.si(paso + ".todas",
                    "SIN WHERE: afectar a TODA la tabla", false)) return filtro;
        }
    }

    private static void comprobarFilas(Golf ui, String clave, int n, List<String> codigo) {
        String modo = ui.opcion(clave + ".filas", "Resultado que exige el enunciado",
                "Exactamente una fila", "Al menos una fila", "Cualquier cantidad (incluido 0)");
        if (modo.startsWith("Exactamente")) codigo.add("Sqlite.exigirUnaFila(filas" + n + ");");
        else if (modo.startsWith("Al menos")) {
            codigo.add("if (filas" + n + " < 1) throw new SQLException(\"No se encontro una fila valida\");");
        }
    }

    private static void borrar(Esquema esquema, Golf ui, String clave, int n,
            Set<String> referencias, List<String> codigo) {
        Tabla tabla = tabla(esquema, ui, clave);
        Fragmento filtro = filtroEscritura(esquema, ui, clave, tabla, referencias);
        String sql = "DELETE FROM " + ident(tabla.nombre())
                + (filtro.sql().isEmpty() ? "" : " WHERE " + filtro.sql());
        codigo.add("int filas" + n + " = " + Alfa.llamada("borrar",
                new Fragmento(sql, filtro.valores())) + ";");
        comprobarFilas(ui, clave, n, codigo);
    }

    private static void metadatos(Esquema esquema, Golf ui, String clave,
            Set<String> referencias, List<String> codigo) {
        String tipo = ui.opcion(clave + ".meta", "Metadatos",
                "Tablas", "Columnas de tabla", "Claves foraneas", "Motor y driver", "Columnas de SELECT");
        switch (tipo) {
            case "Tablas" -> codigo.add("Sqlite.mostrarTablas(c);");
            case "Motor y driver" -> codigo.add("Sqlite.mostrarInfoConexion(c);");
            case "Columnas de SELECT" -> codigo.add(Alfa.llamada("mostrarColumnasConsulta",
                    Delta.construir(esquema, ui, clave + ".select", referencias)) + ";");
            default -> {
                Tabla tabla = tabla(esquema, ui, clave);
                codigo.add("Sqlite." + (tipo.startsWith("Columnas") ? "mostrarColumnas" : "mostrarClavesForaneas")
                        + "(c, " + Alfa.javaTexto(tabla.nombre()) + ");");
            }
        }
    }

    private static void recorrer(Esquema esquema, Golf ui, String clave, int n,
            Set<String> referencias, List<String> codigo) {
        List<Tabla> tablas = esquema.tablas().stream().filter(t -> t.claves().size() == 1
                && t.escribibles().stream().anyMatch(c -> c.numerica() && c.primaria() == 0)).toList();
        if (tablas.isEmpty()) {
            ui.linea("Esta receta necesita PK simple y columna numerica.");
            return;
        }
        Tabla tabla = ui.elegir(clave + ".tabla", "Tabla para recorrer", tablas, Tabla::nombre);
        Columna columna = ui.elegir(clave + ".columna", "Columna numerica",
                tabla.escribibles().stream().filter(c -> c.numerica() && c.primaria() == 0).toList(),
                Columna::etiqueta);
        Fragmento filtro = Delta.filtros(ui, clave + ".filtro",
                esquema.campos(tabla, ""), referencias);
        String select = "SELECT " + ident(tabla.claves().get(0).nombre()) + " AS clave, "
                + ident(columna.nombre()) + " AS valor FROM " + ident(tabla.nombre())
                + (filtro.sql().isEmpty() ? "" : " WHERE " + filtro.sql());
        codigo.add("List<Map<String, Object>> filasJava" + n + " = "
                + Alfa.llamada("consultar", new Fragmento(select, filtro.valores())) + ";");
        codigo.add("for (Map<String, Object> fila : filasJava" + n + ") {");
        codigo.add("    if (!(fila.get(\"valor\") instanceof Number)) throw new SQLException(\"Valor no numerico o NULL\");");
        codigo.add("    double anterior = ((Number) fila.get(\"valor\")).doubleValue();");
        codigo.add("    double nuevo = anterior;");
        if (ui.si(clave + ".reglas", "Calculo con reglas por umbral", false)) {
            int regla = 0;
            do {
                String paso = clave + ".umbral." + regla;
                String operador = ui.opcion(paso + ".op", "Comparar valor anterior", "<=", "<", ">=", ">", "==", "!=");
                Object umbral = pedirNumero(ui, paso + ".valor", "Umbral numerico", referencias);
                String valor = calculoJava(ui, paso + ".entonces", referencias);
                codigo.add("    " + (regla == 0 ? "if" : "else if") + " (anterior " + operador + " "
                        + Alfa.javaValor(umbral) + ") nuevo = " + valor + ";");
                regla++;
            } while (ui.si(clave + ".umbral." + regla + ".mas", "Otra regla (prioridad por orden)", false));
        } else codigo.add("    nuevo = " + calculoJava(ui, clave + ".calculo", referencias) + ";");
        String redondeo = ui.opcion(clave + ".redondeo", "Redondeo del resultado",
                "Sin redondear", "Al entero mas cercano", "Hacia abajo", "Hacia arriba");
        if (!redondeo.equals("Sin redondear")) {
            String funcion = redondeo.startsWith("Al entero") ? "round"
                    : redondeo.equals("Hacia abajo") ? "floor" : "ceil";
            codigo.add("    nuevo = Math." + funcion + "(nuevo);");
        }
        codigo.add("    if (!Double.isFinite(nuevo)) throw new SQLException(\"Resultado no finito\");");
        codigo.add("    Sqlite.exigirUnaFila(Sqlite.actualizar(c, "
                + Alfa.javaTexto("UPDATE " + ident(tabla.nombre()) + " SET "
                        + ident(columna.nombre()) + " = ? WHERE " + ident(tabla.claves().get(0).nombre()) + " = ?")
                + ", nuevo, fila.get(\"clave\")));");
        codigo.add("}");
    }

    private static String calculoJava(Golf ui, String clave, Set<String> referencias) {
        String tipo = ui.opcion(clave + ".tipo", "Calculo", "Sumar", "Restar", "Porcentaje", "Valor fijo");
        Object cantidad = pedirNumero(ui, clave + ".cantidad", "Cantidad", referencias);
        String valor = Alfa.javaValor(cantidad);
        return switch (tipo) {
            case "Sumar" -> "anterior + " + valor;
            case "Restar" -> "anterior - (" + valor + ")";
            case "Porcentaje" -> "anterior * (1 + " + valor + " / 100.0)";
            default -> valor;
        };
    }
}
