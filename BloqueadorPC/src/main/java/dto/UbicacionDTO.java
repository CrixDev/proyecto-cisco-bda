/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

/**
 * Información de ubicación del equipo: número de máquina y nombre del centro
 * (Instituto / Laboratorio) al que pertenece. Se usa en el Paso 1 "Identificar
 * ubicación" del Bloqueador de PC.
 * 
 * @author Dylan
 */
public class UbicacionDTO {

    private int idComputadora;
    private String numeroMaquina;
    private String nombreLaboratorio;
    private String nombreCentro;
    private int idLaboratorio;

    public UbicacionDTO() {
    }

    public int getIdComputadora() {
        return idComputadora;
    }

    public void setIdComputadora(int idComputadora) {
        this.idComputadora = idComputadora;
    }

    public String getNumeroMaquina() {
        return numeroMaquina;
    }

    public void setNumeroMaquina(String numeroMaquina) {
        this.numeroMaquina = numeroMaquina;
    }

    public String getNombreLaboratorio() {
        return nombreLaboratorio;
    }

    public void setNombreLaboratorio(String nombreLaboratorio) {
        this.nombreLaboratorio = nombreLaboratorio;
    }

    public String getNombreCentro() {
        return nombreCentro;
    }

    public void setNombreCentro(String nombreCentro) {
        this.nombreCentro = nombreCentro;
    }

    public int getIdLaboratorio() {
        return idLaboratorio;
    }

    public void setIdLaboratorio(int idLaboratorio) {
        this.idLaboratorio = idLaboratorio;
    }
}
