# Ruta práctica · Primer examen de AD

## Fuentes y alcance

- [Fuente docente UT02-A](<../../DAM2/Fuentes/AD/temario/UT02-A-manejo-de-ficheros.md>).
- [Fuente docente UT02-B](<../../DAM2/Fuentes/AD/temario/UT02-B-librerias-xml-java.md>).
- [Referencias del examen en Obsidian](<../../DAM2/Exámenes/AD/AD - Primer examen.md>).
- [Caso integrador añadido de alumnos](<../../DAM2/Biblioteca/AD - Caso integrador - Alumnos.md>).

Calendario consultado el 21/10/2026: examen el **04/11/2026**; el bloque
19:35–21:25 es horario de referencia, no duración oficial. Evaluación allí
registrada: 60 % test, 30 % práctica y 10 % entrega ya realizada, sin nota conocida.

**Corrección posterior del usuario: NIO NO entra.** No estudiar el capítulo 3 de
UT02-A para este examen. La nota de Obsidian todavía dice «ambos completos» y la
sesión del 30/10 menciona NIO: son referencias desactualizadas en este punto.
Aquí esa sesión se dedica a objetos, posiciones y errores de `java.io`. No se
han editado los eventos, la entrega ni la bóveda.

## Cómo trabajar cada sesión

En una hora: 5 min de recuperación, 10 de traza, 20 de código, 5 de descanso,
15 de variante/corrección y 5 de cierre. En dos horas: 10 + 40 de práctica +
10 de descanso + 40 de variante + 10 de comprobación + 10 de cierre.

No hay que terminar todos los métodos en la sesión: elige el núcleo indicado,
anota lo pendiente y prioriza el error principal. Las predicciones preparan el
test; no repartas mecánicamente cada sesión según los porcentajes de la nota.

La estructura se adapta al aprendizaje: métodos cortos para una operación,
talleres para un ciclo completo y trazas para razonar. Los TODO son ayudas,
no una cuota fija de pasos ni una obligación de copiar un estilo de solución.

| Fecha y hora local (Madrid) | Núcleo que debes intentar | Variante o comprobación |
|---|---|---|
| 22/10 · 09–10 | [Bytes](UT02-A-manejo-de-ficheros/07-flujos-bytes/README.md): `copiar` | 6 bytes/búfer 4; vacío; `anexar` con `off=1,len=2` |
| 24/10 · 21–23 | [DOM](UT02-B-librerias-xml-java/01-parseo-dom/README.md): cargar, extraer, actualizar y guardar | Reabrir; agregar/eliminar; contar hijos sin contar indentación |
| 25/10 · 17–19 | [Texto](UT02-A-manejo-de-ficheros/08-flujos-caracteres/README.md), [reescritura](UT02-A-manejo-de-ficheros/09-acceso-secuencial/README.md) y [seek](UT02-A-manejo-de-ficheros/11-acceso-aleatorio/README.md) | No rehacer todo: trazar texto, implementar eliminar y actualizar un registro |
| 26/10 · 09–10 | [SAX](UT02-B-librerias-xml-java/02-parseo-sax/README.md): completar manejador y extracción | Forzar dos llamadas a `characters`; comprobar `start/length` |
| 27/10 · 09–10 | [StAX](UT02-B-librerias-xml-java/03-parseo-stax/README.md): cursor e iterador | Traza de `getElementText`; leer atributos antes de consumir el texto |
| 30/10 · 09–10 | [Objetos](UT02-A-manejo-de-ficheros/10-serializacion-objetos/README.md) y errores de A | `transient`, casting, `r/rw`, EOF y repaso `File`; **sin NIO** |
| 31/10 · 21–23 | [JAXB](UT02-B-librerias-xml-java/04-enlace-jaxb/README.md): anotar colección, guardar y recuperar | Cambiar un objeto y guardar otro XML; contrastar las cuatro API |
| 01/11 · 17–19 | Integración XML de abajo | Rehacer el fragmento que más falló, sin mirar |
| 03/11 · 09–10 | Simulacro mixto de abajo | Corregir causa y resolver una variante |
| 04/11 · 09–10 | Dos fragmentos cortos de errores previos | Repaso tranquilo, sin contenido nuevo |

## Integración del 01/11

Datos comunes: [alumnos.xml](UT02-B-librerias-xml-java/01-parseo-dom/datos/alumnos.xml).
Es práctica añadida, no una pregunta oficial.

1. Extrae ids y nombres con DOM y SAX. En StAX, reutiliza los lectores existentes
   con etiquetas `nombre` y `alumno`/atributo `id` (cada método recibe el XML completo).
2. Cambia la nota de id `1`, añade id `3` y guarda otro archivo mediante DOM.
3. Vuelve a consultar el archivo guardado y comprueba qué cambió. No confundas el
   DOM modificado con el archivo de origen ni con objetos ya recuperados.
4. Contrasta con la colección de libros de JAXB: ¿qué anotación corresponde al
   contenedor y cuál a cada objeto? No hace falta crear otro modelo JAXB de alumnos.

Resultado esperado del XML original: ids `1, 2`; nombres `Ana, José`.
Si añades a Marta y cambias 8.5 a 9.0, el nuevo XML tendrá tres alumnos y Ana
con 9.0; el origen seguirá teniendo dos alumnos y 8.5.

## Simulacro mixto del 03/11

No tiene ponderación ni duración oficial. Ajusta estas actividades a tu hora:

