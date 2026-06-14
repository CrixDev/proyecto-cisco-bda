/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.paneladministracion;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Cristian Devora
 */
public class PanelAdministracionLogica {

    private final List<Alumno> alumnos;
    private final List<Computadora> computadoras;
    private final List<Apartado> apartados;

    public PanelAdministracionLogica() {
        this.alumnos = new ArrayList<>();
        this.computadoras = new ArrayList<>();
        this.apartados = new ArrayList<>();
        cargarDatosPrueba();
    }

    /**
     * Carga datos prueba en memoria (sin conexión a base de datos).
     */
    private void cargarDatosPrueba() {
        // Datos prueba de alumnos
        alumnos.add(new Alumno(1, "Datos prueba - Juan Pérez", "00000001", false));
        alumnos.add(new Alumno(2, "Datos prueba - María López", "00000002", true));
        alumnos.add(new Alumno(3, "Datos prueba - Carlos Ruiz", "00000003", false));
        alumnos.add(new Alumno(4, "Datos prueba - Ana Gómez", "00000004", true));

        // Datos prueba de computadoras
        computadoras.add(new Computadora(1, "Datos prueba - PC-01", true));
        computadoras.add(new Computadora(2, "Datos prueba - PC-02", false));
        computadoras.add(new Computadora(3, "Datos prueba - PC-03", true));
        computadoras.add(new Computadora(4, "Datos prueba - PC-04", true));

        // Datos prueba de apartados (alumno que aparta una computadora)
        apartados.add(new Apartado(1, alumnos.get(0), computadoras.get(0), "2026-06-14", "09:00"));
        apartados.add(new Apartado(2, alumnos.get(2), computadoras.get(2), "2026-06-14", "10:30"));
        apartados.add(new Apartado(3, alumnos.get(0), computadoras.get(3), "2026-06-15", "12:00"));
    }

    // ------------------------------------------------------------------
    // 1. Mostrar una lista de alumnos bloqueados.
    // ------------------------------------------------------------------
    public List<Alumno> listarAlumnosBloqueados() {
        List<Alumno> bloqueados = new ArrayList<>();
        for (Alumno alumno : alumnos) {
            if (alumno.isBloqueado()) {
                bloqueados.add(alumno);
            }
        }
        return bloqueados;
    }

    // ------------------------------------------------------------------
    // 2. Bloquear y desbloquear alumnos.
    // ------------------------------------------------------------------
    public boolean bloquearAlumno(int idAlumno) {
        Alumno alumno = buscarAlumno(idAlumno);
        if (alumno != null) {
            alumno.setBloqueado(true);
            return true;
        }
        return false;
    }

    public boolean desbloquearAlumno(int idAlumno) {
        Alumno alumno = buscarAlumno(idAlumno);
        if (alumno != null) {
            alumno.setBloqueado(false);
            return true;
        }
        return false;
    }

    private Alumno buscarAlumno(int idAlumno) {
        for (Alumno alumno : alumnos) {
            if (alumno.getId() == idAlumno) {
                return alumno;
            }
        }
        return null;
    }

    // ------------------------------------------------------------------
    // 3. Mostrar los apartados de computadoras.
    // ------------------------------------------------------------------
    public List<Apartado> mostrarApartados() {
        return apartados;
    }

    // ------------------------------------------------------------------
    // 4. Mostrar la lista de computadoras y habilitar / deshabilitar una
    //    PC para el uso de apartado.
    // ------------------------------------------------------------------
    public List<Computadora> listarComputadoras() {
        return computadoras;
    }

    public boolean habilitarComputadora(int idComputadora) {
        Computadora computadora = buscarComputadora(idComputadora);
        if (computadora != null) {
            computadora.setHabilitada(true);
            return true;
        }
        return false;
    }

    public boolean deshabilitarComputadora(int idComputadora) {
        Computadora computadora = buscarComputadora(idComputadora);
        if (computadora != null) {
            computadora.setHabilitada(false);
            return true;
        }
        return false;
    }

    private Computadora buscarComputadora(int idComputadora) {
        for (Computadora computadora : computadoras) {
            if (computadora.getId() == idComputadora) {
                return computadora;
            }
        }
        return null;
    }

    // Acceso a la lista completa de alumnos (datos prueba)
    public List<Alumno> listarAlumnos() {
        return alumnos;
    }
}
