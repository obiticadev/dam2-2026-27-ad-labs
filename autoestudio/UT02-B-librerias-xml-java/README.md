# UT02-B · Librerías de tratamiento de XML en Java

Mini proyectos para practicar los analizadores y el enlace XML que aparecen en
el temario oficial. Cada proyecto contiene teoría específica, enunciado en los
Javadocs, tests y un `main` ejecutable.

## Ruta de proyectos

| Orden | Estado | Proyecto | Concepto y procedencia |
|---|---|---|---|
| 01 | Esqueleto disponible | [`01-parseo-dom`](./01-parseo-dom/README.md) | DOM, adaptación de `b16_xml/Ej145` y escritura/`Transformer` de laboratorios DAM2 |
| 02 | Esqueleto disponible | [`02-parseo-sax`](./02-parseo-sax/README.md) | SAX, adaptación de `b16_xml/Ej145` |
| 03 | Esqueleto disponible | [`03-parseo-stax`](./03-parseo-stax/README.md) | Cursor e iterador; ejemplos DAM2 y escritura StAX añadida |
| 04 | Esqueleto disponible | [`04-enlace-jaxb`](./04-enlace-jaxb/README.md) | Adaptación completa de `b16_xml/Ej143` a JAXB 2 / `javax` |

## Compilar y probar

Desde la raíz `autoestudio/`:

```bash
mvn -pl UT02-B-librerias-xml-java/01-parseo-dom test
mvn -pl UT02-B-librerias-xml-java/02-parseo-sax test
mvn -pl UT02-B-librerias-xml-java/03-parseo-stax test
mvn -pl UT02-B-librerias-xml-java/04-enlace-jaxb test
```

Las APIs DOM, SAX y StAX vienen con Java 17. JAXB 2 necesita dependencias
externas porque ya no forma parte del JDK desde Java 11; `04-enlace-jaxb` las
incluye en su propio `pom.xml`.
