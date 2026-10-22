# Ejercicio 4 - XML con DOM y JAXB

Proyecto Java para resolver los dos apartados de la actividad usando JDK 11, sin Maven ni otro gestor de proyectos.

## Estructura

- `src/ejercicioxml/ListadoDepartamentosEmpleados.java`: lee `datos/DepartamentosEmpleados.xml` con DOM y lista los departamentos y sus empleados.
- `src/ejercicioxml/CrearDatosEmpresa.java`: lee el mismo fichero con DOM y genera `DatosEmpresa.xml` con JAXB.
- `datos/DepartamentosEmpleados.xml`: fichero de entrada de la actividad.
- `lib/`: librerías JAXB necesarias para JDK 11.
- `DatosEmpresa.xml`: resultado del apartado b.

Todos los ficheros de datos se usan con rutas relativas a la carpeta del proyecto. El acceso a ficheros se hace con `java.io.File`, como en los ejemplos de clase.

## Requisitos

JDK 11 instalado y disponible en `PATH` (`java` y `javac`). JAXB no forma parte del JDK desde Java 11, por eso sus JAR están incluidos en `lib/`.

## Compilar y ejecutar manualmente

Desde la carpeta raíz del proyecto:

```text
mkdir bin
javac --release 11 -encoding UTF-8 -cp "lib/*" -d bin src/ejercicioxml/*.java
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

No se necesitan scripts para compilar ni ejecutar. La entrega excluye los archivos `.bat`, `.sh` y los compilados de `bin/`.
