# Tema 3 · JDBC y SQLite

En el temario actual, el Tema 3 es **UT02 · 3A JDBC y 3B SQLite**. Este material entrena la práctica con dos tablas y consultas con `JOIN`; los enunciados son simulacros, no preguntas oficiales.

| Bloque | Para qué sirve |
|---|---|
| [00 · Plantilla de examen](00-examen/b_README.md) | Crear desde `.sql` o abrir `.sqlite`, con herramientas reutilizables de consultas, DML, transacciones, savepoints y metadatos. Incluye datos y recetas de ejemplo que hay que adaptar al enunciado. |
| [01 · Ejercicios atómicos](01-atomicos/README.md) | 12 objetivos pequeños y resueltos. Cada uno empieza con sus propios datos. |
| [02 · Personal](02-simulacros/01-personal/README.md) | Alta + revisión salarial con prioridad de reglas, `INNER JOIN` y rollback si el `UPDATE` afecta a cero filas. |
| [02 · Almacén](02-simulacros/02-almacen/README.md) | Descontar stock + registrar salida, id duplicado, stock insuficiente y resumen con `LEFT JOIN`. |

Desde **esta carpeta**, con JDK 17 o posterior y sin Maven ni conexión a Internet:

```bash
bash ejecutar.sh desde-sql
bash ejecutar.sh desde-sqlite
bash ejecutar.sh atomicos 1 Ventas
bash ejecutar.sh atomicos todos
bash ejecutar.sh personal
bash ejecutar.sh almacen
bash ejecutar.sh consultas
bash ejecutar.sh ejercicios
```

El driver va incluido en `00-examen/lib/sqlite-jdbc-3.51.0.0.jar`: esta carpeta funciona sola en cualquier PC con JDK 17+, sin internet y sin depender de otras carpetas. Fija la carpeta de trabajo para encontrar el SQL sin depender del directorio de la terminal. También puedes abrir este `pom.xml` como proyecto Maven en el IDE (para ejecutar un `main`, usa como directorio de trabajo el módulo correspondiente). La plantilla tiene su propio `pom.xml` y se puede copiar por separado.

Los atómicos usan `:memory:` y no dejan fichero. Cada simulacro trabaja siempre sobre la misma base, `target/personal.sqlite` o `target/almacen.sqlite`, que se rehace desde su `sql/datos.sql` en cada ejecución. Puedes abrirla después con la consola o con DB Browser; ciérrala antes de volver a ejecutar.

## Qué hay fuera de 00-examen y cómo usarlo

**`00-examen` es tu caja de herramientas; `01-atomicos` y `02-simulacros` son material para entrenar, no partes que debas ejecutar en el examen.**

| Elemento | Para qué sirve | Cómo usarlo |
|---|---|---|
| `01-atomicos/` | 12 ejercicios pequeños y resueltos: consultas, JOIN, nulos, altas, cambios, borrados, transacciones, savepoints y metadatos. | Lee su README, intenta un objetivo y compara con el método correspondiente de `Atomicos.java`. Ejecuta uno o todos. |
| `02-simulacros/01-personal/` | Ejercicio completo: alta y revisión salarial con commit o rollback. | Lee su README como enunciado, intenta resolverlo en una copia y compara con `Personal.java`. |
| `02-simulacros/02-almacen/` | Ejercicio completo: stock, salidas, duplicados y rollback. | Lee su README, intenta resolverlo y compara con `Almacen.java`. |
| `ejecutar.sh` | Compila y ejecuta los programas sin Maven; localiza el driver y fija la carpeta de trabajo. | En una terminal Bash situada aquí, usa los comandos de abajo. |
| `00-examen/b_conect.sh` | Arranca los asistentes locales de consultas y generación de Java, también si copias `00-examen` por separado. | Dentro de `00-examen`, usa `bash b_conect.sh consultas` o `bash b_conect.sh ejercicios`; incluye el JAR local. |
| `pom.xml` de esta carpeta | Agrupa los cuatro módulos Java y conecta los ejercicios con las herramientas de `00-examen`. | Abre **esta carpeta completa** en el IDE para trabajar con todos. No es un programa ni se ejecuta como un Main. |
| `README.md` de esta carpeta | Mapa general del material. | Empieza aquí; para las llamadas de examen consulta `00-examen/b_README.md`. |
| `.vscode/` | Configuraciones de ejecución de VS Code, con argumentos y directorios de trabajo. | Abre `Tema-3-JDBC-SQLite` como carpeta y selecciona una configuración en **Ejecutar y depurar**. |
| `sql/` dentro de los ejercicios | Datos iniciales propios de cada ejercicio. | Los carga su programa automáticamente; no los copies sobre la base del examen. |
| `target/` dentro de cada proyecto | Clases/JAR generados y, en simulacros, bases de prueba. | No contiene la solución fuente. Puedes inspeccionar las bases impresas en consola. No hace falta entregarlo salvo indicación del profesor. |

