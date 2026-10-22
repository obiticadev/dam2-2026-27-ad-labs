# 07 · Flujos de bytes

## Objetivo

Copiar un fichero por bloques con `FileInputStream` y `FileOutputStream` sin
escribir bytes sobrantes, y anexar una porción de un array con `off` y `len`.

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
