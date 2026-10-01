# 06 · Boss final · Catalogador de archivos

## Objetivo

Construir un catálogo de ficheros de una extensión seleccionada. El ejercicio
integra la composición de rutas, la creación de directorios, la inspección de
entradas, el listado y `FilenameFilter`.

## Material de este mini proyecto

- [Teoría del catalogador](./teoria/catalogador-archivos.md)
- Enunciado, contratos y TODO: [CatalogadorArchivos.java](./src/main/java/es/dam/accesodatos/autoestudio/file/catalogador/CatalogadorArchivos.java)
- Tests de aceptación: [CatalogadorArchivosTest.java](./src/test/java/es/dam/accesodatos/autoestudio/file/catalogador/CatalogadorArchivosTest.java)

## Mapa de tests

| Test | Qué aclara |
|---|---|
| `preparaUnDirectorioHijoYEsIdempotente` | Crear o reutilizar una carpeta hija |
| `rechazaPadresONombresQueNoSonValidos` | Precondiciones y nombre de un solo componente |
| `noReemplazaUnFicheroQueColisionaConElDirectorioSolicitado` | No destruir un fichero al preparar una carpeta |
| `catalogaSoloFicherosDeLaExtensionYConSusMetadatos` | Filtrado, orden, tamaño y fecha |
| `catalogoVacioEsUnaListaInmutable` | Sin coincidencias y protección de la lista de salida |
| `rechazaUnDirectorioOUnaExtensionInvalidos` | Validación al catalogar |

## Ejecutar

Desde la raíz `autoestudio/`:

```bash
mvn -pl UT02-A-manejo-de-ficheros/06-catalogador-archivos -am test
```

El proyecto depende de los mini proyectos anteriores de UT02-A. En el IDE,
ejecuta `CatalogadorArchivos.main`. El código de ejemplo trabaja bajo
`target/demo-data`, que Git ignora. La [guía general](../../README_GUIA_TERMINAL.md)
explica `-am` y cómo leer el resultado de los tests.
