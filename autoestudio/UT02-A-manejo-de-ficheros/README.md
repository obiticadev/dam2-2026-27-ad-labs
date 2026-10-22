# UT02-A · Manejo de ficheros (sin NIO)

La exclusión de NIO está confirmada por el usuario. Aprende con `java.io`, no
con atajos de `Files`. [Sesiones y cobertura completa](../PLAN_ESTUDIO.md).

## Repaso de la base

1. [Rutas `File`](01-rutas-file/README.md).
2. [Inspección](02-inspeccion-file/README.md).
3. [Operaciones](03-operaciones-file/README.md).
4. [Listado](04-listado-directorio/README.md): no se exige ordenar.
5. [Filtrado](05-filtrado-filename-filter/README.md): sufijo con `endsWith` como en clase.

## Núcleo práctico

7. [Flujos de bytes](07-flujos-bytes/README.md).
8. [Flujos de caracteres](08-flujos-caracteres/README.md).
9. [Acceso secuencial](09-acceso-secuencial/README.md).
10. [Serialización de objetos](10-serializacion-objetos/README.md).
11. [Acceso aleatorio](11-acceso-aleatorio/README.md).

Se mantiene el número 06 del [catalogador](06-catalogador-archivos/README.md)
para no romper rutas. Es ampliación opcional, no el final de toda la unidad.

Los nuevos bloques usan archivos aislados en tests y no dependen de haber resuelto
los wrappers de `File`. Un TODO pendiente no debe bloquear el estudio de bytes.
