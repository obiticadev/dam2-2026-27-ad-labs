# Tema 4 · Bloques de práctica

[Inicio](README.md) · [Configuración](00-CONFIGURACION.md) · [Cobertura](COBERTURA.md)

Cada bloque tiene etiqueta `bNN`: `mvn -o test -Dgroups=b06`, por ejemplo.
`Orm.java` recibe una sesión y transacción abiertas; las controla el test. En una
aplicación propia debes escribir tú ese control, como en `Arranque.java`.
La referencia está en `soluciones/lab/Orm.java`; el modelo completo en `Modelo.java`.
Todos son ejemplos añadidos, no entregas oficiales.

## Índice

- [B01 · Entidades](#b01-entidades-y-mapeo)
- [B02 · CRUD](#b02-crud-y-dirty-checking)
- [B03 · Relaciones](#b03-relaciones-cascadas-y-huerfanos)
- [B04 · Identidad](#b04-identidad-simple-y-compuesta)
- [B05 · Estados](#b05-estados-merge-y-version)
- [B06 · HQL](#b06-hql-y-consulta-nombrada)
- [B07 · Resultados](#b07-resultados-y-paginacion)
- [B08 · Agregación](#b08-agregacion-y-uniones)
- [B09 · LAZY](#b09-carga-diferida)
- [B10 · DML](#b10-modificaciones-masivas)
- [B11 · Criteria](#b11-criteria)
- [B12 · SQLite](#b12-adaptacion-a-sqlite)

## B01 Entidades y mapeo

**Ejercicio de construcción:** en un proyecto nuevo de ensayo, escribe una entidad
`Profesor` con tabla `profesores`, ID generado IDENTITY, nombre no nulo y versión.
Constructor vacío y acceso por campos. Regístrala y persiste un objeto.
Compara tu solución con el inicio de `Modelo.Profesor`, no la copies antes de intentarlo.

**Modelo mental:** clase→tabla, objeto→fila, atributo→columna, referencia→FK.
JPA es especificación; Hibernate, implementación y API adicional. Una fábrica se
reutiliza; la sesión representa contexto/unidad de trabajo y no se comparte entre hilos.

**Comprobar:** `mvn -o test -Dgroups=b01` verifica el modelo de apoyo ya resuelto;
para certificar tu reproducción debes ejecutar su Main. Ese test no evalúa un archivo
que hayas creado en otra carpeta.

<details><summary>Pista y respuesta de comprobación</summary>

`@Entity`, `@Table`, `@Id`, `@GeneratedValue`, `@Column(nullable=false)`, `@Version`.
Si `@Id` está en el campo, JPA usa campos; no mezcles getters anotados sin `@Access`.
`@Transient` excluye persistencia, `@Lob` modela datos grandes, `@Enumerated(STRING)`
evita depender del ordinal. `@Temporal` es para Date/Calendar, no LocalDate.
</details>

**Variante documental:** identifica en 4A `@Inheritance` y discriminadores; no hay
jerarquía ni test de herencia en este proyecto. Son contenido de consulta, no una
función probada por el simple arranque del modelo.

## B02 CRUD y dirty checking

Implementa `crear` y `renombrar`. `crear` persiste profesor y devuelve ID. `renombrar`
busca por ID, devuelve false si falta y modifica la entidad gestionada. No necesita
un UPDATE escrito a mano. El test relee en otra sesión y finalmente elimina con `remove`.

**Comprobar:** `mvn -o test -Dgroups=b02`. Ana→Ada→fila eliminada.

<details><summary>Pista</summary>

`persist(nuevo)`; `get(Profesor.class,id)`; setter sobre entidad gestionada.
Dirty checking detecta el cambio; flush lo envía; commit lo confirma. `get` puede
devolver null. `load` puede devolver proxy y retrasar el fallo de un ID ausente.
</details>

**Variante:** escribe un Main propio con dos transacciones, causa un error en la
segunda y comprueba que no se confirma parcialmente. Tras fallo, cierra/descarta sesión.

## B03 Relaciones cascadas y huerfanos

Implementa `grafo` y `quitarPrimerCorreo`. El módulo llega gestionado y compartido.
Crea profesor Ana con Dirección Centro y correos `a`,`b`, enlaza módulo y persiste.
Después quita el primer correo: debe borrarse la fila huérfana. Borrar Ana no debe
borrar el módulo usado por Luis. Los tests releen en sesiones nuevas.

```text
Profesor --FK única--> Direccion         inverso: Direccion.profesor
Profesor <--FK-- Correo                  propietario: Correo.profesor
Profesor --tabla profesor_modulo-- Modulo
```

**Comprobar:** `mvn -o test -Dgroups=b03`.

<details><summary>Pistas y por qué funciona</summary>

Utiliza los métodos de asociación del modelo: actualizan ambos lados Java.
`mappedBy` nombra atributo Java del propietario, no columna SQL. `@OrderColumn`
conserva posición de List; `@OrderBy` ordena por un atributo, no hace lo mismo.
Cascada ALL cabe para hijos dependientes; PERSIST/MERGE sin REMOVE para módulos
compartidos. `orphanRemoval` no es `ON DELETE CASCADE`.
</details>

**Variantes:** reordena correos y relee; representa avisos como Set sin orden (no se
ha incluido entidad Aviso en este modelo reducido). Si la tabla intermedia tuviera
horas o fecha, conviértela en entidad. Para 1:1 unidireccional elimina solo la referencia
inversa en una copia: no es lo mismo que PK compartida, que requiere otro diseño.

## B04 Identidad simple y compuesta

Implementa `empleado`: para Company gestionada, crea Employee número 7 llamado Ada
y Phone 555. La clave es `(company_id,employee_number)`; Phone referencia ambas columnas.

**Comprobar:** `mvn -o test -Dgroups=b04`; recuperación por `new EmployeeId(1,7)` y
referencia Phone→Employee válidas.

<details><summary>Pista y solución del diseño</summary>

Usa constructores del modelo y persiste empleado antes de teléfono. `EmployeeId`
lleva `@Embeddable`, Serializable, constructor vacío, equals/hashCode por valor.
`Employee` lleva `@EmbeddedId`, `@MapsId("companyId")` y relación Company; Phone,
`@JoinColumns`. No hagas OneToMany hacia EmployeeId: la clave no es entidad.
</details>

**Reproducción de mapeo:** copia a mano esas tres clases en otro proyecto y comprueba
su recuperación. No cambies una PK persistida. Para entidades con ID generado no
construyas hashCode sobre campos mutables sin considerar su cambio dentro de HashSet.
`@IdClass` es otra estrategia; este ejercicio no la implementa.

**Identificadores simples:** Company tiene ID asignado, Profesor usa IDENTITY, Ciclo
usa SEQUENCE declarada coherentemente. TABLE usa tabla auxiliar. No todas las estrategias
funcionan en cualquier motor; SQLite de B12 usa ID asignado deliberadamente.

## B05 Estados merge y version

Implementa `fusionar(s,separado)` devolviendo la instancia gestionada de merge.
El original sigue detached; no necesariamente es el mismo objeto. El test cambia
nombre fuera de sesión, fusiona y relee. Otro test intenta fusionar una versión
obsoleta después de un cambio confirmado: debe detectar bloqueo optimista.

```text
nuevo --persist--> gestionado --close/clear/evict--> separado
separado --merge--> copia gestionada (el original sigue separado)
gestionado --remove--> eliminado --commit--> fila borrada
```

**Comprobar:** `mvn -o test -Dgroups=b05`.

<details><summary>Pista y contraste</summary>

`return (Profesor)s.merge(separado)`. `refresh` recarga y descarta cambios locales;
`detach/evict` no borra fila; `clear` separa todo el contexto; `flush` no es commit.
`@Version` detecta estado obsoleto. El test no es una prueba de carga concurrente.
</details>

**Variante:** carga entidad, cambia nombre, haz `refresh` y observa que vuelve al
valor almacenado. Compara con `evict`. Revisa `PESSIMISTIC_WRITE` en el temario:
requiere transacción y soporte del motor, no se ha probado contención aquí.

## B06 HQL y consulta nombrada

Implementa `buscar` y `nombrada`: nombre exacto y orden por ID. `O'Neil` debe
encontrarse sin escapar SQL a mano; una entrada de inyección no devuelve todos.
La NamedQuery está declarada en el modelo como `Profesor.porNombre`.

**Comprobar:** `mvn -o test -Dgroups=b06`.

<details><summary>Pista</summary>

`from Profesor p where p.nombre=:nombre order by p.id`, consulta tipada y
`setParameter("nombre",valor)`. HQL usa Profesor/nombre Java, no profesores/columna
SQL. Los posicionales HQL son `?1`, no `?` JDBC. Los nombres de entidades/atributos
deben coincidir; la comparación de valores depende del motor/colación.
</details>

**Variante:** `LIKE`, `BETWEEN`, `IN`, `IS NULL` y filtros con tipos temporales reales.
Para una sola fila `uniqueResult` puede devolver null o lanzar `NonUniqueResultException`;
no intentes arreglar una consulta no única suponiendo que cogerá la primera.

## B07 Resultados y paginacion

Implementa `pagina` desde cero y `proyectar`: ID/nombre en `List<Object[]>`.
Tres profesores A/B/C, página 1 tamaño 2→C; página 4→vacía. Rechaza página negativa,
tamaño no positivo y desbordamiento del offset.

**Comprobar:** `mvn -o test -Dgroups=b07`.

<details><summary>Pista</summary>

`Math.multiplyExact(pagina,tamanyo)`, `order by p.id`, `setFirstResult`,
`setMaxResults`. Para proyección `select p.id,p.nombre`. Entidad completa, escalar
y tupla no devuelven el mismo tipo. Count devuelve Long, no Integer.
</details>

**Variante:** añade total de páginas usando `total/tamaño` y resto, con `long`.
No pagines un JOIN FETCH de colección como si las filas SQL fueran padres únicos.

## B08 Agregacion y uniones

Implementa `sobreMedia` y `resumen`. Datos: Ana con ciclos DAM=100h y DAW=200h;
otro profesor sin ciclos. Sobre media→DAW. Resumen→Ana=2, Sin ciclos=0.

**Comprobar:** `mvn -o test -Dgroups=b08`.

<details><summary>Pista</summary>

Subconsulta `(select avg(c2.horas) from Ciclo c2)`; agrupación por ID/nombre, LEFT JOIN
y `count(c)` para conservar ceros. El modelo tiene Ciclo.profesor, no Profesor.ciclos;
la referencia usa unión explícita HQL de Hibernate 5.4 con `on c.profesor=p`.
</details>

**Variante:** `AVG/SUM/MIN/MAX/COUNT` con tipos adecuados, agrupar nombres repetidos
con HAVING y consultar ciclos con profesor mediante `join c.profesor p`. No inventes
una asociación inversa que el modelo no declara.

## B09 Carga diferida

Implementa `conCorreos`: recuperar profesor y correos para consultarlos tras cerrar
sesión. No cambies todo a EAGER ni mantengas una sesión global.

**Comprobar:** `mvn -o test -Dgroups=b09`; colección inicializada y contenido accesible
fuera de la sesión.

<details><summary>Pista</summary>

`select distinct p from Profesor p left join fetch p.correos where p.id=:id`.
El LEFT conserva profesor sin correos; el DISTINCT expresa entidad padre única.
Leer un campo público de un proxy no sustituye a un getter: el modelo está encapsulado.
</details>

**Diagnóstico:** `LazyInitializationException`→relación no cargada fuera de sesión;
`NonUniqueObjectException`→dos objetos del mismo ID asociados al contexto;
`NonUniqueResultException`→consulta que debía ser única devuelve varios.

## B10 Modificaciones masivas

Implementa `masiva`: cambiar todos los nombres anteriores por el nuevo, devolver
recuento y dejar el contexto limpio. El test carga una instancia antes de la operación
y comprueba que queda separada; después relee el nombre real.

**Comprobar:** `mvn -o test -Dgroups=b10`.

<details><summary>Pista</summary>

`s.flush()` → HQL UPDATE parametrizado → `executeUpdate()` → `s.clear()`.
La DML masiva no actualiza automáticamente los objetos ya gestionados ni aplica
las cascadas de `remove` a cada entidad. Debe existir una transacción exterior.
</details>

**Variantes del temario (no ejecutadas por este test):** DELETE masivo solo sobre
profesores sin relaciones dependientes; INSERT SELECT desde una entidad fuente con
destino vacío y política de IDs controlada. HQL 5 permite INSERT SELECT, no el
INSERT VALUES SQL. Consulta la solución original en cobertura; no uses esas variantes
con los grafos compartidos del bloque 03 sin planificar FK y cascadas.

## B11 Criteria

Implementa `criteria` equivalente al filtro exacto por nombre. Usa CriteriaBuilder,
CriteriaQuery, Root y Predicate; orden por ID. Es una **ampliación editorial** de la
entrega, no se atribuye al profesor.

**Comprobar:** `mvn -o test -Dgroups=b11`; igualdad con resultados HQL y resistencia
a entrada de inyección.

<details><summary>Pista</summary>

`cb.createQuery(Profesor.class)`, `cq.from(Profesor.class)`,
`cb.equal(p.get("nombre"),nombre)`, `cq.orderBy(cb.asc(p.get("id")))`.
Los atributos como cadenas aún pueden estar mal escritos: Criteria no elimina todos
los errores de nombres; el metamodelo estático mejora ese aspecto.
</details>

## B12 Adaptacion a SQLite

Lee y reproduce `sqliteConEsquemaManual` de `OrmTest.java` en un Main propio sobre
un archivo nuevo. Este bloque es un **ejemplo completo ya resuelto**, no un método
pendiente de `Orm`: el objetivo es aprender el montaje específico.

**Comprobar referencia:** `mvn -o test -Dgroups=b12`. Crea tabla manualmente, persiste
Ana y actualiza a Ada en dos sesiones Hibernate; relee con JDBC. Solo Usuario.

**Variante documental de motores:** MySQL 8 usa `com.mysql.cj.jdbc.Driver` y
`MySQL8Dialect`; Oracle del temario usa `oracle.jdbc.OracleDriver` y
`Oracle10gDialect` en Hibernate 5.4. Requieren su servidor/credenciales y drivers,
que no se han instalado para este laboratorio. No anuncies compatibilidad completa
por cambiar solo la URL. Consulta versiones y SID/servicio en 4B.

## Caso integrador para después

En un programa propio: crear fábrica una vez, persistir dos profesores con módulo
compartido, recuperar correos con FETCH, paginar profesores, renombrar uno, eliminar
un correo huérfano y borrar un profesor sin borrar el módulo del otro. Verifica en
otra sesión cada cambio y provoca un fallo antes de commit para comprobar rollback.
Este caso reúne los bloques; no es un test extra ya automatizado.
