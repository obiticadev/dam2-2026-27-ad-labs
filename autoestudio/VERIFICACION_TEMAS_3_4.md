# Verificación · Temas 3 y 4

Fecha: **2026-11-22**. Estos resultados son de preparación técnica, no progreso del
alumno. No se han ejecutado ni modificado sus ejercicios anteriores de ficheros/XML.
No se han editado Obsidian ni el repositorio de teoría; no hay commits/publicación.

## Entorno y dependencias

- Linux, OpenJDK **25.0.5**, compilación `--release 17`; no se ha ejecutado con un
  runtime JDK 17 distinto. Maven 3.9.11, JUnit Jupiter 5.11.4.
- SQLite JDBC **3.42.0.0**, H2 **1.4.200**, Hibernate **5.4.33.Final**, dialecto SQLite
  **0.1.2** y dependencias transitivas en `lib/` de cada proyecto.
- Maven encuentra sus dependencias/plugins en la caché existente del equipo. Todas
  las órdenes de preparación y pruebas se ejecutaron con **`-o`**, sin descargar
  versiones nuevas. No se ha desconectado físicamente la red del usuario.
- `lib/`, `target/` y `datos/` son generados y están ignorados por Git. Para trasladarlos
  se utiliza el kit: un clon Git por sí solo no incluye los JAR.

## Pruebas realizadas

| Prueba | Resultado |
|---|---|
| Tema 3, `laboratorio.py soluciones` | **14 tests, 0 fallos, 0 errores**: esquema, parámetros, DML/NULL, metadatos, commit/rollback, savepoint, agrupación, cursor H2, función H2 y tres casos del simulacro |
| Tema 4, `laboratorio.py soluciones` | **14 tests, 0 fallos, 0 errores**: arranque, mapeo, CRUD, relaciones/huérfano/compartido, clave compuesta, merge/version, HQL/NamedQuery, páginas/overflow, agregación, FETCH, DML, Criteria y SQLite |
| Ambos, `mvn -o clean test -Dgroups=b00` | **1 test de configuración por proyecto**, compilación limpia de fuentes y tests |
| Ambos, `laboratorio.py preparar` | B00 y copia de dependencias runtime en modo Maven offline; compilación manual correcta |
| Tema 3, `laboratorio.py arranque` | Compila con javac, crea archivo SQLite y muestra `Conexión lista`, `SQLite` y ruta |
| Tema 4, `laboratorio.py arranque` | Compila con javac y recursos locales, persiste/relee H2, muestra `Arranque listo` |
| Kit copiado a carpeta temporal, con `HOME` vacío | Ambos arranques compilan y ejecutan usando solo JDK, Python y los JAR del kit; sin invocar Maven ni usar `.m2` |
| B02 sobre ambos proyectos del alumno | Falla por `UnsupportedOperationException` pendiente, como corresponde; no se han sustituido sus ejercicios por la referencia |
| Enlaces de las nuevas guías | 11 documentos, 72 enlaces locales y sus anclajes comprobados, sin destinos rotos en el árbol original |
| Main Java de la guía B00 de SQLite | Extraído literalmente a un proyecto nuevo temporal; compila con Maven offline y javac, ejecuta `SELECT 1` y devuelve 1 |
| Repetición del arranque SQLite | El archivo conserva una sola fila en `prueba`; el arranque no duplica la inserción |

Las soluciones se compilan en una copia temporal sin `target/`: no se introducen
sus `.class` en el proyecto del alumno. Los métodos pendientes siguen lanzando
`UnsupportedOperationException`. La ruta normal `mvn -o test` no se presenta como
superada: está pensada para que falle hasta resolver cada ejercicio.

## Qué no certifican estas pruebas

- No se han probado los asistentes ni menús concretos de Eclipse/IntelliJ/otros IDE.
  El arranque comprobado es por terminal y Maven/JDK local.
- No se han probado servidores MySQL/Oracle, sus procedimientos/privilegios ni
  drivers. H2 demuestra la API de cursor/llamada, no los procedimientos de esos motores.
- No se han ejecutado aquí los ocho ejercicios originales World/Chinook ni todas
  las variantes propuestas. [Cobertura T3](Tema-3-JDBC-SQLite/COBERTURA.md) y
  [cobertura T4](Tema-4-Hibernate-HQL/COBERTURA.md) separan práctica automatizada y consulta.
- No hay pruebas de carga, aislamiento entre clientes ni contención pesimista.
  La detección de versión obsoleta es un ensayo secuencial con varias sesiones.
- La prueba Hibernate/SQLite cubre solo Usuario con ID asignado y esquema manual;
  no hace portable automáticamente el modelo H2 con secuencias/relaciones.
- Los avisos de JDK 25 sobre acceso nativo/Unsafe y de Hibernate sobre su pool interno
  no impiden las pruebas, pero estas versiones docentes no son recomendación de producción.

## Repetir

Desde cada tema:

```bash
mvn -o clean test -Dgroups=b00
python3 ../herramientas/laboratorio.py arranque
python3 ../herramientas/laboratorio.py soluciones
```

Para tus intentos usa `mvn -o test -Dgroups=bNN`; para varios bloques, por ejemplo
`mvn -o test '-Dgroups=b00 | b02 | b03'`. No ejecutes todo el reactor antiguo para
comprobar un solo bloque nuevo.
