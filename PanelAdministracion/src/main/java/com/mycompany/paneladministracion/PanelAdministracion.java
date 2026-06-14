/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.paneladministracion;

/**
 *
 * @author Cristian Devora
 */
public class PanelAdministracion {

    public static void main(String[] args) {
        System.out.println("=== Panel de Administración ===\n");

        PanelAdministracionLogica logica = new PanelAdministracionLogica();

        // 1. Mostrar lista de alumnos bloqueados
        System.out.println("1) Alumnos bloqueados:");
        for (Alumno alumno : logica.listarAlumnosBloqueados()) {
            System.out.println("   " + alumno);
        }

        // 2. Bloquear y desbloquear alumnos
        System.out.println("\n2) Bloquear alumno 1 y desbloquear alumno 2:");
        logica.bloquearAlumno(1);
        logica.desbloquearAlumno(2);
        for (Alumno alumno : logica.listarAlumnosBloqueados()) {
            System.out.println("   " + alumno);
        }

        // 3. Mostrar los apartados de computadoras
        System.out.println("\n3) Apartados de computadoras:");
        for (Apartado apartado : logica.mostrarApartados()) {
            System.out.println("   " + apartado);
        }

        // 4. Mostrar computadoras y habilitar / deshabilitar una PC
        System.out.println("\n4) Computadoras:");
        for (Computadora computadora : logica.listarComputadoras()) {
            System.out.println("   " + computadora);
        }
        System.out.println("\n   Habilitar PC-02 y deshabilitar PC-01:");
        logica.habilitarComputadora(2);
        logica.deshabilitarComputadora(1);
        for (Computadora computadora : logica.listarComputadoras()) {
            System.out.println("   " + computadora);
        }
    }
}
