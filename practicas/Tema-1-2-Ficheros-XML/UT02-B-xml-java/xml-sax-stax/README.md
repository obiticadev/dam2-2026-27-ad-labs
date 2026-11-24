# `xml-sax-stax`

Ejemplos de análisis secuencial XML. SAX notifica eventos al manejador; StAX deja que el programa avance con cursor o consuma eventos.

```bash
mvn package
mvn exec:java -Dexec.mainClass="es.dam.accesodatos.xml.streaming.SAXParserDemo"
```

- `Ejemplo1`: análisis SAX con un `DefaultHandler` vacío.
- `SAXParserDemo`: captura eventos e imprime atributos y campos de los alumnos de `input.xml`.
- `SAXUtf8Demo`: lectura SAX de texto UTF-8 usando `InputStreamReader` e `InputSource`.
- `ListaLibrosStAX`: cursor `XMLStreamReader` para extraer títulos e idioma.
- `EventReader`: iterador `XMLEventReader` para mostrar los atributos de cada título y su texto.

Los ficheros XML de entrada (`menu.xml`, `input.xml`, `file-utf.xml` y `books.xml`) están en `src/main/resources`.
