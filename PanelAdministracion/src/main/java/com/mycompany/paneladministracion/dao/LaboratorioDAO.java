package com.mycompany.paneladministracion.dao;

import com.mycompany.paneladministracion.entidades.Laboratorio;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import persistencia.IConexionBD;
import persistencia.PersistenciaException;

/**
 * DAO de laboratorios. La contraseña maestra se recibe ya encriptada.
 */
public class LaboratorioDAO {

    private final IConexionBD conexion;

    public LaboratorioDAO(IConexionBD conexion) {
        this.conexion = conexion;
    }

    public List<Laboratorio> listarTodos() throws PersistenciaException {
        List<Laboratorio> lista = new ArrayList<>();
        String sql = """
            SELECT l.id_laboratorio, l.nombre, l.contrasena_maestra, l.hora_inicio, l.hora_fin,
                   l.id_instituto, i.nombre_oficial
            FROM Laboratorios l
            LEFT JOIN Institutos i ON l.id_instituto = i.id_instituto
            ORDER BY l.nombre;
            """;
        try (Connection con = conexion.crearConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al listar laboratorios: " + e.getMessage());
        }
        return lista;
    }

    public void insertar(Laboratorio l) throws PersistenciaException {
        String sql = "INSERT INTO Laboratorios (nombre, contrasena_maestra, hora_inicio, hora_fin, id_instituto) VALUES (?, ?, ?, ?, ?);";
        try (Connection con = conexion.crearConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, l.getNombre());
            ps.setString(2, l.getContrasenaMaestra());
            ps.setTime(3, Time.valueOf(l.getHoraInicio()));
            ps.setTime(4, Time.valueOf(l.getHoraFin()));
            ps.setInt(5, l.getIdInstituto());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) l.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al insertar laboratorio: " + e.getMessage());
        }
    }

    /**
     * Actualiza el laboratorio. Si nuevaContrasenaHash es null, no cambia la contraseña.
     */
    public void actualizar(Laboratorio l, String nuevaContrasenaHash) throws PersistenciaException {
        StringBuilder sql = new StringBuilder("UPDATE Laboratorios SET nombre = ?, hora_inicio = ?, hora_fin = ?, id_instituto = ?");
        if (nuevaContrasenaHash != null) {
            sql.append(", contrasena_maestra = ?");
        }
        sql.append(" WHERE id_laboratorio = ?;");
        try (Connection con = conexion.crearConexion();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int i = 1;
            ps.setString(i++, l.getNombre());
            ps.setTime(i++, Time.valueOf(l.getHoraInicio()));
            ps.setTime(i++, Time.valueOf(l.getHoraFin()));
            ps.setInt(i++, l.getIdInstituto());
            if (nuevaContrasenaHash != null) {
                ps.setString(i++, nuevaContrasenaHash);
            }
            ps.setInt(i, l.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al actualizar laboratorio: " + e.getMessage());
        }
    }

    public void eliminar(int idLaboratorio) throws PersistenciaException {
        String sql = "DELETE FROM Laboratorios WHERE id_laboratorio = ?;";
        try (Connection con = conexion.crearConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idLaboratorio);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo eliminar el laboratorio (¿tiene computadoras asociadas?): " + e.getMessage());
        }
    }

    private Laboratorio mapear(ResultSet rs) throws SQLException {
        Laboratorio l = new Laboratorio();
        l.setId(rs.getInt("id_laboratorio"));
        l.setNombre(rs.getString("nombre"));
        l.setContrasenaMaestra(rs.getString("contrasena_maestra"));
        Time ini = rs.getTime("hora_inicio");
        Time fin = rs.getTime("hora_fin");
        l.setHoraInicio(ini != null ? ini.toLocalTime() : LocalTime.of(7, 0));
        l.setHoraFin(fin != null ? fin.toLocalTime() : LocalTime.of(21, 0));
        l.setIdInstituto(rs.getInt("id_instituto"));
        l.setInstitutoNombre(rs.getString("nombre_oficial"));
        return l;
    }
}
