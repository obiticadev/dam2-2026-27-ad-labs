# Práctica evaluable UT02 — JDBCMySQL

Actualización de salarios con Java, JDBC y SQLite. El enunciado permite elegir la
base de datos. No hace falta servidor, usuario, contraseña ni máquina virtual.

## Archivos

- `src/main/java/JDBCMySQL.java`: el menú, la consulta, la restauración y las subidas.
- `src/main/java/Conexion.java`: abre la conexión con SQLite.
- `pom.xml`: proyecto Maven, compatible con Java 17 o superior.
- `lib/`: controlador JDBC de SQLite, copiado por Maven al compilar.
- `sql/emp_dept_salgrade_MYSQL.sql`: script original del profesor, sin modificar.
- `sql/emp_dept_salgrade_SQLITE.sql`: adaptación con las mismas tablas y datos.
- `empdept.db`: base de datos local, se crea con la opción 2 y no se sube a Git.
- `target/`: clases y JAR generados al compilar.

## Compilar y ejecutar

### Desde VS Code

Abre la carpeta de la práctica y pulsa **Run** en `JDBCMySQL.java`.
La tarea incluida compila con Maven antes de ejecutar, aunque se haya borrado
`target/`. La primera vez, elige 2 para crear las tablas y cargar los datos.
La configuración incluida permite cargar SQLite sin los avisos de acceso nativo.
En la terminal aparece este menú:

```text
1. Ver empleados
2. Restaurar datos originales (borra las subidas)
3. Aplicar subidas
4. Probar fallo sin guardar las subidas
0. Salir
```

Escribe el número y pulsa Enter. No hace falta escribir comandos.
Para una prueba completa: **2 → 1 → 3 → 1 → 0**.
Para comprobar el fallo: **2 → 4**; el programa deshace las subidas y termina
mostrando el error de prueba. Vuelve a pulsar Run y elige 1 para comprobarlo.

Cada vez que eliges 3 se aplica una nueva subida. Restaura con 2 antes de empezar
otra prueba independiente. Restaurar borra las subidas anteriores.

### Desde la terminal (opcional)

Ejecutar los comandos **desde la carpeta de esta práctica**:

```bash
mvn package

# Abrir el mismo menú que aparece al pulsar Run
java --enable-native-access=ALL-UNNAMED -cp 'target/classes:lib/*' JDBCMySQL
```

El programa se maneja solo con el menú; no utiliza argumentos de consola.
La opción 3 aplica una nueva subida cada vez: **restaurar con 2 antes de cada
prueba independiente**.

Maven está instalado para este usuario en `~/.local/share/maven/apache-maven-3.9.11`,
con el comando `~/.local/bin/mvn`. No se necesita `sudo` para usarlo.
La primera compilación necesita Internet para descargar las dependencias.
Java 25 puede mostrar un aviso de `sun.misc.Unsafe` al arrancar Maven; no impide
compilar. La opción `--enable-native-access` permite al controlador cargar SQLite
sin el aviso de acceso nativo de las versiones recientes de Java.

## Qué se ha cambiado del SQL

El original usa sintaxis de MySQL que SQLite no reconoce. Por eso se conserva
intacto y se ejecuta una copia adaptada:

1. Se sustituyen `DROP DATABASE`, `CREATE DATABASE` y `USE` por `DROP TABLE IF EXISTS`.
   En SQLite la base de datos es el archivo `empdept.db`, creado por JDBC.
2. Se elimina `ENGINE=InnoDB`: es una opción exclusiva de MySQL.
3. Se cambia, por ejemplo, `STR_TO_DATE('1-5-1981','%d-%m-%Y')` por `'1981-05-01'`.
   Las fechas quedan guardadas en formato ISO y Java las lee con `LocalDate`.
4. Se omite el `SELECT * FROM emp` intermedio, que solo era una consulta.
5. Se retira el `COMMIT` del script: Java controla la transacción de restauración.

Se conservan las cuatro tablas, los 14 empleados, los cuatro departamentos y los
cinco niveles salariales, con sus valores originales. No se modifica el archivo
descargado en `Downloads`.

Para restaurar directamente desde el cliente SQLite, con el programa cerrado:

```bash
sqlite3 empdept.db 'PRAGMA foreign_keys=ON;' '.read sql/emp_dept_salgrade_SQLITE.sql'
```

La opción Java `reiniciar` es preferible: además agrupa la restauración en una
transacción.

## Cómo se resuelve

- El script SQL se lee línea a línea con `BufferedReader` y `FileReader`, de
  `java.io`, sin utilizar NIO.
- `Conexion.conectar()` abre la base de datos con `DriverManager` y devuelve
  la conexión. Cada operación del menú abre una conexión y la cierra al terminar.
