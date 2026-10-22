# Sílabo · Autoestudio de Acceso a Datos

## Criterio

Java 17, Maven y JUnit 5. Fuente principal: los Markdown docentes UT02-A/B;
Obsidian aporta explicaciones y prácticas añadidas identificadas como tales.
La Masterclass deja de fijar una cuota de retos o requisitos ajenos al examen.

**No entra NIO**, confirmado por el usuario el 21/10/2026. `StandardCharsets.UTF_8`
es una constante para decodificar texto con `java.io`, no práctica de `Path`/`Files`.
No se añaden REST, Jackson, CSV general, programación de canales ni selectores.

## Ruta básica

Las carpetas anteriores mantienen sus nombres para conservar tus rutas y trabajo.
El orden de estudio lo determina [PLAN_ESTUDIO.md](PLAN_ESTUDIO.md), no la numeración.

| Unidad | Proyecto | Qué resolver | Prioridad |
|---|---|---|---|
| A | `01-rutas-file` | Constructores y componentes de `File` | Repaso breve |
| A | `02-inspeccion-file` | Estado, permisos, tamaño y fecha | Repaso breve |
| A | `03-operaciones-file` | Crear, renombrar y borrar | Repaso breve |
| A | `04-listado-directorio` | Nombres frente a objetos; vacío frente a `null` | Repaso breve |
| A | `05-filtrado-filename-filter` | `endsWith` como en el tema, sin normalización obligatoria | Repaso breve |
| A | `07-flujos-bytes` | `FlujosBytes`: copiar con búfer y anexar un rango | 22/10 |
| A | `08-flujos-caracteres` | `FlujosCaracteres`: líneas UTF-8, sobrescritura y append | 25/10 |
| A | `09-acceso-secuencial` | `AccesoSecuencial`: buscar y eliminar por reescritura | 25/10 |
| A | `10-serializacion-objetos` | Completar `Alumno` y `SerializacionObjetos` | 30/10 |
| A | `11-acceso-aleatorio` | `AccesoAleatorio`: leer/actualizar registros con `seek` | 25 y 30/10 |
| B | `01-parseo-dom` | Tu `ParseoDom` y el nuevo `TallerDom` con archivos | 24/10 |
| B | `02-parseo-sax` | Tu `ParseoSax` y `ManejadorAlumnos` con fragmentación | 26/10 |
| B | `03-parseo-stax` | Lectores de cursor e iteradores de `ParseoStax` | 27/10 |
| B | `04-enlace-jaxb` | `aXml`/`desdeXml`, modelos de colección y `TallerJaxb` | 31/10 |

## Ampliaciones separadas

No bloquean `mvn test`. Se incluyen con `-Pampliaciones`:

- `06-catalogador-archivos`: integración opcional de `File`, ordenación e inmutabilidad.
  No es el ejercicio final de toda UT02-A.
- JAXB: los diez retos antiguos distintos de `aXml`/`desdeXml` (compactar,
  fragmentos, ISO-8859-1, mapas, ISBN, heurísticas y utilidades de dominio).
- StAX: escritores de cursor/eventos. El temario menciona la escritura, pero
  desarrolla sus ejemplos de lectura; no se elimina el concepto de la teoría.
- El helper privado de seguridad ya escrito en `ParseoDom` se conserva, pero no
  se exige usarlo. No se incorpora un test que lo convierta en requisito de examen.

## Progreso personal

Preparar o compilar material no acredita dominio. Ninguna casilla se marca por
esta adaptación. Considera dominado un bloque cuando resuelvas también una variante.

- [ ] `File`, operaciones, listado y filtrado.
- [ ] Bytes: fin de lectura, rango y última lectura parcial.
- [ ] Caracteres: codificación, líneas vacías y append.
- [ ] Acceso secuencial: búsqueda y reescritura.
- [ ] Objetos: serialización, casting y campo temporal.
- [ ] Acceso aleatorio: modos, posición y actualización.
- [ ] DOM: consultar, navegar, crear, modificar, borrar y guardar.
- [ ] SAX: eventos, atributos y acumulación fragmentada.
- [ ] StAX: cursor e iteradores; explicar el avance.
- [ ] JAXB: anotaciones, colección y ciclo de archivo.
- [ ] Simulacro mixto y corrección de errores.
