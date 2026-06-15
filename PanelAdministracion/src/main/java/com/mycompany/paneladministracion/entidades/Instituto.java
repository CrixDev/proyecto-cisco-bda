package com.mycompany.paneladministracion.entidades;

/**
 * Unidad académica del ITSON.
 */
public class Instituto {

    private int id;
    private String nombreOficial;
    private String nombreAbreviado;

    public Instituto() {
    }

    public Instituto(int id, String nombreOficial, String nombreAbreviado) {
        this.id = id;
        this.nombreOficial = nombreOficial;
        this.nombreAbreviado = nombreAbreviado;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombreOficial() { return nombreOficial; }
    public void setNombreOficial(String nombreOficial) { this.nombreOficial = nombreOficial; }
    public String getNombreAbreviado() { return nombreAbreviado; }
    public void setNombreAbreviado(String nombreAbreviado) { this.nombreAbreviado = nombreAbreviado; }

    @Override
    public String toString() {
        return nombreOficial + (nombreAbreviado != null ? " (" + nombreAbreviado + ")" : "");
    }
}