- `Statement` y `ResultSet` recuperan los empleados; `while (empleados.next())`
  recorre las filas.
- `if / else if` aplica una única subida, en el orden del enunciado:
  gerente 10 %, alta anterior al 01/09/1981 15 %, analista del departamento 20
  5 % y vendedor 8 %. Los demás conservan el salario.
- Los salarios se manejan con `double`. Primero calculamos la subida, luego la
  sumamos al salario. No redondeamos el cálculo antes de guardarlo; `printf`
  muestra los importes con dos decimales, sin modificar el valor guardado.
  `double` no representa todos los decimales de forma exacta; para una aplicación
  real con dinero sería preferible `BigDecimal`.
- `PreparedStatement` ejecuta un `UPDATE` por empleado, identificado por `empno`.
  SQLite no admite `ResultSet.updateRow()`, por eso se lee con el `ResultSet`
  y se escribe con una sentencia aparte, sobre la misma conexión.
- `setAutoCommit(false)` inicia el trabajo transaccional; solo se llama a
  `commit()` después de recorrer todos los empleados. Ante un fallo se hace
  `rollback()`. Los recursos JDBC se cierran con `try-with-resources`.
  No hace falta volver a activar el autocommit porque la conexión se cierra.
- Los errores se muestran en los `catch` y el programa termina al llegar al final
  de `main`, sin utilizar `System.exit`.

No se activa `PRAGMA foreign_keys` desde Java: no es necesario para cambiar los
salarios. Las claves ajenas siguen declaradas en el SQL, pero su comprobación
depende de la configuración de SQLite. Esto no cambia las reglas de las subidas.

## Pruebas de la defensa oral

### 1. Confirmación de las subidas

Restaurar con 2, consultar desde el cliente, aplicar las subidas con 3 y repetir **la misma
consulta** desde el cliente:

```bash
sqlite3 -header -column empdept.db \
  'SELECT empno, ename, job, hiredate, deptno, sal FROM emp ORDER BY empno;'
```

| Empleado | Original | Subida | Final |
|---|---:|---:|---:|
| SMITH | 800.00 | 15 % | 920.00 |
| ALLEN | 1600.00 | 15 % | 1840.00 |
| WARD | 1250.00 | 15 % | 1437.50 |
| JONES | 2975.00 | 10 % | 3272.50 |
| MARTIN | 1250.00 | 8 % | 1350.00 |
| BLAKE | 2850.00 | 10 % | 3135.00 |
| CLARK | 2450.00 | 10 % | 2695.00 |
| SCOTT | 3000.00 | 5 % | 3150.00 |
| KING | 5000.00 | 0 % | 5000.00 |
| TURNER | 1500.00 | 8 % | 1620.00 |
| ADAMS | 1100.00 | 0 % | 1100.00 |
| JAMES | 950.00 | 0 % | 950.00 |
| FORD | 3000.00 | 5 % | 3150.00 |
| MILLER | 1300.00 | 0 % | 1300.00 |

ALLEN y WARD son vendedores, pero reciben el 15 % porque la antigüedad se evalúa
antes. Los gerentes reciben el 10 %, no el 15 % ni la suma de ambos.

### 2. Abortar antes del commit

Pulsar Run, restaurar con 2 y elegir 4. Después, volver a pulsar Run y consultar
con 1.

La opción 4 ejecuta los UPDATE y lanza una `SQLException` antes del `commit`.
Debe mostrar el error de prueba y `ROLLBACK`; todos los salarios deben seguir como
al principio. Las líneas de actualización previas eran cambios pendientes, no
confirmados. También hay una línea comentada antes del `commit` para provocar
el error manualmente: descomentar `abortar = true`, ejecutar y elegir 3.

### 3. Errores de conexión y SQL

Para las pruebas que pide el profesor, modificar el código y recompilar:

- Conexión: cambiar temporalmente la URL en `Conexion.java` por
  `jdbc:sqlite:carpeta-inexistente/empdept.db`, sin crear esa carpeta.
  Debe mostrar `Error de base de datos`.
- Sentencia: cambiar `UPDATE emp` por `UPDATE tabla_inexistente` en `cambio`.
  Debe mostrar el error SQL y ejecutar `ROLLBACK`.

Después de cada prueba, restaurar la línea modificada y ejecutar `mvn package`.

## Entrega

El ZIP debe contener el proyecto, incluido `src/`, `pom.xml`, `sql/`, `lib/` y este
README. Se puede incluir `empdept.db` con los datos originales. `target/` no es
necesario: se regenera con `mvn package`.

Renombrar el ZIP con los datos personales siguiendo el patrón del enunciado:
`apellido1_apellido2_nombre_3J_Tarea.zip`, sin tildes ni caracteres especiales.
