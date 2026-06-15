/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

/**
 *
 * @author alecn
 */
public class AlumnoDTO {
    private String numeroControl;
    private String nombreCompleto;
    private String carrera;
    private int tiempoUsadoHoyMinutos;
    private int tiempeLimiteMinutos; // Mantiene el nombre exacto de tu lógica original

    public AlumnoDTO() {}

    public String getNumeroControl() { return numeroControl; }
    public void setNumeroControl(String numeroControl) { this.numeroControl = numeroControl; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getCarrera() { return carrera; }
    public void setCarrera(String carrera) { this.carrera = carrera; }
    public int getTiempoUsadoHoyMinutos() { return tiempoUsadoHoyMinutos; }
    public void setTiempoUsadoHoyMinutos(int t) { this.tiempoUsadoHoyMinutos = t; }
    public int getTiempeLimiteMinutos() { return tiempeLimiteMinutos; }
    public void setTiempeLimiteMinutos(int t) { this.tiempeLimiteMinutos = t; }

    public int getTiempoRestanteMinutos() {
        return Math.max(0, tiempeLimiteMinutos - tiempoUsadoHoyMinutos);
    }
}
