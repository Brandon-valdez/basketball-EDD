package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton de conexión a MySQL.
 * Uso: Connection con = Conexion.getInstancia().getConexion();
 */
public class Conexion {

    private static final String URL      = "jdbc:mysql://localhost:3306/torneos_baloncesto?useSSL=false&serverTimezone=America/El_Salvador";
    private static final String USUARIO  = "root";
    private static final String PASSWORD = "1234";
    private static Conexion instancia;
    private Connection conexion;

    private Conexion() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL no encontrado. Agrega mysql-connector-j al pom.xml", e);
        }
    }

    public static Conexion getInstancia() throws SQLException {
        if (instancia == null || instancia.conexion.isClosed()) {
            instancia = new Conexion();
        }
        return instancia;
    }

    public Connection getConexion() {
        return conexion;
    }

    /** Cierra la conexión (llamar solo al cerrar la app) */
    public void cerrar() throws SQLException {
        if (conexion != null && !conexion.isClosed()) {
            conexion.close();
        }
    }
}