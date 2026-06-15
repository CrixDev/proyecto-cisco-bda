/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.apartadoequipos;
    import persistencia.*;
    import negocio.*;
    import presentacion.Interfaz;
    import javax.swing.SwingUtilities;
    import javax.swing.UIManager;

public class ApartadoEquipos {

    public static void main(String[] args) {
        // Ajustar el diseño al sistema operativo
        try { 
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            // 1. Inicializar la conexión JDBC nativa
            IConexionBD conexion = new ConexionBD();

            // 2. Sembrar unidades, carreras y datos demo de ITSON si la base de datos está vacía
            new DatosIniciales(conexion).insertar();

            // 3. Instanciar la capa de Persistencia (DAOs)
            IAlumnoDAO alumnoDAO = new AlumnoDAO(conexion);
            IApartadoDAO apartadoDAO = new ApartadoDAO(conexion);
            CentroDAO centroDAO = new CentroDAO(conexion);

            // 4. Instanciar la capa de Negocio (Servicios con las reglas de ITSON)
            IAlumnoNegocio alumnoNeg = new AlumnoNegocio(alumnoDAO, apartadoDAO);
            IApartadoNegocio apartadoNeg = new ApartadoNegocio(apartadoDAO, alumnoDAO, centroDAO);

            // 5. Lanzar la interfaz gráfica pasándole sus dependencias listas
            new Interfaz(alumnoNeg, apartadoNeg);
        });
    }
}
