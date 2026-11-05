# Ejercicio 5 - Lectura XML con JAXB (Unmarshalling)

Proyecto Java para realizar el proceso inverso al Ejercicio 4: leer y deserializar el fichero `DatosEmpresa.xml` hacia un árbol de objetos Java (`Empresa`, `Departamento`, `Empleado`) usando JAXB `Unmarshaller`.

## Estructura

- `src/ejercicioxml/LeerDatosEmpresa.java`: programa principal que carga `DatosEmpresa.xml`, ejecuta `unmarshal` y muestra los departamentos y empleados deserializados.
- `DatosEmpresa.xml`: fichero XML de entrada con la estructura de empresas, departamentos y empleados.
- `lib/`: librerías JAXB necesarias para JDK 11 o superior.
- `.vscode/settings.json`: configuración para el entorno de desarrollo VS Code.

## Conceptos clave aplicados

1. **`Unmarshaller`**: componente de JAXB obtenido a partir de `JAXBContext.newInstance(Empresa.class)` para convertir el flujo XML en objetos Java instanciados en memoria.
2. **`@XmlAccessorType(XmlAccessType.FIELD)`**: permite que JAXB acceda e inyecte los datos directamente en los campos privados del modelo sin requerir métodos getters ni setters.
3. **`@XmlType(propOrder = {...})`**: define y valida el orden de los elementos hijos.
4. **`@XmlAttribute`**: mapea atributos de etiquetas XML (como `id` o `numero`) convirtiéndolos a tipos primitivos automáticamente (`int`).

## Compilar y ejecutar manualmente

Desde la carpeta raíz del proyecto (`Ejercicio5-XML-JAXB-Unmarshal`):

```bash
mkdir -p bin
javac --release 11 -encoding UTF-8 -cp "lib/*" -d bin src/ejercicioxml/*.java
java -cp "bin:lib/*" ejercicioxml.LeerDatosEmpresa
```
