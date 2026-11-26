package examen;

import examen.herramientas.Sqlite;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Scanner;

public class MainDesdeSql {
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        File db = new File("datos/examen.sqlite");
        if (!db.exists()) {
            Sqlite.crear(db, new File("sql/crear.sql"));
        }
        try (Connection c = Sqlite.abrir(db)) {
            // CONSULTA
            System.out.println("Introduce una categoría (REDES o IMAGEN):");
            String respuesta = sc.nextLine().toUpperCase();
            buscar(c, respuesta);

            // UPDATE CON TRANSACCION
            iniciarTransaccion(c);
            int unidades = 0;
            System.out.println("Selecciona un código del material:");
            String codigoMaterial = sc.nextLine();
            String sql = """
                    SELECT unidades_disponibles from material
                    WHERE codigo = '?'
                    """;
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, codigoMaterial);
                try (ResultSet rs = ps.executeQuery()) {

                    rs.next();
                    unidades = rs.getInt("unidades_disponibles");

                }
            }

            actualizar(c, "UPDATE material SET unidades_disponibles = ? WHERE codigo = ?", --unidades,
                    codigoMaterial);
            rollback(c);
        }
    }

    private static void buscar(Connection c, String categoria) throws SQLException {
        String sql = """
                SELECT codigo, nombre, categoria, unidades_disponibles from material
                WHERE categoria = ? ORDER BY nombre
                """;
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, categoria);
            try (ResultSet rs = ps.executeQuery()) {
                int filasSinDatos = 0;
                int filas = 0;
                while (rs.next()) {
                    filas++;
                    int unidades = rs.getInt("unidades_disponibles");
                    if (unidades == 0) {
                        filasSinDatos++;
                        continue;
                    }
                    System.out.println(rs.getString("codigo") + " | " + rs.getString("nombre") + " | "
                            + rs.getString("categoria") + " | " + unidades);
                }
                if (filas == filasSinDatos) {
                    System.out.println("Sin resultados");

                }

            }
        }
    }

    public static void iniciarTransaccion(Connection c) throws SQLException {
        if (!c.getAutoCommit())
            throw new SQLException("Ya hay una transacción en curso");
        c.setAutoCommit(false);
    }

    public static void commit(Connection c) throws SQLException {
        exigirTransaccion(c);
        c.commit();
        c.setAutoCommit(true);
    }

    public static void rollback(Connection c) throws SQLException {
        exigirTransaccion(c);
        c.rollback();
        c.setAutoCommit(true);
    }

    private static void exigirTransaccion(Connection c) throws SQLException {
        if (c.getAutoCommit())
            throw new SQLException("Primero llama a iniciarTransaccion(c)");
    }

    public static int actualizar(Connection c, String sql, Object... valores) throws SQLException {
        return ejecutarCambio(c, sql, valores);
    }

    private static int ejecutarCambio(Connection c, String sql, Object... valores) throws SQLException {
        try (PreparedStatement ps = preparar(c, sql, valores)) {
            return ps.executeUpdate();
        }
    }

    public static PreparedStatement preparar(Connection c, String sql, Object... valores)
            throws SQLException {
        PreparedStatement ps = c.prepareStatement(sql);
        try {
            for (int i = 0; i < valores.length; i++) {
                if (valores[i] == null) {
                    ps.setNull(i + 1, Types.NULL);
                } else {
                    ps.setObject(i + 1, valores[i]);
                }
            }
            return ps;
        } catch (SQLException | RuntimeException original) {
            try {
                ps.close();
            } catch (SQLException cierre) {
                original.addSuppressed(cierre);
            }
            throw original;
        }
    }

}
