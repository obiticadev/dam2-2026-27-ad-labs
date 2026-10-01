# Guía de uso · Autoestudio de Acceso a Datos

## Requisitos

- JDK 17 o superior.
- Maven 3.8 o superior.

## Ruta de estudio recomendada

Para cada mini proyecto:

1. Lee `teoria/<concepto>.md`.
2. Lee el Javadoc de la clase y el contrato de cada método.
3. Ejecuta los tests del proyecto antes de implementar para conocer el estado de
   partida.
4. Implementa un método y vuelve a ejecutar el test que lo cubre.
5. Cuando el proyecto esté verde, ejecuta la suite de la unidad.

Los métodos con `TODO` están deliberadamente incompletos. **Al inicio es normal
que fallen sus tests**: el primer resultado rojo te indica qué comportamiento
debes resolver.

## Compilar

Desde esta carpeta, `autoestudio/`:

```bash
mvn compile
mvn test-compile
```

`test-compile` compila los tests, pero no los ejecuta.

## Ejecutar tests

Suite completa:

```bash
mvn test
```

Un mini proyecto:

```bash
mvn -pl UT02-A-manejo-de-ficheros/01-rutas-file test
mvn -pl UT02-B-librerias-xml-java/01-parseo-dom test
mvn -pl UT02-B-librerias-xml-java/02-parseo-sax test
mvn -pl UT02-B-librerias-xml-java/03-parseo-stax test
mvn -pl UT02-B-librerias-xml-java/04-enlace-jaxb test
```

Un test concreto (ejemplo):

```bash
mvn -pl UT02-A-manejo-de-ficheros/01-rutas-file -Dtest=RutasFileTest#representaUnaRutaSinCrearElFichero test
```

El Boss final tiene dependencias en los demás mini proyectos; usa `-am` para
incluirlos en el reactor Maven:

```bash
mvn -pl UT02-A-manejo-de-ficheros/06-catalogador-archivos -am test
```

## Cómo leer el resultado

- **`BUILD SUCCESS`**: todos los tests incluidos en esa ejecución pasaron.
- **`Failures`**: el método devolvió un resultado distinto al contrato; compara
  el valor esperado y el obtenido que muestra el test.
- **`Errors`**: se produjo una excepción no esperada. La traza apunta al método
  que la lanzó y ayuda a distinguirla de una aserción fallida.
- **Error de compilación**: el ejercicio o los tests no compilan; todavía no se
  ha evaluado el comportamiento.

Surefire guarda informes de texto y XML en
`<mini-proyecto>/target/surefire-reports/`. El nombre del test indica el caso y
el método o concepto que debes revisar. Cada clase de test agrupa escenarios de
un concepto.

## Ejecutar el `main`

Cada clase de ejercicio tiene un `main` de demostración que puedes ejecutar
desde el IDE. Con TODO pendientes, imprimirá los valores provisionales del
esqueleto; **los tests son los que especifican el resultado requerido**.

## Seguimiento

Actualiza las casillas de [SYLLABUS.md](./SYLLABUS.md) al completar los
ejercicios. Marca una unidad como completa cuando hayas implementado sus TODO y
pasado sus tests.
