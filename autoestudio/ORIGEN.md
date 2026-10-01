# Correspondencia con materiales de referencia

## Temario oficial de Acceso a Datos

Los proyectos de la unidad UT02-A se corresponden principalmente con
`01-acceso-datos/temario/UT02-A-manejo-de-ficheros.md`:

| Proyecto | Apartado de referencia |
|---|---|
| `01-rutas-file` | 2.4 · Clase `File` |
| `02-inspeccion-file` | 2.4 · Métodos de `File` |
| `03-operaciones-file` | 2.5 · Creación y eliminación de ficheros y directorios |
| `04-listado-directorio` | 2.4 · Listado de directorios |
| `05-filtrado-filename-filter` | 2.6 · Interfaz `FilenameFilter` |
| `06-catalogador-archivos` | Integración de los apartados anteriores |

## Material de laboratorio relacionado

- `dam2-2026-27-ad-labs/tareas/Ejercicio1-Clase-File`: inspección, listado,
  creación y filtro por extensión.
- `dam2-2026-27-ad-labs/practicas/UT02-A-manejo-de-ficheros/java-io-flujos`:
  `FileOperationsDemo` y `FilenameFilterDemo`.

## UT02-B · XML

| Proyecto | Temario/laboratorio | Masterclass |
|---|---|---|
| `01-parseo-dom` | UT02-B, DOM; `practicas/UT02-B-xml-java/xml-dom` para creación y `Transformer` | Adaptación de `b16_xml/Ej145DomSaxParsing.contarConDom` y retos DOM |
| `02-parseo-sax` | UT02-B, SAX; `practicas/UT02-B-xml-java/xml-sax-stax` | Adaptación de `b16_xml/Ej145DomSaxParsing.textosConSax` |
| `03-parseo-stax` | UT02-B y `practicas/UT02-B-xml-java/xml-sax-stax` (`ListaLibrosStAX`, `EventReader`) | No hay ejercicio StAX en `b16_xml`; se amplían los dos estilos de lectura con sus writers usando la skill de Masterclass |
| `04-enlace-jaxb` | UT02-B; `practicas/UT02-B-xml-java/xml-jaxb` usa JAXB 2 / `javax.xml.bind` | Port completo de `b16_xml/Ej143JaxbBinding` y sus diez retos, sustituyendo Jakarta por JAXB 2 del temario |

`b16_xml/Ej144JacksonXml`, `Ej146XmlEndpoint`, CSV y repositorios de ficheros no
se incluyen: no son conceptos de las unidades señaladas.

## Masterclass de autoaprendizaje

La cobertura de ficheros se adapta de `08_IOFicherosMasterclass`; la cobertura
XML reutiliza ejercicios y teoría de `11_APIRESTMasterclass/b16_xml`. Cuando esa
Masterclass omite un punto del temario (StAX y escritura DOM), se completa con el
material DAM2 y la skill `MASTERCLASS_CREATOR_SKILL.md`. NIO, `Path` y `Files` se
excluyen de este alcance.
