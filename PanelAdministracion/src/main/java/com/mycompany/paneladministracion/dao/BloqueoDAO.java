package com.mycompany.paneladministracion.dao;

import com.mycompany.paneladministracion.entidades.Bloqueo;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import persistencia.IConexionBD;
import persistencia.PersistenciaException;

/**
 * DAO de bloqueos de alumnos.
 */
public class BloqueoDAO {

    private final IConexionBD conexion;

    public BloqueoDAO(IConexionBD conexion) {
        this.conexion = conexion;
    }

    public void bloquear(int idAlumno, LocalDate fechaBloqueo, String motivo) throws PersistenciaException {
        String sql = "INSERT INTO Bloqueos (fecha_bloqueo, fecha_desbloqueo, motivo, id_alumno) VALUES (?, NULL, ?, ?);";
        try (Connection con = conexion.crearConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fechaBloqueo));
            ps.setString(2, motivo);
            ps.setInt(3, idAlumno);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al registrar el bloqueo: " + e.getMessage());
        }
    }

    public void desbloquear(int idAlumno) throws PersistenciaException {
        String sql = "UPDATE Bloqueos SET fecha_desbloqueo = ? WHERE id_alumno = ? AND fecha_desbloqueo IS NULL;";
        try (Connection con = conexion.crearConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(LocalDate.now()));
            ps.setInt(2, idAlumno);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al desbloquear el alumno: " + e.getMessage());
        }
    }

    public boolean tieneBloqueoActivo(int idAlumno) throws PersistenciaException {
        String sql = "SELECT COUNT(*) FROM Bloqueos WHERE id_alumno = ? AND fecha_desbloqueo IS NULL;";
        try (Connection con = conexion.crearConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idAlumno);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al verificar el bloqueo: " + e.getMessage());
        }
    }

    public List<Bloqueo> listarActivos() throws PersistenciaException {
        List<Bloqueo> lista = new ArrayList<>();
        String sql = """
            SELECT b.id_bloqueo, b.fecha_bloqueo, b.fecha_desbloqueo, b.motivo, b.id_alumno,
                   CONCAT(a.nombre, ' ', a.apellido) AS alumno_nombre
            FROM Bloqueos b
            INNER JOIN Alumnos a ON b.id_alumno = a.id_alumno
            WHERE b.fecha_desbloqueo IS NULL
            ORDER BY b.fecha_bloqueo DESC;
            """;
        try (Connection con = conexion.crearConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Bloqueo b = new Bloqueo();
                b.setId(rs.getInt("id_bloqueo"));
                Date fb = rs.getDate("fecha_bloqueo");
                Date fd = rs.getDate("fecha_desbloqueo");
                b.setFechaBloqueo(fb != null ? fb.toLocalDate() : null);
                b.setFechaDesbloqueo(fd != null ? fd.toLocalDate() : null);
                b.setMotivo(rs.getString("motivo"));
                b.setIdAlumno(rs.getInt("id_alumno"));
                b.setAlumnoNombre(rs.getString("alumno_nombre"));
                lista.add(b);
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al listar bloqueos: " + e.getMessage());
        }
        return lista;
    }
}
