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

## Práctica extra · Componer recursos y lógica

Implementa los cinco métodos de
`src/main/java/es/dam/accesodatos/autoestudio/texto/FlujosCaracteresPractica.java`.
Los contratos completos están en sus comentarios. Tu `FlujosCaracteres.java`
original no necesita cambios. Los tests nuevos fallarán hasta resolver la práctica:
los métodos empiezan lanzando `UnsupportedOperationException`.

Aquí **componer** tiene dos caras: envolver un recurso para darle capacidades
(`FileReader` → `BufferedReader`) y combinar lectura, decisiones y escritura.
No se trata de cambiar solo el nombre de la variable en el mismo bucle.

### 1. Copiar sin saber de dónde viene el texto

Implementa `copiar(Reader origen, Writer destino, int tamBuffer)` usando `char[]`.
No uses `readLine()`: conserva incluso los saltos `\r\n` originales.
Devuelve los `char` copiados y rechaza tamaños de buffer no positivos.

**Reto de composición:** el método no recibe archivos, sino recursos ya abiertos.
Debe funcionar con `StringReader`/`StringWriter` y también con lectores y escritores
de archivos. No los cierres ni hagas `flush()`: no los has abierto tú.

**Piensa antes de programar:** si el buffer tiene tamaño 4 y la última lectura trae
2 caracteres, ¿qué parte debes escribir? ¿Qué se cuenta con un emoji que ocupa
dos unidades `char`?

### 2. Numerar un archivo sin cargarlo entero

Implementa `numerarLineas`. Para `Ana`, una línea vacía y `José`, el resultado es:

```text
1: Ana
2: 
3: José
```

**Reto de composición:** mantener abiertos lector y escritor a la vez, cada uno
con su buffer. Aquí sí eres responsable de cerrarlos. No uses `leerLineas()` para
cargar una lista completa. Devuelve el número de líneas escritas.

**Piensa:** ¿cuándo incrementas el contador? ¿Una línea vacía termina la lectura?

### 3. Limpiar una configuración sin alterar sus datos

Implementa `filtrarConfiguracion`. Descarta líneas en blanco (`strip().isEmpty()`)
y comentarios cuyo primer carácter tras `strip()` sea `#`. Conserva las demás
sin cambiar sus espacios. Por ejemplo:

```text
  # comentario       → descartar
  nombre=José        → conservar con sus espacios originales
color=#fff           → conservar: no es un comentario
```

**Reto lógico:** distinguir el texto que inspeccionas del que escribes. Devuelve
el número de líneas conservadas.

### 4. Compactar líneas vacías con memoria

Implementa `compactarVacias`. Cada grupo de líneas `""` consecutivas se convierte
en una sola, también al principio y al final. Una línea `" "` NO está vacía.
Devuelve el número de líneas descartadas.

**Reto lógico:** la decisión depende de la línea actual y de lo ocurrido antes.
Usa una variable de estado, no una lista completa.

**Simula en papel:** `""`, `""`, `"Ana"`, `""`, `" "`, `""`, `""`.
¿Cuándo debe reiniciarse el estado? ¿Cuántas líneas se descartan?

### 5. Intercalar dos archivos de distinta longitud

Implementa `intercalar`: una línea de A, una de B, otra de A, otra de B...
Cuando uno termine, continúa con el otro hasta agotarlo. Las vacías consumen turno.
Ejemplo: A contiene `a1`, `a2`, `a3`; B contiene `b1`.
Salida: `a1`, `b1`, `a2`, `a3`, cada una en su línea.

**Reto de composición:** dos lectores y un escritor, con finales independientes.
Devuelve el total de líneas escritas.

**Piensa:** ¿por qué una condición que exige que AMBOS tengan líneas perdería datos?
¿Qué ocurre si A está vacío desde el principio?

### Reglas y comprobación

- En los ejercicios 2-5: UTF-8, lectura línea a línea, sobrescritura del destino y
  `newLine()` tras cada línea escrita. Un origen vacío no es un error.
- No uses `Files`, streams de Java ni listas con todo el archivo. Practica los
  `Reader`/`Writer`, sus envoltorios y `try-with-resources`.
- Entradas existentes, referencias no null, padres existentes y destino distinto
  de las entradas. No hace falta programar validaciones adicionales.
- Los tests crean sus propios archivos temporales y comprueban el resultado sin
  depender de tus métodos de lectura. No necesitas rutas de tu ordenador.
- Los tests comprueban resultados; revisa tú también las restricciones de diseño
  (por ejemplo, no acumular todo el archivo en memoria).

Desde esta carpeta, ejecutar solo el ejercicio 1:

```bash
mvn -Dtest="FlujosCaracteresPracticaTest#copiar*" test
```

Para los siguientes, cambia `copiar*` por `numerar*`, `filtrar*`, `compactar*` o
`intercalar*`. Para toda la práctica:

```bash
mvn -Dtest=FlujosCaracteresPracticaTest test
```

Antes de cada ejercicio dibuja **origen → envoltorios → decisión/estado → destino**.
Después explica quién abrió cada recurso y quién debe cerrarlo. Si puedes explicar
eso y simular un caso vacío, uno normal y uno límite, estás practicando composición,
no solo memorizando constructores.
