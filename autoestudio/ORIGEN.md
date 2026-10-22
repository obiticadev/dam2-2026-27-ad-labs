# Procedencia y criterios de adaptación

## Autoridad de las fuentes

1. Markdown docente UT02-A/B conservado en [Fuentes de Obsidian](<../../DAM2/Fuentes/AD/temario/>).
2. Alcance confirmado por el usuario: **NIO excluido**, 21/10/2026. Prevalece sobre
   la nota de examen antigua y la mención a NIO del calendario.
3. Ejemplos de clase del repositorio `practicas/UT02-A-manejo-de-ficheros` y
   `practicas/UT02-B-xml-java`; las explicaciones y ejercicios de Obsidian se
   reconocen como material añadido cuando así están marcados.
4. Masterclass: origen histórico de algunos esqueletos, no autoridad del examen.

No se han procesado los PDF equivalentes ni cambiado los materiales fuente,
la bóveda, las prácticas entregadas o el calendario.

## Correspondencias

| Proyecto | Apartado docente / referencia |
|---|---|
| A `01`–`05` | §2.4–2.6, `File` y `FilenameFilter`; ejemplo Filtrar con `endsWith` |
| A `06` | Integración opcional heredada; no cubre toda UT02-A |
| A `07` | §2.7 y ejemplo CopiaFicheros; práctica añadida de rangos y tests |
| A `08` | §2.10 y ejemplos de caracteres/buffers |
| A `09` | §2.11; búsqueda/eliminación por reescritura es práctica añadida del repaso |
| A `10` | §2.8–2.9; campo `transient` tomado de la aclaración de repaso de Obsidian |
| A `11` | §2.12; registros `int + double` son práctica añadida del repaso |
| B DOM | §2–2.5; consultas heredadas de `Ej145DomSaxParsing`, taller añadido de alumnos |
| B SAX | §3–3.2; base heredada de `Ej145DomSaxParsing`, manejador añadido para fragmentación determinista |
| B StAX | §4–4.3, ListaLibrosStAX y EventReader; writers conservados como ampliación guiada |
| B JAXB | §5–5.4; base `Ej143JaxbBinding` adaptada a javax; nuevo taller de anotaciones y colecciones |

## Qué deja de ser obligatorio

- Rechazo de `DOCTYPE` y ErrorHandler especial para silenciar diagnósticos. El
  helper de seguridad ya implementado se conserva como trabajo personal opcional.
- Ordenar los listados básicos, normalizar extensiones según locale, distinguir
  mayúsculas de forma distinta a `endsWith` o exigir listas inmutables.
- Los diez retos extra de JAXB y el catalogador no bloquean la suite básica.
- No se exige una cadena XML exacta por indentación o declaración. Las nuevas
  pruebas reabren el XML y comprueban su estructura y datos.

Los contratos básicos conservan controles razonables de entrada y errores de
E/S. No se oculta un error como si fuera una lista vacía o un resultado válido.
`normalize()`, `INDENT` y `JAXB_FORMATTED_OUTPUT` permanecen porque sí aparecen en
los ejemplos docentes. No eliminan el `DOCTYPE` ni son compactación de texto.

## Conservación de trabajo

Las implementaciones personales de `ParseoDom` y `ParseoSax` no se reemplazan.
Sus TODO y errores pendientes se resuelven con los tests básicos. Los nuevos
talleres son esqueletos compilables, no soluciones ya hechas. Los extras se
conservan identificados y se ejecutan expresamente con `-Pampliaciones`.
