# Tema 3 · Fundamentos para consultar rápido

[Prácticas](BLOQUES.md) · [Cobertura y teoría completa](COBERTURA.md)

Esta hoja es repaso añadido, no sustitución del desarrollo 3A/3B.

## Modelo y conexión

- Una PK identifica; una FK referencia una clave válida; SQL no es idéntico en todos
  los gestores porque JDBC unifique la API Java.
- JDBC es para Java; ODBC es otra interfaz. `java.sql` viene con el JDK, el driver no.
- Dos capas: aplicación→BD. Tres capas: cliente→servicios→BD.
- Drivers clásicos: 1 puente ODBC (histórico), 2 biblioteca nativa, 3 middleware,
  4 protocolo directo Java. «Tipo 5» comercial no añade una categoría clásica.
  SQLite JDBC incorpora un motor nativo local: no es un cliente de un servidor remoto.
- `DriverManager` sigue siendo válido; `DataSource` facilita configuración/pooling.
  JDBC 4 descubre drivers compatibles; `Class.forName` no reemplaza un JAR ausente.
- MySQL: `jdbc:mysql://localhost:3306/base`. Oracle servicio:
  `jdbc:oracle:thin:@//localhost:1521/servicio`; SID: `jdbc:oracle:thin:@localhost:1521:SID`.
  SQLite archivo: `jdbc:sqlite:/ruta/base.db`; temporal: `jdbc:sqlite::memory:`.
- El propietario de una conexión la cierra; quien recibe una conexión no la cierra
  por sorpresa. `ResultSet` depende de su sentencia y esta de la conexión.

## Elegir objeto y método

| Necesidad | Objeto/método | Resultado |
|---|---|---|
| SQL constante sin entrada | `Statement` | Según execute elegido |
| Datos del usuario | `PreparedStatement`, `setXxx` | Valores separados del SQL |
| Rutina almacenada | `CallableStatement` | IN por setters, OUT registrado y leído |
| SELECT | `executeQuery` | `ResultSet` |
| INSERT/UPDATE/DELETE | `executeUpdate` | Número de filas, no booleano |
| Tipo de resultado desconocido | `execute` | `true` si el primer resultado son filas; `false` no significa error |
| Forma del resultado | `ResultSetMetaData` | Etiquetas, tipos, cantidad de columnas |
| Forma/capacidades de la BD | `DatabaseMetaData` | Tablas, claves, driver, soporte |

Índices de parámetros y columnas desde **1**. `?` representa un valor, no un nombre
de tabla, columna u orden. Cursor antes de la primera fila: siempre `next()` antes
de leer. Para SQL NULL usa `setNull`; tras `getInt`, `wasNull()` distingue 0 de NULL.
`getBigDecimal` es preferible para importes decimales.

## Transacciones y cursores

ACID: atomicidad (todo o nada), consistencia (restricciones/reglas), aislamiento
(interacción entre transacciones), durabilidad (persistencia del commit según garantías).
Autocommit suele comenzar activado. Desactivarlo antes de las escrituras → verificar
recuentos → commit o rollback. Un UPDATE sin coincidencias devuelve 0: no tiene por
qué lanzar excepción. No todos los motores deshacen DDL; MySQL puede hacer commit implícito.

`Savepoint` permite rollback parcial, no confirma la transacción. Los niveles son
`READ_UNCOMMITTED`, `READ_COMMITTED`, `REPEATABLE_READ` y `SERIALIZABLE`; soporte y
anomalías dependen del motor. No se han hecho pruebas de concurrencia entre clientes aquí.

`TYPE_FORWARD_ONLY` solo avanza; `TYPE_SCROLL_INSENSITIVE` permite desplazamiento;
`TYPE_SCROLL_SENSITIVE` solicita sensibilidad externa, no garantiza soporte.
`CONCUR_READ_ONLY`/`CONCUR_UPDATABLE` controlan actualización. Para cambiar con cursor:
`updateXxx` → `updateRow`; insertar: `moveToInsertRow` → valores → `insertRow` →
`moveToCurrentRow`; borrar: `deleteRow`. Ninguno sustituye al commit.

