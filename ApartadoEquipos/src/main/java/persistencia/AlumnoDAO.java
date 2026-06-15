/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistencia;

import entidad.Alumno;
import entidad.Carrera;
import java.sql.*;
import java.time.LocalTime;

public class AlumnoDAO implements IAlumnoDAO {

    private final IConexionBD conexion;

    public AlumnoDAO(IConexionBD conexion) {
        this.conexion = conexion;
    }

    @Override
    public Alumno buscarPorNumeroControl(String numeroControl) throws PersistenciaException {
        // En tu BD el número de control equivale al id_alumno (INT)
        String sql = """
            SELECT a.id_alumno, a.nombre, a.apellido, a.estatus_inscripcion, a.contrasena,
                   c.id_carrera, c.nombre AS carrera_nombre, c.tiempo_limite_diario,
                   b.id_bloqueo, b.motivo
            FROM Alumnos a
            INNER JOIN Carreras c ON a.id_carrera = c.id_carrera
            LEFT JOIN Bloqueos b ON a.id_alumno = b.id_alumno AND b.fecha_desbloqueo IS NULL
            WHERE a.id_alumno = ?;
            """;

        try (Connection con = conexion.crearConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, Integer.parseInt(numeroControl.trim()));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Carrera carrera = new Carrera();
                    carrera.setId(rs.getInt("id_carrera"));
                    carrera.setNombre(rs.getString("carrera_nombre"));
                    carrera.setTiempoLimiteDiario(rs.getObject("tiempo_limite_diario", LocalTime.class));

                    Alumno alumno = new Alumno();
                    alumno.setId(rs.getInt("id_alumno"));
                    alumno.setNombre(rs.getString("nombre"));
                    alumno.setApellido(rs.getString("apellido"));
                    alumno.setEstatusInscripcion(rs.getString("estatus_inscripcion"));
                    alumno.setContrasena(rs.getString("contrasena"));
                    alumno.setCarrera(carrera);

                    // Validación del bloqueo gestionado por el otro compañero
                    if (rs.getObject("id_bloqueo") != null) {
                        alumno.setBloqueado(true);
                        alumno.setMotivoBloqueo(rs.getString("motivo"));
                    } else {
                        alumno.setBloqueado(false);
                    }
                    return alumno;
                }
            }
        } catch (NumberFormatException e) {
            return null; // Control de error si digitan letras
        } catch (SQLException e) {
            throw new PersistenciaException("Error JDBC al buscar alumno: " + e.getMessage());
        }
        return null;
    }
}
