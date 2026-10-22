# Secuencial · Para borrar un registro, reescribe los que conservas

UT02-A §2.11 enumera crear, leer, copiar, buscar, borrar información y cerrar.
Para registros de texto de longitud variable, borrar una línea no equivale a
sobrescribir sus caracteres: la siguiente línea puede tener otra longitud.

```text
abrir origen → leer línea → separar id
                             ├─ coincide → descartar / contar
                             └─ distinto → escribir línea en destino
                         repetir hasta null → cerrar ambos
```

La práctica añadida genera **otro archivo**, de modo que puedas contrastar el
antes y después sin arriesgar la entrada. No abras un writer sobre el origen:
lo vaciarías antes de terminar de leerlo. Un reemplazo del original sería una
decisión posterior, no una operación oculta de este método.

<details><summary>Pista 1 · Búsqueda</summary>

`linea.split(";", 2)` distingue el campo id del nombre. Convierte el primer campo
a `int` y compáralo; no busques una subcadena del id.

</details>
<details><summary>Pista 2 · Eliminación</summary>

Usa `BufferedReader` y `BufferedWriter`, con UTF-8 y try-with-resources. Al
coincidir, aumenta el contador; si no, escribe la línea y `newLine()`.

</details>
<details><summary>Comprobación</summary>

Eliminar el único registro produce un destino vacío y devuelve 1. Eliminar
un id ausente conserva las líneas y devuelve 0. El origen no cambia en ninguno.

</details>
