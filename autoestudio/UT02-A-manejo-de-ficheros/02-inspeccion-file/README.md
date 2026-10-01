# 02 · Inspección con `File`

## Objetivo

Consultar, mediante `java.io.File`, si una ruta existe, si representa un fichero
o un directorio, qué permisos básicos informa el sistema y cuáles son algunos
de sus metadatos.

## Material de este mini proyecto

- [Teoría de inspección y metadatos](./teoria/inspeccion-file.md)
- Enunciado, contratos y TODO: [InspeccionFile.java](./src/main/java/es/dam/accesodatos/autoestudio/file/inspeccion/InspeccionFile.java)
- Tests de aceptación: [InspeccionFileTest.java](./src/test/java/es/dam/accesodatos/autoestudio/file/inspeccion/InspeccionFileTest.java)

## Mapa de tests

| Test | Qué aclara |
|---|---|
| `distingueUnaRutaExistenteDeUnaInexistente` | Estado de existencia |
| `distingueFicherosDirectoriosYRutasInexistentes` | Diferencia entre fichero, directorio y ausencia |
| `consultaPermisosDeUnaEntradaTemporal` | Permisos efectivos en un fichero temporal propio |
| `obtieneElTamanioEnBytes` | Tamaño en bytes y resultado de ruta inexistente |
| `obtieneLaFechaDeModificacionDeUnaEntradaExistente` | Fecha de modificación disponible |
| `lasConsultasBooleanasDevuelvenFalseParaUnaRutaInexistente` | Resultados de consultas sobre ausencia |
| `rechazaUnaReferenciaNulaEnTodasLasConsultas` | Validación del argumento común |

Correspondencia: UT02-A, métodos de `File` del apartado 2.4.

## Ejecutar

Desde la raíz `autoestudio/`:

```bash
mvn -pl UT02-A-manejo-de-ficheros/02-inspeccion-file test
```

En el IDE, ejecuta `InspeccionFile.main`. Los tests usan una carpeta temporal;
consulta la [guía general](../../README_GUIA_TERMINAL.md) para ejecutar uno y
entender su informe.
