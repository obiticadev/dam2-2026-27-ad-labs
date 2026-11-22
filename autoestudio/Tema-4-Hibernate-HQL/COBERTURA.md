# Tema 4 · Procedencia y cobertura

Preparado el 2026-11-22. Tema 4 corresponde a UT03 de la entrega de recursos.

## Fuentes locales

- [4A · ORM](<../../../dam2-2026-27/01-acceso-datos/recursos/Tema 3 y 4/UT03/contenidos/4A-mapeo-objeto-relacional.md>).
- [4B · Proyecto Hibernate](<../../../dam2-2026-27/01-acceso-datos/recursos/Tema 3 y 4/UT03/contenidos/4B-proyecto-hibernate.md>).
- [4C · HQL y Criteria](<../../../dam2-2026-27/01-acceso-datos/recursos/Tema 3 y 4/UT03/contenidos/4C-consultas.md>).
- [Modelo original revisado de relaciones y claves](<../../../dam2-2026-27/01-acceso-datos/recursos/Tema 3 y 4/UT03/ejercicios/01-relaciones-claves.md>).
- [Consultas completas, incluidos DELETE e INSERT SELECT](<../../../dam2-2026-27/01-acceso-datos/recursos/Tema 3 y 4/UT03/ejercicios/02-consultas.md>).
- [Índice de Obsidian](<../../../DAM2/Asignaturas/AD - Acceso a Datos/Tema 4 - Hibernate y HQL/AD - Tema 4 - Índice.md>).
- [Patrones de ejercicios en Obsidian](<../../../DAM2/Asignaturas/AD - Acceso a Datos/Tema 4 - Hibernate y HQL/AD - Tema 4 - Ejercicios por patrones.md>).

Son enlaces al árbol local, sin acceso a Internet. El kit no copia las fuentes ni
la bóveda: llévalas aparte si trabajas en otro ordenador. No se han modificado.

## Cobertura por contenidos

| Contenido | Práctica | Límite o consulta |
|---|---|---|
| ORM, JPA, entidades y anotaciones | B01, modelo de apoyo, B00 | Reproducción del mapeo en proyecto propio; herencia, LOB, temporal, enum y Transient documentales |
| Configuración nativa/JPA y sesiones | B00, XML, Arranque | Se implementa arranque nativo; persistence.xml explicado como alternativa |
| CRUD y contexto | B02/B05 | Persist/get/dirty checking/remove/merge ejecutados; load/refresh/evict como contraste y variantes |
| Uno a uno | B03 Direccion bidireccional con FK única | Variante unidireccional y PK compartida documentales, no pruebas adicionales |
| Uno a muchos y colecciones | B03 correos List ordenada | Set sin orden se contrasta, no entidad Aviso automatizada |
| Muchos a muchos, propietario | B03 módulo compartido | El test borra un profesor y conserva módulo para el otro |
| Cascadas y huérfanos | B03 | ALL en dependientes; PERSIST/MERGE en compartidos; otras cascadas explicadas en 4A |
| ID simple e igualdad | B01/B04 | Asignado/IDENTITY/SEQUENCE en H2; TABLE e IdClass de consulta |
| Clave compuesta, MapsId y JoinColumns | B04 EmployeeId/Employee/Phone | Igualdad y relectura de asociación probadas; no modificar PK |
| Estados y concurrencia | B05 | Fusión obsoleta detectada por Version; no carga concurrente ni contención pesimista probadas |
| Errores, LAZY y proxies | B09 + diagnóstico B00 | FETCH probado; otros errores explicados, no todos provocados automáticamente |
| HQL y parámetros | B06 | Nombre exacto automatizado; LIKE/BETWEEN/IN/fechas como variantes |
| Agregación, uniones y subconsulta | B08 | COUNT/LEFT JOIN/AVG automatizados; otros agregados/HAVING en variantes y fuente |
| Resultados y páginas | B07 | Tuplas, páginas y overflow ejecutados; count de páginas/resultado único como variantes |
| NamedQuery | B06 | Consulta equivalente a filtro HQL |
| DML masiva | B10 | UPDATE y clear probados; DELETE/INSERT SELECT conservados como variantes con fuente resuelta |
| Criteria | B11 | Ampliación editorial, no atribuida al profesor |
| Proyecto completo | B00 y caso integrador | No se usa política destructiva sobre archivos existentes |
| Motores | H2 principal y B12 SQLite | SQLite solo Usuario con esquema manual e ID asignado; MySQL/Oracle no ejecutados |

Los ejercicios son nuevos y reducidos para aprender el mecanismo, con referencia
separada. No sustituyen todo el código de Mapeos/Consultas de la entrega. La cobertura
documental y las variantes no se presentan como métodos ya implementados ni tests
superados por el estudiante.
