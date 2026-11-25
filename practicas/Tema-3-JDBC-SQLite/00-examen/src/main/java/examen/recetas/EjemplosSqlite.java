package examen.recetas;

import examen.herramientas.Sqlite;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.List;
import java.util.Map;

/*
 * RECETAS PARA COPIAR A TU MAIN.
 *
 * Los ejemplos usan departamentos/empleados de sql/crear.sql.
 * En el examen cambia tablas, columnas, valores y reglas por los del enunciado.
 * Ejecutar ESTA clase solo lista datos; NO ejecuta las recetas de escritura.
 * Directorio de trabajo: 00-examen. Sin argumentos.
 *
 * Puedes usar Sqlite.xxx desde MainDesdeSql o MainDesdeSqlite.
 * EjemplosSqlite NO es necesaria para que funcionen las herramientas.
 */
public class EjemplosSqlite {
    public static void main(String[] args) throws Exception {
        try (Connection c = Sqlite.abrir(new File("datos/examen.sqlite"))) {
            // Copia estas llamadas dentro del try de TU main.
            Sqlite.mostrarConsulta(c, "SELECT * FROM empleados ORDER BY id");
            listarPorDepartamento(c, "Ventas");
            resumen(c);
            System.out.println("Total: " + Sqlite.consultarLong(c, "SELECT COUNT(*) FROM empleados"));

            // Otras herramientas listas para llamar:
            // Sqlite.mostrarTablas(c);
            // Sqlite.mostrarColumnas(c, "empleados");
            // Sqlite.mostrarClavesForaneas(c, "empleados");
            // Sqlite.mostrarInfoConexion(c);
            // Sqlite.mostrarColumnasConsulta(c, "SELECT nombre AS empleado FROM empleados");
        }
    }

    // 1. SELECT con JOIN y parámetro. También puedes pasar "%" + texto + "%"
    // si cambias la condición a LIKE ?. Sin resultados imprime un aviso.
    public static void listarPorDepartamento(Connection c, String departamento) throws SQLException {
        Sqlite.mostrarConsulta(c, """
                SELECT e.id, e.nombre AS empleado, d.nombre AS departamento, e.salario
                FROM empleados e JOIN departamentos d ON e.departamento_id = d.id
                WHERE d.nombre = ? ORDER BY e.id
                """, departamento);
    }

    // 2. LEFT JOIN: también muestra departamentos SIN empleados.
    // COUNT(e.id), no COUNT(*). COALESCE convierte la suma NULL en cero.
    public static void resumen(Connection c) throws SQLException {
        Sqlite.mostrarConsulta(c, """
                SELECT d.nombre AS departamento, COUNT(e.id) AS empleados,
                       COALESCE(SUM(e.salario), 0) AS total_salarios
                FROM departamentos d LEFT JOIN empleados e ON e.departamento_id = d.id
                GROUP BY d.id, d.nombre ORDER BY d.id
                """);
    }

    // 3. INSERT, UPDATE y DELETE sueltos.
    // Estos métodos NO inician transacción: sin una, se confirma cada sentencia.
    public static long insertarEmpleado(Connection c, String nombre, double salario, int departamento)
            throws SQLException {
        return Sqlite.insertarConId(c, """
                INSERT INTO empleados(nombre, salario, departamento_id)
                VALUES (?, ?, ?) RETURNING id
                """, nombre, salario, departamento);
    }

    public static int cambiarSalario(Connection c, int id, double salario) throws SQLException {
        return Sqlite.actualizar(c, "UPDATE empleados SET salario = ? WHERE id = ?", salario, id);
    }

    public static int borrarEmpleado(Connection c, int id) throws SQLException {
        return Sqlite.borrar(c, "DELETE FROM empleados WHERE id = ?", id);
    }

    // También puedes insertar SIN recuperar el ID:
    // int filas = Sqlite.insertar(c,
    //         "INSERT INTO departamentos(nombre) VALUES (?)", "Compras");
    // Sqlite.exigirUnaFila(filas);
    //
    // Para un valor SQL NULL (si la columna lo permite), pasa (Object) null.
    // Por ejemplo, en la base de los atómicos:
    // Sqlite.insertar(c, "INSERT INTO emp(nombre, salario, comision, dept_id) VALUES (?, ?, ?, ?)",
    //         "Eva", 1300, null, 20);
    // No pases la cadena "NULL": sería texto, no un nulo.

