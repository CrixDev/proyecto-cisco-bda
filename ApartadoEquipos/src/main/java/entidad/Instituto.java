/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidad;

import java.io.Serializable;

public class Instituto implements Serializable {
    private int id;
    private String nombreOficial;
    private String nombreAbreviado;

    public Instituto() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombreOficial() { return nombreOficial; }
    public void setNombreOficial(String nombre) { this.nombreOficial = nombre; }
    public String getNombreAbreviado() { return nombreAbreviado; }
    public void setNombreAbreviado(String abreviado) { this.nombreAbreviado = abreviado; }
}
