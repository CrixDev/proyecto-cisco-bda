/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.paneladministracion.dtos;

/**
 * DTO para el método que muestra los apartados de computadoras.
 * Aplana los datos del alumno y de la computadora para mostrarlos.
 *
 * @author Cristian Devora
 */
public class ApartadoDTO {

    private final int idApartado;
    private final String matriculaAlumno;
    private final String nombreAlumno;
    private final String etiquetaComputadora;
    private final String fecha;
    private final String hora;

    public ApartadoDTO(int idApartado, String matriculaAlumno, String nombreAlumno,
            String etiquetaComputadora, String fecha, String hora) {
        this.idApartado = idApartado;
        this.matriculaAlumno = matriculaAlumno;
        this.nombreAlumno = nombreAlumno;
        this.etiquetaComputadora = etiquetaComputadora;
        this.fecha = fecha;
        this.hora = hora;
    }

    public int getIdApartado() {
        return idApartado;
    }

    public String getMatriculaAlumno() {
        return matriculaAlumno;
    }

    public String getNombreAlumno() {
        return nombreAlumno;
    }

    public String getEtiquetaComputadora() {
        return etiquetaComputadora;
    }

    public String getFecha() {
        return fecha;
    }

    public String getHora() {
        return hora;
    }

    @Override
    public String toString() {
        return "[Apartado " + idApartado + "] " + fecha + " " + hora
                + " | " + etiquetaComputadora
                + " | " + matriculaAlumno + " - " + nombreAlumno;
    }
}
