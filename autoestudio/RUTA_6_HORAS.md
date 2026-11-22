# Ruta de seis horas · lunes, martes y miércoles por la mañana

**Objetivo realista:** poder arrancar un proyecto sin Internet, leer el esquema de
una base SQLite y resolver consultas, modificaciones y transacciones consultando
Obsidian. No memorizar todos los métodos ni terminar todos los extras.

El alcance guardado en Obsidian es **tema 3 completo; tema 4 excluido**. Por eso
esta ruta prioriza el tema 3. Hibernate queda preparado para después, en su propio
proyecto. Esta planificación no convierte el tema 4 en materia de ese examen.
Si el profesor cambia el alcance, habrá que redistribuir las seis horas.

## Antes de empezar

Abre [Tema 3](Tema-3-JDBC-SQLite/README.md) como proyecto Maven independiente.
Los JAR de `lib/` ya se han preparado en este equipo. No necesitas descargar nada
para los comandos offline comprobados; en otro ordenador, lleva el kit y el JDK.
No supongas que copiar solo el POM copia las dependencias.

En cada práctica: lee el contrato del método, escribe una primera versión, ejecuta
**solo su bloque** y corrige el primer fallo. Los tests preparan sus propios datos:
no tienes que terminar B01 para probar B03. Las soluciones están aparte; no sustituyas
tu archivo entero. Si te atascas más de diez minutos, consulta primero la pista y
después solo el método correspondiente de `soluciones/lab/Jdbc.java`.

Todos los comandos siguientes se ejecutan dentro de `Tema-3-JDBC-SQLite/`.

## Lunes · 120 minutos · conseguir que funcione y entender el recorrido

| Minutos | Trabajo concreto | Resultado que debes obtener |
|---|---|---|
| 0–30 | [B00: configuración](Tema-3-JDBC-SQLite/00-CONFIGURACION.md). Ejecuta arranque, importa el POM y reproduce un proyecto nuevo en una carpeta vacía con el POM y los JAR. Escribe tu propia conexión y `SELECT 1`. | Compilas sin Internet y sabes localizar el `.db`. Si falla, no avances hasta identificar JDK, classpath o ruta. |
| 30–50 | [B01: esquema y restricciones](Tema-3-JDBC-SQLite/BLOQUES.md#b01-esquema-y-restricciones). Implementa `crear`. | `mvn -o test -Dgroups=b01`: PK, FK, UNIQUE y CHECK funcionan. |
| 50–80 | [B02: lectura](Tema-3-JDBC-SQLite/BLOQUES.md#b02-consultas-y-parametros). Implementa `buscar`. | Solo Ana de 20 años, no Ana de 17; una entrada de inyección devuelve lista vacía. |
| 80–110 | [B03: escrituras](Tema-3-JDBC-SQLite/BLOQUES.md#b03-insertar-modificar-y-borrar). Implementa los tres métodos. | Insertas un apóstrofo y un NULL; modificas y borras solo un ID. |
| 110–120 | Cierra la aplicación y vuelve a abrirla. Explica `Connection → PreparedStatement → ResultSet`, `next()` y cierre. Localiza el recetario en Obsidian. | Sabes repetir el arranque sin seguir ciegamente una captura de pantalla. |

**Si la configuración consume más tiempo:** termina conexión + B02 + una inserción;
el martes usa los primeros quince minutos para acabar UPDATE/DELETE. Reduce variantes,
no elimines la transacción ni el ensayo offline.

## Martes · 120 minutos · cambios seguros y consultas de varias tablas

| Minutos | Trabajo concreto | Resultado que debes obtener |
|---|---|---|
| 0–15 | Abre un proyecto nuevo de ensayo y escribe conexión + SELECT sin copiar `Arranque.java` entero. | Ya no dependes de que el proyecto anterior esté abierto. |
| 15–50 | [B05: transacciones](Tema-3-JDBC-SQLite/BLOQUES.md#b05-transaccion-completa). Implementa `trasladar`. | Éxito confirma; UNIQUE en el segundo cambio deshace el primero; cero filas también se trata como fallo. |
| 50–70 | [B07: JOIN y agregación](Tema-3-JDBC-SQLite/BLOQUES.md#b07-uniones-y-agregacion). Implementa `resumen`. | `DAM:2`, `DAW:1`, `Vacío:0`. |
| 70–85 | [B04: metadatos](Tema-3-JDBC-SQLite/BLOQUES.md#b04-metadatos). Implementa `tablas` y `columnas`. | Distingues estructura del motor y columnas del SELECT. |
| 85–100 | [B06: savepoint](Tema-3-JDBC-SQLite/BLOQUES.md#b06-punto-de-retorno). Implementa `cambioParcial`. | Nombre nuevo, edad original. |
| 100–120 | [B08–B09](Tema-3-JDBC-SQLite/BLOQUES.md#b08-cursores-h2-no-sqlite): lee el patrón, completa `salarios` y `doble` con apuntes. | Entiendes `updateRow`, IN/OUT y por qué no funcionan igual en SQLite. Si falta tiempo, ejecuta la referencia y explica el recorrido. |

Para los bloques finales: `mvn -o test '-Dgroups=b08 | b09'`.
Los bloques 08–09 son importantes para la cobertura teórica, pero no deben desplazar
el dominio de SQLite, SQL parametrizado y rollback.

## Miércoles por la mañana · 120 minutos · ensayo con apuntes y sin Internet

| Minutos | Trabajo concreto | Resultado que debes obtener |
|---|---|---|
| 0–20 | Desconecta la red tú mismo. Repite B00 por Maven offline **y** por `javac/java`. No instales nada nuevo. | Proyecto arrancando, JAR disponibles, ruta de base identificada. |
| 20–80 | [B10: simulacro de matrículas](Tema-3-JDBC-SQLite/BLOQUES.md#b10-simulacro-integrador). Completa `Simulacro.java`, con Obsidian disponible. | Alta atómica, cupos, duplicado que no consume plaza, JOIN seguro. |
| 80–100 | Ejecuta `mvn -o test '-Dgroups=b00 | b01 | b02 | b03 | b04 | b05 | b06 | b07 | b10'`. Corrige el primer error real. | Tu código pasa los casos principales; no solo la solución de referencia. |
| 100–110 | Cambia el dominio en voz alta: curso→producto, plazas→stock, matrícula→venta. Localiza qué SQL, columnas y parámetros cambian. | Reconoces el patrón aunque el examen no use alumnos. |
| 110–120 | Repasa [fundamentos](Tema-3-JDBC-SQLite/FUNDAMENTOS.md) y los errores de configuración. Abre las notas que necesitarías consultar. | Distingues tipos de driver, métodos execute, ACID, NULL y límites del motor. |

## Qué queda fuera de estas seis horas

- Terminar todos los ejercicios originales de World y Chinook: se conserva su
  [mapa de equivalencias](Tema-3-JDBC-SQLite/COBERTURA.md). Aquí se practican sus patrones
  con datos pequeños y autocontenidos; no se presume haber resuelto los ocho originales.
- Variantes avanzadas de cursores, procedimientos de servidor y concurrencia entre
  clientes. Consulta el temario para su sintaxis específica.
- [Tema 4: Hibernate y HQL](Tema-4-Hibernate-HQL/README.md): ruta independiente, sin
  prometer dominar JDBC y todo ORM en seis horas.

**Prioridad si vas justo:** configuración → SELECT con parámetros → INSERT/UPDATE/DELETE
→ transacción con rollback → simulacro. Luego JOIN, metadatos y savepoint. Los apuntes
pueden recordarte una firma; no pueden sustituir entender qué debe confirmarse junto.
