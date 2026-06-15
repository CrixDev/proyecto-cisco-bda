/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistencia;

import entidad.Instituto;
import entidad.Laboratorio;
import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class CentroDAO {

    private final IConexionBD conexion;

    public CentroDAO(IConexionBD conexion) {
        this.conexion = conexion;
    }

    public List<Laboratorio> obtenerTodos() throws PersistenciaException {
        List<Laboratorio> lista = new ArrayList<>();
        String sql = """
            SELECT l.id_laboratorio, l.nombre AS lab_nombre, l.contrasena_maestra,
                   l.hora_inicio, l.hora_fin,
                   i.id_instituto, i.nombre_oficial, i.nombre_abreviado
            FROM Laboratorios l
            INNER JOIN Institutos i ON l.id_instituto = i.id_instituto
            ORDER BY l.nombre;
            """;

        try (Connection con = conexion.crearConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Instituto inst = new Instituto();
                inst.setId(rs.getInt("id_instituto"));
                inst.setNombreOficial(rs.getString("nombre_oficial"));
                inst.setNombreAbreviado(rs.getString("nombre_abreviado"));

                Laboratorio lab = new Laboratorio();
                lab.setId(rs.getInt("id_laboratorio"));
                lab.setNombre(rs.getString("lab_nombre"));
                lab.setContrasenaMaestra(rs.getString("contrasena_maestra"));
                lab.setHoraInicio(rs.getObject("hora_inicio", LocalTime.class));
                lab.setHoraFin(rs.getObject("hora_fin", LocalTime.class));
                lab.setInstituto(inst);

                lista.add(lab);
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error JDBC al obtener laboratorios: " + e.getMessage());
        }
        return lista;
    }

    public Laboratorio obtenerPrimero() throws PersistenciaException {
        List<Laboratorio> lista = obtenerTodos();
        return lista.isEmpty() ? null : lista.get(0);
    }
}
