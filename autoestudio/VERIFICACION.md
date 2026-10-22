# Verificación de la adaptación · 21/10/2026

## Material comprobado

- Reactor Maven completo: **`test-compile` correcto**, con `--release 17`, incluidos
  los nuevos ejercicios y tests. Los esqueletos compilan sin resolverlos.
- **38 tests nuevos o ajustados pasan** contra implementaciones de referencia
  temporales: bytes, caracteres, secuencial, objetos, aleatorio, taller DOM,
  manejador SAX, taller JAXB, listado y filtrado. Las soluciones solo están en
  `/tmp/opencode/ad-reference`, no en tus ejercicios ni enlazadas como tarea.
- Enlaces Markdown locales revisados: 91, sin destinos inexistentes.
- POM bien formados y sin errores de espacios de `git diff --check`.
- Comparación del código de `ParseoDom` y `ParseoSax` con el estado previo,
  excluyendo comentarios y espacios: **implementaciones personales conservadas**.
- Filtro de ampliaciones comprobado: StAX ejecuta 6 tests básicos frente a 9
  con extras; EnlaceJaxb ejecuta 3 frente a 13. Los 3 del taller JAXB son básicos.
  El catalogador no ejecuta tests en la ruta básica y no falla por esa exclusión.

No estaba instalado `mvn` en el PATH. Para verificar se utilizó Maven 3.9.11
descargado temporalmente en `/tmp/opencode`, comprobando su SHA-512 publicado.
No se instaló Maven en el sistema ni se cambiaron tus ajustes. Los comandos
abreviados de las guías requieren Maven disponible o el entorno Maven del IDE.

## Estado real de tus ejercicios

La ejecución `mvn -fn test` recorre **81 tests básicos**. No están todos verdes:
los esqueletos nuevos están pendientes y los intentos anteriores tienen partes
sin terminar. Eso no se presenta como una suite aprobada ni como progreso personal.
`-fn` ignora el código de salida de los fallos; hay que leer el informe.

En tu DOM existente pasan 5 de sus 7 tests básicos. Pendientes observados:

- `obtenerAtributo`: falta cumplir el contrato de argumento nulo con
  `IllegalArgumentException`.
- `contarElementos`: el `catch` imprime el error y devuelve 0; debe comunicar
  el fallo, no confundir XML mal formado con cero coincidencias.

No he resuelto esos métodos por ti. SAX mantiene sus callbacks pendientes.
En JAXB, el error por falta de `@XmlRootElement` en `BibliotecaLibros` es
esperado hasta que completes el ejercicio de anotaciones; no es un problema de
instalación de JAXB. Completa las anotaciones antes del ciclo de archivos.

## Límites

La comprobación con referencias confirma que esos 38 tests tienen soluciones
coherentes; no prueba que cualquier solución que los pase use la técnica pedida.
Además del resultado, comprueba el bucle, la API y la explicación. No se han
verificado ni modificado las entregas, Obsidian o los eventos del calendario.
NIO queda excluido por tu confirmación, no por una deducción de los tests.
