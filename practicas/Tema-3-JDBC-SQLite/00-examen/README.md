# 00 · Plantilla de examen: Java + SQLite

**Para empezar sin argumentos:** ejecuta `MainDesdeSql` si partes de `sql/crear.sql`, o `MainDesdeSqlite` si ya tienes la base. Usa `00-examen` como directorio de trabajo. Comprueba siempre la ruta del fichero que muestra la consola. `Main.java` es la versión personalizable con selector por argumentos y diagnóstico; puede contener ya tus consultas.

## Asistentes locales: consultas y propuestas de ejercicios

**Sin internet, sin IA y con preguntas de 2–3 líneas.** Usan JDK 17+ y el driver de `lib/`. Analizan una base `.sqlite` ya existente; si solo tienes SQL, créala primero con `MainDesdeSql`.

Desde **PowerShell o CMD**, situado en `00-examen` (no necesita Bash):

```powershell
.\asistente.cmd consultas
.\asistente.cmd ejercicios
```

O desde una terminal **Git Bash situada en `00-examen`**:

```bash
bash asistente.sh consultas
bash asistente.sh ejercicios
```

Te preguntarán la ruta de la base (Enter usa `datos/examen.sqlite`). También puedes indicarla directamente:

```bash
bash asistente.sh consultas "datos/examen.sqlite"
bash asistente.sh ejercicios "datos/examen.sqlite"
```

Desde la carpeta superior `Tema-3-JDBC-SQLite`, usa `bash ejecutar.sh consultas` o `bash ejecutar.sh ejercicios`. También hay configuraciones **Asistente: ...** en Ejecutar y depurar de VS Code.

El script fija `00-examen` como directorio de trabajo aunque lo invoques desde otra carpeta. Por tanto, las rutas relativas de los argumentos se interpretan desde `00-examen`; para una base fuera de ella puedes usar su ruta absoluta.

### Cómo contestar en una terminal muy pequeña

Solo aparece **una opción por vez**, no una lista vertical larga:

```text
Conservar filas [2/2]: Todas las de t1 (LEFT)
n/p=otra; numero/Enter=elegir; detalle=ver
>
```

| Entrada | Acción |
|---|---|
| `Enter` | Elegir la opción visible o aceptar el valor por defecto. |
| `n` / `p` en un menú | Ver la opción siguiente / anterior. |
| Un número en un menú | Elegir directamente la opción con ese número. |
| `detalle` en un menú | Leer completa una opción larga, por partes. |
| `atras` | Corregir la respuesta anterior. Las respuestas previas se recuperan sin volver a imprimirlas. |
| `ayuda` o `?` | Ayuda breve, paginada. |
| `esquema` | Consultar tablas, columnas, DDL y claves, de forma paginada. |
| `guardar` durante las preguntas | Ver la ruta de la sesión. Ya se guarda automáticamente tras cada respuesta. |
| `salir` | Terminar sin ejecutar escrituras. EOF también termina limpiamente. |
| `fin` al leer una página o vista previa | Volver al asistente. |

Las preguntas de sí/no usan **`s` o `n`**, no la navegación de los menús. Los números del menú solo tienen significado en la pregunta visible.

Por defecto, el programa limita sus líneas a **64 caracteres**. Si tu ventana es más estrecha:

```bash
ASISTENTE_ANCHO=48 bash asistente.sh consultas
```

En VS Code puedes cambiar `-Dasistente.ancho=64` en `launch.json`. El rango es 32–120 caracteres. No requiere comandos ANSI ni limpia el historial de la terminal. Una entrada que tú escribas más larga que el ancho de la ventana puede ocupar varias líneas por el eco de la propia terminal.

En PowerShell, para ajustar el ancho usa `$env:ASISTENTE_ANCHO = "48"` antes de ejecutar `.\asistente.cmd consultas`. El lanzador `.cmd` admite los mismos argumentos de base y sesión que `.sh`.

### Asistente de consultas: qué genera

`AsistenteConsultas` te pide tabla principal (`t1`), segunda tabla opcional (`t2`), tipo de JOIN, relación, salidas, filtros, agregación, orden y límite.

- Propone las FK detectadas **para que confirmes cuál usar**. Incluye FK compuestas y autorrelaciones; también permite elegir pares de columnas manualmente.
- Admite `INNER JOIN` y `LEFT JOIN`. En un filtro de `t2` con LEFT te pregunta si va en `ON` (conservar todas las filas de `t1`) o `WHERE` (descartar filas sin coincidencia).
- Salidas: columnas, todas las columnas, `COUNT(*)`, `COUNT(columna)`, `SUM`, `AVG`, `MIN` y `MAX`. Puedes asignar alias únicos.
- Si hay agregados, genera `GROUP BY` con las salidas no agregadas; permite filtros `HAVING`.
- Operadores: `=`, `<>`, `>`, `>=`, `<`, `<=`, `LIKE`, `IS NULL`, `IS NOT NULL`, `BETWEEN`, `IN`, `NOT IN`.
- Puedes combinar filtros con `AND`/`OR`. Se agrupan **de izquierda a derecha sobre todo lo anterior**: A, luego OR B, luego AND C produce `((A OR B) AND C)`. No deduce los paréntesis del enunciado.
- Ordena por las salidas elegidas y permite `DISTINCT` y límite de filas.