    // 4. TRANSACCIÓN: alta + revisión. Si la segunda operación afecta a cero
    // filas, exigirUnaFila lanza SQLException y también se deshace el alta.
    // Ejemplo: altaConRevision(c, "Eva", 1200, 1, 1) confirma.
    // Ejemplo: altaConRevision(c, "Hugo", 1200, 1, 999) deshace todo.
    // Llama a esta receta desde el try de la conexión y deja salir el error
    // para que esa conexión se cierre; no continúes usándola tras un fallo.
    public static long altaConRevision(Connection c, String nombre, double salario,
            int departamento, int responsable) throws SQLException {
        Sqlite.iniciarTransaccion(c);
        try {
            long id = insertarEmpleado(c, nombre, salario, departamento);
            int filas = Sqlite.actualizar(c, """
                    UPDATE empleados SET salario = salario + CASE
                        WHEN salario <= 1100 THEN 100
                        WHEN departamento_id = 1 THEN 50
                        ELSE 0 END
                    WHERE id = ? AND departamento_id = ? AND id <> ?
                    """, responsable, departamento, id);
            Sqlite.exigirUnaFila(filas);
            Sqlite.commit(c);
            return id;
        } catch (SQLException | RuntimeException error) {
            Sqlite.deshacer(c, error);
            throw error;
        }
    }

    // 5. ROLLBACK VOLUNTARIO: ninguno de estos cambios se conserva.
    public static void probarSinGuardar(Connection c) throws SQLException {
        Sqlite.iniciarTransaccion(c);
        try {
            Sqlite.exigirUnaFila(cambiarSalario(c, 1, 9999));
            Sqlite.rollback(c);
        } catch (SQLException | RuntimeException error) {
            Sqlite.deshacer(c, error);
            throw error;
        }
    }

    // 6. SAVEPOINT: guarda el primer cambio y deshace solo el segundo.
    public static void guardarSoloPrimerCambio(Connection c) throws SQLException {
        Sqlite.iniciarTransaccion(c);
        try {
            Sqlite.exigirUnaFila(cambiarSalario(c, 1, 1900));
            Savepoint punto = Sqlite.guardarPunto(c, "antes_del_segundo");
            Sqlite.exigirUnaFila(cambiarSalario(c, 2, 9999));
            Sqlite.rollbackHasta(c, punto);
            Sqlite.liberarPunto(c, punto);
            Sqlite.commit(c);
        } catch (SQLException | RuntimeException error) {
            Sqlite.deshacer(c, error);
            throw error;
        }
    }

    // 7. RECORRER Y ACTUALIZAR: consultar ya ha cerrado el ResultSet.
    // Se puede calcular en Java y actualizar después por clave primaria.
    // No se usa updateRow(), que SQLite JDBC no admite.
    public static void revisarSalariosEnJava(Connection c, int departamento) throws SQLException {
        Sqlite.iniciarTransaccion(c);
        try {
            List<Map<String, Object>> empleados = Sqlite.consultar(c,
                    "SELECT id, salario FROM empleados WHERE departamento_id = ?", departamento);
            for (Map<String, Object> empleado : empleados) {
                // getObject puede devolver Integer, Long o Double: usa Number.
                int id = ((Number) empleado.get("id")).intValue();
                double anterior = ((Number) empleado.get("salario")).doubleValue();
                double nuevo = anterior + (anterior <= 1700 ? 100 : 50);
                Sqlite.exigirUnaFila(cambiarSalario(c, id, nuevo));
            }
            Sqlite.commit(c);
        } catch (SQLException | RuntimeException error) {
            Sqlite.deshacer(c, error);
            throw error;
        }
    }

    // 8. CURSOR JDBC Y NULL: si prefieres escribir tu propio while(rs.next()).
    // Este SELECT de prueba devuelve un NULL y un cero, sin depender de tablas.
    public static void distinguirNuloDeCero(Connection c) throws SQLException {
        try (PreparedStatement ps = Sqlite.preparar(c,
                "SELECT NULL AS comision UNION ALL SELECT 0");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Integer comision = Sqlite.leerEnteroNullable(rs, "comision");
                System.out.println(comision == null ? "Sin comisión" : "Comisión: " + comision);
            }
        }
    }

    // PATRÓN DEL ALMACÉN (adapta los nombres a la base que te den):
    //
    // Sqlite.iniciarTransaccion(c);
    // try {
    //     Sqlite.exigirUnaFila(Sqlite.actualizar(c,
    //         "UPDATE producto SET stock = stock - ? WHERE id = ? AND stock >= ?",
    //         cantidad, productoId, cantidad));
    //     Sqlite.exigirUnaFila(Sqlite.insertar(c,
    //         "INSERT INTO salida VALUES (?, ?, ?) ON CONFLICT(referencia) DO NOTHING",
    //         referencia, productoId, cantidad));
    //     Sqlite.commit(c);
    // } catch (SQLException | RuntimeException error) {
    //     Sqlite.deshacer(c, error);
    //     throw error;
    // }
    //
    // Antes de iniciar valida cantidad > 0. El WHERE valida el stock de forma
    // atómica; si el INSERT no inserta, también se deshace el descuento.
    // Una FK o CHECK incorrecta lanza SQLException: NO la ocultes.
}
