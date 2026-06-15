/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidad;

import java.io.Serializable;
import java.time.LocalTime;

public class Laboratorio implements Serializable {
    private int id;
    private String nombre;
    private String contrasenaMaestra;
    private LocalTime horaInicio;   // horario de servicio del centro
    private LocalTime horaFin;
    private Instituto instituto;

    public Laboratorio() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getContrasenaMaestra() { return contrasenaMaestra; }
    public void setContrasenaMaestra(String contrasena) { this.contrasenaMaestra = contrasena; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }
    public Instituto getInstituto() { return instituto; }
    public void setInstituto(Instituto instituto) { this.instituto = instituto; }
}
