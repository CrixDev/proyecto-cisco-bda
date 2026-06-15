package com.mycompany.paneladministracion.entidades;

import java.time.LocalTime;

/**
 * Centro de laboratorio de cómputo. La contraseña maestra se almacena
 * encriptada (SHA-256).
 */
public class Laboratorio {

    private int id;
    private String nombre;
    private String contrasenaMaestra;   // hash SHA-256
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private int idInstituto;
    private String institutoNombre;     // solo para mostrar

    public Laboratorio() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getContrasenaMaestra() { return contrasenaMaestra; }
    public void setContrasenaMaestra(String contrasenaMaestra) { this.contrasenaMaestra = contrasenaMaestra; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }
    public int getIdInstituto() { return idInstituto; }
    public void setIdInstituto(int idInstituto) { this.idInstituto = idInstituto; }
    public String getInstitutoNombre() { return institutoNombre; }
    public void setInstitutoNombre(String institutoNombre) { this.institutoNombre = institutoNombre; }

    @Override
    public String toString() {
        return nombre;
    }
}
