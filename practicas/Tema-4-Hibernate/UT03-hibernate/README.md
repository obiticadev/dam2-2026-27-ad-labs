# UT03 · Hibernate: guía de configuración

Guía para montar tú mismo el entorno Hibernate del Tema 4 (UT03) y empezar las
actividades con la configuración ya funcionando. Sustituye a los vídeos largos del
aula: aquí está el orden de los pasos, qué hace cada línea y qué necesita cada
actividad.

Material de referencia: `dam2-2026-27/01-acceso-datos/recursos/Tema 3 y 4/UT03`
(4A ORM, 4B proyecto, 4C consultas, `ejercicios/01` y `ejercicios/02`).

> Comprobado el 22/11/2026 en este equipo (OpenJDK 25, Maven 3.9.11): el proyecto de
> referencia compila, `Main` ejecuta el CRUD y las 13 pruebas de `verificacion.md`
> pasan. Hibernate 5.4.33 funciona con JDK 25 compilando con `release 11`.

## 1. Un solo proyecto o uno por actividad

**Un solo proyecto Maven.** Las tres actividades usan el mismo `pom.xml` y el mismo
`hibernate.cfg.xml`, y la actividad de consultas trabaja sobre las entidades de la de
relaciones (`Profesor`, `Ciclo`, `Person`...). Si las separases, tendrías que copiar
las entidades y la configuración tres veces. El propio material lo indica: «son parte
del mismo proyecto».

Cada actividad tiene su paquete; la configuración se comparte.

## 2. Árbol de trabajo

Esta carpeta es la raíz del proyecto Maven:

```text
UT03-hibernate/
├── README.md                       ← esta guía
├── pom.xml                         ← paso 1: dependencias
└── src/
    ├── main/
    │   ├── java/
    │   │   ├── utils/HibernateUtils.java   ← paso 3: SessionFactory única
    │   │   ├── modelo/Usuario.java         ← paso 4: primera entidad (CRUD)
    │   │   ├── programas/Main.java         ← paso 5: CRUD de Usuario
    │   │   ├── modelo/Mapeos.java          ← actividad 1: relaciones y claves
    │   │   └── consultas/Consultas.java    ← actividad 2: HQL y Criteria
    │   └── resources/
    │       └── hibernate.cfg.xml           ← paso 2: conexión y mapeos
    └── test/java/revision/RevisionTest.java  ← opcional: pruebas del material
```

Mantén los paquetes `modelo`, `utils`, `programas` y `consultas` tal como aparecen en
el material. Así puedes pegar los ejemplos del temario sin cambiar los `import`. Si
los cambias (por ejemplo a `es.dam.accesodatos.modelo`), cambia también cada
`<mapping class>` del XML.

## 3. Configuración paso a paso

Crea el proyecto en el IDE como *Maven project* (sin arquetipo) apuntando a esta
carpeta, o crea los archivos a mano. El código completo de cada archivo está en
`4B-proyecto-hibernate.md`, apartado 4. Aquí se explica qué hace cada parte.

### Paso 1 · `pom.xml`

| Elemento | Valor | Para qué sirve |
|---|---|---|
| `maven.compiler.release` | `11` | Compila para Java 11 aunque tengas un JDK más nuevo. Es la versión del material. |
| `project.build.sourceEncoding` | `UTF-8` | Tildes y eñes correctas en el código. |
| `hibernate-core` | `5.4.33.Final` | El ORM. Trae JPA (`javax.persistence`), `Session`, HQL y Criteria. |
| `h2` | `1.4.200`, scope `runtime` | Base de datos en memoria: no hay que instalar servidor. Solo se usa al ejecutar. |
| `junit` | `4.13.2`, scope `test` | Solo si copias las pruebas de `verificacion.md`. |
| `maven-compiler-plugin` | `3.13.0` | Fija la versión del compilador. |

Las dependencias de SQLite del POM de 4B (`sqlite-jdbc`, `sqlite-dialect`) solo hacen
falta para la prueba SQLite opcional. Puedes omitirlas.

