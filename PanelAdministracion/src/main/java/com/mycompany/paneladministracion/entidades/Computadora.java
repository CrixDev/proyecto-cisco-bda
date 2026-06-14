/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.paneladministracion.entidades;

/**
 *
 * @author Cristian Devora
 */
public class Computadora {

    private int id;
    private String etiqueta;
    private boolean habilitada;

    public Computadora(int id, String etiqueta, boolean habilitada) {
        this.id = id;
        this.etiqueta = etiqueta;
        this.habilitada = habilitada;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public boolean isHabilitada() {
        return habilitada;
    }

    public void setHabilitada(boolean habilitada) {
        this.habilitada = habilitada;
    }
}
