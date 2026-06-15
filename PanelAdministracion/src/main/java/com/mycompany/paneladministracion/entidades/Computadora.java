package com.mycompany.paneladministracion.entidades;

/**
 * Computadora de un laboratorio.
 */
public class Computadora {

    private int id;
    private int numeroMaquina;
    private String direccionIp;
    private String estatus;            // Disponible / Apartada / Deshabilitada
    private String tipoComputadora;    // Windows / Mac / Linux
    private int idLaboratorio;
    private String laboratorioNombre;  // solo para mostrar

    public Computadora() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getNumeroMaquina() { return numeroMaquina; }
    public void setNumeroMaquina(int numeroMaquina) { this.numeroMaquina = numeroMaquina; }
    public String getDireccionIp() { return direccionIp; }
    public void setDireccionIp(String direccionIp) { this.direccionIp = direccionIp; }
    public String getEstatus() { return estatus; }
    public void setEstatus(String estatus) { this.estatus = estatus; }
    public String getTipoComputadora() { return tipoComputadora; }
    public void setTipoComputadora(String tipoComputadora) { this.tipoComputadora = tipoComputadora; }
    public int getIdLaboratorio() { return idLaboratorio; }
    public void setIdLaboratorio(int idLaboratorio) { this.idLaboratorio = idLaboratorio; }
    public String getLaboratorioNombre() { return laboratorioNombre; }
    public void setLaboratorioNombre(String laboratorioNombre) { this.laboratorioNombre = laboratorioNombre; }

    @Override
    public String toString() {
        return "Equipo #" + numeroMaquina + " (" + direccionIp + ")";
    }
}
