/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.paneladministracion.dao;

import com.mycompany.paneladministracion.dtos.ApartadoDTO;
import com.mycompany.paneladministracion.entidades.Alumno;
import com.mycompany.paneladministracion.entidades.Apartado;
import com.mycompany.paneladministracion.entidades.Computadora;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de apartados. Maneja datos prueba en memoria (sin conexión a base de datos).
 *
 * @author Cristian Devora
 */
public class ApartadoDAO {

    private final List<Apartado> apartados;

    public ApartadoDAO() {
        this.apartados = new ArrayList<>();
        cargarDatosPrueba();
    }

    private void cargarDatosPrueba() {
        Alumno alumno1 = new Alumno(1, "Datos prueba - Juan Pérez", "00000001", false);
        Alumno alumno3 = new Alumno(3, "Datos prueba - Carlos Ruiz", "00000003", false);
        Computadora pc1 = new Computadora(1, "Datos prueba - PC-01", true);
        Computadora pc3 = new Computadora(3, "Datos prueba - PC-03", true);
        Computadora pc4 = new Computadora(4, "Datos prueba - PC-04", true);

        apartados.add(new Apartado(1, alumno1, pc1, "2026-06-14", "09:00"));
        apartados.add(new Apartado(2, alumno3, pc3, "2026-06-14", "10:30"));
        apartados.add(new Apartado(3, alumno1, pc4, "2026-06-15", "12:00"));
    }

    // 3. Mostrar los apartados de computadoras.
    public List<ApartadoDTO> mostrarApartados() {
        List<ApartadoDTO> lista = new ArrayList<>();
        for (Apartado apartado : apartados) {
            lista.add(new ApartadoDTO(
                    apartado.getId(),
                    apartado.getAlumno().getMatricula(),
                    apartado.getAlumno().getNombre(),
                    apartado.getComputadora().getEtiqueta(),
                    apartado.getFecha(),
                    apartado.getHora()));
        }
        return lista;
    }
}