1. Predice la copia de 6 bytes con búfer de 4. ¿Qué error introduce `write(buffer)`?
2. Distingue `readLine()==null` de `readLine().isEmpty()` y `append` de sobrescribir.
3. Para registro `int + double`, explica las posiciones de registro 2 y de su nota.
4. Completa sin apuntes una operación de `TallerDom` o un lector SAX/StAX.
5. Explica `@XmlRootElement`, `@XmlType`, `@XmlElement`, `@XmlAttribute` y
   `@XmlElementWrapper`. Distingue `marshal` de `unmarshal`.

<details><summary>Comprobaciones después de intentarlo</summary>

1. Lecturas 4 y 2, después -1. El último `write(buffer)` reutiliza bytes sobrantes.
2. `null` es EOF; `""` es una línea vacía. `append=true` añade al final.
3. Registro 2 en byte 24; su nota en 28. Id e índice no son lo mismo.
4. Comprueba con el test concreto y explica el error antes de repetir.
5. Raíz; tipo/orden; elemento; atributo; contenedor de colección. Objetos→XML y XML→objetos.

</details>

## Cobertura del contenido evaluable

Se distingue teoría docente de práctica añadida. No se promete una pregunta por
cada método de las tablas; los puntos sin un método propio tienen comprobación
razonada para no dejar huecos.

| Apartados docentes | Práctica o comprobación |
|---|---|
| A §1–2.3: persistencia, flujos, clases y formas de acceso | Explica RAM/archivo, entrada/salida, byte/carácter y secuencial/aleatorio antes de `07`, `08`, `11`. Reconoce `InputStream`, `OutputStream`, `Reader`, `Writer` como abstractas; `DataOutputStream` escribe tipos primitivos. Sin programación NIO. |
| A §2.4–2.6: `File`, operaciones y filtros | Proyectos `01`–`05`; repaso de API de abajo. Catalogador opcional. |
| A §2.7: bytes | `07`: lectura individual frente a bloques, -1, off/len, append, cierre y errores. |
| A §2.8–2.9: objetos | `10`: modelo serializable, escritura/lectura y casting. `transient` es aclaración añadida del repaso. |
| A §2.10: caracteres | `08`: `FileReader`/`FileWriter`, buffers, UTF-8, líneas y cierre. Contrasta `InputStreamReader` y `OutputStreamWriter`, `flush` y `close`; reconoce `PrintWriter`/`StringWriter`. |
| A §2.11: acceso secuencial | `09`: buscar, copiar/reescribir sin un registro y cerrar. |
| A §2.12: acceso aleatorio | `11`: leer/escribir, modos, longitud y posición. Registros fijos: práctica añadida del repaso; explica también `skipBytes`, `getFilePointer`, `readFully` y el ejemplo docente de anexar con `seek(length())`. |
| A §2.13 | Enlaces de consulta, no un nuevo ejercicio. |
| A §3 (NIO) | **Excluido por confirmación del usuario**. |
| B §1–1.3: XML, parser y serialización | [Elección de API](UT02-B-librerias-xml-java/README.md): textual/portable/jerárquico; secuencial frente a árbol; push/pull; TrAX/XSLT y XStream se reconocen como referencias mencionadas, no implementaciones añadidas. |
| B §2–2.5: DOM | `ParseoDom` + `TallerDom`: parse/newDocument, factorías, jerarquía de nodos, navegación, atributos/texto, creación, normalización y transformación. [Traza de navegación](UT02-B-librerias-xml-java/01-parseo-dom/teoria/taller-dom.md). |
| B §3–3.2: SAX | `ParseoSax` + `ManejadorAlumnos`: factoría/parser, eventos de documento/elemento, atributos y fragmentos; lectura UTF-8 explicada en la guía. |
| B §4–4.3: StAX | Lectores de `ParseoStax`: cursor, iterador, atributos, texto y posición. Escritura: concepto docente y práctica guiada opcional, no requisito de la ruta básica. |
| B §5–5.4: JAXB | `EnlaceJaxb` básico + `BibliotecaLibros`, `FichaLibro`, `TallerJaxb`: anotaciones, colección, archivos, modificación y ciclo de enlace. Reconoce esquema/XSD, generación `xjc`, compilación y validación opcional del ciclo; no se exige programar un generador. |

### Repaso breve de API que no tiene un método propio

Predice y comprueba con los ejemplos docentes, no añadas un proyecto por método:

- `getPath`, `getAbsolutePath`, `getCanonicalPath`: ¿cuál lanza `IOException`?
  ¿Hacer absoluta una ruta prueba que exista? No; la ruta canónica resuelve redundancias.
- `getParent` devuelve cadena y `getParentFile` devuelve objeto; ambos pueden dar `null`.
  `isAbsolute` describe la ruta, no su existencia.
- `listRoots` lista raíces; `createTempFile` crea físicamente un temporal con nombre único;
  `deleteOnExit` difiere el borrado al fin de la JVM; `setLastModified` intenta cambiar la fecha.
- `renameTo` no cambia el String del objeto `File` original. Sus resultados dependen
  del sistema; la protección de no sobrescribir del proyecto es una decisión didáctica.
- `available()` no es un detector general de EOF. `read()` devuelve 0–255 o -1;
  leer un bloque devuelve una cantidad, no el valor del byte.
- `InputStreamReader` convierte bytes a caracteres; `OutputStreamWriter` hace la
  conversión inversa. La codificación de caracteres no es `normalize()` de DOM.
- Los atributos DOM son `Attr`; `Document`, `Element`, `Text`, `Attr` derivan de `Node`.
  `NodeList` es una colección, no un nodo. `DocumentBuilder` y `Transformer` son abstractas.

## Registro de errores

Comunica qué método intentaste, qué esperabas y qué obtuviste; puedo registrar tu
avance. No necesitas mantener notas a mano. Una sesión planificada no implica que
esté completada. Las soluciones de referencia siguen en Obsidian y las pistas
locales se despliegan solo después del intento.
