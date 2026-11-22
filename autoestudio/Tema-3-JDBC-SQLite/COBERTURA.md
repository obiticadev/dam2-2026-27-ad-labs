# Tema 3 · Procedencia y cobertura

**Preparado el 2026-11-22.** Tema 3 es UT02 en la entrega de recursos, no la carpeta
UT02 de ficheros del laboratorio. No se modifica la numeración original de las fuentes.

## Fuentes locales consultadas

- [3A · Manejo de conectores](<../../../dam2-2026-27/01-acceso-datos/recursos/Tema 3 y 4/UT02/contenidos/3A-manejo-de-conectores.md>).
- [3B · SQLite](<../../../dam2-2026-27/01-acceso-datos/recursos/Tema 3 y 4/UT02/contenidos/3B-sqlite.md>).
- [Teoría, ocho ejercicios y soluciones de la entrega](<../../../dam2-2026-27/01-acceso-datos/recursos/Tema 3 y 4/UT02/ejercicios/INDICE.md>).
- [Índice del tema 3 en Obsidian](<../../../DAM2/Asignaturas/AD - Acceso a Datos/Tema 3 - JDBC y SQLite/AD - Tema 3 - Índice.md>).
- [Alcance del examen en Obsidian](<../../../DAM2/Exámenes/AD/AD - Tema 3 - Examen.md>).
- [Recetario JDBC en Obsidian](<../../../DAM2/Biblioteca/AD - Tema 3 - Recetario JDBC.md>).
- [Ejercicios por patrones](<../../../DAM2/Asignaturas/AD - Acceso a Datos/Tema 3 - JDBC y SQLite/AD - Tema 3 - Ejercicios por patrones.md>).

Los enlaces anteriores son rutas del árbol local del usuario, no páginas de Internet.
En una copia del kit fuera de ese árbol, lleva además tus fuentes y tu bóveda; el kit
no las duplica ni transforma sus wikilinks. Los PDF equivalentes no se han reprocesado.

## Correspondencia con la teoría completa

| Fuente | Tratamiento en el laboratorio |
|---|---|
| 3A §1–2: bases, JDBC/ODBC, arquitectura y drivers | [Fundamentos](FUNDAMENTOS.md), preguntas de recuperación al final de la ruta |
| 3A §3–4: dependencias, URLs, recursos, objetos JDBC | [B00](00-CONFIGURACION.md), Main funcional, tabla de diagnóstico; MySQL/Oracle solo consulta |
| 3A §5: consultas, parámetros, métodos execute, NULL | B02/B03 y fundamentos; variante IS NULL |
| 3A §6: metadatos y capacidades | B04 automatizado; variantes PK/FK, schemas/rutinas y soporte se consultan en 3A |
| 3A §7: DDL/DML, objetos alumnos, ventas Map | B01/B03/B05; array y Map como variantes; no se replica toda la clase del original |
| 3A §8: procedimientos IN/OUT y funciones | B09 automatiza CallableStatement con alias H2; creación de rutina MySQL/Oracle documental |
| 3A §9: ACID, commit, rollback, aislamiento, Savepoint | B05/B06/B10 automatizados y fundamentos; no prueba de carga/aislamiento concurrente |
| 3A §10: cursores, movimientos y actualizaciones | B08 automatiza actualización H2; movimientos, insertRow/deleteRow como variantes guiadas |
| 3A §11: actividades | Tabla inferior de equivalencias; originales accesibles en fuentes |
| 3B §1–2: embebida, límites, concurrencia | Fundamentos; SQLite real en los tests principales |
| 3B §3–4: consola, tipos, afinidades, fechas | Fundamentos + arranque; consola opcional, no necesaria para Java |
| 3B §5–6: contactos/grupos, restricciones, rowid, acciones FK | B01 y fundamentos; M:N y proveedores como variantes; no se duplica el esquema completo de contactos |
| 3B §7: BEGIN, modos, Savepoint | B06 y fundamentos; variante de consola |
| 3B §8: consultas y Chinook | B07/B10 y ejercicio original; EXISTS/COALESCE como variante |

## Correspondencia con los ocho ejercicios originales

| Original | Patrón practicado aquí | Diferencia deliberada |
|---|---|---|
| 01 Idiomas de España | B02: filtro, parámetros, lectura | Personas pequeñas en vez de World; no reproduce porcentajes de idiomas |
| 02 Tablas y metadatos | B04 | Esquema local con dos tablas |
| 03 DML seguro | B03 | Persona propia, no ciudad temporal |
| 04 Buscador de ciudades | B02 + B07 + listado B10 | No servidor World; filtro con JOIN se practica en cursos/matrículas |
| 05 Transferencia de población | B05 + B10 | Misma atomicidad y verificación de recuentos; otro dominio |
| 06 Salarios con cursor | B08 | Incremento fijo para aislar mecanismo; reglas por prioridad/fecha quedan como variante con original |
| 07 Procedimientos clientes | B09 | Función alias H2; no certifica sintaxis/privilegios de procedimientos de servidor |
| 08 Chinook | B07 + variante EXISTS/COALESCE | Incluye ceros; no incorpora ni descarga la base completa Chinook |

**Alcance honesto:** todos los bloques principales tienen entrada de práctica o
consulta; los tests cubren el núcleo operativo. No es una transcripción del temario
ni una ejecución de los ocho originales. La ruta de seis horas selecciona lo
imprescindible; el contenido completo sigue en fuentes/Obsidian. La mera existencia
de este material no significa que el alumno lo haya resuelto ni dominado.
