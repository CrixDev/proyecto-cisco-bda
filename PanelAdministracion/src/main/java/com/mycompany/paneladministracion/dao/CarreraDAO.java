package com.mycompany.paneladministracion.dao;

import com.mycompany.paneladministracion.entidades.Carrera;
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
 * DAO de carreras (licenciaturas) del ITSON.
 */
public class CarreraDAO {

    private final IConexionBD conexion;

    public CarreraDAO(IConexionBD conexion) {
        this.conexion = conexion;
    }

    public List<Carrera> listarTodas() throws PersistenciaException {
        List<Carrera> lista = new ArrayList<>();
        String sql = "SELECT id_carrera, nombre, tiempo_limite_diario, telefono_academia FROM Carreras ORDER BY nombre;";
        try (Connection con = conexion.crearConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al listar carreras: " + e.getMessage());
        }
        return lista;
    }

    public void insertar(Carrera c) throws PersistenciaException {
        String sql = "INSERT INTO Carreras (nombre, tiempo_limite_diario, telefono_academia) VALUES (?, ?, ?);";
        try (Connection con = conexion.crearConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getNombre());
            ps.setTime(2, Time.valueOf(c.getTiempoLimiteDiario()));
            ps.setString(3, c.getTelefonoAcademia());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) c.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al insertar carrera: " + e.getMessage());
        }
    }

    public void actualizar(Carrera c) throws PersistenciaException {
        String sql = "UPDATE Carreras SET nombre = ?, tiempo_limite_diario = ?, telefono_academia = ? WHERE id_carrera = ?;";
        try (Connection con = conexion.crearConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setTime(2, Time.valueOf(c.getTiempoLimiteDiario()));
            ps.setString(3, c.getTelefonoAcademia());
            ps.setInt(4, c.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al actualizar carrera: " + e.getMessage());
        }
    }

    public void eliminar(int idCarrera) throws PersistenciaException {
        String sql = "DELETE FROM Carreras WHERE id_carrera = ?;";
        try (Connection con = conexion.crearConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCarrera);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo eliminar la carrera (¿tiene alumnos asociados?): " + e.getMessage());
        }
    }

    public boolean existeNombre(String nombre, int excluirId) throws PersistenciaException {
        String sql = "SELECT COUNT(*) FROM Carreras WHERE LOWER(nombre) = LOWER(?) AND id_carrera <> ?;";
        try (Connection con = conexion.crearConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre.trim());
            ps.setInt(2, excluirId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al verificar carrera duplicada: " + e.getMessage());
        }
    }

    private Carrera mapear(ResultSet rs) throws SQLException {
        Carrera c = new Carrera();
        c.setId(rs.getInt("id_carrera"));
        c.setNombre(rs.getString("nombre"));
        Time t = rs.getTime("tiempo_limite_diario");
        c.setTiempoLimiteDiario(t != null ? t.toLocalTime() : LocalTime.of(2, 0));
        c.setTelefonoAcademia(rs.getString("telefono_academia"));
        return c;
    }
}
