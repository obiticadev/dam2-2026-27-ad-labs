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

## Integración local/remoto · 22/10/2026

Esta sección actualiza el estado tras integrar los tres commits de `origin/main`;
las cifras y observaciones anteriores corresponden a la adaptación del 21/10.

- Trabajo local original conservado en la rama
  `backup/pre-integracion-20261022-165745` y en el respaldo externo
  `../.git-integration-backups/20261022-165745/` desde la carpeta del repositorio.
- El 07 mantiene la clase y el paquete remotos `autoestudio.flujos.bytes`, incluido
  el avance personal de programación, sin sustituirlo por el esqueleto local.
  Se combinan teoría y pistas y se añaden comprobaciones de bytes 0/255,
  capacidades 1/3/4 y propagación de errores de apertura del destino.
- Se mantiene el contrato remoto: un origen inexistente es un argumento inválido
  (`IllegalArgumentException`); un fallo al abrir el destino propaga `IOException`.
  No se conservan dos clases ni dos contratos contradictorios para el mismo ejercicio.
- `ParseoSax` conserva el código funcional remoto y los ajustes locales de
  comentarios; `textosDe` ya contiene el avance remoto, pero otros TODO siguen pendientes.
- Los módulos 08–11, el plan de estudio y los demás cambios locales se conservan.
- Compilación de producción y tests correcta en los 18 proyectos Maven, con
  `--release 17`. Maven 3.9.11 temporal, verificado mediante SHA-512, sin instalación
  ni cambios de configuración en el sistema.
- Suite integrada ejecutada en una copia temporal sin clases compiladas antiguas:
  **92 tests: 12 correctos, 73 fallos y 7 errores**. No es una suite aprobada;
  siguen existiendo ejercicios y métodos incompletos. No se han resuelto por la integración.
- Comparados los 18 tests remotos existentes de 07/SAX contra una copia limpia de
  `origin/main`: todos conservan exactamente su resultado previo. El nuevo test
  de error de apertura pasa; el de bytes binarios sigue pendiente de completar `copiar`.

El 07 sigue siendo un ejercicio en curso: usa solo archivos temporales o copias
prescindibles. La protección contra copiar un fichero sobre sí mismo aún falla
en el intento remoto conservado; la integración no corrige ni completa ese intento.
