package com.mycompany.paneladministracion.dao;

import com.mycompany.paneladministracion.entidades.Instituto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import persistencia.IConexionBD;
import persistencia.PersistenciaException;

/**
 * DAO de unidades académicas (Institutos). Solo lectura: el catálogo se
 * inserta automáticamente al sembrar la base de datos.
 */
public class InstitutoDAO {

    private final IConexionBD conexion;

    public InstitutoDAO(IConexionBD conexion) {
        this.conexion = conexion;
    }

    public List<Instituto> listarTodos() throws PersistenciaException {
        List<Instituto> lista = new ArrayList<>();
        String sql = "SELECT id_instituto, nombre_oficial, nombre_abreviado FROM Institutos ORDER BY nombre_oficial;";
        try (Connection con = conexion.crearConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Instituto(
                        rs.getInt("id_instituto"),
                        rs.getString("nombre_oficial"),
                        rs.getString("nombre_abreviado")));
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al listar institutos: " + e.getMessage());
        }
        return lista;
    }
}
