# 09 · Buscar y eliminar por reescritura

**Sesión 25/10 · segundo tramo.** Fuente: UT02-A §2.11; búsqueda/eliminación
son prácticas añadidas del repaso de Obsidian. [Teoría y pistas](teoria/acceso-secuencial.md).

Completa `src/main/java/es/dam/accesodatos/autoestudio/secuencial/AccesoSecuencial.java`.
Datos conocidos UTF-8: `id;nombre`, ids únicos y sin cabecera/líneas vacías.
No implementes un lector CSV general ni nuevas reglas de validación.

```text
Entrada:       Eliminar id 2 → otro archivo:
1;Ana          1;Ana
2;José         3;Luis
3;Luis
```

`buscarPorId` devuelve la línea completa o `null`. `eliminarPorId` devuelve
0 o 1 y no toca el origen; el destino se sobrescribe. Los tests crean las entradas.

```bash
mvn -pl UT02-A-manejo-de-ficheros/09-acceso-secuencial test
```

Desde `autoestudio/`. El `main` consulta archivo/id, sin borrar nada.
Variante: eliminar el primer registro, el último, uno ausente y el único registro.
Explica por qué buscar id 1 no debe encontrar id 10.