### Uso recomendado para estudiar

1. Practica un objetivo con `01-atomicos`.
2. Identifica qué herramienta equivalente tienes en `Sqlite.java`.
3. Intenta un simulacro completo sin mirar primero la solución.
4. Para el examen, trabaja en una copia de **`00-examen`** con el driver disponible; no necesitas llevar los otros ejercicios si no te los piden.

```bash
# Desde Tema-3-JDBC-SQLite:
bash ejecutar.sh atomicos 1 Ventas   # Solo el ejercicio 1, con filtro Ventas.
bash ejecutar.sh atomicos 8         # Solo el ejemplo de rollback.
bash ejecutar.sh atomicos todos    # Los 12, cada uno con datos nuevos.
bash ejecutar.sh personal          # Simulacro de personal.
bash ejecutar.sh almacen           # Simulacro de almacén.
```

Son **soluciones ejecutables**, no menús que hagan preguntas: salvo variantes indicadas en sus README, los casos ya están programados. Cambia argumentos o trabaja en una copia para ensayar otras situaciones.

### VS Code: si aparecen rojos después de renombrar la carpeta

Los ejercicios importan `examen.herramientas.Sqlite`, que está en `00-examen`. Maven mantiene esa dependencia mediante el identificador **`plantilla-sqlite`**; ese nombre interno no tiene que coincidir con el nombre de la carpeta.

1. Abre la carpeta **`Tema-3-JDBC-SQLite`**, no solo `01-atomicos` ni un archivo Java suelto.
2. Espera a que Java termine de importar los proyectos Maven.
3. Si sigue rojo, pulsa `Ctrl+Shift+P` y ejecuta **Java: Clean Java Language Server Workspace**; acepta reiniciar y espera a la nueva importación.
4. En **Ejecutar y depurar** tienes configuraciones para los dos MainDesde, los atómicos, ambos simulacros y las pruebas de herramientas.
5. Si persiste un error, consulta su texto en **Problemas** (`Ctrl+Shift+M`): el color de la carpeta por sí solo no identifica la causa.

No añadas a mano rutas Java ni copies `Sqlite.java` dentro de cada ejercicio: los proyectos ya están relacionados por Maven. Las configuraciones `.vscode` se aplican al abrir esta carpeta como raíz.

### Comprobar la compilación con Maven (opcional)

Si tienes Maven disponible, desde esta carpeta ejecuta:

```bash
mvn verify
```

Debe terminar con los cinco proyectos del reactor en `SUCCESS`. Esto compila las pruebas, pero **no ejecuta** `PruebasSqlite`, que es un programa de comprobación con su propio `main`, no un test JUnit. Para sus 60 comprobaciones usa la configuración **Comprobar herramientas (60 pruebas)** de VS Code.

No hace falta instalar Maven para usar `ejecutar.sh`: el script solo necesita Bash, JDK y el JAR del driver.

## Qué practicar según lo que te pidan

| Familia del aula | Ejercicios de esta carpeta |
|---|---|
| 1 y 4 · Leer y buscar filas | 01–03: `JOIN`, parámetros, lista vacía, agregados y `NULL`. En el 01 puedes buscar por argumento o por teclado. |
| 2 · Metadatos | 10: tablas, claves foráneas y etiquetas de las columnas del resultado. |
| 3 · DML seguro | 04–06: `INSERT`, ID generado, `UPDATE`, filas afectadas, `DELETE` y restricciones. |
| 5 · Transacciones | 07–09 y 11: commit, rollback total por cero filas o por excepción, y rollback parcial. Los dos simulacros verifican el resultado al reabrir. |
| 6 · Salarios con cursor | 12 calcula en Java, cierra el `ResultSet` y hace `UPDATE ... WHERE id = ?` en la misma transacción. Personal resuelve prioridades directamente con `UPDATE ... CASE`. |
| 7 · Procedimiento de clientes | Solo teoría en SQLite: no hay procedimientos almacenados ni `CallableStatement` utilizable aquí. |
| 8 · Chinook | 02 y Almacén: resumen y agregación con dos tablas; se mantienen entidades sin filas relacionadas. |

Para el test, esta práctica no sustituye la teoría de 3A (JDBC) y 3B (SQLite): drivers, arquitectura, ACID, aislamiento, afinidades, `ROWID` y consola. SQLite no tiene cursores actualizables ni procedimientos almacenados, así que esa parte del temario aquí solo se estudia en teoría.
