# `java-nio-archivos`

Ejemplos de rutas (`Path`, `FileSystem`) y operaciones de lectura, escritura, comprobación, copia y movimiento con `Files`.

```bash
mvn package
mvn exec:java -Dexec.mainClass="es.dam.accesodatos.nio.PathEjemplo"
```

Clases incluidas: `PathEjemplo`, `EjemploPath2`, `FileEjemplo1` (lectura completa desde un argumento), `FileEjemplo9` (lectura con búfer), `FileEjemplo` (existencia y permisos), `FileEjemplo2` (crear/borrar), `DeleteHandlingDemo` (excepciones de borrado), `FileEjemplo4` (crear directorio), `FileEjemplo5` (copiar directorio vacío), `CopiaFicheroDemo`, `MovimientoFicheroDemo`, `FileEjemplo10` (copiar usando un array de bytes) y `FileEjemplo11` (copiar con búferes).

Para `FileEjemplo1` se puede pasar un fichero de entrada como argumento:

```bash
mvn exec:java -Dexec.mainClass="es.dam.accesodatos.nio.FileEjemplo1" -Dexec.args="src/main/resources/hola.txt"
```

Las rutas de entrada están en `src/main/resources`; todas las escrituras se limitan a `target/demo-data`.
