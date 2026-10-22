# Teoría · Enlace de datos XML con JAXB 2

## Del POJO al XML y vuelta

JAXB enlaza una clase Java con una estructura XML. `Marshaller` convierte el
objeto a XML; `Unmarshaller` reconstruye el objeto. La prueba fundamental es el
round-trip: los datos relevantes deben conservarse en ambos sentidos.

```mermaid
flowchart LR
    J[Objeto Java anotado] -->|Marshaller / marshal| X[XML]
    X -->|Unmarshaller / unmarshal| R[Objeto reconstruido]
    J -. mismos datos .-> R
```

## Anotaciones usadas por el ejercicio

| Anotación | Función |
|---|---|
| `@XmlRootElement` | Asocia la clase con el elemento raíz |
| `@XmlAccessorType(XmlAccessType.FIELD)` | Indica que el binding observa los campos |
| `@XmlAttribute` | Representa un campo como atributo XML |
| `@XmlElement` | Representa un campo como elemento hijo |
| `@XmlType(propOrder = ...)` | Define el orden de los elementos al serializar |

El `Unmarshaller` necesita poder crear el objeto. Por eso el modelo `Libro`
mantiene un constructor público sin argumentos. Las anotaciones del ejercicio
usan acceso a campos, de modo que no es necesario crear setters para los
atributos del modelo.

La forma XML del modelo de este ejercicio es:

```xml
<libro isbn="978-84">
    <titulo>Clean Code</titulo>
    <anio>2008</anio>
</libro>
```

## Contexto y operaciones

`JAXBContext` se crea para las clases de modelo que participan en el binding. De
él se obtienen un `Marshaller` y un `Unmarshaller`. El destino/origen puede ser
un `Writer` o un stream, por lo que JAXB puede producir una cadena o trabajar
con un fichero.

```mermaid
sequenceDiagram
    participant A as Aplicación
    participant C as JAXBContext
    participant M as Marshaller
    participant U as Unmarshaller
    A->>C: contexto para Libro
    C-->>A: contexto
    A->>M: marshal(objeto, destino)
    M-->>A: XML
    A->>U: unmarshal(origen XML)
    U-->>A: Libro reconstruido
```

## Taller de anotaciones y colecciones (ruta de examen)

UT02-B §5.1–5.4 usa JavaBeans con anotaciones en setters. El `Libro` anterior
con FIELD se conserva como ejemplo; para practicar el enfoque docente completa
`BibliotecaLibros` y `FichaLibro`, sin mezclar ambos estilos en la misma clase.

| Modelo | Qué debes anotar |
|---|---|
| BibliotecaLibros | `@XmlRootElement(name="libreria")` |
| Lista de fichas | `@XmlElementWrapper(name="ListaLibro")` y `@XmlElement(name="Libro")` |
| FichaLibro | `@XmlType(propOrder={"titulo","anio"})` |
| codigo | `@XmlAttribute` |
| titulo, anio, nombre, lugar | `@XmlElement` |

`Wrapper` nombra el contenedor y `Element` cada elemento; no se sustituyen.
El constructor sin argumentos permite instanciar los modelos. `propOrder`
contiene nombres de propiedades Java de los elementos, no del atributo codigo.

```text
List<FichaLibro> → <ListaLibro> → <Libro codigo="L1"> ... </Libro> → ...
objeto raíz → JAXBContext → Marshaller → archivo
archivo → Unmarshaller → objeto raíz con colección → setters → guardar otro archivo
```

<details><summary>Pista 1 · Anotaciones</summary>

Usa imports de `javax.xml.bind.annotation`. Sitúa RootElement y XmlType en las
clases y las anotaciones de propiedades en sus setters, como en el tema. No
añadas FIELD si estás usando getters/setters anotados.

</details>
<details><summary>Pista 2 · Operaciones</summary>

Crea `JAXBContext.newInstance(BibliotecaLibros.class)`. Obtén Marshaller,
configura `JAXB_FORMATTED_OUTPUT=true` y llama a `marshal(biblioteca, destino)`.
Para leer, `createUnmarshaller().unmarshal(origen)` devuelve Object: haz casting.

</details>
<details><summary>Comprobación</summary>

Si no aparece ListaLibro, revisa Wrapper; si se llama libros en lugar de Libro,
revisa Element. Si cambias el año del objeto recuperado y guardas otro archivo,
el original sigue con 2008; la salida nueva contiene el año modificado.

</details>

### Reconocer el ciclo de enlace completo

El tema menciona escribir clases a mano o generarlas con `xjc` desde XSD,
compilar, unmarshal, posible validación de esquema, modificación y marshal.
Aquí se escriben las clases a mano. No se pide generar un esquema ni programar
validación XSD; deserializar no significa validar automáticamente todas las
reglas de negocio.

## Propiedades y límites de los retos extra (opcionales)

- `JAXB_FORMATTED_OUTPUT` hace más legible el XML.
- `JAXB_FRAGMENT` omite la declaración XML en la salida.
- `JAXB_ENCODING` configura la codificación declarada al serializar. En este
  proyecto se prueba la declaración de un `StringWriter`; si se necesitan bytes
  reales ISO-8859-1, el destino debe ser un `OutputStream` configurado como tal.
- `compactarXml` es un reto de texto basado en espacios en blanco, no un
  formateador XML seguro: un parser/transformer es la herramienta adecuada para
  modificar documentos XML.
- `esXmlDeLibro` conserva la utilidad heurística de la Masterclass; buscar una
  subcadena no valida que un documento sea XML bien formado.
- El clon usa marshal/unmarshal para obtener una instancia distinta con los
  mismos valores, no una serialización Java de objetos.

JAXB 2 usa `javax.xml.bind` y desde Java 11 requiere librerías externas. Este
proyecto declara API y runtime JAXB 2.3, igual que el laboratorio DAM2. La
Masterclass actual usa `jakarta.xml.bind`; aquí se adapta el paquete, no el
concepto.

## Qué comprueban los tests

La suite procede de `Ej143JaxbBindingTest`. Solo las operaciones base y el nuevo
taller forman parte de `mvn test`; los diez retos extra llevan tag `ampliacion`.
Se corrigió una aserción contradictoria del test original
de compactación: la especificación que coincide con la transformación de espacios
es `<libro> </libro>`.

## Comprueba tu comprensión

- ¿Qué diferencia hay entre atributo XML y elemento hijo en el modelo `Libro`?
- ¿Qué condición necesita una clase para que JAXB pueda reconstruirla?
- ¿Qué demuestra que un round-trip conserva los datos y crea una instancia nueva?
