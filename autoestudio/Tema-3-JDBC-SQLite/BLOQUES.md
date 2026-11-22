# Tema 3 · Bloques de práctica

[Inicio](README.md) · [Configuración](00-CONFIGURACION.md) · [Seis horas](../RUTA_6_HORAS.md)

Ejercicios **añadidos para practicar los patrones**, no enunciados oficiales. El
contrato preciso está también junto a cada método de `src/main/java/lab/Jdbc.java`.
No cierres la conexión recibida: cierra las sentencias y resultados que creas.

## Índice

- [B01 · Esquema y restricciones](#b01-esquema-y-restricciones)
- [B02 · Consultas y parámetros](#b02-consultas-y-parametros)
- [B03 · Insertar, modificar y borrar](#b03-insertar-modificar-y-borrar)
- [B04 · Metadatos](#b04-metadatos)
- [B05 · Transacción completa](#b05-transaccion-completa)
- [B06 · Punto de retorno](#b06-punto-de-retorno)
- [B07 · Uniones y agregación](#b07-uniones-y-agregacion)
- [B08 · Cursores H2, no SQLite](#b08-cursores-h2-no-sqlite)
- [B09 · Llamada a una función](#b09-llamada-a-una-funcion)
- [B10 · Simulacro integrador](#b10-simulacro-integrador)

## Datos comunes

Los tests B02–B07 crean esta situación independientemente de tu método B01:

```text
grupos:   (1,DAM) (2,DAW) (3,Vacío)
personas: (1,Ana,20,1) (2,Ana,17,1) (3,Luis,NULL,2)
          id,nombre,edad,grupo_id
```

## B01 Esquema y restricciones

**Objetivo:** `crear(c)` crea dos tablas en una base vacía, no rellena ni borra.

| Tabla | Columnas y restricciones |
|---|---|
| grupos | `id INTEGER PRIMARY KEY`, `nombre TEXT NOT NULL UNIQUE` |
| personas | `id INTEGER PRIMARY KEY`, `nombre TEXT NOT NULL`, `edad INTEGER CHECK(edad>=0)`, `grupo_id INTEGER NOT NULL` con FK a grupos y `ON DELETE CASCADE` |

**Pasos:** crear primero el padre, después el hijo; usar `Statement` para DDL constante;
activar FK al abrir cada conexión (ya lo hace `Arranque.abrir`). La prueba fuerza un
duplicado, una edad negativa y una FK inexistente; todos deben ser rechazados.

**Comprobar:** `mvn -o test -Dgroups=b01`.

<details><summary>Pista</summary>

Dos `executeUpdate("CREATE TABLE …")` dentro del mismo `try (Statement …)`.
No añadas `AUTOINCREMENT` para conseguir simplemente un ID asignado por SQLite.
`CHECK(edad>=0)` permite NULL; eso es intencionado.
</details>

**Variante:** añade una pertenencia muchos-a-muchos `persona_grupo` con PK compuesta.
Intenta insertar dos veces el mismo par y explica por qué falla. No cubierta por el test básico.

## B02 Consultas y parametros

**Enunciado:** `buscar(c,nombre,edadMinima)` devuelve `List<Persona>` con nombre exacto,
edad mayor o igual al mínimo y orden por ID. Consulta con dos `?`, nunca concatena entradas.

**Recorrido:** preparar → `setString(1,…)` → `setInt(2,…)` → ejecutar → `while(rs.next())`
→ construir el registro → añadirlo a la lista → cerrar resultado y sentencia.
Nombre `Ana`, mínimo 18 → solo `(1,Ana,20,1)`. La entrada `' OR 1=1 --` → lista vacía.

**Comprobar:** `mvn -o test -Dgroups=b02`.

<details><summary>Pista y matiz de NULL</summary>

`WHERE nombre=? AND edad>=? ORDER BY id`. Los índices empiezan en 1.
`getInt("edad")` + `wasNull()` debe hacerse seguido si lees columnas numéricas anulables.
En este filtro una edad NULL no pasa `edad>=?`; la rama nula queda para reutilizar el
mapeo en consultas sin ese filtro. El test verifica el filtro NULL, no fuerza esa rama.
</details>

**Variante:** buscar edades desconocidas con `IS NULL`; después buscar un prefijo con
`LIKE ?` y asignar el valor `prefijo + "%"` como dato, no como SQL concatenado.

## B03 Insertar modificar y borrar

Implementa `insertar`, `renombrar` y `borrar`. Usa columnas explícitas, parámetros y
`WHERE id=?`. Inserta `O'Neil`, edad NULL, grupo 1. Devuelve el ID generado, no `MAX(id)`.
Renombrar y borrar devuelven filas afectadas: 1 si se encuentra y 0 si no existe.

**Antes/después:** 3 personas → insertar una → 4 → modificar esa fila → 4 → borrar esa
misma fila → 3. Ninguna fila vecina cambia.

**Comprobar:** `mvn -o test -Dgroups=b03`.

<details><summary>Pista</summary>

Prepara el INSERT con `Statement.RETURN_GENERATED_KEYS`; tras `executeUpdate`, recorre
`getGeneratedKeys()` con `next()`. Para edad ausente usa `setNull(2,Types.INTEGER)`.
El `?` de renombrar nombre va antes del de ID si el SQL dice `SET nombre=? WHERE id=?`.
</details>

**Variante:** provoca una FK inexistente al insertar. Debe fallar sin añadir una persona.
Después escribe un alta desde un array de objetos y comprueba cada recuento.

## B04 Metadatos

`tablas(c)` devuelve nombres de tablas de usuario ordenados, excluyendo `sqlite_…`.
`columnas(c)` describe `SELECT id AS codigo,nombre FROM personas` como `etiqueta:tipo`.
No fijes el tipo por adivinación: consulta los metadatos del driver.

**Resultado:** tablas `[grupos, personas]`; dos columnas, primera `codigo:…` y segunda
`nombre:…`. El test acepta el tipo que declare SQLite, pero no un tipo vacío.

**Comprobar:** `mvn -o test -Dgroups=b04`.

<details><summary>Pista</summary>

`c.getMetaData().getTables(null,null,"%",new String[]{"TABLE"})` devuelve un
`ResultSet` que también hay que cerrar. Para columnas usa `rs.getMetaData()`,
`getColumnCount()`, `getColumnLabel(i)` y `getColumnTypeName(i)` desde 1.
</details>

**Variante:** enumera columnas, PK y FK con `getColumns`, `getPrimaryKeys` y
`getImportedKeys`. Consulta además `supportsTransactions()` y capacidades del cursor.
No supongas que `getMaxConnections()==0` significa que no se puede conectar.

## B05 Transaccion completa

`trasladar(c,persona,grupo,nuevoNombre)` cambia el grupo de una persona y renombra
el grupo destino. Las dos operaciones deben confirmarse juntas. Si alguna afecta
cero filas o viola una restricción, se deshace todo. Exige autocommit al entrar y lo
restaura al salir; rechaza una transacción que ya fuera de otro código.

```text
Inicio: Ana→DAM; grupo 2=DAW
Éxito con destino 2, nombre Web: Ana→Web; grupo 2=Web
Fallo con destino 2, nombre DAM: UNIQUE falla después del primer UPDATE
Resultado tras rollback: Ana→DAM; grupo 2=DAW (estado original)
```

**Comprobar:** `mvn -o test -Dgroups=b05`.

<details><summary>Pistas progresivas</summary>

1. Desactiva autocommit **antes** del primer UPDATE.
2. Comprueba `executeUpdate()!=1`; no esperes una excepción por cero filas.
3. `commit()` solo al terminar ambos; `rollback()` en el catch; propaga el fallo.
4. Restaura autocommit en `finally`. Si falla el rollback, conserva esa información
   como excepción suprimida. En un pool real, una conexión de estado dudoso se descarta.
</details>

**Variante:** una transferencia de stock o población: restar origen y sumar destino,
comprobando origen con saldo suficiente. No basta con que el destino exista.

## B06 Punto de retorno

`cambioParcial(c,1)`: renombra Ana a `Ana Maria`, crea un `Savepoint`, cambia edad a
99, deshace hasta el punto y confirma. Resultado: `Ana Maria`, **20**, no 99.

**Comprobar:** `mvn -o test -Dgroups=b06`.

<details><summary>Pista</summary>

`Savepoint p=c.setSavepoint(); … c.rollback(p); c.releaseSavepoint(p); c.commit();`
No llames a `rollback()` sin argumento si solo quieres deshacer el último tramo.
En JDBC no mezcles este patrón con `BEGIN` manual.
</details>

**Variante en consola SQLite:** `BEGIN IMMEDIATE; … SAVEPOINT p; … ROLLBACK TO p;
RELEASE p; COMMIT;`. Explica qué sigue pendiente después de `ROLLBACK TO`.

## B07 Uniones y agregacion

`resumen(c)` devuelve `nombre:cuenta` por grupo, en orden de ID. Debe incluir los
grupos sin personas: `DAM:2`, `DAW:1`, `Vacío:0`.

**Comprobar:** `mvn -o test -Dgroups=b07`.

<details><summary>Pista</summary>

Parte de grupos con `LEFT JOIN personas`; agrupa por `g.id,g.nombre` y cuenta
`COUNT(p.id)`, no `COUNT(*)`. La fila sintética del LEFT JOIN no es una persona.
</details>

**Variante Chinook:** clientes con total facturado incluyendo cero (`COALESCE(SUM(…),0)`)
y álbumes con alguna pista jazz (`EXISTS`). Usa el ejercicio original enlazado en cobertura.

## B08 Cursores H2 no SQLite

`salarios(c)` recibe H2 con `empleados(id,salario DECIMAL(10,2))`. Recorre un cursor
desplazable y actualizable, suma `10.00` con `BigDecimal`, llama a `updateRow()` por
fila y confirma una sola vez. Antes 100/200 → después 110.00/210.00.

**Comprobar:** `mvn -o test -Dgroups=b08`. El test abre H2 él solo y comprueba que
SQLite declara no soportar esa combinación. No cambies el driver SQLite para forzarla.

<details><summary>Pista</summary>

Solicita `TYPE_SCROLL_INSENSITIVE, CONCUR_UPDATABLE` en `createStatement`.
Selecciona una sola tabla con PK. `rs.updateBigDecimal("salario",nuevo)` cambia el
buffer; `rs.updateRow()` envía el cambio; `c.commit()` confirma la transacción.
</details>

**Variantes guiadas (no incluidas en el test básico):**

1. En H2, haz `last()`, `getRow()`, `first()`, `absolute(2)` y `previous()` antes de
   actualizar. `absolute(1)` es la primera fila; `absolute(-1)`, la última.
2. Inserta con `moveToInsertRow` → `updateInt`/`updateBigDecimal` → `insertRow` →
   `moveToCurrentRow`. Borra una fila válida con `deleteRow`. Todo dentro de la transacción.
3. Reproduce las reglas excluyentes y fechas del ejercicio original de salarios;
   si dos reglas coinciden, utiliza la prioridad del enunciado, no dos subidas acumuladas.
4. En SQLite, resuelve la misma actualización con un `UPDATE` parametrizado por ID,
   no con un cursor actualizable. Eso cambia el mecanismo, no la regla de negocio.

## B09 Llamada a una funcion

`doble(c,7)` devuelve 14 mediante `CallableStatement`. El test registra una función
Java como alias H2 `DOBLE`; no crea un procedimiento MySQL ni PL/SQL en SQLite.

**Comprobar:** `mvn -o test -Dgroups=b09`.

<details><summary>Pista</summary>

`prepareCall("{? = call DOBLE(?)}")` → `registerOutParameter(1,Types.INTEGER)` →
`setInt(2,numero)` → `execute()` → `getInt(1)`. No vuelvas a pasar SQL a `execute`.
</details>

**Variante documental de servidor:** compara `{call insertaCliente(?,?,?)}` con
`{? = call funcion(?)}`. En la primera, el retorno no ocupa la posición 1;
en la segunda, sí. Para crear rutinas MySQL/Oracle necesitas ese servidor y su driver,
aunque pueda estar instalado localmente sin Internet. Aquí no se han instalado ni probado.

## B10 Simulacro integrador

**Tiempo recomendado: 60 minutos con apuntes.** Escribe `Simulacro.java` sin llamar
a `Jdbc`: debes reproducir el patrón, no encadenar métodos ya terminados.

```text
cursos(id PK,nombre UNIQUE NOT NULL,plazas NOT NULL CHECK>=0)
matriculas(id PK,alumno NOT NULL,curso_id FK,UNIQUE(alumno,curso_id))
Datos iniciales: curso 1 JDBC, 2 plazas; curso 2 XML, 0 plazas.
```

1. `matricular(c,alumno,curso)` descuenta una plaza solo si existe y tiene cupo;
   inserta la matrícula con parámetros; devuelve el ID generado. Atomicidad total.
2. Si el alumno está duplicado, la plaza descontada debe recuperarse por rollback.
3. `alumnos(c,nombreCurso)` hace JOIN y devuelve nombres ordenados; curso inexistente
   o entrada de inyección devuelve lista vacía.
4. No cierres la conexión recibida; restaura autocommit. El contrato rechaza una
   transacción previa. La restricción NOT NULL es la que impide alumno SQL NULL.

**Comprobar:** `mvn -o test -Dgroups=b10`. Los tests crean el esquema; como ensayo
de configuración escribe además un `Main` propio que lo cree en un archivo nuevo,
active FK y llame a estos métodos. La creación manual del esquema no está probada
por estos tres tests: forma parte de tu ejercicio de arranque.

<details><summary>Pista de la parte difícil</summary>

Usa `UPDATE cursos SET plazas=plazas-1 WHERE id=? AND plazas>0` y exige recuento 1.
Después INSERT, recuperar clave y commit. Si el INSERT falla por UNIQUE, rollback
deshace también el descuento. No hagas `SELECT plazas` y confirmes el descuento
en otra transacción separada.
</details>

**Resultado de la variante:** producto y stock, transferencia de población o reserva
de entradas comparten el patrón. Cambian nombres, condiciones y datos; no cambia
la obligación de confirmar o deshacer la unidad completa.
