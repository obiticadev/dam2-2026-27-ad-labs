# 04 · Enlace de datos con JAXB

## Objetivo

Adaptar completo el ejercicio `b16_xml/Ej143JaxbBinding`: anotar un POJO,
serializarlo a XML (`marshal`), reconstruirlo (`unmarshal`) y conservar sus
retos de fragmento, codificación, mapeo y clon por round-trip. Se usa JAXB 2
(`javax.xml.bind`) para coincidir con el temario y el laboratorio DAM2.

## Material de este mini proyecto

- [Teoría específica de JAXB](./teoria/enlace-jaxb.md)
- Ejercicio y TODO: [EnlaceJaxb.java](./src/main/java/es/dam/accesodatos/autoestudio/xml/jaxb/EnlaceJaxb.java)
- Modelo anotado: [Libro.java](./src/main/java/es/dam/accesodatos/autoestudio/xml/jaxb/Libro.java)
- Tests derivados de la Masterclass: [EnlaceJaxbTest.java](./src/test/java/es/dam/accesodatos/autoestudio/xml/jaxb/EnlaceJaxbTest.java)

La clase y sus tests portan `aXml`, `desdeXml` y los diez retos extra de
`b16_xml/Ej143JaxbBinding`. El test de compactación usa el resultado coherente
con su contrato y elimina la aserción contradictoria del test original.

## Mapa de tests

| Test | Funcionalidad que comprueba |
|---|---|
| `serializaConRaizAtributoYElementos` | Anotaciones y salida XML |
| `roundTripPreservaTodosLosDatosDelModelo` | Objeto → XML → objeto |
| `rechazaEntradasInvalidasYXmlMalFormado` | Argumentos y XML mal formado |
| `validaFormatoDeIsbn` | Reto extra de validación |
| `compactaTextoXmlSinDejarEspaciosEnLosExtremos` | Reto extra de tratamiento de texto |
| `extraeAnioUsandoElUnmarshalJaxb` | Reto extra que reutiliza unmarshal |
| `serializaFragmentoSinDeclaracionXml` | Propiedad `JAXB_FRAGMENT` |
| `convierteElModeloEnUnMapaConTiposConservados` | Reto extra de mapeo |
| `creaUnLibroPorDefecto` | Reto extra de construcción |
| `detectaLaRaizLibroConUnaHeuristicaTextual` | Reto extra de detección léxica |
| `declaraCodificacionIso8859` | Propiedad `JAXB_ENCODING` |
| `comparaPublicacionConCorteEstricto` | Reto extra de regla de dominio |
| `clonaMedianteRoundTripSinCompartirInstancia` | Round-trip con instancia distinta |

## Ejecutar

Desde la raíz `autoestudio/`:

```bash
mvn -pl UT02-B-librerias-xml-java/04-enlace-jaxb test
```

JAXB 2 está incluido en este proyecto como dependencia. En el IDE, ejecuta
`EnlaceJaxb.main`; con TODO pendientes mostrará un mensaje indicando qué falta.
