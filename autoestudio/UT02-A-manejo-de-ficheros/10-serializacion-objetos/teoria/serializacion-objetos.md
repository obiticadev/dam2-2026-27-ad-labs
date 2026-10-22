# Objetos · Guardar estado, reconstruir estado

UT02-A §2.8–2.9 utiliza `Serializable`, `ObjectOutputStream` y `ObjectInputStream`.

```text
Alumno → writeObject → FileOutputStream → bytes en archivo
bytes → FileInputStream → readObject → Object → casting a Alumno
```

`Serializable` es una interfaz marcadora sin métodos. Los objetos miembro que
se guardan también deben ser serializables. `String` lo es; los primitivos no
plantean ese problema. La clase debe estar disponible al reconstruir el objeto.

El campo `transient` no participa en el estado serializado y recupera su valor
predeterminado: `null` si es una referencia, cero si es un número. No es cifrado.
Esta precisión procede del repaso añadido de Obsidian; no se presenta como una
nueva sección de la fuente docente.

No confundas este formato con `RandomAccessFile` ni con JAXB. Tampoco basta
`append=true` para reabrir sucesivamente un `ObjectOutputStream`: sus cabeceras
pueden romper la lectura. Aquí se guarda un solo objeto y se sobrescribe.

<details><summary>Pista 1 · Modelo</summary>

Añade `implements Serializable` y su import de `java.io`. Marca únicamente
`claveTemporal` como `transient`, no nombre/edad. No necesitas inventar métodos.

</details>
<details><summary>Pista 2 · Flujos</summary>

Envuelve el flujo de archivo con el flujo de objetos. `writeObject(alumno)`
guarda; `(Alumno) in.readObject()` recupera. Propaga `IOException` y, en lectura,
`ClassNotFoundException`, o trátalas donde llames al método.

</details>
<details><summary>Comprobación</summary>

La copia de Ángela tendrá 20 años y clave `null`. Cambiar el objeto original
después de guardar no actualiza el archivo automáticamente. Un archivo ausente
debe causar error, no una recuperación ficticia con `null`.

</details>
