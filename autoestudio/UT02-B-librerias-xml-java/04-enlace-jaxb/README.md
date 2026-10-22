# 04 · JAXB: objetos, anotaciones y colecciones

**Sesión 31/10 · 2 horas.** Fuente: UT02-B §5–5.4. Se mantiene JAXB 2
`javax.xml.bind`, con dependencias Maven; no mezcles con `jakarta`.
[Teoría y pistas](teoria/enlace-jaxb.md).

## Orden de trabajo

En `src/main/java/es/dam/accesodatos/autoestudio/xml/jaxb/`:

1. Completa `EnlaceJaxb.aXml` y `desdeXml` con el `Libro` ya anotado.
2. **Practica las anotaciones tú** en `BibliotecaLibros` y `FichaLibro`, como
   las clases Libreria/Libro del tema. No necesitas cambiar el modelo anterior.
3. Completa `TallerJaxb.guardar` y `leer`: archivo → objetos → modificar → otro archivo.

Forma XML que debe producir el nuevo modelo:

```xml
<libreria>
    <ListaLibro>
        <Libro codigo="L1"><titulo>Sol &amp; Luna</titulo><anio>2008</anio></Libro>
        <Libro codigo="L2"><titulo>XML</titulo><anio>2020</anio></Libro>
    </ListaLibro>
    <nombre>Biblioteca</nombre>
    <lugar>Barrio</lugar>
</libreria>
```

El orden de los campos de cada Libro debe ser titulo/anio. No se exige un orden
de los campos de la librería, ni espacios o declaración exactos. Cambiar un libro
recuperado no cambia el archivo original hasta guardar expresamente.

## Tests básicos y extras

- `EnlaceJaxbTest`: operaciones base, preservación de datos y errores de entrada.
- `TallerJaxbTest`: anotaciones, contenedor, elementos, atributo, orden, archivos
  y modificación. Completa primero las anotaciones: son prerrequisito del binding.
- Los otros diez retos antiguos se conservan como **ampliaciones opcionales**.
  No se pide compactar XML, quitar cabecera, convertir a Map ni validar ISBN.

```bash
mvn -pl UT02-B-librerias-xml-java/04-enlace-jaxb test
```

Solo si quieres incluir extras:

```bash
mvn -pl UT02-B-librerias-xml-java/04-enlace-jaxb -Pampliaciones test
```

Comandos desde `autoestudio/`. Variante: añadir un tercer libro con caracteres
especiales, guardar y recuperar. Explica `Wrapper` frente a `Element`, y por qué
el atributo codigo no se incluye en `propOrder`.
