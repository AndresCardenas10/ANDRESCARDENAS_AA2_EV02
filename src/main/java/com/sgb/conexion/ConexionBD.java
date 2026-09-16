package com.sgb.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase responsable únicamente de abrir la conexión JDBC hacia la base de
 * datos MySQL (servida localmente por XAMPP) del proyecto SGB.
 *
 * Ajusta las constantes URL, USUARIO y CLAVE según tu instalación local.
 * Por defecto XAMPP expone MySQL en el puerto 3306 con usuario "root" y
 * clave vacía.
 */
public class ConexionBD {

    private static final String URL =
        "jdbc:mysql://localhost:3307/sgb?useTimezone=true&serverTimezone=UTC";
    private static final String USUARIO = "root";
    private static final String CLAVE = "";

    // Clase de utilidad: no debe instanciarse.
    private ConexionBD() {
    }

    /**
     * Abre y retorna una nueva conexión hacia la base de datos.
     * Quien llame este método es responsable de cerrarla (se recomienda
     * usar try-with-resources, como se hace en UsuarioDAO).
     */
    public static Connection obtenerConexion() throws SQLException {
        try {
            // Fuerza el registro del driver de MySQL. Sin esta linea, en
            // algunos casos (como despliegues repetidos dentro del mismo
            // Tomcat) el driver no queda registrado a tiempo y sale el
            // error "No suitable driver found" aunque el .jar si este
            // presente en WEB-INF/lib.
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontro el driver de MySQL: " + e.getMessage());
        }
        return DriverManager.getConnection(URL, USUARIO, CLAVE);
    }
}