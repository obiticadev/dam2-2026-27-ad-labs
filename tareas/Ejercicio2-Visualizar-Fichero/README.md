# Ejercicio2-Visualizar-Fichero

Proyecto Java del segundo ejercicio de ficheros. La clase entregable es
`EjercicioFicheros.ArchivoApp2` y está en `src/EjercicioFicheros/ArchivoApp2.java`.

## Compilar y ejecutar

Ejecuta estos comandos desde el directorio raíz del proyecto:

```powershell
javac -encoding UTF-8 -d bin src\EjercicioFicheros\ArchivoApp2.java
java -cp bin EjercicioFicheros.ArchivoApp2
```

El programa lee y muestra su propio fuente desde
`src/EjercicioFicheros/ArchivoApp2.java`. La ruta es relativa al directorio desde
el que se ejecuta el comando, por lo que hay que ejecutarlo desde la raíz del
proyecto.

El programa comprueba que el fichero exista, no sea un directorio y se pueda leer.
Si hay un problema al abrirlo o leerlo, muestra un mensaje de error.