**Valores:** según el tipo declarado, escribe texto o números con punto decimal. Usa `null` para SQL NULL, `texto:null` para el texto `"null"`, `texto:salir` para un dato igual a un comando, `entero:12` o `decimal:12.5` para indicar el tipo explícitamente. No escribas comillas SQL alrededor del texto: el exportador las añade y escapa. `LIKE` recibe el patrón, por ejemplo `%Ana%`.

Comparar `= null` o `<> null` genera `IS NULL` / `IS NOT NULL`. Para `BETWEEN` e `IN` no acepta NULL: configura un filtro de nulos separado si eso es lo que pide el enunciado.

Al terminar, crea archivos **nuevos**, sin sobrescribir:

| Archivo en `consultas/` | Contenido |
|---|---|
| `consulta-...sql` | Consulta SQLite con los valores escapados, lista para DB Browser o sqlite3. |
| `consulta-...-java.txt` | La misma consulta con `?` y la llamada `Sqlite.mostrarConsulta(c, ..., valores...)`. |
| `consulta-...-esquema.txt` | Esquema utilizado para construirla. |

Menú final: `ver` muestra las rutas; `sql` muestra el SQL paginado; `probar` permite leer **una fila por vez, hasta 20**; `nueva` inicia otra consulta; `atras` vuelve a la última pregunta; `sesion` muestra la ruta para continuar.

**Ejemplo con los datos incluidos:** elige `departamentos` como primera tabla, `empleados` como segunda, LEFT JOIN y la relación detectada. Añade la salida `t1.nombre` con alias `departamento` y `COUNT columna` sobre `t2.id` con alias `total_empleados`. Termina las salidas y no añadas filtros. Obtendrás el conteo por departamento, incluido Administración con cero empleados. Para contar hijos en un LEFT JOIN, elige `COUNT(t2.id)`, **no `COUNT(*)`**.

### Asistente de ejercicios: qué genera

`AsistenteEjercicios` te pide el modo de transacción y una secuencia de operaciones:

| Operación | Decisiones que puedes configurar |
|---|---|
| Consulta | Abre el mismo constructor de consultas de una o dos tablas. |
| Insertar | Tabla, columnas y valores. Omite columnas generadas y permite dejar valores por defecto. Puede recuperar una PK entera. |
| Modificar | Columna, valor fijo, suma, resta, porcentaje o reglas ordenadas con `CASE`. Cada regla compara una o varias columnas con AND/OR; solo gana la primera que se cumpla. |
| Borrar | Tabla y filtros. |
| Comprobar filas en UPDATE/DELETE | Exactamente una, al menos una, o cualquier cantidad. Un resultado que incumpla la regla lanza `SQLException`. |
| Metadatos | Tablas, columnas, FK, motor/driver o columnas de un SELECT. |
| Guardar / deshacer hasta / liberar punto | Savepoints de la transacción actual; solo ofrece puntos que sigan activos. |
| Cálculo en Java | Lee una tabla con PK simple, cierra el cursor y modifica una columna numérica que no sea la PK; admite reglas ordenadas por umbral y redondeo opcional. |

Modos de transacción:

- **Todo o nada:** commit al final; ante un error SQL o Java, rollback y relanzamiento.
- **Ensayo:** rollback incluso si todo sale bien.
- **Independientes:** autocommit; un error posterior no deshace cambios anteriores. No permite recetas que requieren una transacción común.

Para un UPDATE/DELETE sin filtros exige confirmar expresamente que quieres afectar **toda la tabla**. Un INSERT duplicado o una FK/CHECK inválida no se ocultan: deben provocar el fallo y, si hay transacción, deshacer la operación.

Cuando un alta recupera una clave, la pantalla indica su variable: por ejemplo **`$id1`**. Puedes escribir `$id1` como valor de una operación posterior. El número corresponde a la **operación**, no al número de altas. No uses una referencia esperando que siga existiendo su fila si tú mismo has deshecho su alta mediante un savepoint.

Genera en `propuestas/`:

- `Solucion...java`: clase con `main` y `ejecutar(Connection c)`, usando tus métodos de `Sqlite.java`.
- `Solucion...-revision.txt`: avisos, esquema y casos que deberías probar.

**No ejecuta esa clase ni modifica tu Main.** Puedes abrirla y copiar/adaptar `ejecutar(c)` a tu solución. Si quieres ejecutar la clase entera, copia el `.java` a `src/main/java/examen/`, conserva su nombre y pásale como argumento la ruta de una **copia de la base**. El Main generado exige ese argumento para evitar una ejecución accidental con la base original.

Los valores se generan como constantes; conviértelos en parámetros o lecturas de teclado si el enunciado lo exige. Un UPDATE cambia una columna por operación: añade más operaciones dentro de la misma transacción si necesitas cambiar otras.

### Guardar y continuar después

Las respuestas se guardan en `sesiones/*.properties`. Durante las preguntas usa `guardar`, o al terminar usa `sesion`, para consultar la ruta. Para continuar:

```bash
bash asistente.sh consultas "datos/examen.sqlite" "sesiones/consultas-....properties"
bash asistente.sh ejercicios "datos/examen.sqlite" "sesiones/ejercicios-....properties"
```

Debes indicar el archivo real, no escribir los puntos suspensivos. La sesión comprueba que sean la misma base, el mismo asistente y el mismo esquema. Cambiar filas no impide continuar; cambiar tablas, índices o restricciones sí requiere una sesión nueva. Las respuestas ya dadas se reproducen silenciosamente. Para corregirlas usa `atras`.

`nueva` vacía las respuestas de la sesión actual para empezar otra configuración; los SQL y Java ya exportados no se borran. Guarda una copia del `.properties` antes si quieres conservar también la configuración anterior.

Las sesiones y los archivos generados **contienen los valores que introduzcas**: son material local, no los publiques si contienen datos privados.

### Límites y seguridad

- El analizador y las vistas previas abren la base con `mode=ro` y `PRAGMA query_only=ON`. Solo escriben sus archivos de salida y sesión, nunca datos de la base.
- Es un generador guiado, **no entiende el texto del enunciado ni garantiza una solución correcta**. Debes introducir y comprobar las reglas.
- Esta versión cubre tablas normales, consultas de hasta dos tablas y las recetas descritas; no construye consultas arbitrarias con subconsultas, UNION ni escritura de BLOB.
- No inventa procedimientos almacenados ni `updateRow()`, que SQLite JDBC no admite.
- Los cálculos con `double` necesitan revisión si el enunciado exige precisión monetaria o redondeos concretos.
- Al copiar `00-examen` fuera del repositorio, incluye `lib/sqlite-jdbc-3.51.0.0.jar` y prueba el arranque sin red. El JAR ya se ha copiado localmente; está ignorado por Git, por lo que no aparecerá automáticamente en otra clonación.
- Confirma que se permita usar un generador preparado en el examen.

Pruebas desde la carpeta superior:

```bash
bash ejecutar.sh pruebas             # Herramientas originales.
bash ejecutar.sh pruebas-asistentes  # SQL, interfaz y Java generado.
```

Las pruebas de asistentes compilan y ejecutan propuestas sobre bases aisladas en memoria o en una carpeta nueva de `target/`. No usan tu base de trabajo.

## Herramientas preparadas para usar desde cualquier Main

Todo está en [`Sqlite.java`](src/main/java/examen/Sqlite.java), sin `var` ni dependencias nuevas. Puedes copiar las recetas de [`EjemplosSqlite.java`](src/main/java/examen/EjemplosSqlite.java). Ejecutar esa clase **solo consulta**, no ejecuta las recetas que modifican datos.

### Cómo leer este catálogo

Esta es la lista de **todos los métodos públicos de `Sqlite.java`**, incluidos los de preparación de la base. Los métodos privados son internos y no se llaman desde el Main.

- `c`: la conexión abierta a la base. No la abras de nuevo para cada operación.
- `sql`: la instrucción SQL que adaptas al enunciado.
- `Object... valores`: tantos valores como `?` tenga el SQL, en el mismo orden. Si no hay `?`, no añadas valores.
- `rs`: el resultado de una consulta, dentro de su `try` y después de `rs.next()`.
- `void`: no devuelve un valor; hace la operación o lanza una excepción.
- Las rutas `datos/` y `sql/` se resuelven desde `00-examen`.

**Los ejemplos son independientes, no una secuencia para ejecutar entera.** Los que insertan, modifican o borran cambian la base. Las tablas de ejemplo son `departamentos` y `empleados`; adáptalas al examen.

Los ejemplos con `c` van normalmente aquí:

```java
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.List;
import java.util.Map;

// Dentro de main, que puede declarar throws Exception:
try (Connection c = Sqlite.abrir(new File("datos/examen.sqlite"))) {
    // Pega aquí las llamadas que necesites, por ejemplo:
    Sqlite.mostrarConsulta(c, "SELECT * FROM empleados");
}
```

`Sqlite` y tu Main están en el mismo paquete `examen`: no necesitas importar `Sqlite`. Si estás en otro paquete, añade `import examen.Sqlite;`.

### A. Abrir, crear y cargar la base

| Método y parámetros | Devuelve / para qué sirve | Ejemplo de llamada |
|---|---|---|
| `abrir(File archivo)` | `Connection`: abre un `.sqlite` **existente** y configura sus FK. Tú debes cerrar la conexión. | `Connection c = Sqlite.abrir(new File("datos/examen.sqlite"));` |
| `crear(File archivo, File script)` | `void`: crea una base **nueva** y carga el `.sql` en una transacción. Rechaza una base existente. | `Sqlite.crear(new File("datos/examen-nuevo.sqlite"), new File("sql/crear.sql"));` |
| `configurar(Connection c)` | `void`: activa y comprueba las claves foráneas. `abrir` ya lo llama; solo lo necesitas si abriste la conexión de otra forma. | `Sqlite.configurar(c);` |
| `leerSql(File script)` | `String`: lee el texto UTF-8 del `.sql`. No lo ejecuta. | `String texto = Sqlite.leerSql(new File("sql/crear.sql"));` |
| `crearFichero(File archivo)` | `boolean`: crea carpetas y un fichero vacío; `true` si lo creó, `false` si ya existía. **No crea tablas ni carga datos.** | `boolean nuevo = Sqlite.crearFichero(new File("datos/prueba-vacia.sqlite"));` |
| `importar(Connection c, String sql)` | `void`: ejecuta un script de inicialización de confianza en su propia transacción. Exige autocommit activado. | `Sqlite.importar(c, Sqlite.leerSql(new File("sql/crear.sql")));` |

