/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.paneladministracion.dao;

import com.mycompany.paneladministracion.entidades.Apartado;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import persistencia.IConexionBD;
import persistencia.PersistenciaException;

/**
 * DAO de consulta de apartados (préstamos) para el panel del encargado.
 *
 * @author Cristian Devora
 */
public class ApartadoDAO {

    private final IConexionBD conexion;

    public ApartadoDAO(IConexionBD conexion) {
        this.conexion = conexion;
    }

    public List<Apartado> listarTodos() throws PersistenciaException {
        List<Apartado> lista = new ArrayList<>();
        String sql = """
            SELECT p.id_prestamo, p.inicio_prestamo, p.fin_prestamo,
                   CONCAT(a.nombre, ' ', a.apellido) AS alumno_nombre,
                   c.numero_maquina, l.nombre AS lab_nombre
            FROM Prestamos p
            INNER JOIN Alumnos a ON p.id_alumno = a.id_alumno
            INNER JOIN Computadoras c ON p.id_computadora = c.id_computadora
            LEFT JOIN Laboratorios l ON c.id_laboratorio = l.id_laboratorio
            ORDER BY p.inicio_prestamo DESC;
            """;
        try (Connection con = conexion.crearConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Apartado ap = new Apartado();
                ap.setId(rs.getInt("id_prestamo"));
                ap.setAlumnoNombre(rs.getString("alumno_nombre"));
                ap.setNumeroMaquina(rs.getInt("numero_maquina"));
                ap.setLaboratorioNombre(rs.getString("lab_nombre"));
                ap.setInicioPrestamo(rs.getObject("inicio_prestamo", LocalDateTime.class));
                ap.setFinPrestamo(rs.getObject("fin_prestamo", LocalDateTime.class));
                lista.add(ap);
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al listar apartados: " + e.getMessage());
        }
        return lista;
    }
}