## SQLite sin confusiones

- Motor embebido, sin servicio de red; fichero persistente y posibles auxiliares WAL/diario.
  El formato es portable, el acceso depende de permisos del archivo y bibliotecas.
  Escrituras serializadas; WAL no crea varios escritores simultáneos.
- Almacenamiento: NULL, INTEGER, REAL, TEXT, BLOB. NUMERIC es afinidad. En tablas
  normales el tipo declarado no siempre prohíbe otros tipos; STRICT añade reglas.
- Booleanos: 0/1; fechas: convención consistente, por ejemplo texto ISO. `rowid`:
  entero de 64 bits con signo. `INTEGER PRIMARY KEY` es alias de rowid.
- AUTOINCREMENT no es necesario para asignar ID y tiene coste; no garantiza una
  secuencia sin huecos. WITHOUT ROWID usa otra organización.
- `UNIQUE` admite varios NULL; `NOT NULL` no impide `''`; `CHECK` puede admitir NULL;
  `DEFAULT` no sustituye un NULL explícito. Combina restricciones según requisitos.
- FK se activa **por conexión** fuera de una transacción: `PRAGMA foreign_keys=ON`.
  CASCADE propaga, SET NULL necesita nulabilidad, SET DEFAULT necesita valor válido;
  RESTRICT y NO ACTION no son idénticos en todo momento de comprobación.
- No hay procedimientos MySQL/Oracle ni esos cursores actualizables. ALTER TABLE
  es más limitado, triggers por fila, vistas con posibles INSTEAD OF triggers.
  RIGHT/FULL JOIN y DROP COLUMN existen en versiones modernas: revisa versión.

## Consola SQLite opcional

```bash
sqlite3 datos/ensayo.db
```

```text
.databases
.tables
.schema
.headers on
.mode column
.read mi-esquema.sql
.exit
```

Órdenes con punto: sin `;`, solo en consola. SQL: con `;`. `.import` requiere
configurar modo CSV y cabecera; `.timer on`, `.echo on` y `.help` ayudan a inspeccionar.
`BEGIN DEFERRED/IMMEDIATE/EXCLUSIVE` difieren en adquisición de bloqueo; no mezcles
BEGIN manual con el control JDBC de la misma transacción.

## Preguntas de comprobación

<details><summary>¿Por qué compila Java y luego dice No suitable driver?</summary>

Compila contra `java.sql` del JDK, pero en ejecución falta la implementación SQLite
en el classpath. Revisa el comando `java -cp`, no solo los imports.
</details>
<details><summary>¿Por qué no hay filas si sé que existen?</summary>

Imprime ruta absoluta, revisa archivo/tabla/filtro y parámetros. `:memory:` crea una
base por conexión en este uso; abrir otra conexión no recupera automáticamente la anterior.
</details>
<details><summary>¿Qué está mal en `while (rs.next()) { return …; }` si piden una lista?</summary>

Sale en la primera fila. Construye lista, añade en cada iteración y devuelve al final.
</details>
<details><summary>¿Puedo confirmar tras el primer UPDATE y deshacerlo si falla el segundo?</summary>

No con rollback de la segunda transacción. El primer commit ya terminó la unidad.
Las dos escrituras inseparables deben estar en la misma transacción.
</details>
<details><summary>¿Por qué COUNT(*) da 1 para un padre sin hijos en LEFT JOIN?</summary>

Cuenta la fila extendida con nulos del JOIN. `COUNT(hijo.id)` ignora ese NULL y da 0.
</details>
<details><summary>¿Sirve cualquier curso de Hibernate moderno para estos ejercicios?</summary>

No directamente: este tema 3 es JDBC, y el tema 4 conserva Hibernate 5.4 con
`javax.persistence`. Hibernate 6/7 usa otras versiones/API; no mezclar imports ni dialectos.
</details>
