# UT02-A · Manejo de ficheros

Ejemplos prácticos de `java.io` y `java.nio.file`, organizados en tres proyectos Maven independientes. Se usa Java 17 o superior; los ficheros de entrada están en `src/main/resources` y los resultados se guardan en `target/demo-data`.

## Proyectos

| Proyecto | Contenidos principales |
| --- | --- |
| [`java-io-flujos`](./java-io-flujos/README.md) | `File`, `FilenameFilter`, copia de bytes, serialización y flujos de caracteres |
| [`java-io-aleatorio`](./java-io-aleatorio/README.md) | `RandomAccessFile`, puntero, anexado y lectura |
| [`java-nio-archivos`](./java-nio-archivos/README.md) | `Path`, `FileSystem`, `Files`, lectura y escritura, copia, movimiento y directorios |

## Compilar todos los proyectos

Desde esta carpeta:

```bash
mvn package
```

Para compilar un solo proyecto: `mvn -pl java-io-flujos package` (cambia el nombre del módulo según la tabla). También se pueden importar directamente en IntelliJ IDEA o Eclipse como proyectos Maven.

Cada README de módulo incluye las clases de ejemplo, los comandos de ejecución y las correspondencias con el tema.
