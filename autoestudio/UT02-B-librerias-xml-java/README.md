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
| 04 | Esqueleto disponible | [`04-enlace-jaxb`](./04-enlace-jaxb/README.md) | JAXB 2 / `javax`, anotaciones y colecciones del temario |

## Elegir API y distinguir conceptos

| API | Modelo | Control | Objetivo de la práctica |
|---|---|---|---|
| DOM | Árbol completo en memoria | Consultas y navegación | Leer, crear, modificar, borrar y guardar |
| SAX | Eventos, sin árbol | El parser llama al handler (push) | Extraer atributos/texto acumulando fragmentos |
| StAX | Cursor u objetos de eventos | Tú pides el siguiente (pull) | Recorrer y extraer datos con ambos lectores |
| JAXB | Objetos Java mapeados | Marshal/unmarshal | Anotar colección, guardar y reconstruir objetos |

XML representa datos textuales jerárquicos y portables; la declaración puede
indicar la codificación. No escribas un parser propio ni manipules marcas con regex.
El tema menciona también TrAX/XSLT (transformación basada en reglas) y XStream
(binding); reconocerlos no obliga a añadir proyectos con esas bibliotecas.

La escritura StAX se conserva guiada como ampliación. DOM y JAXB sí requieren
guardar y volver a leer en la ruta básica. Ningún bloque básico pide compactar
XML, quitar su declaración ni configurar rechazo de DOCTYPE. Usa solo XML local conocido.

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
