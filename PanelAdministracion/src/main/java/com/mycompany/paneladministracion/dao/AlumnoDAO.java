/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.paneladministracion.dao;

import com.mycompany.paneladministracion.entidades.Alumno;
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
 * DAO de alumnos (JDBC contra CISCOBDA). La contraseña se recibe ya encriptada.
 *
 * @author Cristian Devora
 */
public class AlumnoDAO {

    private final IConexionBD conexion;

    public AlumnoDAO(IConexionBD conexion) {
        this.conexion = conexion;
    }

    public List<Alumno> listarTodos() throws PersistenciaException {
        List<Alumno> lista = new ArrayList<>();
        String sql = """
            SELECT a.id_alumno, a.nombre, a.apellido, a.estatus_inscripcion, a.contrasena,
                   a.id_carrera, c.nombre AS carrera_nombre,
                   b.id_bloqueo
            FROM Alumnos a
            LEFT JOIN Carreras c ON a.id_carrera = c.id_carrera
            LEFT JOIN Bloqueos b ON a.id_alumno = b.id_alumno AND b.fecha_desbloqueo IS NULL
            GROUP BY a.id_alumno, a.nombre, a.apellido, a.estatus_inscripcion, a.contrasena,
                     a.id_carrera, c.nombre, b.id_bloqueo
            ORDER BY a.nombre, a.apellido;
            """;
        try (Connection con = conexion.crearConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al listar alumnos: " + e.getMessage());
        }
        return lista;
    }

    public void insertar(Alumno a) throws PersistenciaException {
        String sql = "INSERT INTO Alumnos (nombre, apellido, estatus_inscripcion, contrasena, id_carrera) VALUES (?, ?, ?, ?, ?);";
        try (Connection con = conexion.crearConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, a.getNombre());
            ps.setString(2, a.getApellido());
            ps.setString(3, a.getEstatusInscripcion());
            ps.setString(4, a.getContrasena());
            ps.setInt(5, a.getIdCarrera());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) a.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al insertar alumno: " + e.getMessage());
        }
    }

    /**
     * Actualiza el alumno. Si nuevaContrasenaHash es null, conserva la contraseña actual.
     */
    public void actualizar(Alumno a, String nuevaContrasenaHash) throws PersistenciaException {
        StringBuilder sql = new StringBuilder("UPDATE Alumnos SET nombre = ?, apellido = ?, estatus_inscripcion = ?, id_carrera = ?");
        if (nuevaContrasenaHash != null) {
            sql.append(", contrasena = ?");
        }
        sql.append(" WHERE id_alumno = ?;");
        try (Connection con = conexion.crearConexion();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int i = 1;
            ps.setString(i++, a.getNombre());
            ps.setString(i++, a.getApellido());
            ps.setString(i++, a.getEstatusInscripcion());
            ps.setInt(i++, a.getIdCarrera());
            if (nuevaContrasenaHash != null) {
                ps.setString(i++, nuevaContrasenaHash);
            }
            ps.setInt(i, a.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al actualizar alumno: " + e.getMessage());
        }
    }

    public void eliminar(int idAlumno) throws PersistenciaException {
        String sql = "DELETE FROM Alumnos WHERE id_alumno = ?;";
        try (Connection con = conexion.crearConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idAlumno);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo eliminar el alumno (¿tiene préstamos o bloqueos asociados?): " + e.getMessage());
        }
    }

    public boolean existeDuplicado(String nombre, String apellido, int excluirId) throws PersistenciaException {
        String sql = "SELECT COUNT(*) FROM Alumnos WHERE LOWER(nombre) = LOWER(?) AND LOWER(apellido) = LOWER(?) AND id_alumno <> ?;";
        try (Connection con = conexion.crearConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre.trim());
            ps.setString(2, apellido.trim());
            ps.setInt(3, excluirId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al verificar alumno duplicado: " + e.getMessage());
        }
    }

    private Alumno mapear(ResultSet rs) throws SQLException {
        Alumno a = new Alumno();
        a.setId(rs.getInt("id_alumno"));
        a.setNombre(rs.getString("nombre"));
        a.setApellido(rs.getString("apellido"));
        a.setEstatusInscripcion(rs.getString("estatus_inscripcion"));
        a.setContrasena(rs.getString("contrasena"));
        a.setIdCarrera(rs.getInt("id_carrera"));
        a.setCarreraNombre(rs.getString("carrera_nombre"));
        a.setBloqueado(rs.getObject("id_bloqueo") != null);
        return a;
    }
}
