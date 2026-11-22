# B00 · De carpeta vacía a SQLite conectado

**Lo que necesitas:** JDK 17 o posterior; para la ruta Maven, Maven y dependencias
ya guardadas; para la alternativa, los JAR de `lib/`. SQLite no requiere servidor,
cuentas, contraseña ni puerto. `sqlite3` y DB Browser son opcionales: Java puede
crear el archivo y las tablas sin esas aplicaciones.

## 1. Arranque rápido de este laboratorio

Desde `Tema-3-JDBC-SQLite/`:

```bash
java -version
javac -version
mvn -version
mvn -o clean test -Dgroups=b00
python3 ../herramientas/laboratorio.py arranque
```

Salida significativa: `Conexión lista`, `SQLite` y ruta absoluta de
`datos/arranque.db`. Repetir la orden no duplica la fila. Los avisos del JDK moderno
sobre acceso nativo no son un fallo si el proceso termina correctamente.

**Ejercicio de configuración:** abre `Arranque.java`; identifica JAR → URL → conexión
→ tabla → INSERT parametrizado → SELECT → cierre. Cambia la ruta a un archivo de
ensayo y comprueba que ves su ubicación real. No utilices una base de clase para probar
DDL o borrados.

## 2. Proyecto Maven nuevo, sin asistente ni arquetipo

No dependas de «Create from archetype»: puede intentar descargar plantillas.
En una carpeta **nueva**, crea esta estructura con tu editor o terminal:

```text
mi-practica/
  pom.xml
  src/main/java/lab/Main.java
  lib/                       ← solo para la alternativa manual
```

1. Copia el `pom.xml` de este proyecto; es independiente de `autoestudio/pom.xml`.
   Puedes cambiar `artifactId`; no cambies versiones la mañana del examen.
2. En Eclipse: importar **Existing Maven Projects**, elegir esa carpeta y usar el
   JDK correcto. En otro IDE, abrir/importar el POM. Trabaja offline y no fuerces
   actualización de dependencias. Son indicaciones generales, no una prueba de tu IDE.
3. Escribe este `Main.java` completo:

```java
package lab;

import java.sql.*;

public class Main {
    public static void main(String[] args) throws SQLException {
        System.out.println(new java.io.File("examen.db").getAbsolutePath());
        try (Connection c = DriverManager.getConnection("jdbc:sqlite:examen.db");
             Statement st = c.createStatement()) {
            st.execute("PRAGMA foreign_keys=ON");
            try (ResultSet rs = st.executeQuery("SELECT 1 AS prueba")) {
                if (rs.next()) System.out.println(rs.getInt("prueba"));
            }
        }
    }
}
```

4. Compila con `mvn -o compile`. Ejecuta `lab.Main` desde el IDE o con la alternativa
   manual de abajo. Compilar **no** prueba que el driver esté en el classpath de ejecución.
5. Añade un `CREATE TABLE` mediante `executeUpdate`, inserta una fila con
   `PreparedStatement` y recupérala. Cierra y vuelve a ejecutar para comprobar persistencia.

## 3. Sin Maven: JAR locales y JDK

Copia los JAR de `lib/` a la carpeta `lib/` del proyecto nuevo. SQLite JDBC 3.42.0.0
se distribuye aquí con sus dependencias runtime resueltas por Maven. No copies solo
un JAR al azar de otro curso. Desde `mi-practica/`, en Linux/macOS:

```bash
mkdir -p out
javac --release 17 -encoding UTF-8 -cp 'lib/*' -d out src/main/java/lab/Main.java
java -cp 'out:lib/*' lab.Main
```

En Windows, el separador del classpath de ejecución es `;`: `"out;lib/*"`.
En Eclipse sin Maven: crea un proyecto Java y añade **todos** los JAR de `lib/` al
Build Path (bibliotecas/classpath, no un módulo Java nuevo). Ejecuta la clase Main.

## 4. Preparar y transportar antes de estar sin red

En este equipo ya se ha ejecutado la preparación. Para repetirla, desde el tema:

```bash
python3 ../herramientas/laboratorio.py preparar
```

Es offline por defecto. Solo si faltan dependencias y todavía tienes conexión:

```bash
python3 ../herramientas/laboratorio.py preparar --online
```

Esto prueba B00, resuelve/copia dependencias runtime a `lib/` y compila también con
`javac`. No considera resueltos los ejercicios. Para copiar un kit a una carpeta nueva:

```bash
python3 ../herramientas/laboratorio.py kit --destino /ruta/USB/kit-tema3
```

El kit incluye ambos temas con código, guías, ruta, script y JAR; prepara ambos
proyectos antes de generarlo. **No incluye JDK, Maven, `.m2`, fuentes ni Obsidian**.
La alternativa manual funciona con JDK + JAR y Python para el script; también puedes
usar directamente los dos comandos anteriores sin Python. Para Maven en otro equipo,
necesitas además Maven y su caché de dependencias **y plugins**, probados allí con
`mvn -o clean test -Dgroups=b00`. Copiar solo `lib/` no prepara Maven ni JUnit.

## 5. Diagnóstico en un minuto

| Mensaje o síntoma | Qué comprobar |
|---|---|
| `No suitable driver` / `ClassNotFoundException: org.sqlite.JDBC` | Falta driver en el classpath **de ejecución**. Añadir dependencia al POM no basta si ejecutas `java` sin `-cp`. |
| Error de Maven «offline» al resolver un artefacto | No está disponible en esa caché. Antes del examen, preparar con red; durante, usar `lib/` + `javac/java`. |
| `release version 17 not supported` | Maven está usando un JDK anterior: mirar `mvn -version`, no solo `java -version`. |
| `no such table` | Archivo distinto, esquema no creado o `:memory:` nuevo en otra conexión. Imprime ruta y consulta `sqlite_master`. |
| `unable to open database file` | Carpeta padre inexistente o permisos. Crear carpeta y usar ruta válida. |
| `FOREIGN KEY constraint failed` | Referencia inexistente; no desactives FK para esconder el error. |
| `database is locked` | Otro escritor o transacción abierta. Cierra recursos y revisa commit/rollback. |
| `UnsupportedOperationException: B…` | Has llegado al método pendiente del ejercicio; **no** es un fallo de instalación. |

**Prueba final:** sin red, compila desde cero, crea un archivo nuevo, conecta,
inserta y consulta. Abrir un proyecto que solo conserva `.class` antiguos no demuestra
que puedas configurarlo de nuevo.
