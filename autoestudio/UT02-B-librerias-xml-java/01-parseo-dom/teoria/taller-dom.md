# DOM · Del archivo al árbol y vuelta

Fuente docente UT02-B §2–2.5. El taller adapta las operaciones a alumnos y
mantiene tu implementación anterior de consultas. [Entrada](../datos/alumnos.xml).

```text
Document
└─ Element alumnos
   ├─ Text (salto + espacios)
   ├─ Element alumno [Attr id="1"]
   │  ├─ Text (indentación)
   │  ├─ Element nombre → Text "Ana"
   │  └─ Element nota   → Text "8.5"
   └─ Element alumno [Attr id="2"] → nombre "José", nota "6.0"
```

El árbol es esquemático: hay también textos de indentación entre y después de
los elementos. Un atributo no se recorre como un hijo corriente con `getChildNodes()`.

| Operación | Modelo que debes entender |
|---|---|
| `DocumentBuilderFactory` → `DocumentBuilder` | Factoría porque builder es abstracta |
| `parse(File)` / `newDocument()` | Leer árbol existente / crear árbol vacío |
| `getDocumentElement()` | Raíz, no el nodo Document completo |
| `getChildNodes()` | Hijos inmediatos de todos los tipos |
| `getElementsByTagName()` | Descendientes, no solo hijos inmediatos |
| `getTextContent()` | Texto del elemento y sus descendientes |
| `getNodeValue()` | Texto para Text/Attr; para Element devuelve null |
| `createElement()` + `appendChild()` | Crear no equivale a insertar |
| `setAttribute()` / `createAttribute()` | Crear atributo directamente / crear Attr y asociarlo |
| `createTextNode()` / `setTextContent()` | Crear un Text / sustituir el contenido textual |
| `removeChild()` | Modificar la relación padre-hijo en memoria |
| `DOMSource` → `Transformer` → `StreamResult` | Árbol → XML en archivo o consola |

### Tres cosas distintas, no tres formas de «limpiar XML»

- **`normalize()`** une Text adyacentes y elimina Text vacíos. No elimina textos
  que contienen espacios de indentación; no quita cabeceras ni DOCTYPE.
- **`OutputKeys.INDENT="yes"`** pide salida legible al Transformer; no cambia el
  modelo de datos. Sí aparece en el ejemplo docente.
- Rechazar **`<!DOCTYPE ...>`** sería configuración de seguridad. No es normalizar,
  ni borrar **`<?xml ...?>`**, que es la declaración XML. No se pide aquí.

### Traza de navegación (predice antes de mirar)

Con la entrada, ¿el primer hijo de `alumnos` es necesariamente `alumno`?
¿`getNextSibling()` del primer alumno devuelve siempre el segundo alumno?

<details><summary>Comprobación y pista de navegación</summary>

No: puede ser un nodo Text de indentación. `getFirstChild`, `getLastChild`,
`getNextSibling` y `getPreviousSibling` incluyen nodos de todos los tipos.
Comprueba `getNodeType()==Node.ELEMENT_NODE` para filtrar. El recorrido puede
terminar con null. La raíz tiene dos hijos Element y seis descendientes Element.

</details>
<details><summary>Pista 1 · Extraer</summary>

Recorre `doc.getElementsByTagName("alumno")` con `getLength()` e `item(i)`.
Haz el cast a Element y consulta su nombre por etiqueta; el id es atributo,
no texto de un elemento hijo.

</details>
<details><summary>Pista 2 · Modificar y crear</summary>

Localiza el id exacto; cambia la nota con `setTextContent`. Para añadir crea
alumno, sus campos y textos con el mismo Document, enlázalos y añade a la raíz.
Para borrar usa `alumno.getParentNode().removeChild(alumno)` y termina la búsqueda.

</details>
<details><summary>Pista 3 · Guardar y resultado</summary>

Crea `TransformerFactory.newInstance().newTransformer()`, configura INDENT y
llama a transform con `new DOMSource(doc)` y `new StreamResult(destino)`.
Usa un destino distinto del origen. Cambiar el árbol no modifica ningún archivo
hasta guardar; reabre la salida para comprobarlo. No escapes & ni < manualmente.

</details>
