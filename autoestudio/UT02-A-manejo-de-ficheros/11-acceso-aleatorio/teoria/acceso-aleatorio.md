# Aleatorio · El puntero se mide en bytes

`RandomAccessFile` permite lectura y escritura en la misma clase. UT02-A §2.12
desarrolla modos, puntero y métodos. La práctica de `int + double` viene del
repaso añadido: hace visible el cálculo de posiciones.

```text
Registro 0: [int: bytes 0..3][double: 4..11]
Registro 1: [int: 12..15]   [double: 16..23]
Registro 2: [int: 24..27]   [double: 28..35]
```

`"r"` abre solo lectura; `"rw"` permite escribir. `seek(pos)` sitúa el puntero
desde el inicio; `skipBytes(n)` avanza relativamente y devuelve cuánto saltó.
`getFilePointer()` informa la posición y `length()` el tamaño. Leer/escribir
tipos avanza el puntero: tras `readInt()` avanza 4 y tras `readDouble()` 8.

El patrón docente `seek(length())` añade al final. Aquí actualizar exige otra
posición: **`indice * 12L + 4`**. `id` no es índice. La `L` fuerza aritmética long.
`rw` no significa truncar el archivo. Un `seek` fuera de rango no valida que el
registro exista; debes comprobarlo antes. `readFully` intenta llenar el array
completo y falla si no hay suficientes bytes; `read()` devuelve -1 al final.

<details><summary>Pista 1 · Validez</summary>

Comprueba `indice >= 0` y `indice < archivo.length()/12` sobre el
`RandomAccessFile` abierto. Las entradas de esta práctica tienen registros completos.

</details>
<details><summary>Pista 2 · Operación</summary>

Después de `seek(indice * 12L + 4)`, llama a `readDouble()` o `writeDouble(nota)`.
No necesitas recorrer los anteriores ni reescribir los siguientes.

</details>
<details><summary>Comprobación</summary>

Tras leer la nota de índice 2 el puntero queda en 36. Actualizar índice 1 toca
los bytes 16–23 y mantiene 36 bytes de longitud. Un objeto serializado tiene
otro formato: no puedes aplicarle estos desplazamientos.

</details>
