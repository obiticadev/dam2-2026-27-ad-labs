# Autoestudio · Acceso a Datos

## Ahora · Temas 3 y 4

- **[Ruta de seis horas · lunes, martes y miércoles](RUTA_6_HORAS.md)**: configuración,
  JDBC/SQLite y simulacro con apuntes, sin Internet. Prioriza el examen del tema 3.
- [Tema 3 · JDBC y SQLite](Tema-3-JDBC-SQLite/README.md): arranque offline, diez bloques
  de práctica después de configuración, pistas, tests y soluciones separadas.
- [Tema 4 · Hibernate y HQL](Tema-4-Hibernate-HQL/README.md): configuración, mapeo,
  relaciones, estados y consultas; recorrido independiente, fuera de esas seis horas.
- [Pruebas y límites de los temas 3/4](VERIFICACION_TEMAS_3_4.md).

Abre cada carpeta nueva como proyecto Maven independiente. El POM raíz conserva
los módulos anteriores y no ejecuta estos ejercicios incompletos por sorpresa.

## Material conservado · Primer examen

Ejercicios para **resolver tú**, con teoría, pistas progresivas y tests. Se conserva
el código que ya has escrito en DOM y SAX. No se sustituyen tus intentos por soluciones.

## Empieza aquí

1. [Plan por sesiones y cobertura](PLAN_ESTUDIO.md): qué trabajar cada día del calendario.
2. [Sílabo y progreso](SYLLABUS.md): proyectos y prioridades.
3. [Cómo compilar y ejecutar tests](README_GUIA_TERMINAL.md).
4. [Procedencia y cambios de criterio](ORIGEN.md).
5. [Qué se ha verificado y qué sigue pendiente de resolver](VERIFICACION.md).

## Alcance vigente

**UT02-A sin NIO + UT02-B. NIO está excluido del examen por confirmación del
usuario el 21/10/2026.** Esta confirmación prevalece sobre la nota antigua de
Obsidian y la sesión del calendario que todavía menciona NIO. No se han modificado
ni la bóveda ni los eventos.

- [UT02-A · Ficheros](UT02-A-manejo-de-ficheros/README.md): `File`, filtros,
  bytes, caracteres, secuencial, objetos y acceso aleatorio.
- [UT02-B · XML](UT02-B-librerias-xml-java/README.md): DOM, SAX, StAX y JAXB 2 (`javax`).

Los tests básicos no exigen rechazar `DOCTYPE`, eliminar la declaración XML,
compactar XML, usar mapas ni devolver listas inmutables. El formato legible de
DOM/JAXB y `normalize()` **sí aparecen en la teoría**; se explican sin confundirlos.

`mvn test` ejecuta la ruta básica. `mvn -Pampliaciones test` incluye también los
extras conservados de la Masterclass (y el catalogador). Los tests fallarán hasta
que completes los ejercicios; compilar no equivale a haberlos resuelto.
