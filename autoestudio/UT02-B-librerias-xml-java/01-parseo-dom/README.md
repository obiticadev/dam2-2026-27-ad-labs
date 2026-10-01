# 01 · Parseo y creación XML con DOM

## Objetivo

Adaptar el núcleo DOM de `b16_xml/Ej145DomSaxParsing` —conteo de nodos,
atributos, textos y raíz— y completar la parte que ese ejercicio no cubre:
construir un árbol `Document` y transformarlo a XML.

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
| `rechazaXmlMalFormadoYDeclaracionesDoctype` | Errores de parseo y protección de DOCTYPE |

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
