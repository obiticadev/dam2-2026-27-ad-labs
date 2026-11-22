# B00 · Proyecto Hibernate sin Internet

## Arranque corto

Desde `Tema-4-Hibernate-HQL/`:

```bash
mvn -o clean test -Dgroups=b00
python3 ../herramientas/laboratorio.py arranque
```

Verás `Arranque listo` entre los mensajes de Hibernate. Se crea una BD H2 **en memoria**,
se persiste un profesor y se recupera después de `flush/clear`. No hay servidor que
instalar. Al cerrar la fábrica se elimina el esquema de ensayo.

## Crear uno nuevo sin descargar un arquetipo

1. En una carpeta nueva, copia este `pom.xml` independiente; no el POM padre de
   ficheros/XML. Importa como proyecto Maven existente con JDK 17+ y trabajo offline.
2. Reproduce estas rutas y **respeta los paquetes**:

```text
src/main/java/lab/Modelo.java
src/main/java/lab/Arranque.java
src/main/resources/hibernate.cfg.xml
```

3. Para aprender el mapeo, empieza solo con una entidad sencilla y registra esa clase
   mediante `addAnnotatedClass`. El ejemplo completo registra las clases anidadas de
   `Modelo` que llevan `@Entity`. `@Embeddable` no se registra como entidad separada.
4. En la configuración identifica driver, URL, dialecto, política de esquema y
   credenciales de H2. `hibernate.cfg.xml` debe quedar en el classpath, no junto al POM.
5. Crea **una** `SessionFactory`; abre una `Session` por unidad de trabajo; comienza
   transacción; haz persist/get/cambio/remove; commit o rollback; cierra sesión y al
   final la fábrica. En un proyecto nuevo, comprueba la fila con otra sesión.

La DTD del XML usa una URL histórica, pero Hibernate la resuelve con su recurso
incluido. El arranque funciona con los JAR locales; no debes descargarla a mano.
`persistence.xml` es la alternativa de arranque JPA estándar, no otro archivo que
deba duplicarse obligatoriamente para este arranque nativo.

## Alternativa sin Maven

Las dependencias transitivas también son necesarias: JPA, JAXB, logging, bytecode,
etc. No basta `hibernate-core.jar`. `lib/` contiene el conjunto runtime resuelto.

```bash
python3 ../herramientas/laboratorio.py preparar
python3 ../herramientas/laboratorio.py manual
java -cp 'target/manual:lib/*' lab.Arranque
```

`manual` usa `javac --release 17` y copia `src/main/resources` al directorio de salida.
En Windows el classpath es `"target/manual;lib/*"`. Para un proyecto nuevo puedes
copiar `lib/`, compilar todos los `.java` con ese classpath y copiar el XML a `out/`.
El script es comodidad; no es un requisito de Hibernate.

Si faltan JAR antes del examen, `preparar --online` permite descargarlos. Sin esa
opción no se conecta para resolver dependencias. [Reglas de transporte del kit y
diagnóstico general](../Tema-3-JDBC-SQLite/00-CONFIGURACION.md).

## Si la tarea usa SQLite

JDBC SQLite necesita driver; Hibernate SQLite necesita **además un dialecto** compatible
con su versión. Aquí: `org.sqlite.hibernate.dialect.SQLiteDialect`, no una clase de
Hibernate 6. La prueba `mvn -o test -Dgroups=b12` muestra el montaje local completo:

1. Crear un archivo nuevo y tabla `usuarios` por JDBC.
2. Registrar solo `Usuario`, con ID asignado (sin SEQUENCE).
3. Driver SQLite, URL del archivo, dialecto externo y `hbm2ddl.auto=none`.
4. Persistir y actualizar en transacciones Hibernate; releer con JDBC tras cerrar.

Este ensayo **no certifica** que todas las relaciones, secuencias o alteraciones de
esquema de H2 funcionen igual en SQLite. Para FK en un modelo SQLite con relaciones,
asegura su activación en **cada conexión** del proveedor; el ensayo Usuario no tiene FK.

## Fallos típicos

| Fallo | Revisión |
|---|---|
| No se encuentra `hibernate.cfg.xml` | Copiar a `src/main/resources`; en compilación manual, también a `out/`. |
| `Unknown entity` | Clase registrada, anotación y paquetes correctos. Clases anidadas usan nombre binario con `$` en XML. |
| `NoClassDefFoundError` | Falta un JAR transitivo; usar todos los de `lib/`, no solo core. |
| Imports `jakarta` no compilan | Este proyecto es Hibernate 5.4/`javax`; no mezclar generaciones. |
| La fila no cambia | ¿Entidad gestionada? ¿Transacción confirmada? ¿Relectura real tras limpiar contexto? |
| Error de dialecto | Revisar clase y versión; SQLite requiere aquí la dependencia externa. |
| `LazyInitializationException` | Diseñar la consulta y cargar lo necesario dentro de la sesión, no hacer todo EAGER. |

**Seguridad:** `create` y `create-drop` pueden destruir tablas. El XML incluido solo
los usa con memoria H2. No lo apuntes a una base personal o del profesor conservando
esa política. Para una existente, esquema preparado y `none`/`validate` según soporte;
`update` no sustituye a una migración revisada.
