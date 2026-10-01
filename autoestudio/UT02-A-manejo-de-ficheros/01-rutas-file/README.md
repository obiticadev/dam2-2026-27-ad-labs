# 01 · Rutas con `File`

## Objetivo

Practicar cómo representar una ruta mediante `java.io.File`, combinar un
directorio y un nombre, y consultar los componentes de esa ruta. Crear un objeto
`File` **no crea** el fichero ni el directorio en el sistema.

## Material de este mini proyecto

- [Teoría de rutas y `File`](./teoria/rutas-file.md)
- Enunciado, contratos y TODO: [RutasFile.java](./src/main/java/es/dam/accesodatos/autoestudio/file/rutas/RutasFile.java)
- Tests de aceptación: [RutasFileTest.java](./src/test/java/es/dam/accesodatos/autoestudio/file/rutas/RutasFileTest.java)

## Mapa de tests

| Test | Qué aclara |
|---|---|
| `representaUnaRutaSinCrearElFichero` | Construir un `File` no crea la entrada física |
| `combinaUnDirectorioYUnNombre` | Constructor con directorio y nombre |
| `combinaUnObjetoPadreConElNombreDelHijo` | Constructor con padre `File` |
| `obtieneElNombreDeLaRutaAunqueNoExista` | `getName()` no requiere que exista el fichero |
| `devuelveLaCadenaDeRutaOriginal` | Diferencia entre ruta representada y normalizada |
| `obtieneElPadreInmediatoOElResultadoNuloDeFile` | Padre disponible o `null` |
| `rechazaArgumentosInvalidos` | Validación de argumentos del contrato |

Correspondencia: UT02-A, apartado 2.4 del temario oficial.

## Ejecutar

Desde la raíz `autoestudio/`:

```bash
mvn -pl UT02-A-manejo-de-ficheros/01-rutas-file test
```

En el IDE, ejecuta `RutasFile.main`. La guía de
[`autoestudio`](../../README_GUIA_TERMINAL.md) explica cómo ejecutar un único
test y localizar su informe.
