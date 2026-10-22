# 10 · Serialización de objetos

**Sesión 30/10 · sin NIO.** Fuente: UT02-A §2.8–2.9; `transient` es una
aclaración añadida del repaso. [Teoría y pistas](teoria/serializacion-objetos.md).

En `src/main/java/es/dam/accesodatos/autoestudio/objetos/`:

1. Completa `Alumno`: interfaz marcadora y campo temporal.
2. Implementa `SerializacionObjetos.guardar` y `leer`.
3. Predice los campos recuperados antes de probar.

Entrada: Alumno `Ángela, 20, solo-en-memoria`. Al recuperar: mismo nombre/edad,
clave temporal `null`, nueva instancia. El archivo guarda bytes, no XML.

```bash
mvn -pl UT02-A-manejo-de-ficheros/10-serializacion-objetos test
```

Los tests usan archivos aislados y comprueban modelo, guardado y lectura por
separado. Completa primero el modelo: la lectura de su archivo depende de que
sea serializable. El `main` solo lee un archivo propio de este ejercicio.

Variante: cambia los datos y repite. Explica por qué `readObject()` necesita
casting y por qué no debes deserializar un archivo no confiable.
