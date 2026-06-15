/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.paneladministracion;

import com.mycompany.paneladministracion.dao.AlumnoDAO;
import com.mycompany.paneladministracion.dao.ApartadoDAO;
import com.mycompany.paneladministracion.dao.BloqueoDAO;
import com.mycompany.paneladministracion.dao.CarreraDAO;
import com.mycompany.paneladministracion.dao.ComputadoraDAO;
import com.mycompany.paneladministracion.dao.InstitutoDAO;
import com.mycompany.paneladministracion.dao.LaboratorioDAO;
import com.mycompany.paneladministracion.dao.SoftwareDAO;
import com.mycompany.paneladministracion.gui.VentanaPrincipal;
import com.mycompany.paneladministracion.negocio.AdminNegocio;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import persistencia.ConexionBD;
import persistencia.DatosIniciales;
import persistencia.IConexionBD;

/**
 *
 * @author Cristian Devora
 */
public class PanelAdministracion {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            IConexionBD conexion = new ConexionBD();

            // Sembrar catálogo del ITSON (unidades y carreras) si la BD está vacía
            new DatosIniciales(conexion).insertar();

            // Capa de persistencia (DAOs)
            AlumnoDAO alumnoDAO = new AlumnoDAO(conexion);
            ComputadoraDAO computadoraDAO = new ComputadoraDAO(conexion);
            CarreraDAO carreraDAO = new CarreraDAO(conexion);
            LaboratorioDAO laboratorioDAO = new LaboratorioDAO(conexion);
            InstitutoDAO institutoDAO = new InstitutoDAO(conexion);
            SoftwareDAO softwareDAO = new SoftwareDAO(conexion);
            BloqueoDAO bloqueoDAO = new BloqueoDAO(conexion);
            ApartadoDAO apartadoDAO = new ApartadoDAO(conexion);

            // Capa de negocio (validaciones, encriptación, notificación)
            AdminNegocio negocio = new AdminNegocio(alumnoDAO, computadoraDAO, carreraDAO,
                    laboratorioDAO, institutoDAO, softwareDAO, bloqueoDAO, apartadoDAO);

            // Interfaz gráfica
            new VentanaPrincipal(negocio).setVisible(true);
        });
    }
}
