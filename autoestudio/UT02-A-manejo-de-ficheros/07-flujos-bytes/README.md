# 07 · Flujos de bytes

**Sesión 22/10 · 1 hora.** Fuente: UT02-A §2.7. [Teoría y pistas](teoria/flujos-bytes.md).

Abre `src/main/java/es/dam/accesodatos/autoestudio/bytes/FlujosBytes.java`.
Resuelve primero `copiar`; después `anexar`. No uses `Files.copy` ni cargues
el archivo entero en memoria: el objetivo es entender la lectura por bloques.

## Entrada y resultado

Los tests preparan archivos temporales; no necesitas crear datos manualmente.
Origen de 6 bytes, búfer de 4 → destino con exactamente los mismos 6 bytes.
El destino previo se sobrescribe. Anexar `[10,20,30,40]` con `off=1,len=2`
a `[9]` debe producir `[9,20,30]`.

```bash
mvn -pl UT02-A-manejo-de-ficheros/07-flujos-bytes test
```

Desde `autoestudio/`. En el IDE, `main` admite rutas origen/destino distintas;
**el destino se sobrescribe**, usa solo una copia de práctica.

## Tests y criterio de dominio

- Última lectura parcial, byte cero y byte 255.
- Archivo vacío y tamaño múltiplo del búfer.
- Rango `off/len`, anexado y rango de longitud cero.
- Capacidad inválida y error de apertura.

Variante: cambia el búfer a 1 y a 3. Deben conservarse los mismos bytes.
Explica por qué `read()` y `read(byte[])` devuelven cosas distintas.
