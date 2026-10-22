# 04 · Listados de directorio con `File`

## Objetivo

Obtener nombres y objetos `File` de las entradas de un directorio, distinguir un
listado vacío de un error. No se exige ordenar: los tests ordenan sus propias
copias para comparar sin añadir trabajo ajeno al objetivo.

## Material de este mini proyecto

- [Teoría de listados de directorio](./teoria/listado-directorio.md)
- Enunciado, contratos y TODO: [ListadoDirectorio.java](./src/main/java/es/dam/accesodatos/autoestudio/file/listado/ListadoDirectorio.java)
- Tests de aceptación: [ListadoDirectorioTest.java](./src/test/java/es/dam/accesodatos/autoestudio/file/listado/ListadoDirectorioTest.java)

## Mapa de tests

| Test | Qué aclara |
|---|---|
| `listaNombresInmediatosSinExigirOrden` | `list()` sin garantizar orden |
| `listaObjetosFileSinExigirOrden` | `listFiles()` y objetos `File` |
| `devuelveArraysVaciosParaUnDirectorioVacio` | Vacío no es lo mismo que error |
| `soloListaLasEntradasInmediatas` | El listado no es recursivo |
| `detectaCuandoUnDirectorioContieneEntradas` | Resultado del predicado de directorio vacío |
| `rechazaRutasNulasInexistentesYQueNoSeanDirectorios` | Validación antes del listado |

Correspondencia: UT02-A, apartado 2.4 del temario oficial.

## Ejecutar

Desde la raíz `autoestudio/`:

```bash
mvn -pl UT02-A-manejo-de-ficheros/04-listado-directorio test
```

En el IDE, ejecuta `ListadoDirectorio.main`. Consulta la
[guía general](../../README_GUIA_TERMINAL.md) para ejecutar un test concreto y
leer el detalle del fallo.