**No uses Hibernate 6.** Cambia `javax.persistence` por `jakarta.persistence` y los
ejemplos del tema dejan de compilar.

Para lanzar `Main` desde la terminal, añade también el plugin `exec-maven-plugin`
(`org.codehaus.mojo`, `3.5.0`), como en los proyectos de UT02. Desde el IDE no hace
falta.

### Paso 2 · `src/main/resources/hibernate.cfg.xml`

Debe estar en `src/main/resources`. `new Configuration().configure()` lo busca en el
classpath con ese nombre exacto.

| Propiedad | Valor de referencia | Qué hace |
|---|---|---|
| `hibernate.connection.driver_class` | `org.h2.Driver` | Driver JDBC. Es la misma clase que cargabas en JDBC. |
| `hibernate.connection.url` | `jdbc:h2:mem:ut03;DB_CLOSE_DELAY=-1` | Base `ut03` en memoria. `DB_CLOSE_DELAY=-1` la mantiene viva mientras dure el programa, aunque se cierre una conexión. |
| `hibernate.connection.username` / `password` | `sa` / vacío | Usuario por defecto de H2. |
| `hibernate.dialect` | `org.hibernate.dialect.H2Dialect` | Le dice a Hibernate qué variante de SQL generar. |
| `hibernate.hbm2ddl.auto` | `create-drop` | Crea las tablas a partir de las entidades al arrancar y las borra al cerrar. Solo con bases de prueba. |
| `hibernate.show_sql` | `true` mientras practicas | Imprime cada SQL generado. Así ves qué hace Hibernate con cada `persist`, `get` o consulta. |
| `hibernate.format_sql` | `true` (opcional) | Muestra el SQL indentado. |
| `<mapping class="..."/>` | `modelo.Usuario` | Registra cada entidad. Una entidad sin registrar da error «is not mapped». |

Valores de `hbm2ddl.auto`: `create-drop` y `create` borran datos; `update` altera
tablas sin migración; `validate` solo comprueba; `none` no toca el esquema. Para H2 en
memoria, `create-drop`. Para una base real con datos, `validate` o `none`.

### Paso 3 · `utils/HibernateUtils.java`

Crea **una sola** `SessionFactory` (es pesada: lee el XML, conecta y genera el esquema)
y la reparte. Cada operación abre una `Session` ligera y la cierra. `shutdown()` se
llama una vez, al terminar el programa. Si creas una fábrica por operación, con
`create-drop` pierdes los datos cada vez.

### Paso 4 · `modelo/Usuario.java`

La entidad mínima: `@Entity`, `@Table(name = "usuarios")`, `@Id`, un constructor sin
argumentos (Hibernate lo necesita para crear objetos al leer) y getters y setters.
Importa de `javax.persistence.*`.

### Paso 5 · `programas/Main.java` y prueba de funcionamiento

`Main` abre una sesión, inicia una transacción y hace alta, lectura, modificación y
borrado. Si algo falla, hace rollback. Ejecútalo desde el IDE o con:

```bash
mvn compile exec:java -Dexec.mainClass=programas.Main
```

**Está bien configurado si:** termina sin excepción y, con `show_sql=true`, ves en
este orden `create table usuarios`, `insert`, `select`, `update`, `delete` y, al
cerrar, `drop table`.

## 4. Qué necesita cada actividad

| Actividad | Archivos | Cambios en la configuración | Funciona cuando |
|---|---|---|---|
| **0 · CRUD de Usuario** (4B) | `Usuario`, `HibernateUtils`, `Main` | Solo `<mapping class="modelo.Usuario"/>`. Es la base de las demás. | `Main` termina sin errores (paso 5). |
| **1 · Relaciones y claves** (`ejercicios/01`) | `modelo/Mapeos.java` | Registrar las 11 entidades anidadas. Mismo POM. | Arranca y en el log aparecen `create table profesores`, `profesor_modulo`, `Employee`... |
| **2 · HQL y Criteria** (`ejercicios/02`) | `consultas/Consultas.java` | **Ninguno.** Usa las entidades de la actividad 1. La consulta nombrada `Profesores.ByName` ya está declarada en `Profesor`. | Las consultas devuelven datos sin error «is not mapped» ni `QuerySyntaxException`. |

