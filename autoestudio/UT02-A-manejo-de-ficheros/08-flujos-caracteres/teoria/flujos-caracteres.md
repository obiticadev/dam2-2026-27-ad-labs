# Caracteres · Interpretar bytes con una codificación

UT02-A §2.10 presenta `Reader`, `Writer`, sus implementaciones y buffers.

```text
archivo (bytes UTF-8) → FileReader → BufferedReader → String por línea
String por línea → BufferedWriter → FileWriter → archivo (bytes UTF-8)
```

`InputStreamReader` convierte bytes a caracteres; `OutputStreamWriter` realiza
la conversión inversa. `FileReader` y `FileWriter` permiten especificar un
`Charset` en Java 17. Aquí usamos `StandardCharsets.UTF_8` para no depender de
la configuración del equipo: **no se estudia NIO Path/Files por usar esa constante**.

| Operación | Qué significa |
|---|---|
| `read(char[], off, len)` | Cantidad de caracteres leídos o -1 |
| `readLine()` → `""` | Línea vacía, seguir leyendo |
| `readLine()` → `null` | Final, no añadir otra línea |
| `newLine()` | Separador del sistema; no lo inventes con un carácter fijo |
| `flush()` | Vacía el buffer, no cierra |
| `close()` | Finaliza y libera; también vuelca la salida pendiente |

`BufferedReader` reduce accesos al origen y ofrece `readLine()`, que `FileReader`
no ofrece. `BufferedWriter` añade buffer y `newLine()`. `PrintWriter` facilita
impresión de texto; `StringWriter` mantiene texto en memoria. Reconócelos aunque
no necesites todos en este ejercicio.

<details><summary>Pista 1 · Lectura</summary>

Crea una `ArrayList<String>`; envuelve `FileReader(archivo, UTF_8)` con
`BufferedReader`. Usa `while ((linea = reader.readLine()) != null)`.

</details>
<details><summary>Pista 2 · Escritura</summary>

El constructor es `FileWriter(archivo, UTF_8, append)`. Envuélvelo con
`BufferedWriter` y escribe cada línea seguida de `newLine()`. El enunciado
garantiza que el contenido previo acaba en salto si vas a anexar.

</details>
<details><summary>Comprobación</summary>

Una línea vacía en medio no termina el bucle. Una última línea sin salto se
devuelve normalmente. Un archivo vacío no es un error; uno ausente sí puede
producir `IOException`.

</details>
