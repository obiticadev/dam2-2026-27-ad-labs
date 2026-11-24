# 01 · Ejercicios atómicos resueltos

Desde el [Tema 3](../README.md), usa `bash ejecutar.sh atomicos <número>` o `bash ejecutar.sh atomicos todos`. El código está en [`Atomicos.java`](src/main/java/repaso/Atomicos.java) y cada ejercicio importa una base `dept`–`emp` limpia de [`sql/datos.sql`](sql/datos.sql). En el IDE, usa `repaso.Atomicos`, argumento `todos` y carpeta de trabajo `01-atomicos/` (`-ea` activa los `assert`).

| N.º | Objetivo: qué te podrían pedir | Comprobación |
|---|---|---|
| 01 | Lista los empleados de un departamento con `INNER JOIN` y `WHERE d.nombre = ?`. | `Ventas` da Ana y Luis; `atomicos 1 Archivo` da «Sin resultados»; `atomicos 1 --teclado` pide el nombre. |
| 02 | Cuenta empleados y suma salarios de **todos** los departamentos. | Archivo sale con 0 empleados y total 0. Usa `COUNT(e.id)`, no `COUNT(*)`. |
| 03 | Distingue `NULL` de una comisión real de 0. | Ana tiene `NULL`; Luis tiene `0`. `wasNull()` debe ir justo después de `getInt`. |
| 04 | Inserta con comisión nula y recupera el ID generado. | Eva recibe ID 4. Con `RETURNING`, lo lees con `executeQuery()`; sin `RETURNING`, usa `executeUpdate()` y lee su recuento. |
| 05 | Modifica salario por ID y distingue «no existe». | ID 1 afecta 1 fila; ID 999 afecta 0. Cero no es excepción JDBC. |
| 06 | Borra un departamento vacío y prueba otro con hijos. | Archivo se borra; Ventas no. La FK se aplica porque se activó en esa conexión. |
| 07 | Confirma dos actualizaciones como una sola operación. | Salarios 1100 y 1100 tras `commit()`. |
| 08 | Deshaz el primer cambio cuando el segundo actualiza cero filas. | Salario 1000 tras `rollback()`. |
| 09 | Deshaz el segundo cambio pero conserva el primero. | Salarios 1100 y 1200 tras `rollback(savepoint)` seguido de `commit()`. |
| 10 | Averigua si existen las dos tablas, qué FK hay y qué columnas devuelve la consulta. | `dept`, `emp`, `dept_id → dept`, alias `empleado` y `departamento`. |
| 11 | Provoca un fallo real de FK tras una escritura y deshaz la operación. | La segunda sentencia lanza `SQLException`; la primera no queda persistida. |
| 12 | Recorre Ventas y aplica una regla calculada en Java sin `updateRow()`. | Ventas queda en 1100 y 1250; Soporte sigue en 1500. Cierra el cursor antes del `UPDATE`. |

Regla para elegir método: `executeQuery()` devuelve `ResultSet`; `executeUpdate()` devuelve filas afectadas (normalmente 0 en DDL); `execute()` indica **si el primer resultado es un `ResultSet`**, no «si fue bien».

**Variantes cortas:** cambia `= ?` por `LIKE ?` y pasa `"%"+texto+"%"` con `setString`; prueba `IS NULL` (no `= NULL`); mueve un filtro de empleado de `ON` a `WHERE` y comprueba por qué desaparece Archivo del `LEFT JOIN`.
