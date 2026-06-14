/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.paneladministracion.dtos;

/**
 * DTO para el método que lista los alumnos bloqueados.
 *
 * @author Cristian Devora
 */
public class AlumnoBloqueadoDTO {

    private final int id;
    private final String matricula;
    private final String nombre;

    public AlumnoBloqueadoDTO(int id, String matricula, String nombre) {
        this.id = id;
        this.matricula = matricula;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public String getMatricula() {
        return matricula;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return "[ID " + id + "] " + matricula + " - " + nombre + " (BLOQUEADO)";
    }
}
