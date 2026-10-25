# 07 · Flujos de bytes

**Sesión 22/10 · 1 hora.** Fuente: UT02-A §2.7.

## Objetivo

Copiar un fichero por bloques con `FileInputStream` y `FileOutputStream` sin
escribir bytes sobrantes, y anexar una porción de un array con `off` y `len`.
No uses `Files.copy` ni cargues el archivo entero en memoria: el objetivo es
entender la lectura por bloques.

Después, afianzar el mismo patrón en cinco situaciones sencillas: guardar un
array, contar bytes, buscar coincidencias, anexar un fichero y copiar sustituyendo
un byte. No hacen falta nuevas clases de flujos ni algoritmos avanzados.

## Material de este mini proyecto

- [Teoría de flujos de bytes](./teoria/flujos-bytes.md). Empieza por la tabla «Predice antes de programar».
- Enunciado, contratos y TODO: [FlujosBytes.java](./src/main/java/es/dam/accesodatos/autoestudio/flujos/bytes/FlujosBytes.java)
- Tests de aceptación: [FlujosBytesTest.java](./src/test/java/es/dam/accesodatos/autoestudio/flujos/bytes/FlujosBytesTest.java)
- Tests de los ejercicios nuevos: [FlujosBytesPracticaTest.java](./src/test/java/es/dam/accesodatos/autoestudio/flujos/bytes/FlujosBytesPracticaTest.java)

## Mapa de tests

| Test | Qué aclara |
|---|---|
| `copiaSeisBytesConBuferDeCuatroSinBytesSobrantes` | `write(buffer, 0, leidos)` en la última lectura parcial |
| `copiaUnArchivoVacioSinEscribirNada` | Primera lectura `-1`, el destino queda creado y vacío |
| `copiaUnBloqueExacto` | Tamaño múltiplo del búfer |
| `copiaUnArchivoMuchoMayorQueElBufer` | Bucle con muchas vueltas |
| `sobrescribeUnDestinoPrevioEnLugarDeAnexar` | `FileOutputStream` sin append trunca |
| `copiarRechazaArgumentosInvalidos` | Origen, destino y capacidad válidos |
| `copiarRechazaQueElDestinoSeaElOrigenYNoLoDestruye` | Validar antes de abrir y comparar rutas canónicas |
| `anexaUnaPorcionDelArrayConOffYLen` | `[9]` + `{10,20,30,40}`, off=1, len=2 → `[9,20,30]` |
| `anexaUnBloqueCompletoAUnArchivoVacio` | Anexar a un archivo vacío |
| `anexarVariasVecesConservaLoAnterior` | Modo append acumulado y `len = 0` |
| `anexarCreaElDestinoSiNoExiste` | Append también crea el fichero |
| `anexarRechazaRangosFueraDelArraySinTocarElDestino` | Validar `off`/`len` antes de abrir el flujo |
| `anexarRechazaArgumentosNulos` | Validación de referencias nulas |
| `copiaBytesCeroY255ConDistintasCapacidades` | Bytes binarios y búfer de 1, 3 y 4 posiciones |
| `copiarPropagaErrorDeAperturaDelDestino` | Los errores de E/S se propagan como `IOException` |

Prioridad: primero los tests de `copiar`, después los de `anexar`.

## Práctica progresiva: ejercicios 3 a 7

Los métodos nuevos tienen contrato y TODO en `FlujosBytes.java`.
`guardar` ya tiene implementación; los ejercicios 4 a 7 siguen pendientes y
devuelven `-1`. Es normal que los tests de esos ejercicios fallen al principio.
Los ejercicios originales y sus tests se mantienen separados.

| Orden | Método | Qué practicas | Tests que empiezan por |
|---|---|---|---|
| 3 | `guardar` | Solo salida; escribir un array completo y sobrescribir | `guardar` |
| 4 | `contarBytes` | Solo entrada; acumular lo realmente leído | `contarBytes` |
| 5 | `contarApariciones` | Procesar cada byte válido del bloque con un `for` | `contarApariciones` |
| 6 | `anexarArchivo` | Entrada y salida; conservar el destino y añadir otro fichero | `anexarArchivo` |
| 7 | `copiarSustituyendo` | Modificar la parte válida del búfer antes de escribir | `copiarSustituyendo` |

### Cómo trabajar cada ejercicio

1. Lee su contrato y predice el resultado del ejemplo del `main`.
2. Decide qué flujos necesitas y si la salida sobrescribe o anexa.
3. Completa sus TODO sin cambiar los tests. Valida antes de abrir.
4. Ejecuta solo los tests de ese ejercicio; no necesitas terminar todos para avanzar.
5. Ejecuta el `main` y compara el resultado con el esperado.

Los tests comprueban situaciones normales, archivo vacío, lectura parcial y
argumentos inválidos. En `contarApariciones`, presta especial atención al caso
`[7, 2, 7, 4, 5]` con capacidad 4: los restos del búfer no son datos nuevos.
En `anexarArchivo`, el total devuelto cuenta solo lo añadido. En
`copiarSustituyendo`, cuenta todos los bytes escritos, no solo los cambiados.

Antes de pasar al siguiente, explica en una frase qué cambió respecto de `copiar`
y qué parte del patrón sigue igual. No uses `Files.copy`, `readAllBytes` ni
`File.length()` para resolver estos métodos. El `leer` auxiliar del playground
puede usar `readAllBytes` porque solo muestra los pequeños ejemplos preparados.

Correspondencia: UT02-A, apartado 2.7 del temario oficial.

## Ejecutar

Desde la raíz `autoestudio/`:

```bash
mvn -pl UT02-A-manejo-de-ficheros/07-flujos-bytes test
```

Para un test concreto:

```bash
mvn -pl UT02-A-manejo-de-ficheros/07-flujos-bytes -Dtest=FlujosBytesTest#copiaSeisBytesConBuferDeCuatroSinBytesSobrantes test
```

Para practicar solo el ejercicio 3 (cambia `guardar*` por el prefijo de la tabla):

```bash
mvn -pl UT02-A-manejo-de-ficheros/07-flujos-bytes "-Dtest=FlujosBytesPracticaTest#guardar*" test
```

Para ejecutar únicamente los dos ejercicios originales:

```bash
mvn -pl UT02-A-manejo-de-ficheros/07-flujos-bytes -Dtest=FlujosBytesTest test
```

También puedes ejecutar cada método de test desde el IDE.

En el IDE, ejecuta `FlujosBytes.main`. Escribe sus ficheros de prueba en
`target/demo-data/07-flujos-bytes`, una carpeta que Git ignora. Consulta la
[guía general](../../README_GUIA_TERMINAL.md) para leer el detalle de un fallo.
El playground incluye los siete ejercicios y muestra sus valores esperados.
Regenera los datos iniciales en cada ejecución, de modo que los anexados no se
acumulen de una ejecución a otra. Los ejemplos pendientes muestran diferencias
hasta que los implementes.

## Tests y criterio de dominio

Los tests preparan archivos temporales; no necesitas crear datos manualmente.

- Última lectura parcial, byte cero y byte 255.
- Archivo vacío y tamaño múltiplo del búfer.
- Rango `off/len`, anexado y rango de longitud cero.
- Capacidad inválida y error de apertura del destino. Un origen inexistente se
  rechaza con `IllegalArgumentException`, de acuerdo con el contrato de esta versión.

Variante: cambia el búfer a 1 y a 3. Deben conservarse los mismos bytes.
Explica por qué `read()` y `read(byte[])` devuelven cosas distintas.
