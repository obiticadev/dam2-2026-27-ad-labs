# 03 · Operaciones con `File`

## Objetivo

Practicar las operaciones de `java.io.File` para crear ficheros, crear
directorios, cambiar el nombre de una entrada y borrar ficheros o directorios
vacíos.

## Material de este mini proyecto

- [Teoría de operaciones con `File`](./teoria/operaciones-file.md)
- Enunciado, contratos y TODO: [OperacionesFile.java](./src/main/java/es/dam/accesodatos/autoestudio/file/operaciones/OperacionesFile.java)
- Tests de aceptación: [OperacionesFileTest.java](./src/test/java/es/dam/accesodatos/autoestudio/file/operaciones/OperacionesFileTest.java)

## Mapa de tests

| Test | Qué aclara |
|---|---|
| `creaFicheroNuevoYNoSobrescribeElExistente` | `createNewFile()` y su booleano |
| `propagaElFalloSiFaltaElDirectorioPadre` | `IOException` cuando falta el padre |
| `mkdirCreaUnSoloNivelYNoCreaPadres` | Alcance de `mkdir()` |
| `mkdirsCreaLosNivelesPadresQueFaltan` | Alcance de `mkdirs()` |
| `renombraUnaEntradaDentroDelMismoDirectorio` | Renombrado de una entrada |
| `noRenombraSiElOrigenFaltaOSiElDestinoYaExiste` | Origen/destino inválidos sin sobrescritura |
| `borraFicherosPeroNoDirectoriosNoVacios` | Borrado de fichero y directorio no vacío |
| `rechazaArgumentosNulos` | Validación de referencias nulas |

Correspondencia: UT02-A, apartado 2.5 del temario oficial.

## Ejecutar

Desde la raíz `autoestudio/`:

```bash
mvn -pl UT02-A-manejo-de-ficheros/03-operaciones-file test
```

En el IDE, ejecuta `OperacionesFile.main`. Consulta la
[guía general](../../README_GUIA_TERMINAL.md) para ejecutar un test concreto y
leer el detalle del fallo.
