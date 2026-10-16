# Librerías JAXB

Estas librerías permiten usar JAXB 2 (`javax.xml.bind`) con JDK 11, que ya no lo incluye en el JDK. Se cargan desde los scripts y comandos con `lib/*`.

- `jakarta.xml.bind-api-2.3.3.jar`: API JAXB 2; aunque el nombre del artefacto contiene `jakarta`, esta versión utiliza el paquete `javax.xml.bind`.
- `jaxb-runtime-2.3.8.jar`: implementación de JAXB.
- `txw2-2.3.8.jar`: dependencia del runtime JAXB.
- `istack-commons-runtime-3.0.12.jar`: utilidades usadas por JAXB.
- `javax.activation-api-1.2.0.jar` y `jakarta.activation-1.2.2.jar`: API e implementación de Activation requeridas por JAXB.
