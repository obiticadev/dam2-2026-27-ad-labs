# 11 · Acceso aleatorio

**Sesiones 25 y 30/10.** Fuente: UT02-A §2.12. Los registros de tamaño fijo
son práctica añadida del repaso. [Teoría y pistas](teoria/acceso-aleatorio.md).

Completa `src/main/java/es/dam/accesodatos/autoestudio/aleatorio/AccesoAleatorio.java`.
Los tests escriben tres registros con Java antes de llamar a tus métodos:

| Índice | id | nota | Inicio | Inicio de nota |
|---|---|---|---|---|
| 0 | 10 | 8.5 | 0 | 4 |
| 1 | 20 | 6.0 | 12 | 16 |
| 2 | 30 | 7.5 | 24 | 28 |

`leerNota(archivo, 1)` devuelve 6.0. `actualizarNota(archivo, 1, 9.0)` debe
cambiar solo esa nota: id 20, vecinos y longitud de 36 bytes se conservan.
No insertes nombres variables en este formato.

```bash
mvn -pl UT02-A-manejo-de-ficheros/11-acceso-aleatorio test
```

Desde `autoestudio/`. El `main` solo consulta un archivo propio con este formato.
Variante: actualizar primero y último. Predecir qué ocurre con índice -1 o 3;
el ejercicio exige rechazarlos antes de escribir, sin ampliar el archivo.
