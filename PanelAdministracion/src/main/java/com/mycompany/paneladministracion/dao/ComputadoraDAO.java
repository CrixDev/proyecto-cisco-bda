/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.paneladministracion.dao;

import com.mycompany.paneladministracion.entidades.Computadora;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import persistencia.IConexionBD;
import persistencia.PersistenciaException;

/**
 * DAO de computadoras (JDBC contra CISCOBDA), incluye la asignación de software.
 *
 * @author Cristian Devora
 */
public class ComputadoraDAO {

    private final IConexionBD conexion;

    public ComputadoraDAO(IConexionBD conexion) {
        this.conexion = conexion;
    }

    public List<Computadora> listarTodas() throws PersistenciaException {
        List<Computadora> lista = new ArrayList<>();
        String sql = """
            SELECT c.id_computadora, c.numero_maquina, c.direccion_ip, c.estatus, c.tipo_computadora,
                   c.id_laboratorio, l.nombre AS lab_nombre
            FROM Computadoras c
            LEFT JOIN Laboratorios l ON c.id_laboratorio = l.id_laboratorio
            ORDER BY c.numero_maquina;
            """;
        try (Connection con = conexion.crearConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al listar computadoras: " + e.getMessage());
        }
        return lista;
    }

    public void insertar(Computadora c) throws PersistenciaException {
        String sql = "INSERT INTO Computadoras (numero_maquina, direccion_ip, estatus, tipo_computadora, id_laboratorio) VALUES (?, ?, ?, ?, ?);";
        try (Connection con = conexion.crearConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, c.getNumeroMaquina());
            ps.setString(2, c.getDireccionIp());
            ps.setString(3, c.getEstatus());
            ps.setString(4, c.getTipoComputadora());
            ps.setInt(5, c.getIdLaboratorio());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) c.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al insertar computadora: " + e.getMessage());
        }
    }

    public void actualizar(Computadora c) throws PersistenciaException {
        String sql = "UPDATE Computadoras SET numero_maquina = ?, direccion_ip = ?, estatus = ?, tipo_computadora = ?, id_laboratorio = ? WHERE id_computadora = ?;";
        try (Connection con = conexion.crearConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, c.getNumeroMaquina());
            ps.setString(2, c.getDireccionIp());
            ps.setString(3, c.getEstatus());
            ps.setString(4, c.getTipoComputadora());
            ps.setInt(5, c.getIdLaboratorio());
            ps.setInt(6, c.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al actualizar computadora: " + e.getMessage());
        }
    }

    public void eliminar(int idComputadora) throws PersistenciaException {
        // Primero se quitan las relaciones de software (la PC podría no tener préstamos)
        try (Connection con = conexion.crearConexion()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM computadorasoftware WHERE id_computadora = ?;")) {
                ps.setInt(1, idComputadora);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM Computadoras WHERE id_computadora = ?;")) {
                ps.setInt(1, idComputadora);
                ps.executeUpdate();
            }
            con.commit();
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo eliminar la computadora (¿tiene préstamos asociados?): " + e.getMessage());
        }
    }

    public boolean existeIp(String ip, int excluirId) throws PersistenciaException {
        String sql = "SELECT COUNT(*) FROM Computadoras WHERE direccion_ip = ? AND id_computadora <> ?;";
        try (Connection con = conexion.crearConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ip.trim());
            ps.setInt(2, excluirId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al verificar IP duplicada: " + e.getMessage());
        }
    }

    public Set<Integer> idsSoftwareAsignado(int idComputadora) throws PersistenciaException {
        Set<Integer> ids = new HashSet<>();
        String sql = "SELECT id_software FROM computadorasoftware WHERE id_computadora = ?;";
        try (Connection con = conexion.crearConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idComputadora);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ids.add(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al obtener software de la computadora: " + e.getMessage());
        }
        return ids;
    }

    public void asignarSoftware(int idComputadora, List<Integer> idsSoftware) throws PersistenciaException {
        try (Connection con = conexion.crearConexion()) {
            con.setAutoCommit(false);
            try (PreparedStatement del = con.prepareStatement("DELETE FROM computadorasoftware WHERE id_computadora = ?;")) {
                del.setInt(1, idComputadora);
                del.executeUpdate();
            }
            try (PreparedStatement ins = con.prepareStatement("INSERT INTO computadorasoftware (id_software, id_computadora) VALUES (?, ?);")) {
                for (Integer idSw : idsSoftware) {
                    ins.setInt(1, idSw);
                    ins.setInt(2, idComputadora);
                    ins.executeUpdate();
                }
            }
            con.commit();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al asignar software a la computadora: " + e.getMessage());
        }
    }

    private Computadora mapear(ResultSet rs) throws SQLException {
        Computadora c = new Computadora();
        c.setId(rs.getInt("id_computadora"));
        c.setNumeroMaquina(rs.getInt("numero_maquina"));
        c.setDireccionIp(rs.getString("direccion_ip"));
        c.setEstatus(rs.getString("estatus"));
        c.setTipoComputadora(rs.getString("tipo_computadora"));
        c.setIdLaboratorio(rs.getInt("id_laboratorio"));
        c.setLaboratorioNombre(rs.getString("lab_nombre"));
        return c;
    }
}