**Lo habitual:** usa `crear` una sola vez si partes de SQL; después `abrir`. Si te dan `.sqlite`, usa solo `abrir`. No necesitas llamar además a `crearFichero`, `configurar` o `importar`: `crear`/`abrir` ya hacen lo necesario.

`abrir` debe ir en try-with-resources, como en el ejemplo inicial. `importar` es para una base recién abierta y vacía, no para volver a cargar un script sobre tablas existentes. El script no debe contener `BEGIN`, `COMMIT`, `ROLLBACK`, comandos de consola ni `SELECT` cuyos resultados quieras leer.

### B. Insertar, modificar, borrar y comprobar

Los métodos de escritura de esta tabla **no inician una transacción ni confirman/deshacen por su cuenta**. Sin transacción, cada sentencia se confirma automáticamente.

| Método y parámetros | Devuelve / para qué sirve | Ejemplo de llamada |
|---|---|---|
| `insertar(Connection c, String sql, Object... valores)` | `int`: número de filas insertadas. SQL sin `RETURNING`. | `int filas = Sqlite.insertar(c, "INSERT INTO departamentos(nombre) VALUES (?)", "Compras");` |
| `insertarConId(Connection c, String sql, Object... valores)` | `long`: ID entero de **una sola fila** insertada. El SQL debe devolver el ID con `RETURNING id`. | `long id = Sqlite.insertarConId(c, "INSERT INTO empleados(nombre, salario, departamento_id) VALUES (?, ?, ?) RETURNING id", "Eva", 1200, 1);` |
| `actualizar(Connection c, String sql, Object... valores)` | `int`: filas afectadas por el `UPDATE`. | `int filas = Sqlite.actualizar(c, "UPDATE empleados SET salario = ? WHERE id = ?", 1900, 1);` |
| `borrar(Connection c, String sql, Object... valores)` | `int`: filas eliminadas por el `DELETE`. | `int filas = Sqlite.borrar(c, "DELETE FROM empleados WHERE id = ?", 4);` |
| `exigirUnaFila(int filas)` | `void`: lanza `SQLException` si el número no es exactamente 1. No hace rollback por sí mismo. | `Sqlite.exigirUnaFila(Sqlite.actualizar(c, "UPDATE empleados SET salario = ? WHERE id = ?", 1900, 1));` |

En el `UPDATE` anterior, `1900` sustituye el primer `?` (salario) y `1` el segundo (ID). Cero filas no es un error JDBC: lo compruebas tú, o usas `exigirUnaFila`.

Para pasar SQL `NULL`, usa `null`, no el texto `"NULL"`. Si es el único valor variable, escribe `(Object) null` para evitar confundirlo con el array de parámetros. Por ejemplo, **solo si `salario` admite nulos en la base real**:

```java
Sqlite.actualizar(c, "UPDATE empleados SET salario = ? WHERE id = ?", null, 1);
```

Nuestra base de ejemplo no permite salarios nulos: ese ejemplo lanzaría una excepción de restricción.

### C. Consultar y trabajar con los resultados

| Método y parámetros | Devuelve / para qué sirve | Ejemplo de llamada |
|---|---|---|
| `mostrarConsulta(Connection c, String sql, Object... valores)` | `void`: muestra nombres de columnas y filas en consola; avisa si no hay resultados. | `Sqlite.mostrarConsulta(c, "SELECT id, nombre, salario FROM empleados WHERE salario >= ?", 1700);` |
| `consultar(Connection c, String sql, Object... valores)` | `List<Map<String, Object>>`: lista de filas ya leídas; lista vacía si no hay resultados. | `List<Map<String, Object>> filas = Sqlite.consultar(c, "SELECT id, nombre, salario FROM empleados WHERE departamento_id = ?", 1);` |
| `consultarLong(Connection c, String sql, Object... valores)` | `long`: un único entero. Exige exactamente una fila, una columna y valor no nulo. | `long total = Sqlite.consultarLong(c, "SELECT COUNT(*) FROM empleados WHERE departamento_id = ?", 1);` |
| `preparar(Connection c, String sql, Object... valores)` | `PreparedStatement`: sentencia con parámetros ya asignados. **La devuelve abierta**: debes cerrarla con try-with-resources. | `PreparedStatement ps = Sqlite.preparar(c, "SELECT nombre FROM empleados WHERE id = ?", 1);` |
| `leerEnteroNullable(ResultSet rs, String columna)` | `Integer`: valor entero, o `null` si el campo SQL es `NULL`. No confunde nulo con cero. | `Integer id = Sqlite.leerEnteroNullable(rs, "id");` |
| `leerDecimalNullable(ResultSet rs, String columna)` | `Double`: valor decimal, o `null` si el campo SQL es `NULL`. | `Double salario = Sqlite.leerDecimalNullable(rs, "salario");` |

