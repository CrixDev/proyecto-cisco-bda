/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistencia;

/**
 *
 * @author alecn
 */
import java.sql.Connection;
import java.sql.SQLException;

public interface IConexionBD {
    Connection crearConexion() throws SQLException;
}
