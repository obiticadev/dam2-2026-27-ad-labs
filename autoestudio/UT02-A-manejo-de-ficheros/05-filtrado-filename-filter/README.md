# 05 · FilenameFilter como en el tema

Fuente: UT02-A §2.6, ejemplo `Filtrar`. [Teoría y pistas](teoria/filename-filter.md).

Completa el constructor, `accept` y `listarPorExtension` de
[FiltradoFilenameFilter.java](src/main/java/es/dam/accesodatos/autoestudio/file/filtrado/FiltradoFilenameFilter.java).

El criterio básico es **nombre terminado en el sufijo**, sensible a mayúsculas:
`.txt` acepta `notas.txt`, no `NOTAS.TXT`. También acepta un directorio llamado
`respaldo.txt`: `FilenameFilter` filtra nombres, no tipos. No debes normalizar
extensiones ni comprobar el disco dentro de `accept`.

El listado valida el directorio y distingue `null` de array vacío. No se exige
orden. Los tests preparan los datos y comprueban esas tres decisiones.

```bash
mvn -pl UT02-A-manejo-de-ficheros/05-filtrado-filename-filter test
```

Desde `autoestudio/`. Variante razonada: si se pidieran solo ficheros, ¿dónde
añadirías `isFile()`? Ese requisito pertenece al catalogador opcional, no al filtro básico.
