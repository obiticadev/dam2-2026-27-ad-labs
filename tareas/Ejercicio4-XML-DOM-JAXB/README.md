# Ejercicio 4 - XML con DOM y JAXB

Proyecto Java para resolver los dos apartados de la actividad usando JDK 11, sin Maven ni otro gestor de proyectos.

## Estructura

- `src/ejercicioxml/ListadoDepartamentosEmpleados.java`: lee `datos/DepartamentosEmpleados.xml` con DOM y lista los departamentos y sus empleados.
- `src/ejercicioxml/CrearDatosEmpresa.java`: lee el mismo fichero con DOM y genera `DatosEmpresa.xml` con JAXB.
- `datos/DepartamentosEmpleados.xml`: fichero de entrada de la actividad.
- `lib/`: librerías JAXB necesarias para JDK 11.
- `ejecutar-a.bat` y `ejecutar-b.bat`: compilan y ejecutan cada apartado desde Windows.
- `ejecutar-a.sh` y `ejecutar-b.sh`: compilan y ejecutan cada apartado desde Fedora/Linux y macOS.

Todos los ficheros de datos se usan con rutas relativas a la carpeta del proyecto. El acceso a ficheros se hace con `java.io.File`, como en los ejemplos de clase.

## Requisitos

JDK 11 instalado y disponible en `PATH` (`java` y `javac`). JAXB no forma parte del JDK desde Java 11, por eso sus JAR están incluidos en `lib/`.

## Ejecutar en Windows

Desde esta carpeta, ejecuta:

```text
ejecutar-a.bat
```

para listar los datos, o:

```text
ejecutar-b.bat
```

para crear/sobrescribir `DatosEmpresa.xml`.

## Ejecutar en Fedora/Linux o macOS

Desde una terminal, entra en la carpeta del proyecto y da permiso de ejecución a los scripts una vez:

```sh
chmod +x ejecutar-a.sh ejecutar-b.sh
```

Después ejecuta el apartado que quieras:

```sh
./ejecutar-a.sh
./ejecutar-b.sh
```

Los scripts cambian a la carpeta del proyecto, compilan las dos clases con compatibilidad Java 11 y ejecutan el apartado correspondiente. Ajustan el separador del classpath según el sistema operativo. Para el apartado b, crean/sobrescriben `DatosEmpresa.xml`.

## Compilar y ejecutar manualmente

Desde la carpeta raíz del proyecto:

```text
javac --release 11 -encoding UTF-8 -cp "lib/*" -d bin src/ejercicioxml/ListadoDepartamentosEmpleados.java src/ejercicioxml/CrearDatosEmpresa.java
```

En Windows, usa `;` como separador del classpath al ejecutar:

```text
java -cp "bin;lib/*" ejercicioxml.ListadoDepartamentosEmpleados
java -cp "bin;lib/*" ejercicioxml.CrearDatosEmpresa
```

En Linux/macOS, el separador del classpath es `:`:

```text
java -cp "bin:lib/*" ejercicioxml.ListadoDepartamentosEmpleados
java -cp "bin:lib/*" ejercicioxml.CrearDatosEmpresa
```

## Formato creado

`DatosEmpresa.xml` contiene un elemento `EMPRESA`, los departamentos con su número en el atributo `id` y los empleados asociados dentro de `EMPLEADOS`. El número de empleado se escribe en el atributo `numero`; el número de departamento del empleado queda implícito en el departamento contenedor, como indica el esquema de la actividad.

Para la entrega, comprime la carpeta del proyecto y nombra el ZIP con el patrón indicado por el profesor: `apellido1_apellido2_nombre_2I_Tarea.zip`.
