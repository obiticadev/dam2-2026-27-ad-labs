# UT02-B · Librerías de tratamiento de XML en Java

Ejemplos de DOM, SAX, StAX y JAXB basados en el temario. DOM, SAX y StAX usan las APIs incluidas en el JDK; JAXB está separado porque desde Java 11 necesita dependencias externas.

| Proyecto | Contenidos principales |
| --- | --- |
| [`xml-dom`](./xml-dom/README.md) | Lectura y escritura XML con DOM y `Transformer` |
| [`xml-sax-stax`](./xml-sax-stax/README.md) | Parseo por eventos con SAX y lectura por cursor/iterador con StAX |
| [`xml-jaxb`](./xml-jaxb/README.md) | Anotaciones JAXB, `marshal` y `unmarshal` |

## Compilar todos los proyectos

Desde esta carpeta:

```bash
mvn package
```

Para compilar un módulo concreto: `mvn -pl xml-dom package`. Los ejemplos se pueden importar en IntelliJ IDEA o Eclipse como proyectos Maven. Los ficheros XML de entrada están incluidos como recursos y los generados se guardan en `target/demo-data`.