### Registrar las entidades de `Mapeos`

Son clases anidadas, así que su nombre lleva `$`. Añádelas al XML debajo de `Usuario`:

```xml
<mapping class="modelo.Mapeos$Profesor"/>
<mapping class="modelo.Mapeos$Perfil"/>
<mapping class="modelo.Mapeos$Direccion"/>
<mapping class="modelo.Mapeos$Aviso"/>
<mapping class="modelo.Mapeos$Correo"/>
<mapping class="modelo.Mapeos$Modulo"/>
<mapping class="modelo.Mapeos$Company"/>
<mapping class="modelo.Mapeos$Employee"/>
<mapping class="modelo.Mapeos$Phone"/>
<mapping class="modelo.Mapeos$Ciclo"/>
<mapping class="modelo.Mapeos$Person"/>
```

`EmployeeId` no se registra: es `@Embeddable`, no `@Entity`. Comprobado: con estas
líneas, `Main` arranca y genera las 13 tablas y la secuencia `ciclo_seq`.

La otra opción, la que usan las pruebas del material, es registrarlas en código con
`cfg.addAnnotatedClass(...)`. Usa una de las dos, no ambas.

## 5. Errores típicos

| Síntoma | Causa | Solución |
|---|---|---|
| `Could not locate cfg.xml resource [hibernate.cfg.xml]` | El XML no está en `src/main/resources` o tiene otro nombre. | Muévelo o renómbralo. |
| `Unknown entity` / `X is not mapped` | Falta su `<mapping class>`, o la HQL usa el nombre de la tabla en lugar del de la entidad. | Registra la clase. En HQL escribe `from Profesor`, no `from profesores`. |
| `ClassNotFoundException` en un mapping | Paquete mal escrito o falta el `$` de la clase anidada. | `modelo.Mapeos$Profesor`. |
| `No default constructor for entity` | La entidad no tiene constructor vacío. | Añade `public X() {}`. |
| `package javax.persistence does not exist` | Hibernate 6 o falta la dependencia. | `hibernate-core 5.4.33.Final`. |
| `LazyInitializationException` | Se lee una colección LAZY con la sesión ya cerrada. | Léela dentro de la sesión o usa `join fetch` (`Consultas.conCorreos`). |
| La base aparece vacía en otra operación | Se crea una `SessionFactory` nueva cada vez con `create-drop`. | Usa siempre `HibernateUtils.getSessionFactory()`. |

## 6. Si en clase piden MySQL en vez de H2

La configuración es la misma. Cambian la dependencia y cuatro propiedades (4B,
apartado 6):

| Qué | MySQL 8 |
|---|---|
| Dependencia | `mysql:mysql-connector-java:8.0.28` |
| `driver_class` | `com.mysql.cj.jdbc.Driver` |
| `url` | `jdbc:mysql://localhost:3306/ejemplo` |
| `dialect` | `org.hibernate.dialect.MySQL8Dialect` |
| `hbm2ddl.auto` | `update` o `create` mientras practicas sobre una base vacía; nunca sobre datos que quieras conservar. |

En este equipo no hay MySQL instalado. Si lo necesitas, puedes levantarlo con Docker,
que sí está instalado. No subas al repositorio un XML con contraseñas reales.

## 7. Comprobación antes de la evaluación

1. `mvn compile` termina con `BUILD SUCCESS`.
2. `Main` ejecuta el CRUD sin excepciones y el log muestra el SQL del paso 5.
3. Con `Mapeos` registrado, el arranque crea las tablas de la actividad 1.
4. Opcional: copia `RevisionTest.java` de `documentacion/verificacion.md`, añade
   JUnit (y las dependencias SQLite si mantienes esa prueba) y ejecuta `mvn test`.
   Deben pasar 13 pruebas.
