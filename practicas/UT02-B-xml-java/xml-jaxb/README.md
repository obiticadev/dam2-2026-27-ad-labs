# `xml-jaxb`

Ejemplo de enlace de datos XML con JAXB 2: clases anotadas, serialización (`marshal`) de Java a XML y deserialización (`unmarshal`) de XML a Java.

El proyecto incluye las dependencias JAXB que ya no forman parte del JDK desde Java 11. Se conserva `javax.xml.bind`, tal como aparece en el temario.

```bash
mvn package
mvn exec:java -Dexec.mainClass="es.dam.accesodatos.xml.jaxb.JavaJAXB"
```

`Libreria` mapea el elemento raíz y su lista de libros; `Libro` define el orden de sus elementos con `@XmlType`. `JavaJAXB` crea dos libros, genera `target/demo-data/libreria-jaxb.xml`, vuelve a leerlo y muestra los objetos recuperados.
