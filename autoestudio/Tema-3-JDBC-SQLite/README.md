# Tema 3 · JDBC y SQLite

Proyecto de práctica **para completar tú**, compatible con Java 17+. Sin servidor,
sin descargas en cada ejercicio y sin tocar tus soluciones de UT02.

1. [B00 · Configuración rápida y proyecto nuevo sin Internet](00-CONFIGURACION.md).
2. [Ruta de seis horas para el miércoles](../RUTA_6_HORAS.md).
3. [B01–B10 · Enunciados, pistas y variantes](BLOQUES.md).
4. [Fundamentos y preguntas con respuesta](FUNDAMENTOS.md).
5. [Cobertura, teoría y ejercicios originales](COBERTURA.md).
6. [Verificación y límites](../VERIFICACION_TEMAS_3_4.md).

## Archivos que usarás

| Archivo | Función |
|---|---|
| `src/main/java/lab/Arranque.java` | Ejemplo mínimo funcional: crea y reabre un archivo SQLite. |
| `src/main/java/lab/Jdbc.java` | Tus ejercicios B01–B09; comienza con métodos por completar. |
| `src/main/java/lab/Simulacro.java` | B10: dos métodos para resolver desde cero. |
| `src/test/java/lab/` | Tests con datos aislados, no usan ninguna base personal. |
| `soluciones/lab/` | Referencia separada, con las mismas firmas. No se compila normalmente. |
| `lib/` | JAR preparados localmente para no depender de Maven al ejecutar. Ignorados por Git. |

Desde esta carpeta:

```bash
mvn -o test -Dgroups=b00
mvn -o test -Dgroups=b02
python3 ../herramientas/laboratorio.py arranque
```

`mvn -o test` prueba **tus intentos** y fallará mientras queden métodos pendientes.
Para comprobar la referencia sin sobrescribirlos:

```bash
python3 ../herramientas/laboratorio.py soluciones
```

La referencia se copia a un directorio temporal limpio que se elimina al terminar.
No hace falta instalar el proyecto padre. Este POM es independiente; el `mvn test`
de `autoestudio/` sigue reservado a las prácticas antiguas.
