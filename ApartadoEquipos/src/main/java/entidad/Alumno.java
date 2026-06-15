/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidad;

import java.io.Serializable;

public class Alumno implements Serializable {
    private int id;
    private String nombre;
    private String apellido;
    private String estatusInscripcion;
    private String contrasena;
    private Carrera carrera;
    
    // Campos lógicos para la validación de bloqueos que hará tu DAO
    private boolean bloqueado;
    private String motivoBloqueo;

    public Alumno() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getEstatusInscripcion() { return estatusInscripcion; }
    public void setEstatusInscripcion(String estatus) { this.estatusInscripcion = estatus; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public Carrera getCarrera() { return carrera; }
    public void setCarrera(Carrera carrera) { this.carrera = carrera; }
    
    public boolean isBloqueado() { return bloqueado; }
    public void setBloqueado(boolean bloqueado) { this.bloqueado = bloqueado; }
    public String getMotivobloqueo() { return motivoBloqueo; }
    public void setMotivoBloqueo(String motivo) { this.motivoBloqueo = motivo; }

    // Métodos adaptadores para tu capa de negocio actual
    public String getNumeroControl() { return String.valueOf(id); }
    public String getNombreCompleto() { return nombre + " " + apellido; }
    public boolean isInscrito() { return "INSCRITO".equalsIgnoreCase(estatusInscripcion); }
}
