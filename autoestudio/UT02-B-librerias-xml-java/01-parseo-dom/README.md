# 01 · Parseo y creación XML con DOM

## Objetivo

Adaptar el núcleo DOM de `b16_xml/Ej145DomSaxParsing` —conteo de nodos,
atributos, textos y raíz— y completar la parte que ese ejercicio no cubre:
construir un árbol `Document` y transformarlo a XML.

**Sesión 24/10.** Conserva tu intento de `ParseoDom`; continúa después con
[TallerDom.java](src/main/java/es/dam/accesodatos/autoestudio/xml/dom/TallerDom.java),
[datos comunes](datos/alumnos.xml) y [teoría, trazas y pistas del taller](teoria/taller-dom.md).
El nuevo taller cubre cargar archivos, varios campos, hijos directos,
modificar, agregar, eliminar, guardar y reabrir. No exige código de seguridad.

## Material de este mini proyecto

- [Teoría específica de DOM](./teoria/parseo-dom.md)
- Enunciados, contratos y TODO: [ParseoDom.java](./src/main/java/es/dam/accesodatos/autoestudio/xml/dom/ParseoDom.java)
- Tests derivados y ampliados: [ParseoDomTest.java](./src/test/java/es/dam/accesodatos/autoestudio/xml/dom/ParseoDomTest.java)

## Mapa de tests

| Test | Qué aclara |
|---|---|
| `cuentaElementosPorNombre` | Consultas DOM por etiqueta |
| `obtieneElAtributoDelPrimerElementoCoincidente` | Atributo del primer resultado y casos ausentes |
| `obtieneTextoDelPrimerElementoEnOrdenDocumental` | Texto del primer nodo coincidente |
| `compruebaRaizYCuentaTodosLosNodosElemento` | Elemento raíz y conteo total |
| `creaUnDocumentoDomYTransformaElTextoEscapandoCaracteres` | Crear árbol y serializar con `Transformer` |
| `rechazaArgumentosNulosOVacios` | Validación de contratos |
| `informaXmlMalFormadoComoFallo` | Error de sintaxis, sin exigir rechazo de DOCTYPE |

`TallerDomTest` prueba cada operación por separado con entradas preparadas por
Java, sin depender de tus otros métodos. Compara el XML reabierto, no su
indentación ni una cabecera exacta. Soluciona primero el núcleo cargar/nombres/
actualizar/guardar; deja agregar/eliminar y navegación para la variante.

Correspondencia: UT02-B, apartados de DOM, estructura `Document` y escritura con
`Transformer`. La creación y transformación se complementan con
`practicas/UT02-B-xml-java/xml-dom`.

## Ejecutar

Desde la raíz `autoestudio/`:

```bash
mvn -pl UT02-B-librerias-xml-java/01-parseo-dom test
```

En el IDE, ejecuta `ParseoDom.main`. La
[guía general](../../README_GUIA_TERMINAL.md) explica cómo ejecutar un test
concreto y localizar el informe de Surefire.
