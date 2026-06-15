/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

import java.util.List;

/**
 *
 * @author alecn
 */
public class ComputadoraDTO {
    private Long id;
    private String numeroMaquina;
    private String direccionIp;
    private String estado; // LIBRE, OCUPADA, DESHABILITADA
    private String nombreAlumnoActual; 
    private List<String> softwareInstalado;

    public ComputadoraDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNumeroMaquina() { return numeroMaquina; }
    public void setNumeroMaquina(String numeroMaquina) { this.numeroMaquina = numeroMaquina; }
    public String getDireccionIp() { return direccionIp; }
    public void setDireccionIp(String direccionIp) { this.direccionIp = direccionIp; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getNombreAlumnoActual() { return nombreAlumnoActual; }
    public void setNombreAlumnoActual(String nombre) { this.nombreAlumnoActual = nombre; }
    public List<String> getSoftwareInstalado() { return softwareInstalado; }
    public void setSoftwareInstalado(List<String> sw) { this.softwareInstalado = sw; }

    public boolean isLibre() { 
        return "LIBRE".equals(estado); 
    }
}
