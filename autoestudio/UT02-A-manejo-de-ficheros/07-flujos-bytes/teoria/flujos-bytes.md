# Bytes · Leer una cantidad, escribir esa cantidad

`FileInputStream` y `FileOutputStream` son flujos concretos de entrada/salida.
`InputStream` y `OutputStream` son sus clases abstractas. UT02-A §2.7 desarrolla
la copia por bloques. Un archivo binario no debe interpretarse como texto.

```text
Origen:       [0, 1, 2, 3, 4, 255]     búfer: 4 posiciones
read #1 → 4  buffer = [0, 1, 2, 3]     write(buffer, 0, 4)
read #2 → 2  buffer = [4,255,2,3]      write(buffer, 0, 2)
read #3 → -1                           terminar, NO escribir
Destino:      [0, 1, 2, 3, 4, 255]
```

Las posiciones sobrantes conservan valores anteriores. `off` es el índice inicial
del array y `len` es una cantidad, no el índice final ni una posición del archivo.
`read()` sin array devuelve un byte como `int` 0–255, o -1. `available()` no
sustituye el criterio de fin de lectura.

`new FileOutputStream(destino)` sobrescribe; con `true` añade al final.
`try-with-resources` cierra incluso si hay errores. No captures una excepción
para devolver un total ficticio: propaga `IOException`, permitido en el tema.

<details><summary>Pista 1 · Orden de trabajo</summary>

Valida la capacidad antes de crear el array. Abre entrada y salida en el mismo
try-with-resources. Necesitas un `int` para la lectura y un `long` para el total.

</details>
<details><summary>Pista 2 · Bucle</summary>

El patrón es `while ((leidos = in.read(buffer)) != -1)`. Dentro, escribe solo
`leidos` bytes y súmalos al total. No conviertas el array a `String`.

</details>
<details><summary>Comprobación y variante</summary>

Para 6 bytes y búfer de 3: lecturas 3, 3, -1. El total sigue siendo 6.
Para vacío: la primera lectura es -1, el total es 0 y el destino debe quedar vacío.

</details>
