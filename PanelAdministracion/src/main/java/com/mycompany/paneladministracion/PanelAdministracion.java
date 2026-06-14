/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.paneladministracion;

import com.mycompany.paneladministracion.dao.AlumnoDAO;
import com.mycompany.paneladministracion.dao.ApartadoDAO;
import com.mycompany.paneladministracion.dao.ComputadoraDAO;
import com.mycompany.paneladministracion.dtos.AlumnoBloqueadoDTO;
import com.mycompany.paneladministracion.dtos.ApartadoDTO;
import com.mycompany.paneladministracion.dtos.ComputadoraDTO;

/**
 *
 * @author Cristian Devora
 */
public class PanelAdministracion {

    public static void main(String[] args) {
        System.out.println("=== Panel de Administración ===\n");

        AlumnoDAO alumnoDAO = new AlumnoDAO();
        ComputadoraDAO computadoraDAO = new ComputadoraDAO();
        ApartadoDAO apartadoDAO = new ApartadoDAO();

        // 1. Mostrar lista de alumnos bloqueados
        System.out.println("1) Alumnos bloqueados:");
        for (AlumnoBloqueadoDTO alumno : alumnoDAO.listarAlumnosBloqueados()) {
            System.out.println("   " + alumno);
        }

        // 2. Bloquear y desbloquear alumnos
        System.out.println("\n2) Bloquear alumno 1 y desbloquear alumno 2:");
        alumnoDAO.bloquearAlumno(1);
        alumnoDAO.desbloquearAlumno(2);
        for (AlumnoBloqueadoDTO alumno : alumnoDAO.listarAlumnosBloqueados()) {
            System.out.println("   " + alumno);
        }

        // 3. Mostrar los apartados de computadoras
        System.out.println("\n3) Apartados de computadoras:");
        for (ApartadoDTO apartado : apartadoDAO.mostrarApartados()) {
            System.out.println("   " + apartado);
        }

        // 4. Mostrar computadoras y habilitar / deshabilitar una PC
        System.out.println("\n4) Computadoras:");
        for (ComputadoraDTO computadora : computadoraDAO.listarComputadoras()) {
            System.out.println("   " + computadora);
        }
        System.out.println("\n   Habilitar PC-02 y deshabilitar PC-01:");
        computadoraDAO.habilitarComputadora(2);
        computadoraDAO.deshabilitarComputadora(1);
        for (ComputadoraDTO computadora : computadoraDAO.listarComputadoras()) {
            System.out.println("   " + computadora);
        }
    }
}
