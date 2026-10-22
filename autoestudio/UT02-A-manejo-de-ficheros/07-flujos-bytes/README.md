# 07 · Flujos de bytes

**Sesión 22/10 · 1 hora.** Fuente: UT02-A §2.7.

## Objetivo

Copiar un fichero por bloques con `FileInputStream` y `FileOutputStream` sin
escribir bytes sobrantes, y anexar una porción de un array con `off` y `len`.
No uses `Files.copy` ni cargues el archivo entero en memoria: el objetivo es
entender la lectura por bloques.

## Material de este mini proyecto

- [Teoría de flujos de bytes](./teoria/flujos-bytes.md). Empieza por la tabla «Predice antes de programar».
- Enunciado, contratos y TODO: [FlujosBytes.java](./src/main/java/es/dam/accesodatos/autoestudio/flujos/bytes/FlujosBytes.java)
- Tests de aceptación: [FlujosBytesTest.java](./src/test/java/es/dam/accesodatos/autoestudio/flujos/bytes/FlujosBytesTest.java)

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

En el IDE, ejecuta `FlujosBytes.main`. Escribe sus ficheros de prueba en
`target/demo-data/07-flujos-bytes`, una carpeta que Git ignora. Consulta la
[guía general](../../README_GUIA_TERMINAL.md) para leer el detalle de un fallo.

## Tests y criterio de dominio

Los tests preparan archivos temporales; no necesitas crear datos manualmente.

- Última lectura parcial, byte cero y byte 255.
- Archivo vacío y tamaño múltiplo del búfer.
- Rango `off/len`, anexado y rango de longitud cero.
- Capacidad inválida y error de apertura del destino. Un origen inexistente se
  rechaza con `IllegalArgumentException`, de acuerdo con el contrato de esta versión.

Variante: cambia el búfer a 1 y a 3. Deben conservarse los mismos bytes.
Explica por qué `read()` y `read(byte[])` devuelven cosas distintas.
