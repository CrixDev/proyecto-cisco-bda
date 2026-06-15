package com.mycompany.paneladministracion.entidades;

/**
 * Alumno del ITSON. La contraseña se almacena encriptada (SHA-256).
 */
public class Alumno {

    private int id;
    private String nombre;
    private String apellido;
    private String estatusInscripcion;
    private String contrasena;       // hash SHA-256
    private int idCarrera;
    private String carreraNombre;    // solo para mostrar
    private boolean bloqueado;       // derivado de Bloqueos activos

    public Alumno() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getEstatusInscripcion() { return estatusInscripcion; }
    public void setEstatusInscripcion(String estatusInscripcion) { this.estatusInscripcion = estatusInscripcion; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public int getIdCarrera() { return idCarrera; }
    public void setIdCarrera(int idCarrera) { this.idCarrera = idCarrera; }
    public String getCarreraNombre() { return carreraNombre; }
    public void setCarreraNombre(String carreraNombre) { this.carreraNombre = carreraNombre; }
    public boolean isBloqueado() { return bloqueado; }
    public void setBloqueado(boolean bloqueado) { this.bloqueado = bloqueado; }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    @Override
    public String toString() {
        return getNombreCompleto();
    }
}
