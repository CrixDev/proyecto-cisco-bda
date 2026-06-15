/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

/**
 * 
 * Representa el apartado activo (préstamo vigente) detectado para el equipo en
 * turno. Se usa para mostrar el nombre del alumno (Paso 2), validar la
 * contraseña para desbloquear (Paso 3) y liberar el apartado (Paso 4).
 * 
 * @author Dylan
 */
public class SesionDTO {

    private int idPrestamo;
    private int idAlumno;
    private String nombreCompleto;
    private int idComputadora;

    public SesionDTO() {
    }

    public int getIdPrestamo() {
        return idPrestamo;
    }

    public void setIdPrestamo(int idPrestamo) {
        this.idPrestamo = idPrestamo;
    }

    public int getIdAlumno() {
        return idAlumno;
    }

    public void setIdAlumno(int idAlumno) {
        this.idAlumno = idAlumno;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public int getIdComputadora() {
        return idComputadora;
    }

    public void setIdComputadora(int idComputadora) {
        this.idComputadora = idComputadora;
    }
}
