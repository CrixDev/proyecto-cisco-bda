package com.mycompany.paneladministracion.entidades;

import java.time.LocalTime;

/**
 * Carrera (licenciatura) del ITSON con su tiempo límite diario de uso de
 * laboratorios y el teléfono de su academia (para notificar bloqueos).
 */
public class Carrera {

    private int id;
    private String nombre;
    private LocalTime tiempoLimiteDiario;
    private String telefonoAcademia;

    public Carrera() {
    }

    public Carrera(int id, String nombre, LocalTime tiempoLimiteDiario, String telefonoAcademia) {
        this.id = id;
        this.nombre = nombre;
        this.tiempoLimiteDiario = tiempoLimiteDiario;
        this.telefonoAcademia = telefonoAcademia;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public LocalTime getTiempoLimiteDiario() { return tiempoLimiteDiario; }
    public void setTiempoLimiteDiario(LocalTime tiempoLimiteDiario) { this.tiempoLimiteDiario = tiempoLimiteDiario; }
    public String getTelefonoAcademia() { return telefonoAcademia; }
    public void setTelefonoAcademia(String telefonoAcademia) { this.telefonoAcademia = telefonoAcademia; }

    @Override
    public String toString() {
        return nombre;
    }
}
