# FilenameFilter · Un criterio sobre nombres

En UT02-A §2.6 la clase `Filtrar` implementa `FilenameFilter` y su método
`accept(File dir, String name)` devuelve `name.endsWith(extension)`.

```text
File.list(filtro) → por cada nombre llama accept(directorio, nombre)
                → incluye los que devuelven true
```

`list(filtro)` devuelve nombres; `listFiles(filtro)` devuelve objetos `File`.
Ninguno hace un recorrido recursivo. `null` significa que el listado no se pudo
obtener; array vacío significa que se pudo obtener y no hubo coincidencias.

El ejemplo docente compara el sufijo exactamente, no cambia mayúsculas ni usa
`Locale.ROOT`. Tampoco excluye directorios. La versión anterior del autoestudio
añadía esas exigencias; ahora el contrato básico vuelve al enfoque del tema.

`FilenameFilter` recibe padre y nombre por separado. `FileFilter` recibe un
`File` completo; es otra interfaz, no necesaria para resolver este ejercicio.

<details><summary>Pista</summary>

Guarda el sufijo en el constructor. En `accept`, comprueba el nombre con
`endsWith`. Para listar, entrega una instancia del filtro a `directorio.listFiles`.
La existencia del directorio se valida en el método de listado, no en el criterio.

</details>
<details><summary>Comprueba y explica</summary>

Un nombre que acaba en `.txt` no prueba que sea un fichero. Si el enunciado pide
solo ficheros, añade `new File(dir, name).isFile()` explícitamente como otro
criterio. Aquí no se exige; el catalogador opcional sí lo pide.

</details>