**Cómo recorrer lo devuelto por `consultar`:**

```java
List<Map<String, Object>> filas = Sqlite.consultar(c,
        "SELECT id, nombre, salario FROM empleados WHERE departamento_id = ?", 1);

for (Map<String, Object> fila : filas) {
    int id = ((Number) fila.get("id")).intValue();
    String nombre = (String) fila.get("nombre");
    double salario = ((Number) fila.get("salario")).doubleValue();
    System.out.println(id + " - " + nombre + " - " + salario);
}
if (filas.isEmpty()) System.out.println("No hay empleados");
```

`consultar` conserva SQL `NULL` como `null` de Java: compruébalo antes de convertirlo a un número. En un JOIN usa alias distintos para que las claves del mapa no se repitan. Este método guarda todas las filas en memoria.

**Cómo usar `preparar` y los lectores de nulos con tu propio cursor:**

```java
try (PreparedStatement ps = Sqlite.preparar(c,
        "SELECT id, nombre, salario FROM empleados WHERE departamento_id = ?", 1);
        ResultSet rs = ps.executeQuery()) {
    while (rs.next()) {
        Integer id = Sqlite.leerEnteroNullable(rs, "id");
        Double salario = Sqlite.leerDecimalNullable(rs, "salario");
        System.out.println(id + " - " + rs.getString("nombre") + " - " + salario);
    }
}
```

No devuelvas un `ResultSet` desde un método que ya ha cerrado su sentencia/conexión. `consultar` evita ese problema devolviendo los datos ya leídos.

### D. Transacciones: commit y rollback

| Método y parámetros | Devuelve / para qué sirve | Ejemplo de llamada |
|---|---|---|
| `iniciarTransaccion(Connection c)` | `void`: desactiva autocommit. Rechaza una transacción ya iniciada. | `Sqlite.iniciarTransaccion(c);` |
| `commit(Connection c)` | `void`: confirma todos los cambios y después reactiva autocommit, solo si el commit terminó bien. | `Sqlite.commit(c);` |
| `rollback(Connection c)` | `void`: deshace todos los cambios y después reactiva autocommit, solo si el rollback terminó bien. | `Sqlite.rollback(c);` |
| `deshacer(Connection c, SQLException original)` | `void`: intenta rollback conservando el error SQL original. Si falla el rollback, lo añade como error secundario. No reactiva autocommit ni relanza el original por ti. | Dentro de `catch (SQLException error)`: `Sqlite.deshacer(c, error);` y después `throw error;`. |
| `deshacer(Connection c, Exception original)` | `void`: igual, pero admite también excepciones Java. Esta sobrecarga se usa en el catch combinado del ejemplo siguiente. | Dentro de `catch (SQLException \| RuntimeException error)`: `Sqlite.deshacer(c, error);` y después `throw error;`. |

**Diferencia:** usa `rollback` cuando tú decides cancelar sin una excepción previa. Usa `deshacer(c, error)` dentro de un `catch` para no perder el error que provocó el fallo. Ambos necesitan una transacción iniciada para que haya cambios pendientes que deshacer.

**Ejemplo completo: dos cambios o ninguno**

```java
Sqlite.iniciarTransaccion(c);
try {
    Sqlite.exigirUnaFila(Sqlite.actualizar(c,
            "UPDATE empleados SET salario = ? WHERE id = ?", 1900, 1));
    Sqlite.exigirUnaFila(Sqlite.actualizar(c,
            "UPDATE empleados SET salario = ? WHERE id = ?", 1750, 2));
    Sqlite.commit(c);
} catch (SQLException | RuntimeException error) {
    Sqlite.deshacer(c, error);
    throw error;
}
```

Añade `import java.sql.SQLException;`. Este bloque va dentro del `try` de la conexión. Si falla, deja salir el error para cerrar esa conexión. `deshacer` conserva el error original si también falla el rollback y no reactiva autocommit. No lo reactives en un `finally`.

**Cancelar voluntariamente sin guardar:**

```java
Sqlite.iniciarTransaccion(c);
try {
    Sqlite.exigirUnaFila(Sqlite.actualizar(c,
            "UPDATE empleados SET salario = ? WHERE id = ?", 9999, 1));
    Sqlite.rollback(c); // El salario vuelve al valor que tenía antes.
} catch (SQLException | RuntimeException error) {
    Sqlite.deshacer(c, error);
    throw error;
}
```

### E. Savepoints: deshacer solo una parte

| Método y parámetros | Devuelve / para qué sirve | Ejemplo de llamada |
|---|---|---|
| `guardarPunto(Connection c, String nombre)` | `Savepoint`: marca un punto dentro de la transacción actual. | `Savepoint punto = Sqlite.guardarPunto(c, "antes_del_segundo");` |
| `rollbackHasta(Connection c, Savepoint punto)` | `void`: deshace lo posterior al punto. La transacción sigue abierta. | `Sqlite.rollbackHasta(c, punto);` |
| `liberarPunto(Connection c, Savepoint punto)` | `void`: libera el punto; **no confirma** los cambios. | `Sqlite.liberarPunto(c, punto);` |

