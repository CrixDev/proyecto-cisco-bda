/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.paneladministracion.dao;

import com.mycompany.paneladministracion.dtos.AlumnoBloqueadoDTO;
import com.mycompany.paneladministracion.entidades.Alumno;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de alumnos. Maneja datos prueba en memoria (sin conexión a base de datos).
 *
 * @author Cristian Devora
 */
public class AlumnoDAO {

    private final List<Alumno> alumnos;

    public AlumnoDAO() {
        this.alumnos = new ArrayList<>();
        cargarDatosPrueba();
    }

    private void cargarDatosPrueba() {
        alumnos.add(new Alumno(1, "Datos prueba - Juan Pérez", "00000001", false));
        alumnos.add(new Alumno(2, "Datos prueba - María López", "00000002", true));
        alumnos.add(new Alumno(3, "Datos prueba - Carlos Ruiz", "00000003", false));
        alumnos.add(new Alumno(4, "Datos prueba - Ana Gómez", "00000004", true));
    }

    // 1. Mostrar una lista de alumnos bloqueados.
    public List<AlumnoBloqueadoDTO> listarAlumnosBloqueados() {
        List<AlumnoBloqueadoDTO> bloqueados = new ArrayList<>();
        for (Alumno alumno : alumnos) {
            if (alumno.isBloqueado()) {
                bloqueados.add(new AlumnoBloqueadoDTO(
                        alumno.getId(), alumno.getMatricula(), alumno.getNombre()));
            }
        }
        return bloqueados;
    }

    // 2. Bloquear y desbloquear alumnos.
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
}
