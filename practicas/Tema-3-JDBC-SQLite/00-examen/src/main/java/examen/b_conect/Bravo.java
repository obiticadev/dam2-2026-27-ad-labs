package examen.b_conect;

import examen.b_conect.Alfa.Contexto;
import examen.b_conect.Alfa.Fragmento;
import java.nio.file.Path;
import java.util.Set;

// Ejecuta esta clase desde 00-examen o usa: bash ejecutar.sh consultas
// Argumentos opcionales: base.sqlite sesion.properties
public class Bravo {
    public static void main(String[] args) {
        Golf ui = new Golf();
        try (Contexto contexto = Contexto.abrir("consultas", args, ui)) {
            while (true) {
                try {
                    ui.reiniciar();
                    Fragmento consulta = Delta.construir(contexto.esquema, ui, "consulta", Set.of());
                    String nombre = "consulta-" + Alfa.unico();
                    Path sql = Alfa.guardarNuevo(Path.of("b_consultas"), nombre + ".sql",
                            Alfa.literalizar(consulta) + ";\n");
                    Alfa.guardarNuevo(Path.of("b_consultas"), nombre + "-java.txt",
                            "// Dentro de try (Connection c = Sqlite.abrir(db)):\n"
                                    + Alfa.llamada("mostrarConsulta", consulta) + ";\n");
                    Alfa.guardarNuevo(Path.of("b_consultas"), nombre + "-esquema.txt",
                            contexto.esquema.informe());
                    ui.linea("SQL: " + sql);
                    while (true) {
                        String comando = ui.comando("ver / sql / probar / nueva / atras / sesion / salir");
                        switch (comando.toLowerCase()) {
                            case "ver" -> ui.ver(sql + "\n" + sql.toString().replace(".sql", "-java.txt"));
                            case "sql" -> ui.ver(Alfa.literalizar(consulta) + ";");
                            case "sesion" -> ui.ver(contexto.sesion.toString());
                            case "probar" -> {
                                try {
                                    Alfa.probarConsulta(contexto.conexion, consulta, ui);
                                } catch (java.sql.SQLException error) {
                                    ui.linea("Consulta no valida: " + error.getMessage());
                                }
                            }
                            case "nueva" -> {
                                ui.nueva();
                                throw new Golf.Volver();
                            }
                            case "atras" -> ui.retroceder();
                            default -> ui.linea("Opciones: ver, sql, probar, nueva, sesion, salir.");
                        }
                    }
                } catch (Golf.Volver volver) {
                    // Reconstruye las decisiones anteriores sin volver a imprimirlas.
                }
            }
        } catch (Golf.Salir salir) {
            ui.linea("Fin. Las respuestas se conservan en b_sesiones/.");
        } catch (Exception error) {
            ui.linea("Error: " + error.getMessage());
            System.exit(1);
        }
    }
}
