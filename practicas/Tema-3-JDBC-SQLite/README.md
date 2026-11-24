# Tema 3 · JDBC y SQLite

En el temario actual, el Tema 3 es **UT02 · 3A JDBC y 3B SQLite**. Este material entrena la práctica con dos tablas y consultas con `JOIN`; los enunciados son simulacros, no preguntas oficiales.

| Bloque | Para qué sirve |
|---|---|
| [00 · Plantilla de examen](00-plantilla/README.md) | Comprobar el driver sin internet, crear una base desde `.sql` o abrir un `.sqlite` existente. Sin tablas de aplicación ni código de ejercicio. |
| [01 · Ejercicios atómicos](01-atomicos/README.md) | 12 objetivos pequeños y resueltos. Cada uno empieza con sus propios datos. |
| [02 · Personal](02-simulacros/01-personal/README.md) | Alta + revisión salarial con prioridad de reglas, `INNER JOIN` y rollback si el `UPDATE` afecta a cero filas. |
| [02 · Almacén](02-simulacros/02-almacen/README.md) | Descontar stock + registrar salida, id duplicado, stock insuficiente y resumen con `LEFT JOIN`. |

Desde **esta carpeta**, con JDK 17 o posterior y sin Maven ni conexión a Internet:

```bash
bash ejecutar.sh plantilla
bash ejecutar.sh atomicos 1 Ventas
bash ejecutar.sh atomicos todos
bash ejecutar.sh personal
bash ejecutar.sh almacen
```

El lanzador usa el JAR que ya existe en `entregable/practica-evaluable-ut02/lib/` (o una copia en `00-plantilla/lib/`). Fija la carpeta de trabajo para encontrar el SQL sin depender del directorio de la terminal. También puedes abrir este `pom.xml` como proyecto Maven en el IDE (para ejecutar un `main`, usa como directorio de trabajo el módulo correspondiente). La plantilla tiene su propio `pom.xml` y se puede copiar por separado.

Los atómicos usan `:memory:` y no dejan fichero. Cada simulacro imprime la ruta de una `.sqlite` nueva dentro de su `target/`; la puedes abrir desde la consola o DB Browser. No se reutiliza ni se borra la base de una ejecución anterior.

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

Para el test, esta práctica no sustituye [3A · teoría JDBC](<../../../dam2-2026-27/01-acceso-datos/recursos/Tema 3 y 4/UT02/contenidos/3A-manejo-de-conectores.md>) ni [3B · teoría SQLite](<../../../dam2-2026-27/01-acceso-datos/recursos/Tema 3 y 4/UT02/contenidos/3B-sqlite.md>). Ahí están drivers, arquitectura, ACID, aislamiento, afinidades, `ROWID` y consola. Consulta los [ocho enunciados del aula](<../../../dam2-2026-27/01-acceso-datos/recursos/Tema 3 y 4/UT02/ejercicios/INDICE.md>), el [alcance y repaso en Obsidian](<../../../obsidian/Exámenes/AD/AD - Tema 3 - Examen.md>) y tus [recetas JDBC](<../../../obsidian/Biblioteca/AD/AD - Tema 3 - Recetario JDBC.md>). Si quieres ejecutar la parte de cursores actualizables o llamadas a rutinas, tienes [los ejemplos de H2 en autoestudio](../../autoestudio/Tema-3-JDBC-SQLite/README.md); no son ejemplos SQLite.
