/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD implements IConexionBD {

    private static final String URL = "jdbc:mysql://localhost:3306/CISCOBDA?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "Cedm.12345";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ No se encontró el Driver de MySQL en el proyecto: " + e.getMessage());
        }
    }

    @Override
    public Connection crearConexion() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
