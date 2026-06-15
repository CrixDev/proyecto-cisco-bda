/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidad;

import java.io.Serializable;

/**
 *
 * @author Dylan
 */
public class Computadora implements Serializable {

    private int id;
    private int numeroMaquina;
    private String direccionIp;
    private String estatus; // 'Disponible', 'Deshabilitada', 'Ocupada', etc.
    private int idLaboratorio;

    public Computadora() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumeroMaquinaRaw() {
        return numeroMaquina;
    }

    public void setNumeroMaquinaRaw(int numero) {
        this.numeroMaquina = numero;
    }

    public String getDireccionIp() {
        return direccionIp;
    }

    public void setDireccionIp(String ip) {
        this.direccionIp = ip;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public int getIdLaboratorio() {
        return idLaboratorio;
    }

    public void setIdLaboratorio(int idLaboratorio) {
        this.idLaboratorio = idLaboratorio;
    }

    // Formato visual: "Equipo 01"
    public String getNumeroMaquina() {
        return String.format("%02d", numeroMaquina);
    }
}
