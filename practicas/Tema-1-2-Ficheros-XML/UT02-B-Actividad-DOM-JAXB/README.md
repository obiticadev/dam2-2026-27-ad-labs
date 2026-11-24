# UT02-B · Actividad DOM y JAXB

Plantilla sin resolver para practicar la lectura de `DepartamentosEmpleados.xml` con DOM y la creación de `DatosEmpresa.xml` con JAXB.

## Contenido

- `src/ejercicioxml/LeerDepartamentosEmpleadosDOM.java`: esqueleto del apartado a), con los `TODO` para listar departamentos y sus empleados.
- `src/ejercicioxml/CrearDatosEmpresaJAXB.java`: esqueleto del apartado b), con clases JAXB vacías y los `TODO` para completar el modelo y la escritura.
- `datos/DepartamentosEmpleados.xml`: fichero de entrada incluido; ambos apartados lo usan mediante una ruta relativa.
- `lib/`: API e implementación JAXB y dependencias locales, necesarias porque JAXB no está incluido en JDK 11.
- `ejecutar-a.bat` y `ejecutar-b.bat`: compilación y ejecución en Windows.
- `ejecutar-a.sh` y `ejecutar-b.sh`: compilación y ejecución en Fedora/Linux y macOS.

Las clases compilan y, de momento, muestran un mensaje indicando que cada apartado está pendiente. Completa los `TODO` para que realicen el ejercicio.

## Requisitos

JDK 11 disponible en `PATH` (`java` y `javac`). No se usa Maven ni otro gestor de proyectos.

## Windows

Abre PowerShell o Símbolo del sistema en esta carpeta y ejecuta uno de los scripts:

```text
ejecutar-a.bat
ejecutar-b.bat
```

## Fedora/Linux y macOS

Desde una terminal en esta carpeta, habilita los scripts una vez y ejecuta el apartado deseado:

```sh
chmod +x ejecutar-a.sh ejecutar-b.sh
./ejecutar-a.sh
./ejecutar-b.sh
```

Los scripts compilan con `--release 11` y seleccionan el separador de classpath correspondiente al sistema operativo.

## Resumen de lo que pide la actividad

**Apartado a:** con DOM, listar número, nombre y localidad de cada departamento; debajo, apellido, oficio, salario y comisión de sus empleados.

**Apartado b:** leer con DOM el mismo fichero y escribir con JAXB `DatosEmpresa.xml`, con raíz `EMPRESA`, título, departamentos con atributo `id` y empleados agrupados en `EMPLEADOS`. Cada empleado lleva el número en el atributo `numero` y los elementos `APELLIDO`, `OFICIO`, `DIR`, `FECHA_ALT`, `SALARIO` y `COMISION`.