```java
Sqlite.iniciarTransaccion(c);
try {
    Sqlite.exigirUnaFila(Sqlite.actualizar(c,
            "UPDATE empleados SET salario = ? WHERE id = ?", 1900, 1));
    Savepoint punto = Sqlite.guardarPunto(c, "antes_del_segundo");
    Sqlite.exigirUnaFila(Sqlite.actualizar(c,
            "UPDATE empleados SET salario = ? WHERE id = ?", 9999, 2));
    Sqlite.rollbackHasta(c, punto); // Deshace solo el cambio del empleado 2.
    Sqlite.liberarPunto(c, punto);
    Sqlite.commit(c); // Guarda el cambio del empleado 1.
} catch (SQLException | RuntimeException error) {
    Sqlite.deshacer(c, error);
    throw error;
}
```

### F. Metadatos: conocer la estructura

Todos estos métodos devuelven `void`: muestran información en consola.

| Método y parámetros | Qué muestra | Ejemplo de llamada |
|---|---|---|
| `mostrarInfoConexion(Connection c)` | Nombre/versión del motor y del driver. | `Sqlite.mostrarInfoConexion(c);` |
| `mostrarTablas(Connection c)` | Tablas, excluyendo las internas `sqlite_`. | `Sqlite.mostrarTablas(c);` |
| `mostrarColumnas(Connection c, String tabla)` | Columnas, tipos y si admiten nulos de la tabla indicada. | `Sqlite.mostrarColumnas(c, "empleados");` |
| `mostrarClavesForaneas(Connection c, String tabla)` | FK de esa tabla y sus tablas/columnas de destino. | `Sqlite.mostrarClavesForaneas(c, "empleados");` |
| `mostrarColumnasConsulta(Connection c, String sql, Object... valores)` | Etiquetas/alias y tipos de las columnas de un SELECT. | `Sqlite.mostrarColumnasConsulta(c, "SELECT nombre AS empleado, salario FROM empleados WHERE id = ?", 1);` |

### G. Recetas adicionales de EjemplosSqlite.java

Estas no son herramientas genéricas: contienen SQL y reglas **de la base de ejemplo**. Puedes llamarlas desde cualquier Main del paquete `examen`, o copiar y adaptar su contenido.

| Método y parámetros | Devuelve / qué hace | Ejemplo de llamada |
|---|---|---|
| `listarPorDepartamento(Connection c, String departamento)` | `void`: listado con JOIN filtrado por nombre de departamento. Solo lectura. | `EjemplosSqlite.listarPorDepartamento(c, "Ventas");` |
| `resumen(Connection c)` | `void`: LEFT JOIN con conteo y suma por departamento, incluidos los vacíos. Solo lectura. | `EjemplosSqlite.resumen(c);` |
| `insertarEmpleado(Connection c, String nombre, double salario, int departamento)` | `long`: inserta y devuelve el ID. **Escritura.** | `long id = EjemplosSqlite.insertarEmpleado(c, "Eva", 1200, 1);` |
| `cambiarSalario(Connection c, int id, double salario)` | `int`: modifica y devuelve filas afectadas. **Escritura.** | `int filas = EjemplosSqlite.cambiarSalario(c, 1, 1900);` |
| `borrarEmpleado(Connection c, int id)` | `int`: elimina y devuelve filas afectadas. **Escritura.** | `int filas = EjemplosSqlite.borrarEmpleado(c, 4);` |
| `altaConRevision(Connection c, String nombre, double salario, int departamento, int responsable)` | `long`: alta + revisión del responsable en una transacción; devuelve el ID nuevo. **Confirma si todo va bien.** | `long id = EjemplosSqlite.altaConRevision(c, "Eva", 1200, 1, 1);` |
| `probarSinGuardar(Connection c)` | `void`: cambia el salario del ID 1 y luego hace rollback. | `EjemplosSqlite.probarSinGuardar(c);` |
| `guardarSoloPrimerCambio(Connection c)` | `void`: ejemplo de savepoint; confirma el salario 1900 para ID 1 y deshace el cambio del ID 2. **Escritura.** | `EjemplosSqlite.guardarSoloPrimerCambio(c);` |
| `revisarSalariosEnJava(Connection c, int departamento)` | `void`: lee, calcula subidas en Java y actualiza por ID dentro de una transacción. **Escritura.** | `EjemplosSqlite.revisarSalariosEnJava(c, 1);` |
| `distinguirNuloDeCero(Connection c)` | `void`: muestra la diferencia entre NULL y 0 mediante un SELECT sin tablas. Solo lectura. | `EjemplosSqlite.distinguirNuloDeCero(c);` |

Las cuatro recetas que gestionan su propia transacción (`altaConRevision`, `probarSinGuardar`, `guardarSoloPrimerCambio`, `revisarSalariosEnJava`) se llaman **sin una transacción previa**. Si fallan, deja salir el error para cerrar la conexión del Main.

Los métodos `main(String[] args)` son puntos de entrada: ejecútalos desde el IDE, no los llames como herramientas:

