# 00 · Plantilla de examen: Java + SQLite

**Lo primero:** ejecuta `bash ejecutar.sh plantilla` desde la carpeta del Tema 3. Debe mostrar la ruta absoluta, la versión SQLite, `FK activas` y `Tablas de usuario: 0`. Crea `00-plantilla/target/arranque.sqlite`, que es una base de prueba, no la del examen.

## 1. Si te dan un `.sql`: crea la base desde `main`

Pega el enunciado en [`sql/crear.sql`](sql/crear.sql) o guarda el fichero del profesor en `sql/`. Desde la carpeta del Tema 3:

```bash
bash ejecutar.sh plantilla crear datos/examen.sqlite sql/crear.sql
bash ejecutar.sh plantilla abrir datos/examen.sqlite
```

`crear` hace `mkdirs()` si hace falta, lee el script como UTF-8 con `java.io`, crea el fichero **solo si no existía** y lo carga dentro de una transacción. El lanzador resuelve `sql/` y `datos/` dentro de `00-plantilla/`. También se admiten rutas absolutas entre comillas. Para ensayar hoy sin editar la plantilla:

```bash
bash ejecutar.sh plantilla crear datos/ensayo.sqlite ../01-atomicos/sql/datos.sql
```

El ejemplo debe mostrar **2 tablas**. El script puede tener varias sentencias y punto y coma dentro de literales; la versión fijada de Xerial lo ejecuta completo con `Statement.executeUpdate(String)`. Es **SQL SQLite de confianza para inicializar una base**, no un lanzador de dumps de cualquier motor: prepara solo DDL/DML, sin `BEGIN`/`COMMIT`/`ROLLBACK` (la transacción es de Java), `PRAGMA` de conexión ni comandos de consola como `.read`. Si un dump SQLite trae `BEGIN TRANSACTION;` y `COMMIT;`, quítalos en una copia para importarlo desde `main`. Si falla un script de DDL/DML, se revierten sus cambios; el fichero nuevo puede quedar vacío. Corrige el SQL y prueba con un nombre nuevo. No uses este importador para un `SELECT` del que esperas leer filas.

## 2. Si te dan un `.sqlite`: ábrelo sin importar nada

Copia el fichero a `00-plantilla/datos/` o pasa su ruta absoluta. Trabaja sobre una copia si vas a cambiar datos:

```bash
bash ejecutar.sh plantilla abrir "datos/base del profesor.sqlite"
```

La URL es `jdbc:sqlite:file:/…?mode=rw`: **abre en escritura solo un fichero existente**. Si el nombre no coincide, no crea otra base vacía. `File.getCanonicalFile()` y `File.toURI()` resuelven la ruta y codifican espacios, `#` y acentos. `PRAGMA foreign_keys = ON` se comprueba para **cada conexión**, antes de iniciar cualquier transacción. SQLite va embebido en el proceso Java: no se indica servidor, puerto ni contraseña.

Para mirar el fichero desde la carpeta de la plantilla (opcional, requiere el programa `sqlite3`):

```bash
sqlite3 -readonly datos/ensayo.sqlite ".tables"
sqlite3 -readonly datos/ensayo.sqlite ".schema emp"
```

Puedes crear la misma base por consola (alternativa a `main`, con un nombre **nuevo**):

```bash
mkdir -p datos
sqlite3 -bail datos/consola.sqlite \
  'PRAGMA foreign_keys = ON;' 'BEGIN;' '.read sql/crear.sql' 'COMMIT;'
```

`.sql` es texto con sentencias; `.sqlite` es el fichero que contiene las tablas y filas. **Renombrar `.sql` a `.sqlite` no lo convierte.**

## 3. Usa la plantilla fuera del repositorio o desde el IDE

