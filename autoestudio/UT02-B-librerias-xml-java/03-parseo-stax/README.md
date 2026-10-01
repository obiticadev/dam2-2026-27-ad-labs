# 03 · Parseo XML con StAX

## Objetivo

Practicar las dos formas de consumir eventos StAX: el cursor `XMLStreamReader` y
el iterador `XMLEventReader`. También se plantean las APIs de escritura
`XMLStreamWriter` y `XMLEventWriter`, para completar el manejo de lectura y
escritura XML de la unidad.

## Material de este mini proyecto

- [Teoría específica de StAX](./teoria/parseo-stax.md)
- Enunciados, contratos y TODO: [ParseoStax.java](./src/main/java/es/dam/accesodatos/autoestudio/xml/stax/ParseoStax.java)
- Tests de aceptación: [ParseoStaxTest.java](./src/test/java/es/dam/accesodatos/autoestudio/xml/stax/ParseoStaxTest.java)

**Procedencia:** `11_APIRESTMasterclass/b16_xml` no contiene ejercicios StAX.
Los ejercicios de lectura parten de los ejemplos DAM2 `ListaLibrosStAX` y
`EventReader`, conservando la diferencia entre cursor y objetos `XMLEvent`.
Como los ejemplos fuente se limitan a lectura, la escritura con ambas APIs se
añade siguiendo el temario y la skill de creación de Masterclass.

## Mapa de tests

| Test | Qué aclara |
|---|---|
| `cursorExtraeTextoEnOrdenYDecodificaEntidades` | Cursor de lectura, texto y entidades |
| `cursorLeeAtributosDelEventoActual` | Atributos del elemento actual del cursor |
| `iteradorExtraeTextoEnOrden` | Lectura mediante objetos de eventos |
| `iteradorLeeAtributosDesdeStartElement` | Atributos desde `StartElement` |
| `iteradorCuentaElementosYDevuelveCeroSiNoHayCoincidencias` | Conteo y resultado sin coincidencias |
| `cursorEscribeXMLQuePuedeVolverALeer` | Writer de cursor y round-trip |
| `eventWriterEscribeXMLQuePuedeVolverALeer` | Writer de eventos y round-trip |
| Tests de entrada inválida | Errores de lectura y escritura |

## Ejecutar

Desde la raíz `autoestudio/`:

```bash
mvn -pl UT02-B-librerias-xml-java/03-parseo-stax test
```

En el IDE, ejecuta `ParseoStax.main`. Consulta la
[guía general](../../README_GUIA_TERMINAL.md) para ejecutar un test concreto y
leer el informe de Surefire.