- `Main`: selector `crear`/`abrir` y tu solución personal.
- `MainDesdeSql`: crea desde `sql/crear.sql` si no existe la base de salida y luego la abre.
- `MainDesdeSqlite`: abre la base existente indicada en su código.
- `EjemplosSqlite`: ejecuta solo los listados de demostración.
- `PruebasSqlite` (en `src/test/java`): ejecuta las comprobaciones aisladas.
- `AsistenteConsultas`: construye consultas y llamadas Java mediante preguntas.
- `AsistenteEjercicios`: construye una propuesta de solución Java, sin ejecutarla.
- `PruebasAsistentes` (en `src/test/java`): comprueba SQL, navegación y compilación/ejecución del Java generado en bases aisladas.

### Reglas para usar las herramientas con seguridad

Los valores corresponden a los `?` en el mismo orden. No concatenes entradas del usuario. Los nombres de tabla y columna se escriben en el SQL, no se pasan como `?`.

**Importante:** insertar/actualizar/borrar no controlan transacciones ni exigen una fila por defecto. Sin transacción, cada sentencia se confirma automáticamente. Para poder deshacer, inicia la transacción **antes** de los cambios. `exigirUnaFila` provoca un error si salen 0 o varias filas, pero no hace rollback por sí mismo.

`EjemplosSqlite.java` contiene también `JOIN`, `LEFT JOIN`, agregados, `NULL`, reglas con `CASE`, rollback voluntario, savepoints, el patrón de stock y recorrido con cálculo en Java. `consultar` cierra el cursor y devuelve una lista de mapas; usa alias distintos en los JOIN y `Number` para convertir números. Para grandes resultados usa un cursor propio.

**Al llegar el examen:** adapta SQL, valores y reglas. Estas herramientas no resuelven automáticamente el enunciado ni sustituyen saber JDBC. Confirma que el profesor permita usarlas. SQLite no admite procedimientos almacenados ni cursores actualizables mediante `updateRow()`.

Las pruebas están en `src/test/java/examen/PruebasSqlite.java`; se ejecutan como un `main`, sin JUnit, desde `00-examen`. Usan bases en memoria y un fichero nuevo en `target/`, no tus bases de trabajo.

## 1. Si te dan un `.sql`: crea la base desde `main`

Pega el enunciado en [`sql/crear.sql`](sql/crear.sql) o guarda el fichero del profesor en `sql/`. Desde la carpeta del Tema 3:

```bash
bash ejecutar.sh plantilla crear datos/examen.sqlite sql/crear.sql
bash ejecutar.sh plantilla abrir datos/examen.sqlite
```

`crear` hace `mkdirs()` si hace falta, lee el script como UTF-8 con `java.io`, crea el fichero **solo si no existía** y lo carga dentro de una transacción. El lanzador resuelve `sql/` y `datos/` dentro de `00-examen/`. También se admiten rutas absolutas entre comillas. Para ensayar hoy sin editar la plantilla:

```bash
bash ejecutar.sh plantilla crear datos/ensayo.sqlite sql/crear.sql
```

El ejemplo debe mostrar **2 tablas**. El script puede tener varias sentencias y punto y coma dentro de literales; la versión fijada de Xerial lo ejecuta completo con `Statement.executeUpdate(String)`. Es **SQL SQLite de confianza para inicializar una base**, no un lanzador de dumps de cualquier motor: prepara solo DDL/DML, sin `BEGIN`/`COMMIT`/`ROLLBACK` (la transacción es de Java), `PRAGMA` de conexión ni comandos de consola como `.read`. Si un dump SQLite trae `BEGIN TRANSACTION;` y `COMMIT;`, quítalos en una copia para importarlo desde `main`. Si falla un script de DDL/DML, se revierten sus cambios; el fichero nuevo puede quedar vacío. Corrige el SQL y prueba con un nombre nuevo. No uses este importador para un `SELECT` del que esperas leer filas.

## 2. Si te dan un `.sqlite`: ábrelo sin importar nada

Copia el fichero a `00-examen/datos/` o pasa su ruta absoluta. Trabaja sobre una copia si vas a cambiar datos:

```bash
bash ejecutar.sh plantilla abrir "datos/base del profesor.sqlite"
```

La URL es `jdbc:sqlite:file:/…?mode=rw`: **abre en escritura solo un fichero existente**. Si el nombre no coincide, no crea otra base vacía. `File.getCanonicalFile()` y `File.toURI()` resuelven la ruta y codifican espacios, `#` y acentos. `PRAGMA foreign_keys = ON` se comprueba para **cada conexión**, antes de iniciar cualquier transacción. SQLite va embebido en el proceso Java: no se indica servidor, puerto ni contraseña.

Para mirar el fichero desde la carpeta de la plantilla (opcional, requiere el programa `sqlite3`):

```bash
sqlite3 -readonly datos/ensayo.sqlite ".tables"
sqlite3 -readonly datos/ensayo.sqlite ".schema empleados"
```

Puedes crear la misma base por consola (alternativa a `main`, con un nombre **nuevo**):

```bash
mkdir -p datos
sqlite3 -bail datos/consola.sqlite \
  'PRAGMA foreign_keys = ON;' 'BEGIN;' '.read sql/crear.sql' 'COMMIT;'
```