- **IDE con Maven:** abre `00-plantilla/pom.xml`, selecciona JDK 17+, ejecuta `examen.Main` y pon como directorio de trabajo la carpeta que contiene ese `pom.xml`. Para crear la base, añade los argumentos `crear datos/examen.sqlite sql/crear.sql` a la configuración de ejecución. No dependas de una primera descarga de Maven el día del examen.
- **Sin Maven y sin red:** antes de copiar `00-plantilla/`, guarda `sqlite-jdbc-3.51.0.0.jar` en su carpeta `lib/`. El repositorio ya tiene el [JAR exacto que necesitas](../../../entregable/practica-evaluable-ut02/lib/sqlite-jdbc-3.51.0.0.jar). Desde la carpeta copiada, ejecuta en Bash:

  ```bash
  mkdir -p target/clases
  javac --release 17 -encoding UTF-8 -d target/clases src/main/java/examen/*.java
  java --enable-native-access=ALL-UNNAMED -cp "target/clases:lib/sqlite-jdbc-3.51.0.0.jar" examen.Main
  ```

  Añade `crear datos/examen.sqlite sql/crear.sql` o `abrir datos/examen.sqlite` al final de la última línea. En Windows/PowerShell, el separador del classpath es `;` **dentro de las comillas** en lugar de `:`. En un entorno con JDK moderno, la opción `--enable-native-access=ALL-UNNAMED` permite al driver cargar la biblioteca SQLite nativa; en el IDE se añade a opciones de la VM, no a argumentos del programa.

## 4. Del arranque de prueba al código del examen

Si el examen permite partir de una plantilla, trabaja en una **copia** y sigue este orden:

1. **Verifica antes de borrar.** Ejecuta la plantilla sin argumentos y comprueba `FK activas` y `Tablas de usuario: 0`. Después, usa `crear` si te dan un `.sql` o `abrir` si te dan un `.sqlite`. Con la base de dos tablas, comprueba la ruta impresa y que salga `Tablas de usuario: 2`.
2. **Quita solo el diagnóstico.** En [`Main.java`](src/main/java/examen/Main.java), borra desde `INICIO BORRAR 1` hasta `FIN BORRAR 1`, **ambas líneas incluidas**. Haz lo mismo desde `INICIO BORRAR 2` hasta `FIN BORRAR 2`. Quita los **dos bloques juntos**: el segundo usa la variable `pruebaArranque` del primero. No necesitas cambiar los imports. A partir de ahí, ejecuta siempre con `crear <base.sqlite> <script.sql>` o `abrir <base.sqlite>`; sin argumentos ya no hay modo de prueba ni se imprime el diagnóstico.
3. **Escribe tu solución.** En `ESCRIBE AQUÍ`, dentro de `try (Connection c = Sqlite.abrir(db))`, llama a tus métodos de alta, listado o modificación pasando `c`. Pon sus definiciones fuera de `main` pero dentro de la clase, o en otra clase. Vuelve a ejecutar con `abrir` para probar la solución sin intentar crear otra vez la base. Si el enunciado exige arrancar sin argumentos, sustituye el selector `crear`/`abrir` por la ruta que pida el profesor.

**No borres** `Sqlite.java`, el `try` de la conexión ni la dependencia del driver. El mensaje `FK activas` es diagnóstico; el `PRAGMA foreign_keys = ON` y su comprobación en `Sqlite.java` son parte de la configuración real.

## 5. Qué comprobar antes de entregar

- [`Sqlite.java`](src/main/java/examen/Sqlite.java): conserva la detección de ruta, FK y el cierre de recursos. Para una operación de examen, desactiva autocommit, comprueba filas afectadas y llama a `commit()` solo cuando se cumpla todo; ante fallo, `rollback()`. No actives autocommit en un `finally` sin saber si el rollback funcionó: el cambio de modo puede confirmar pendientes.
- Si ves **`No suitable driver`**, revisa que el JAR esté en el *classpath al ejecutar*, no solo descargado. Con este driver JDBC no hace falta `Class.forName`.
- Si ves **`no such table`** o salen **0 tablas** tras abrir, lee la ruta impresa y abre el fichero correcto; si acabas de ejecutar `crear`, revisa el contenido del script.
- Si ves **`database is locked`**, cierra otras conexiones con escrituras pendientes, incluida una edición sin guardar en DB Browser. Trabaja en un directorio local con permiso de escritura.
- Ensaya en el ordenador que usarás: primero arranque sin red, luego creación y reapertura de una base de dos tablas. Comprueba por último un camino que falle después del primer cambio y verifica el fichero tras reabrirlo.
