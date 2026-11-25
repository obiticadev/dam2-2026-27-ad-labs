package examen;

import examen.ApoyoAsistentes.Contexto;
import examen.ConstructorEjercicio.Ejercicio;
import java.nio.file.Path;

// Genera una PROPUESTA de Java. No ejecuta las operaciones de escritura.
// Directorio de trabajo: 00-examen. Argumentos: [base.sqlite] [sesion.properties].
public class AsistenteEjercicios {
    public static void main(String[] args) {
        TerminalCompacta ui = new TerminalCompacta();
        try (Contexto contexto = Contexto.abrir("ejercicios", args, ui)) {
            while (true) {
                try {
                    ui.reiniciar();
                    Ejercicio ejercicio = ConstructorEjercicio.construir(contexto.esquema, ui);
                    String clase = "Solucion" + ApoyoAsistentes.unico();
                    Path archivo = ApoyoAsistentes.guardarNuevo(Path.of("propuestas"), clase + ".java",
                            ejercicio.fuente(clase));
                    ApoyoAsistentes.guardarNuevo(Path.of("propuestas"), clase + "-revision.txt",
                            "PROPUESTA, NO SOLUCION VERIFICADA\n"
                                    + String.join("\n", ejercicio.avisos()) + "\n\n"
                                    + "Prueba exito, ID inexistente, duplicados, FK/CHECK y fallo tras el primer cambio.\n"
                                    + "Comprueba que commit/rollback persisten al reabrir una COPIA de la base.\n"
                                    + "Los calculos double no sustituyen una politica de dinero/precision del enunciado.\n"
                                    + "Una lectura LEFT JOIN no implica que haya que modificar filas sin relacion.\n"
                                    + "Origen: " + contexto.base + "\n\n" + contexto.esquema.informe());
                    ui.linea("No se ejecuto ningun cambio.");
                    ui.linea("Java: " + archivo);
                    while (true) {
                        String comando = ui.comando("ver / codigo / avisos / nueva / atras / sesion / salir");
                        switch (comando.toLowerCase()) {
                            case "ver" -> ui.ver(archivo.toString());
                            case "codigo" -> ui.ver(ejercicio.fuente(clase));
                            case "avisos" -> ui.ver(String.join("\n", ejercicio.avisos()));
                            case "sesion" -> ui.ver(contexto.sesion.toString());
                            case "nueva" -> {
                                ui.nueva();
                                throw new TerminalCompacta.Volver();
                            }
                            case "atras" -> ui.retroceder();
                            default -> ui.linea("Opciones: ver, codigo, avisos, nueva, atras, sesion, salir.");
                        }
                    }
                } catch (TerminalCompacta.Volver volver) {
                    // Reproduce silenciosamente las respuestas previas a la corrección.
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
