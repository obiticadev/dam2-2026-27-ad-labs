# Sílabo · Autoestudio de Acceso a Datos

## Variables del proyecto

| Variable | Decisión |
|---|---|
| Lenguaje/tecnología | Java 17, `java.io` y XML de Java |
| Unidades DAM2 | UT02-A · Manejo de ficheros; UT02-B · Librerías XML |
| Conceptos | `File`, `FilenameFilter`, DOM, SAX, StAX y JAXB |
| Ejercicios | Diez mini proyectos por concepto |
| Gestor de paquetes | Maven |
| Punto de ejecución | Método `main` de cada ejercicio desde el IDE |
| Framework de tests | JUnit 5 |
| Fuera de alcance | NIO (`Path`, `Files`, canales y búferes), Jackson XML, APIs REST y CSV |

## Arquitectura

Cada mini proyecto contiene su propia guía, teoría limitada a ese concepto,
enunciado en los Javadocs, código con TODOs, método `main` y tests bajo
`src/test/java`. Las unidades se mantienen separadas.

```text
autoestudio/
├── README.md
├── README_GUIA_TERMINAL.md
├── SYLLABUS.md
├── pom.xml
├── UT02-A-manejo-de-ficheros/
│   ├── README.md
│   ├── pom.xml
│   ├── 01-rutas-file/
│   ├── 02-inspeccion-file/
│   ├── 03-operaciones-file/
│   ├── 04-listado-directorio/
│   ├── 05-filtrado-filename-filter/
│   └── 06-catalogador-archivos/
└── UT02-B-librerias-xml-java/
    ├── README.md
    ├── pom.xml
    ├── 01-parseo-dom/
    ├── 02-parseo-sax/
    ├── 03-parseo-stax/
    └── 04-enlace-jaxb/
```

## Ruta de ejercicios

| Unidad | Bloque | Proyecto | Archivo principal | Test principal | Procedencia y enfoque |
|---|---|---|---|---|---|
| UT02-A | 01 | `01-rutas-file` | `RutasFile.java` | `RutasFileTest.java` | `File` y rutas |
| UT02-A | 02 | `02-inspeccion-file` | `InspeccionFile.java` | `InspeccionFileTest.java` | Estado, permisos y metadatos |
| UT02-A | 03 | `03-operaciones-file` | `OperacionesFile.java` | `OperacionesFileTest.java` | Creación, renombrado y borrado |
| UT02-A | 04 | `04-listado-directorio` | `ListadoDirectorio.java` | `ListadoDirectorioTest.java` | `list()` y `listFiles()` |
| UT02-A | 05 | `05-filtrado-filename-filter` | `FiltradoFilenameFilter.java` | `FiltradoFilenameFilterTest.java` | `FilenameFilter` |
| UT02-A | 06 · Boss | `06-catalogador-archivos` | `CatalogadorArchivos.java` | `CatalogadorArchivosTest.java` | Integración de UT02-A |
| UT02-B | 01 | `01-parseo-dom` | `ParseoDom.java` | `ParseoDomTest.java` | Adaptación de `b16_xml/Ej145` y complemento para crear/transformar DOM |
| UT02-B | 02 | `02-parseo-sax` | `ParseoSax.java` | `ParseoSaxTest.java` | Adaptación de `b16_xml/Ej145` |
| UT02-B | 03 | `03-parseo-stax` | `ParseoStax.java` | `ParseoStaxTest.java` | Creado para cubrir cursor e iteradores no incluidos en `b16_xml` |
| UT02-B | 04 | `04-enlace-jaxb` | `EnlaceJaxb.java` | `EnlaceJaxbTest.java` | Adaptación de `b16_xml/Ej143` al JAXB `javax` del temario |

Los ejercicios conservan la forma de trabajo de la Masterclass: esqueletos
compilables, 4–7 TODOs por método, tests como especificación y teoría local con
diagramas Mermaid. Los tests quedan pendientes hasta implementar los retos.

## Progreso

- [ ] UT02-A · Bloque I: rutas e inspección (esqueletos preparados; faltan TODO y tests)
- [ ] UT02-A · Bloque II: operaciones y listados (esqueletos preparados; faltan TODO y tests)
- [ ] UT02-A · Bloque III: `FilenameFilter` (esqueletos preparados; faltan TODO y tests)
- [ ] UT02-A · Boss final: catalogador (esqueleto preparado; faltan TODO y tests)
- [ ] UT02-B · Bloque I: DOM y SAX (esqueletos preparados; pendiente implementar TODO y pasar tests)
- [ ] UT02-B · 03 StAX (cursor, iterador y sus writers; esqueletos preparados, faltan TODO y tests)
- [ ] UT02-B · 04 JAXB (ejercicio y diez retos portados; faltan TODO y tests)
