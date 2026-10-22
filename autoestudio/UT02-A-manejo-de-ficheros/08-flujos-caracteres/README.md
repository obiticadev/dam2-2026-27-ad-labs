# 08 · Flujos de caracteres

**Sesión 25/10 · primer tramo.** Fuente: UT02-A §2.10.
[Teoría y pistas](teoria/flujos-caracteres.md).

Archivo: `src/main/java/es/dam/accesodatos/autoestudio/texto/FlujosCaracteres.java`.
Implementa `leerLineas` y `escribirLineas` con las clases del tema.

Entrada de lectura: `Ángela`, línea vacía, `José` (sin salto final).
Resultado: lista `['Ángela', '', 'José']`. Para escribir, añade un `newLine()`
tras cada elemento; con `append=false` se sustituye el contenido anterior.
Los tests preparan los archivos; no requieren configurar carpetas.

```bash
mvn -pl UT02-A-manejo-de-ficheros/08-flujos-caracteres test
```

Desde `autoestudio/`. El `main` lee una ruta UTF-8 y no la modifica.

Dominio: explica el final `null`, la codificación y la diferencia entre buffer,
`flush` y cierre. Variante: escribir nombres con tildes, anexar una línea y
volver a leer. No basta con que funcione solo con ASCII.
