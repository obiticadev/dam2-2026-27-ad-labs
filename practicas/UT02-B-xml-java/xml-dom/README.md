# `xml-dom`

Ejemplos del modelo de árbol DOM: parsear XML, recorrer elementos, leer atributos y generar XML desde un `Document`.

```bash
mvn package
mvn exec:java -Dexec.mainClass="es.dam.accesodatos.xml.dom.Ejemplo2"
```

- `XmlCtrlDom`: parsea `cd_catalog.xml` y transforma el DOM a un fichero XML.
- `PasosDOM`: muestra la estructura de nodos, atributos y elementos de `clase.xml`.
- `Ejemplo2`: recorre e imprime todos los alumnos de `clase.xml`.
- `PasosCrearXML`: construye un DOM mínimo y lo escribe en XML.
- `WriteXMLFile`: construye el ejemplo de la compañía con empleado, atributo y subelementos.

Los XML de entrada están en `src/main/resources`; los generados se crean en `target/demo-data`.
