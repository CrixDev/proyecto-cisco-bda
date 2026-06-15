package com.mycompany.paneladministracion.dao;

import com.mycompany.paneladministracion.entidades.Software;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import persistencia.IConexionBD;
import persistencia.PersistenciaException;

/**
 * DAO del catálogo de software.
 */
public class SoftwareDAO {

    private final IConexionBD conexion;

    public SoftwareDAO(IConexionBD conexion) {
        this.conexion = conexion;
    }

    public List<Software> listarTodos() throws PersistenciaException {
        List<Software> lista = new ArrayList<>();
        String sql = "SELECT id_software, nombre, descripcion FROM SOFTWARE ORDER BY nombre;";
        try (Connection con = conexion.crearConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Software(rs.getInt("id_software"), rs.getString("nombre"), rs.getString("descripcion")));
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al listar software: " + e.getMessage());
        }
        return lista;
    }

    public void insertar(Software s) throws PersistenciaException {
        String sql = "INSERT INTO SOFTWARE (nombre, descripcion) VALUES (?, ?);";
        try (Connection con = conexion.crearConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getNombre());
            ps.setString(2, s.getDescripcion());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) s.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al insertar software: " + e.getMessage());
        }
    }

    public void actualizar(Software s) throws PersistenciaException {
        String sql = "UPDATE SOFTWARE SET nombre = ?, descripcion = ? WHERE id_software = ?;";
        try (Connection con = conexion.crearConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getNombre());
            ps.setString(2, s.getDescripcion());
            ps.setInt(3, s.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al actualizar software: " + e.getMessage());
        }
    }

    public void eliminar(int idSoftware) throws PersistenciaException {
        String sql = "DELETE FROM SOFTWARE WHERE id_software = ?;";
        try (Connection con = conexion.crearConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idSoftware);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo eliminar el software (¿está asignado a computadoras?): " + e.getMessage());
        }
    }

    public boolean existeNombre(String nombre, int excluirId) throws PersistenciaException {
        String sql = "SELECT COUNT(*) FROM SOFTWARE WHERE LOWER(nombre) = LOWER(?) AND id_software <> ?;";
        try (Connection con = conexion.crearConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre.trim());
            ps.setInt(2, excluirId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al verificar software duplicado: " + e.getMessage());
        }
    }
}
