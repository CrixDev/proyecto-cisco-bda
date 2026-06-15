/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistencia;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;

import dto.SesionDTO;
import entidad.Alumno;
import entidad.Computadora;
import entidad.Instituto;
import entidad.Laboratorio;

/**
 *
 * @author Dylan
 */
public class BloqueoDAO implements IBloqueoDAO {

    private final IConexionBD conexion;
    private String ipCacheada = null;

    public BloqueoDAO(IConexionBD conexion) {
        this.conexion = conexion;
    }

    // ══════════════════════════════════════════
    //  PASO 1 — IDENTIFICAR UBICACIÓN
    // ══════════════════════════════════════════
    @Override
    public Computadora identificarComputadora() throws PersistenciaException {
        if (ipCacheada == null) {
            ipCacheada = obtenerIpLocal();
        }
        String ipLocal = ipCacheada;

        String sql = "SELECT id_computadora, numero_maquina, direccion_ip, estatus, id_laboratorio "
                + "FROM Computadoras WHERE direccion_ip = ?;";

        try (Connection con = conexion.crearConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, ipLocal);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearComputadora(rs);
                }
            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error al identificar la computadora: " + e.getMessage());
        }

        // No se cae silenciosamente a otra PC: si la IP de esta máquina no está
        // registrada, se informa con claridad para que se registre correctamente.
        throw new PersistenciaException("Esta computadora (IP " + ipLocal
                + ") no está registrada en la base de datos. Regístrala con su IP real en el Panel de Administración.");
    }

    private Computadora mapearComputadora(ResultSet rs) throws SQLException {
        Computadora pc = new Computadora();
        pc.setId(rs.getInt("id_computadora"));
        pc.setNumeroMaquinaRaw(rs.getInt("numero_maquina"));
        pc.setDireccionIp(rs.getString("direccion_ip"));
        pc.setEstatus(rs.getString("estatus"));
        pc.setIdLaboratorio(rs.getInt("id_laboratorio"));
        return pc;
    }

    /**
     * Detecta la IPv4 real de la máquina (LAN) recorriendo las interfaces de red
     * y descartando loopback, interfaces caídas y adaptadores virtuales
     * (VPN/VMware/VirtualBox/Hyper-V). Es más fiable que InetAddress.getLocalHost(),
     * que suele devolver 127.0.0.1 o la IP de un adaptador virtual.
     */
    private String obtenerIpLocal() {
        try {
            Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
            while (ifaces.hasMoreElements()) {
                NetworkInterface iface = ifaces.nextElement();
                if (!iface.isUp() || iface.isLoopback() || iface.isVirtual()) {
                    continue;
                }
                String nombre = iface.getDisplayName() == null ? "" : iface.getDisplayName().toLowerCase();
                if (nombre.contains("virtual") || nombre.contains("vmware")
                        || nombre.contains("vbox") || nombre.contains("hyper-v")
                        || nombre.contains("loopback")) {
                    continue;
                }
                Enumeration<InetAddress> addrs = iface.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    InetAddress addr = addrs.nextElement();
                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress() && addr.isSiteLocalAddress()) {
                        return addr.getHostAddress();
                    }
                }
            }
            // Último recurso si no se encontró una IP de LAN
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "0.0.0.0";
        }
    }

    @Override
    public Laboratorio obtenerLaboratorio(int idLaboratorio) throws PersistenciaException {
        String sql = """
            SELECT l.id_laboratorio, l.nombre AS lab_nombre, l.contrasena_maestra,
                   i.id_instituto, i.nombre_oficial, i.nombre_abreviado
            FROM Laboratorios l
            INNER JOIN Institutos i ON l.id_instituto = i.id_instituto
            WHERE l.id_laboratorio = ?;
            """;

        try (Connection con = conexion.crearConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idLaboratorio);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Instituto inst = new Instituto();
                    inst.setId(rs.getInt("id_instituto"));
                    inst.setNombreOficial(rs.getString("nombre_oficial"));
                    inst.setNombreAbreviado(rs.getString("nombre_abreviado"));

                    Laboratorio lab = new Laboratorio();
                    lab.setId(rs.getInt("id_laboratorio"));
                    lab.setNombre(rs.getString("lab_nombre"));
                    lab.setContrasenaMaestra(rs.getString("contrasena_maestra"));
                    lab.setInstituto(inst);
                    return lab;
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al obtener el laboratorio: " + e.getMessage());
        }
        return null;
    }

    // ══════════════════════════════════════════
    //  PASO 2 — VERIFICAR APARTADO / MOSTRAR ALUMNO
    // ══════════════════════════════════════════
    @Override
    public SesionDTO obtenerApartadoActivo(int idComputadora) throws PersistenciaException {
        String sql = """
            SELECT p.id_prestamo, a.id_alumno, a.nombre, a.apellido
            FROM Prestamos p
            INNER JOIN Alumnos a ON p.id_alumno = a.id_alumno
            WHERE p.id_computadora = ? AND p.fin_prestamo IS NULL
            ORDER BY p.inicio_prestamo DESC
            LIMIT 1;
            """;

        try (Connection con = conexion.crearConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idComputadora);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    SesionDTO sesion = new SesionDTO();
                    sesion.setIdPrestamo(rs.getInt("id_prestamo"));
                    sesion.setIdAlumno(rs.getInt("id_alumno"));
                    sesion.setNombreCompleto(rs.getString("nombre") + " " + rs.getString("apellido"));
                    sesion.setIdComputadora(idComputadora);
                    return sesion;
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al verificar el apartado activo: " + e.getMessage());
        }
        return null;
    }

    // ══════════════════════════════════════════
    //  PASO 3 — DESBLOQUEAR (VALIDAR CONTRASEÑA)
    // ══════════════════════════════════════════
    @Override
    public Alumno obtenerAlumno(int idAlumno) throws PersistenciaException {
        String sql = "SELECT id_alumno, nombre, apellido, contrasena FROM Alumnos WHERE id_alumno = ?;";

        try (Connection con = conexion.crearConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idAlumno);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Alumno alumno = new Alumno();
                    alumno.setId(rs.getInt("id_alumno"));
                    alumno.setNombre(rs.getString("nombre"));
                    alumno.setApellido(rs.getString("apellido"));
                    alumno.setContrasena(rs.getString("contrasena"));
                    return alumno;
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al obtener los datos del alumno: " + e.getMessage());
        }
        return null;
    }

    // ══════════════════════════════════════════
    //  PASO 4 — LIBERAR APARTADO
    // ══════════════════════════════════════════
    @Override
    public void liberarApartado(int idComputadora) throws PersistenciaException {
        Connection con = null;
        try {
            con = conexion.crearConexion();
            con.setAutoCommit(false);

            String sqlFin = "UPDATE Prestamos SET fin_prestamo = NOW() WHERE id_computadora = ? AND fin_prestamo IS NULL;";
            try (PreparedStatement ps = con.prepareStatement(sqlFin)) {
                ps.setInt(1, idComputadora);
                ps.executeUpdate();
            }

            String sqlEstatus = "UPDATE Computadoras SET estatus = 'Disponible' WHERE id_computadora = ?;";
            try (PreparedStatement ps = con.prepareStatement(sqlEstatus)) {
                ps.setInt(1, idComputadora);
                ps.executeUpdate();
            }

            con.commit();
        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    /* Ignorar */ }
            }
            throw new PersistenciaException("Error al liberar el apartado: " + e.getMessage());
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    /* Ignorar */ }
            }
        }
    }
}
