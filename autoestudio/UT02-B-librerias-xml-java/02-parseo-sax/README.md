# 02 · Parseo XML con SAX

## Objetivo

Adaptar `b16_xml/Ej145DomSaxParsing.textosConSax` para recorrer XML con eventos,
acumular textos y mantener el orden del documento. Se añaden retos de lectura
de atributos y conteo de etiquetas por eventos.

## Material de este mini proyecto

- [Teoría específica de SAX](./teoria/parseo-sax.md)
- Enunciados, contratos y TODO: [ParseoSax.java](./src/main/java/es/dam/accesodatos/autoestudio/xml/sax/ParseoSax.java)
- Tests derivados y ampliados: [ParseoSaxTest.java](./src/test/java/es/dam/accesodatos/autoestudio/xml/sax/ParseoSaxTest.java)

## Mapa de tests

| Test | Qué aclara |
|---|---|
| `extraeTextosEnOrdenYDecodificaEntidadesXml` | Orden, texto y entidades XML |
| `recuperaAtributosYUsaCadenaVaciaCuandoFaltan` | Atributos en callbacks de apertura |
| `cuentaEtiquetasEnUnaPasada` | Conteo mediante eventos SAX |
| `rechazaArgumentosNulosOVacios` | Validación de entrada |
| `informaXmlMalFormadoComoFallo` | Propagación de errores de parseo |

Correspondencia: UT02-B, procesamiento secuencial XML con SAX. Los ejemplos de
laboratorio relacionados están en `practicas/UT02-B-xml-java/xml-sax-stax`.

## Ejecutar

Desde la raíz `autoestudio/`:

```bash
mvn -pl UT02-B-librerias-xml-java/02-parseo-sax test
```

En el IDE, ejecuta `ParseoSax.main`. La
[guía general](../../README_GUIA_TERMINAL.md) explica cómo ejecutar un test
concreto y localizar el informe de Surefire.
