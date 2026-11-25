# Simulacro 02 · Stock y salidas sin duplicados

**Enunciado.** Con [`producto` y `salida`](sql/datos.sql):

1. Registra una salida con referencia única, ID de producto y cantidad positiva. No permitas que el stock quede negativo ni dependas de un `SELECT` previo para validarlo.
2. Actualiza el stock e inserta la salida en **la misma transacción**. Si no hay stock, el producto no existe o la referencia está repetida, debe quedar la base como antes del intento. El caso de la referencia repetida debe comprobarse aunque ya se haya descontado el stock en esa transacción.
3. Muestra nombre, stock actual, número de salidas y unidades totales por producto. Incluye productos sin salidas con `LEFT JOIN`.

**Casos de prueba.** De 10 teclados, `S-01` retira 3 y deja 7 (`COMMIT`). `S-02` intenta retirar 8 (`ROLLBACK`). Se reenvía `S-01` con 2 unidades: el `UPDATE` alcanzaría a dejar 5, pero el `INSERT` no añade la referencia repetida; el rollback restaura 7. Al reabrir: Teclado / 7 / 1 salida / 3 unidades; Ratón / 5 / 0 salidas / 0 unidades.

**Solución:** [`Almacen.java`](src/main/java/repaso/Almacen.java). Ejecuta `bash ejecutar.sh almacen` desde el Tema 3 (o `repaso.Almacen` desde el IDE con esta carpeta como directorio de trabajo y `-ea`). Trabaja siempre sobre `target/almacen.sqlite`, que se rehace desde `sql/datos.sql` en cada ejecución.

**Variante de examen:** quita `ON CONFLICT ... DO NOTHING` y observa que ahora el duplicado lanza `SQLException` en vez de devolver 0. En ambos casos hay que deshacer el descuento. No uses `INSERT OR IGNORE` para ocultar indiscriminadamente otros fallos de restricciones.
