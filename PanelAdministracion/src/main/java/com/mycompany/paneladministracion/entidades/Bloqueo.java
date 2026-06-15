package com.mycompany.paneladministracion.entidades;

import java.time.LocalDate;

/**
 * Bloqueo de acceso de un alumno a los laboratorios.
 */
public class Bloqueo {

    private int id;
    private LocalDate fechaBloqueo;
    private LocalDate fechaDesbloqueo;   // null si sigue bloqueado
    private String motivo;
    private int idAlumno;
    private String alumnoNombre;         // solo para mostrar

    public Bloqueo() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDate getFechaBloqueo() { return fechaBloqueo; }
    public void setFechaBloqueo(LocalDate fechaBloqueo) { this.fechaBloqueo = fechaBloqueo; }
    public LocalDate getFechaDesbloqueo() { return fechaDesbloqueo; }
    public void setFechaDesbloqueo(LocalDate fechaDesbloqueo) { this.fechaDesbloqueo = fechaDesbloqueo; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public int getIdAlumno() { return idAlumno; }
    public void setIdAlumno(int idAlumno) { this.idAlumno = idAlumno; }
    public String getAlumnoNombre() { return alumnoNombre; }
    public void setAlumnoNombre(String alumnoNombre) { this.alumnoNombre = alumnoNombre; }

    public boolean isActivo() {
        return fechaDesbloqueo == null;
    }
}
