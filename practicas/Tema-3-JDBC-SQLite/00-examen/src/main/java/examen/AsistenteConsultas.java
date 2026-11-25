package examen;

import examen.ApoyoAsistentes.Contexto;
import examen.ApoyoAsistentes.Fragmento;
import java.nio.file.Path;
import java.util.Set;

// Ejecuta esta clase desde 00-examen o usa: bash ejecutar.sh consultas
// Argumentos opcionales: base.sqlite sesion.properties
public class AsistenteConsultas {
    public static void main(String[] args) {
        TerminalCompacta ui = new TerminalCompacta();
        try (Contexto contexto = Contexto.abrir("consultas", args, ui)) {
            while (true) {
                try {
                    ui.reiniciar();
                    Fragmento consulta = ConstructorConsulta.construir(contexto.esquema, ui, "consulta", Set.of());
                    String nombre = "consulta-" + ApoyoAsistentes.unico();
                    Path sql = ApoyoAsistentes.guardarNuevo(Path.of("consultas"), nombre + ".sql",
                            ApoyoAsistentes.literalizar(consulta) + ";\n");
                    ApoyoAsistentes.guardarNuevo(Path.of("consultas"), nombre + "-java.txt",
                            "// Dentro de try (Connection c = Sqlite.abrir(db)):\n"
                                    + ApoyoAsistentes.llamada("mostrarConsulta", consulta) + ";\n");
                    ApoyoAsistentes.guardarNuevo(Path.of("consultas"), nombre + "-esquema.txt",
                            contexto.esquema.informe());
                    ui.linea("SQL: " + sql);
                    while (true) {
                        String comando = ui.comando("ver / sql / probar / nueva / atras / sesion / salir");
                        switch (comando.toLowerCase()) {
                            case "ver" -> ui.ver(sql + "\n" + sql.toString().replace(".sql", "-java.txt"));
                            case "sql" -> ui.ver(ApoyoAsistentes.literalizar(consulta) + ";");
                            case "sesion" -> ui.ver(contexto.sesion.toString());
                            case "probar" -> {
                                try {
                                    ApoyoAsistentes.probarConsulta(contexto.conexion, consulta, ui);
                                } catch (java.sql.SQLException error) {
                                    ui.linea("Consulta no valida: " + error.getMessage());
                                }
                            }
                            case "nueva" -> {
                                ui.nueva();
                                throw new TerminalCompacta.Volver();
                            }
                            case "atras" -> ui.retroceder();
                            default -> ui.linea("Opciones: ver, sql, probar, nueva, sesion, salir.");
                        }
                    }
                } catch (TerminalCompacta.Volver volver) {
                    // Reconstruye las decisiones anteriores sin volver a imprimirlas.
                }
            }
        } catch (TerminalCompacta.Salir salir) {
            ui.linea("Fin. Las respuestas se conservan en sesiones/.");
        } catch (Exception error) {
            ui.linea("Error: " + error.getMessage());
            System.exit(1);
        }
    }
}
