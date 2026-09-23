# `java-io-flujos`

Ejemplos del apartado `java.io`: ficheros, filtros, flujos de bytes, serialización y flujos de caracteres.

## Ejecutar

Desde esta carpeta, compila el módulo (o el proyecto padre) y lanza una clase:

```bash
mvn package
mvn exec:java -Dexec.mainClass="es.dam.accesodatos.io.FileOperationsDemo"
```

Sustituye la clase principal por cualquiera de las indicadas abajo. En el IDE, ejecuta directamente el método `main` de la clase. Los ejemplos usan rutas relativas y crean sus salidas en `target/demo-data`.

## Clases

- `FileOperationsDemo`: constructores y metadatos de `File`, creación de ficheros y directorios.
- `FilenameFilterDemo`: listado de ficheros `.txt` mediante `FilenameFilter`.
- `ByteCopyDemo`: copia de `origen.txt` a un fichero de destino con `FileInputStream` y `FileOutputStream`.
- `StringSerializationDemo`: serialización y deserialización de un `String`.
- `AlumnoSerializationDemo` y `Alumno`: serialización de un objeto propio que implementa `Serializable`.
- `PersonaSerializationDemo` y `Persona`: serialización y lectura de un objeto con `try-with-resources`.
- `EjemploInputStreamReader`: conversión de bytes UTF-8 a caracteres.
- `LeeCaracteres`: lectura de caracteres con `FileReader`.
- `EjemploBufferedReader`: lectura de un fichero línea a línea.
- `EjemploFileWriter`: escritura de texto con `FileWriter` en modo anexado.
- `EjemploBufferedWriter`: escritura de líneas con `BufferedWriter`.
- `EjemploAnexarBufferedWriter`: anexado de una línea con `BufferedWriter`.
