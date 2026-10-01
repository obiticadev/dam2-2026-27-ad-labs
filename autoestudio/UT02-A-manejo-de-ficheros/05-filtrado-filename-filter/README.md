# 05 · Filtrado con `FilenameFilter`

## Objetivo

Implementar el contrato de `FilenameFilter` y usarlo para seleccionar ficheros
por extensión desde un directorio. El filtro no debe incluir directorios aunque
su nombre termine en la extensión buscada.

## Material de este mini proyecto

- [Teoría de `FilenameFilter`](./teoria/filename-filter.md)
- Enunciado, contratos y TODO: [FiltradoFilenameFilter.java](./src/main/java/es/dam/accesodatos/autoestudio/file/filtrado/FiltradoFilenameFilter.java)
- Tests de aceptación: [FiltradoFilenameFilterTest.java](./src/test/java/es/dam/accesodatos/autoestudio/file/filtrado/FiltradoFilenameFilterTest.java)

## Mapa de tests

| Test | Qué aclara |
|---|---|
| `aceptaFicherosConExtensionIgnorandoMayusculas` | Extensión insensible a mayúsculas y exclusión de carpetas/ausentes |
| `rechazaArgumentosNulosYRutasQueNoSeanDirectorio` | `accept` rechaza candidatos no válidos |
| `listaSoloFicherosCoincidentesEnOrdenNatural` | Integración con `listFiles(filter)` |
| `devuelveArrayVacioSiNoHayCoincidencias` | El resultado vacío es válido |
| `rechazaExtensionYDirectorioInvalidos` | Contrato de constructor y listado |

Correspondencia: UT02-A, apartado 2.6 del temario oficial.

## Ejecutar

Desde la raíz `autoestudio/`:

```bash
mvn -pl UT02-A-manejo-de-ficheros/05-filtrado-filename-filter test
```

En el IDE, ejecuta `FiltradoFilenameFilter.main`. Consulta la
[guía general](../../README_GUIA_TERMINAL.md) para ejecutar un test concreto y
leer el detalle del fallo.