`.sql` es texto con sentencias; `.sqlite` es el fichero que contiene las tablas y filas. **Renombrar `.sql` a `.sqlite` no lo convierte.**

## 3. Usa la plantilla fuera del repositorio o desde el IDE

- **IDE con Maven:** abre `00-examen/pom.xml`, selecciona JDK 17+, ejecuta `examen.Main` y pon como directorio de trabajo la carpeta que contiene ese `pom.xml`. Para crear la base, añade los argumentos `crear datos/examen.sqlite sql/crear.sql` a la configuración de ejecución. No dependas de una primera descarga de Maven el día del examen.
- **Sin Maven y sin red:** antes de copiar `00-examen/`, guarda `sqlite-jdbc-3.51.0.0.jar` en su carpeta `lib/`. El repositorio ya tiene el [JAR exacto que necesitas](../../../entregable/practica-evaluable-ut02/lib/sqlite-jdbc-3.51.0.0.jar). Desde la carpeta copiada, ejecuta en Bash:

  ```bash
  mkdir -p target/clases
  javac --release 17 -encoding UTF-8 -d target/clases src/main/java/examen/*.java
  java --enable-native-access=ALL-UNNAMED -cp "target/clases:lib/sqlite-jdbc-3.51.0.0.jar" examen.Main
  ```

  Añade `crear datos/examen.sqlite sql/crear.sql` o `abrir datos/examen.sqlite` al final de la última línea. En Windows/PowerShell, el separador del classpath es `;` **dentro de las comillas** en lugar de `:`. En un entorno con JDK moderno, la opción `--enable-native-access=ALL-UNNAMED` permite al driver cargar la biblioteca SQLite nativa; en el IDE se añade a opciones de la VM, no a argumentos del programa.

## 4. Del arranque de prueba al código del examen

Si el examen permite partir de una plantilla, trabaja en una **copia** y sigue este orden:

1. **Verifica antes de borrar.** Ejecuta primero `MainDesdeSql` para crear o reutilizar la base de ejemplo. Tu `Main` actual abre `datos/examen.sqlite` sin argumentos y lista empleados; con ese ejemplo debe mostrar `FK activas` y `Tablas de usuario: 2`. Si usas otra base, adapta o quita el `SELECT` de `empleados`: no todas las bases tienen esa tabla. Para elegir otro fichero desde `Main`, usa `crear` si te dan un `.sql` o `abrir` si te dan un `.sqlite` y comprueba la ruta impresa.
2. **Quita solo el diagnóstico.** En [`Main.java`](src/main/java/examen/Main.java), borra desde `INICIO BORRAR 1` hasta `FIN BORRAR 1`, **ambas líneas incluidas**. Haz lo mismo desde `INICIO BORRAR 2` hasta `FIN BORRAR 2`. Quita los **dos bloques juntos**: el segundo usa la variable `pruebaArranque` del primero. No necesitas cambiar los imports. A partir de ahí, ejecuta siempre con `crear <base.sqlite> <script.sql>` o `abrir <base.sqlite>`; sin argumentos ya no hay modo de prueba ni se imprime el diagnóstico.
3. **Escribe tu solución.** En `ESCRIBE AQUÍ`, dentro de `try (Connection c = Sqlite.abrir(db))`, llama a tus métodos de alta, listado o modificación pasando `c`. Pon sus definiciones fuera de `main` pero dentro de la clase, o en otra clase. Vuelve a ejecutar con `abrir` para probar la solución sin intentar crear otra vez la base. Si el enunciado exige arrancar sin argumentos, sustituye el selector `crear`/`abrir` por la ruta que pida el profesor.

**No borres** `Sqlite.java`, el `try` de la conexión ni la dependencia del driver. El mensaje `FK activas` es diagnóstico; el `PRAGMA foreign_keys = ON` y su comprobación en `Sqlite.java` son parte de la configuración real.

## 5. Qué comprobar antes de entregar

- [`Sqlite.java`](src/main/java/examen/Sqlite.java): conserva la detección de ruta, FK y el cierre de recursos. Para una operación de examen, desactiva autocommit, comprueba filas afectadas y llama a `commit()` solo cuando se cumpla todo; ante fallo, `rollback()`. No actives autocommit en un `finally` sin saber si el rollback funcionó: el cambio de modo puede confirmar pendientes.
- Si ves **`No suitable driver`**, revisa que el JAR esté en el *classpath al ejecutar*, no solo descargado. Con este driver JDBC no hace falta `Class.forName`.
- Si ves **`no such table`** o salen **0 tablas** tras abrir, lee la ruta impresa y abre el fichero correcto; si acabas de ejecutar `crear`, revisa el contenido del script.
- Si ves **`database is locked`**, cierra otras conexiones con escrituras pendientes, incluida una edición sin guardar en DB Browser. Trabaja en un directorio local con permiso de escritura.
- Ensaya en el ordenador que usarás: primero arranque sin red, luego creación y reapertura de una base de dos tablas. Comprueba por último un camino que falle después del primer cambio y verifica el fichero tras reabrirlo.
