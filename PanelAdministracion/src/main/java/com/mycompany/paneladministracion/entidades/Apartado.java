package com.mycompany.paneladministracion.entidades;

import java.time.LocalDateTime;

/**
 * Vista de un préstamo (apartado) registrado para consulta del encargado.
 */
public class Apartado {

    private int id;
    private String alumnoNombre;
    private int numeroMaquina;
    private String laboratorioNombre;
    private LocalDateTime inicioPrestamo;
    private LocalDateTime finPrestamo;   // null si está activo

    public Apartado() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getAlumnoNombre() { return alumnoNombre; }
    public void setAlumnoNombre(String alumnoNombre) { this.alumnoNombre = alumnoNombre; }
    public int getNumeroMaquina() { return numeroMaquina; }
    public void setNumeroMaquina(int numeroMaquina) { this.numeroMaquina = numeroMaquina; }
    public String getLaboratorioNombre() { return laboratorioNombre; }
    public void setLaboratorioNombre(String laboratorioNombre) { this.laboratorioNombre = laboratorioNombre; }
    public LocalDateTime getInicioPrestamo() { return inicioPrestamo; }
    public void setInicioPrestamo(LocalDateTime inicioPrestamo) { this.inicioPrestamo = inicioPrestamo; }
    public LocalDateTime getFinPrestamo() { return finPrestamo; }
    public void setFinPrestamo(LocalDateTime finPrestamo) { this.finPrestamo = finPrestamo; }

    public boolean isActivo() {
        return finPrestamo == null;
    }
}
