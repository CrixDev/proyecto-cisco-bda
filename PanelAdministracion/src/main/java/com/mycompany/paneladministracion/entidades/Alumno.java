/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.paneladministracion.entidades;

/**
 *
 * @author Cristian Devora
 */
public class Alumno {

    private int id;
    private String nombre;
    private String matricula;
    private boolean bloqueado;

    public Alumno(int id, String nombre, String matricula, boolean bloqueado) {
        this.id = id;
        this.nombre = nombre;
        this.matricula = matricula;
        this.bloqueado = bloqueado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }
}
