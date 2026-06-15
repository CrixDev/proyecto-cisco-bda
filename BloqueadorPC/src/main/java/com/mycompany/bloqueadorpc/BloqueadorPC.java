/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.bloqueadorpc;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import negocio.BloqueoNegocio;
import negocio.IBloqueoNegocio;
import persistencia.BloqueoDAO;
import persistencia.ConexionBD;
import persistencia.IBloqueoDAO;
import persistencia.IConexionBD;
import presentacion.Interfaz;
/**
 *
 * @author Dylan
 */
public class BloqueadorPC {

    public static void main(String[] args) {
        // Ajustar el diseño al sistema operativo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        // Inicializar la lógica de negocio antes de entrar al hilo de la interfaz
        // Esto evita que la GUI se congele si la conexión a la DB tarda en responder
        IConexionBD conexion = new ConexionBD();
        IBloqueoDAO bloqueoDAO = new BloqueoDAO(conexion);
        IBloqueoNegocio bloqueoNegocio = new BloqueoNegocio(bloqueoDAO);

        SwingUtilities.invokeLater(() -> {
            // Lanzar la interfaz gráfica
            new Interfaz(bloqueoNegocio);
        });
    }
}
