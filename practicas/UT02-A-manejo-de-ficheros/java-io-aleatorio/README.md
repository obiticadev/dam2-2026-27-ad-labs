# `java-io-aleatorio`

Ejemplos de acceso directo a ficheros con `RandomAccessFile`.

```bash
mvn package
mvn exec:java -Dexec.mainClass="es.dam.accesodatos.aleatorio.RandomLogDemo"
```

- `RandomLogDemo`: se posiciona al final del fichero y añade una línea, como el ejemplo `Log.java`.
- `RandomEjemplo`: muestra posición del puntero, anexado, lectura con `readLine()` y `readFully()`.

Los ficheros se crean en `target/demo-data`. El ejemplo escribe bytes con la codificación ISO-8859-1 para que `writeBytes` y `readLine` muestren correctamente el texto de prueba; estos métodos no son la opción recomendada para texto UTF-8 general.
