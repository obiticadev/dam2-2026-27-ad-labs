import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Scanner;

// Leemos los empleados y actualizamos sus salarios
public class JDBCMySQL {
    public static void main(String[] args) {
        try (Scanner teclado = new Scanner(System.in)) {
            boolean continuar = true;

            while (continuar) {
                System.out.println("\n1. Ver empleados");
                System.out.println("2. Restaurar datos originales (borra las subidas)");
                System.out.println("3. Aplicar subidas");
                System.out.println("4. Probar fallo sin guardar las subidas");
                System.out.println("0. Salir");
                System.out.print("Elige una opción: ");
                String opcion = teclado.nextLine();

                // Cada opción usa una conexión que se cierra al terminar
                try (Connection conexion = Conexion.conectar()) {
                    switch (opcion) {
                        case "1" -> mostrar(conexion);
                        case "2" -> reiniciar(conexion);
                        case "3" -> actualizar(conexion, false);
                        case "4" -> actualizar(conexion, true);
                        case "0" -> continuar = false;
                        default -> System.err.println("Opción no válida");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error de base de datos: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error al leer el script SQL: " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("Error al procesar los datos: " + e.getMessage());
        }
    }

    // Dejamos las tablas como estaban al principio
    private static void reiniciar(Connection conexion) throws SQLException, IOException {
        String script = "";
        // Leemos el SQL línea a línea
        try (BufferedReader lector = new BufferedReader(new FileReader("sql/emp_dept_salgrade_SQLITE.sql"))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                script += linea + "\n";
            }
        }

        conexion.setAutoCommit(false);
        try {
            // Ejecutamos el script por partes, separando por punto y coma
            try (Statement sentencia = conexion.createStatement()) {
                for (String sql : script.split(";")) {
                    if (!sql.isBlank()) {
                        sentencia.executeUpdate(sql);
                    }
                }
            }
            conexion.commit();
            System.out.println("Base de datos restaurada: 14 empleados con sus salarios originales.");
        } catch (SQLException e) {
            conexion.rollback();
            throw e;
        }
    }

    // Mostramos los datos para ver qué ha cambiado
    private static void mostrar(Connection conexion) throws SQLException {
        String sql = "SELECT empno, ename, job, hiredate, deptno, sal FROM emp ORDER BY empno";
        try (Statement sentencia = conexion.createStatement();
                ResultSet empleados = sentencia.executeQuery(sql)) {
            System.out.printf("%-6s %-10s %-10s %-12s %-5s %10s%n",
                    "NUM", "NOMBRE", "OFICIO", "ALTA", "DEP", "SALARIO");
            while (empleados.next()) {
                System.out.printf("%-6d %-10s %-10s %-12s %-5d %10.2f%n",
                        empleados.getInt("empno"), empleados.getString("ename"),
                        empleados.getString("job"), empleados.getString("hiredate"),
                        empleados.getInt("deptno"), empleados.getDouble("sal"));
            }
        }
    }

    private static void actualizar(Connection conexion, boolean abortar) throws SQLException {
        String consulta = "SELECT empno, ename, job, hiredate, deptno, sal FROM emp ORDER BY empno";
        String cambio = "UPDATE emp SET sal = ? WHERE empno = ?";
        LocalDate fechaLimite = LocalDate.of(1981, 9, 1);

        // Guardamos todos los cambios juntos al terminar
        conexion.setAutoCommit(false);
        try {
            try (Statement sentencia = conexion.createStatement();
                    ResultSet empleados = sentencia.executeQuery(consulta);
                    PreparedStatement actualizacion = conexion.prepareStatement(cambio)) {
                while (empleados.next()) {
                    int numero = empleados.getInt("empno");
                    String oficio = empleados.getString("job");
                    int departamento = empleados.getInt("deptno");
                    LocalDate fechaAlta = LocalDate.parse(empleados.getString("hiredate"));
                    double salario = empleados.getDouble("sal");
                    int porcentaje = 0;

                    // Aplicamos solo la primera subida que le corresponda
                    if ("MANAGER".equals(oficio)) {
                        porcentaje = 10;
                    } else if (fechaAlta.isBefore(fechaLimite)) {
                        porcentaje = 15;
                    } else if ("ANALYST".equals(oficio) && departamento == 20) {
                        porcentaje = 5;
                    } else if ("SALESMAN".equals(oficio)) {
                        porcentaje = 8;
                    }

                    // Calculamos la subida y la sumamos al salario
                    double subida = salario * porcentaje / 100;
                    double nuevoSalario = salario + subida;

                    // Cambiamos el salario del empleado que estamos leyendo
                    actualizacion.setDouble(1, nuevoSalario);
                    actualizacion.setInt(2, numero);
                    actualizacion.executeUpdate();
                    System.out.printf("%-10s %8.2f -> %8.2f (%d%%), pendiente de commit%n",
                            empleados.getString("ename"), salario, nuevoSalario, porcentaje);
                }
            }

            // abortar = true;
            if (abortar) {
                throw new SQLException("Fallo de prueba antes del commit.");
            }

            conexion.commit();
            System.out.println("COMMIT: todos los salarios se han guardado.");
        } catch (SQLException | RuntimeException e) {
            conexion.rollback();
            System.err.println("ROLLBACK: no se ha guardado ninguna subida.");
            throw e;
        }
    }
}
