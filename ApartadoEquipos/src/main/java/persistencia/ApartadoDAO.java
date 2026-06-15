/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistencia;

import entidad.Apartado;
import entidad.Computadora;
import entidad.Software;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ApartadoDAO implements IApartadoDAO {

    private final IConexionBD conexion;

    public ApartadoDAO(IConexionBD conexion) {
        this.conexion = conexion;
    }

    @Override
    public Apartado guardar(Apartado apartado) throws PersistenciaException {
        Connection conTransaccion = null;
        try {
            conTransaccion = conexion.crearConexion();
            conTransaccion.setAutoCommit(false); // Estilo transaccional controlado

            // 1. Obtener o crear el id_prestamosPD para la fecha de hoy en el laboratorio del equipo
            int idPrestamosPD = obtenerOCrearPrestamosPD(conTransaccion, apartado.getComputadora().getIdLaboratorio());

            // 2. Insertar en la tabla Prestamos
            String sqlInsert = """
                INSERT INTO Prestamos (inicio_prestamo, fin_prestamo, id_alumno, id_computadora, id_prestamosPD)
                VALUES (?, ?, ?, ?, ?);
                """;
            
            try (PreparedStatement ps = conTransaccion.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
                ps.setObject(1, apartado.getFechaHoraInicio());
                ps.setObject(2, apartado.getFechaHoraFin());
                ps.setInt(3, apartado.getAlumno().getId());
                ps.setInt(4, apartado.getComputadora().getId());
                ps.setInt(5, idPrestamosPD);
                ps.executeUpdate();

                try (ResultSet rsKeys = ps.getGeneratedKeys()) {
                    if (rsKeys.next()) {
                        apartado.setId(rsKeys.getInt(1));
                    }
                }
            }

            // 3. Marcar la computadora como 'Apartada' (mantener estatus sincronizado)
            String sqlEstatus = "UPDATE Computadoras SET estatus = 'Apartada' WHERE id_computadora = ?;";
            try (PreparedStatement ps = conTransaccion.prepareStatement(sqlEstatus)) {
                ps.setInt(1, apartado.getComputadora().getId());
                ps.executeUpdate();
            }

            conTransaccion.commit();
            return apartado;

        } catch (Exception e) {
            if (conTransaccion != null) {
                try { conTransaccion.rollback(); } catch (SQLException ex) { /* Ignorar */ }
            }
            throw new PersistenciaException("Error al guardar el apartado: " + e.getMessage());
        } finally {
            if (conTransaccion != null) {
                try { conTransaccion.close(); } catch (SQLException ex) { /* Ignorar */ }
            }
        }
    }

    private int obtenerOCrearPrestamosPD(Connection con, int idLaboratorio) throws SQLException {
        String query = "SELECT id_prestamosPD FROM PrestamosPD WHERE fecha = ? AND id_laboratorio = ?;";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setObject(1, LocalDate.now());
            ps.setInt(2, idLaboratorio);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }

        // Tomar el horario de servicio real del laboratorio (no horas fijas)
        Time horaInicio = Time.valueOf("07:00:00");
        Time horaFin = Time.valueOf("21:00:00");
        String sqlHorario = "SELECT hora_inicio, hora_fin FROM Laboratorios WHERE id_laboratorio = ?;";
        try (PreparedStatement ps = con.prepareStatement(sqlHorario)) {
            ps.setInt(1, idLaboratorio);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    if (rs.getTime("hora_inicio") != null) horaInicio = rs.getTime("hora_inicio");
                    if (rs.getTime("hora_fin") != null) horaFin = rs.getTime("hora_fin");
                }
            }
        }

        String insert = "INSERT INTO PrestamosPD (hora_Inicio, hora_fin, fecha, id_laboratorio) VALUES (?, ?, ?, ?);";
        try (PreparedStatement ps = con.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setTime(1, horaInicio);
            ps.setTime(2, horaFin);
            ps.setObject(3, LocalDate.now());
            ps.setInt(4, idLaboratorio);
            ps.executeUpdate();
            try (ResultSet rsKeys = ps.getGeneratedKeys()) {
                if (rsKeys.next()) return rsKeys.getInt(1);
            }
        }
        return 0;
    }

    @Override
    public List<Computadora> obtenerComputadorasPorCentro(Long laboratorioId) throws PersistenciaException {
        List<Computadora> computadoras = new ArrayList<>();

        // Consulta SQL base con el LEFT JOIN dinámico exigido por la escuela
        String sqlComp = """
            SELECT c.id_computadora, c.numero_maquina, c.direccion_ip, c.estatus, c.id_laboratorio,
                   CONCAT(a.nombre, ' ', a.apellido) AS alumno_nombre
            FROM Computadoras c
            LEFT JOIN Prestamos p ON c.id_computadora = p.id_computadora AND p.fin_prestamo IS NULL
            LEFT JOIN Alumnos a ON p.id_alumno = a.id_alumno
            WHERE c.id_laboratorio = ?;
            """;

        try (Connection con = conexion.crearConexion();
             PreparedStatement ps = con.prepareStatement(sqlComp)) {

            ps.setLong(1, laboratorioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Computadora pc = new Computadora();
                    pc.setId(rs.getInt("id_computadora"));
                    pc.setNumeroMaquinaRaw(rs.getInt("numero_maquina"));
                    pc.setDireccionIp(rs.getString("direccion_ip"));
                    pc.setEstatus(rs.getString("estatus"));
                    pc.setIdLaboratorio(rs.getInt("id_laboratorio"));

                    String alumnoOcupante = rs.getString("alumno_nombre");
                    if (alumnoOcupante != null) {
                        pc.setNombreAlumnoActual(alumnoOcupante);
                    } else {
                        pc.setNombreAlumnoActual("Disponible");
                    }

                    // ── ¡NUEVO UPDATE DE CÓDIGO! ──
                    // Busca y carga los programas instalados en esta máquina desde la BD
                    pc.setSoftware(obtenerSoftwareDeComputadora(con, pc.getId()));

                    computadoras.add(pc);
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al cargar computadoras con software: " + e.getMessage());
        }
        return computadoras;
    }

    // Método auxiliar indispensable dentro de ApartadoDAO.java para leer la relación Muchos a Muchos
    private List<Software> obtenerSoftwareDeComputadora(Connection con, int idComputadora) throws SQLException {
        List<Software> lista = new ArrayList<>();
        String sql = """
            SELECT s.id_software, s.nombre, s.descripcion 
            FROM computadorasoftware cs
            INNER JOIN SOFTWARE s ON cs.id_software = s.id_software
            WHERE cs.id_computadora = ?;
            """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idComputadora);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Software sw = new Software();
                    sw.setId(rs.getInt("id_software"));
                    sw.setNombre(rs.getString("nombre"));
                    sw.setDescripcion(rs.getString("descripcion"));
                    lista.add(sw);
                }
            }
        }
        return lista;
    }

    @Override
    public boolean tieneApartadoActivo(String numeroControl) throws PersistenciaException {
        String sql = "SELECT COUNT(*) FROM Prestamos WHERE id_alumno = ? AND fin_prestamo IS NULL;";
        try (Connection con = conexion.crearConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(numeroControl.trim()));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (NumberFormatException e) {
            throw new PersistenciaException("El número de control debe ser numérico: " + numeroControl);
        } catch (SQLException e) {
            throw new PersistenciaException("Error al verificar apartados activos: " + e.getMessage());
        }
        return false;
    }

    @Override
    public int obtenerMinutosUsadosHoy(String numeroControl) throws PersistenciaException {
        String sql = "SELECT inicio_prestamo, fin_prestamo FROM Prestamos WHERE id_alumno = ? AND DATE(inicio_prestamo) = CURDATE();";
        int totalMinutos = 0;
        try (Connection con = conexion.crearConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(numeroControl.trim()));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LocalDateTime inicio = rs.getObject("inicio_prestamo", LocalDateTime.class);
                    LocalDateTime fin = rs.getObject("fin_prestamo", LocalDateTime.class);
                    if (fin == null) fin = LocalDateTime.now();
                    
                    totalMinutos += (int) java.time.Duration.between(inicio, fin).toMinutes();
                }
            }
        } catch (NumberFormatException e) {
            throw new PersistenciaException("El número de control debe ser numérico: " + numeroControl);
        } catch (SQLException e) {
            throw new PersistenciaException("Error al calcular el tiempo de uso diario: " + e.getMessage());
        }
        return totalMinutos;
    }

    @Override
    public Apartado obtenerApartadoActivo(String numeroControl) throws PersistenciaException {
        String sql = "SELECT id_prestamo, inicio_prestamo, id_computadora FROM Prestamos WHERE id_alumno = ? AND fin_prestamo IS NULL;";
        try (Connection con = conexion.crearConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(numeroControl.trim()));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Apartado ap = new Apartado();
                    ap.setId(rs.getInt("id_prestamo"));
                    ap.setFechaHoraInicio(rs.getObject("inicio_prestamo", LocalDateTime.class));
                    
                    Computadora pc = new Computadora();
                    pc.setId(rs.getInt("id_computadora"));
                    ap.setComputadora(pc);
                    return ap;
                }
            }
        } catch (Exception e) {
            throw new PersistenciaException("Error al obtener apartado activo.");
        }
        return null;
    }
    public void terminarPrestamo(String numeroControl) throws PersistenciaException {
        String sql = "UPDATE Prestamos SET fin_prestamo = NOW() WHERE id_alumno = ? AND fin_prestamo IS NULL;";
        try (Connection con = conexion.crearConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(numeroControl.trim()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al liberar la computadora: " + e.getMessage());
        }
    }
}