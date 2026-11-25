# Simulacro 01 · Alta y revisión salarial

**Enunciado.** Con [`dept` y `emp`](sql/datos.sql):

1. Da de alta un empleado con salario de 1200 y departamento existente. El ID y el nombre son parámetros.
2. Revisa el sueldo de su responsable, que debe ser otra persona del mismo departamento. Si cobra hasta 1100 inclusive, súbele 100. Si no cumple la primera regla y está en Ventas (ID 10), súbele 50. Para el resto no hay subida. Las reglas no se acumulan.
3. Confirma ambos cambios o ninguno. Si el `UPDATE` no encuentra responsable válido, deshaz el alta, aunque JDBC no haya lanzado una excepción.
4. Lista por ID los empleados de Ventas con nombre del departamento usando `JOIN`.

**Casos de prueba.** Alta de Eva en Ventas con Ana como responsable: Eva existe y Ana pasa de 1000 a 1100, no a 1150. Intento de alta de Hugo con Omar como responsable: Omar está en Soporte, el `UPDATE` devuelve 0, Hugo no existe y Omar sigue en 1500. Listado final de Ventas: Ana/1100, Luis/1300, Eva/1200.

**Solución:** [`Personal.java`](src/main/java/repaso/Personal.java). Ejecuta `bash ejecutar.sh personal` desde el Tema 3 (o `repaso.Personal` desde el IDE con esta carpeta como directorio de trabajo y `-ea`). Trabaja siempre sobre `target/personal.sqlite`, que se rehace desde `sql/datos.sql` en cada ejecución.

**Variante de examen:** cambia al responsable por Luis y comprueba la segunda rama, 1300 → 1350. Cambia el departamento del alta a uno inexistente y explica por qué se rechaza ya en el `INSERT`.
