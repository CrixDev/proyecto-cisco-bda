/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidad;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Apartado implements Serializable {
    private int id;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private Alumno alumno;
    private Computadora computadora;
    private int idPrestamosPD;

    public Apartado() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDateTime getFechaHoraInicio() { return fechaHoraInicio; }
    public void setFechaHoraInicio(LocalDateTime inicio) { this.fechaHoraInicio = inicio; }
    public LocalDateTime getFechaHoraFin() { return fechaHoraFin; }
    public void setFechaHoraFin(LocalDateTime fin) { this.fechaHoraFin = fin; }
    public Alumno getAlumno() { return alumno; }
    public void setAlumno(Alumno alumno) { this.alumno = alumno; }
    public Computadora getComputadora() { return computadora; }
    public void setComputadora(Computadora computadora) { this.computadora = computadora; }
    public int getIdPrestamosPD() { return idPrestamosPD; }
    public void setIdPrestamosPD(int id) { this.idPrestamosPD = id; }

    public String getEstado() { return (fechaHoraFin == null) ? "ACTIVO" : "FINALIZADO"; }
}
