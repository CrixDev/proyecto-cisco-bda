/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

/**
 *
 * @author alecn
 */
public class ApartadoResultadoDTO {
    private Long apartadoId;
    private String numeroMaquina;
    private String nombreAlumno;
    private String laboratorio;
    private int duracionMinutos;

    public ApartadoResultadoDTO() {}

    public Long getApartadoId() { return apartadoId; }
    public void setApartadoId(Long apartadoId) { this.apartadoId = apartadoId; }
    public String getNumeroMaquina() { return numeroMaquina; }
    public void setNumeroMaquina(String numeroMaquina) { this.numeroMaquina = numeroMaquina; }
    public String getNombreAlumno() { return nombreAlumno; }
    public void setNombreAlumno(String nombreAlumno) { this.nombreAlumno = nombreAlumno; }
    public String getLaboratorio() { return laboratorio; }
    public void setLaboratorio(String laboratorio) { this.laboratorio = laboratorio; }
    public int getDuracionMinutos() { return duracionMinutos; }
    public void setDuracionMinutos(int duracionMinutos) { this.duracionMinutos = duracionMinutos; }
}
