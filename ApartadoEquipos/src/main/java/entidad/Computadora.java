/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidad;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Computadora implements Serializable {
    private int id;
    private int numeroMaquina;
    private String direccionIp;
    private String estatus; // 'Disponible', 'Deshabilitada', etc.
    private int idLaboratorio;
    private List<Software> software = new ArrayList<>();
    private String nombreAlumnoActual = "Disponible";



    public Computadora() {}
    public String getNombreAlumnoActual() { return nombreAlumnoActual; }
    public void setNombreAlumnoActual(String nombre) { this.nombreAlumnoActual = nombre; }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getNumeroMaquinaRaw() { return numeroMaquina; }
    public void setNumeroMaquinaRaw(int numero) { this.numeroMaquina = numero; }
    public String getDireccionIp() { return direccionIp; }
    public void setDireccionIp(String ip) { this.direccionIp = ip; }
    public String getEstatus() { return estatus; }
    public void setEstatus(String estatus) { this.estatus = estatus; }
    public int getIdLaboratorio() { return idLaboratorio; }
    public void setIdLaboratorio(int idLaboratorio) { this.idLaboratorio = idLaboratorio; }
    public List<Software> getSoftware() { return software; }
    public void setSoftware(List<Software> software) { this.software = software; }

    // Adaptadores para la capa de negocio e interfaz gráfica
    public String getNumeroMaquina() { return String.format("%02d", numeroMaquina); }
    public boolean isDisponible() { return "Disponible".equalsIgnoreCase(estatus); }
    public void setDisponible(boolean d) { this.estatus = d ? "Disponible" : "Deshabilitada"; }
}
