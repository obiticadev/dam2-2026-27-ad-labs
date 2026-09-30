# Ejercicio3-Fichero-de-Acceso-Aleatorio

Proyecto Java del tercer ejercicio de ficheros: gestión de cursos mediante fichero binario de acceso aleatorio.

La clase entregable es `EjercicioFicheros.CursosAleatorioApp` y está en `src/EjercicioFicheros/CursosAleatorioApp.java`.

## Estructura del registro (42 bytes por curso)

| Campo | Tipo | Tamaño |
| :--- | :--- | :---: |
| Número de curso | `int` | 4 bytes |
| Nombre del curso | 15 caracteres (`writeChars`) | 30 bytes |
| Coste | `double` | 8 bytes |
| **Total por registro** | | **42 bytes** |

## Compilar y ejecutar

Ejecuta estos comandos desde el directorio raíz del proyecto (`tareas/Ejercicio3-Fichero-de-Acceso-Aleatorio`):

### Linux / macOS / Bash:
```bash
javac -encoding UTF-8 -d bin src/EjercicioFicheros/CursosAleatorioApp.java
java -cp bin EjercicioFicheros.CursosAleatorioApp
```

### Windows (PowerShell):
```powershell
javac -encoding UTF-8 -d bin src\EjercicioFicheros\CursosAleatorioApp.java
java -cp bin EjercicioFicheros.CursosAleatorioApp
```

## Características implementadas
- **Acceso aleatorio (`seek`)**: Se posiciona directamente en el byte correspondiente `(curso - 1) * 42`.
- **Slots vacíos**: Los slots no utilizados se inicializan con el valor `-1` en su campo número.
- **Formateo de cadenas**: Uso de `StringBuilder` con `setLength(15)` para garantizar 15 caracteres rellenados con `\0`.
- **Buenas prácticas**: `try-with-resources`, constantes descriptivas sin números mágicos y separación modular de métodos.
