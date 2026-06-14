/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.paneladministracion.dtos;

/**
 * DTO para el método que lista las computadoras.
 *
 * @author Cristian Devora
 */
public class ComputadoraDTO {

    private final int id;
    private final String etiqueta;
    private final boolean habilitada;

    public ComputadoraDTO(int id, String etiqueta, boolean habilitada) {
        this.id = id;
        this.etiqueta = etiqueta;
        this.habilitada = habilitada;
    }

    public int getId() {
        return id;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public boolean isHabilitada() {
        return habilitada;
    }

    @Override
    public String toString() {
        return "[ID " + id + "] " + etiqueta
                + " (" + (habilitada ? "HABILITADA para apartado" : "Deshabilitada") + ")";
    }
}
