/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidad;

import java.io.Serializable;

public class Laboratorio implements Serializable {
    private int id;
    private String nombre;
    private String contrasenaMaestra;
    private Instituto instituto;

    public Laboratorio() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getContrasenaMaestra() { return contrasenaMaestra; }
    public void setContrasenaMaestra(String contrasena) { this.contrasenaMaestra = contrasena; }
    public Instituto getInstituto() { return instituto; }
    public void setInstituto(Instituto instituto) { this.instituto = instituto; }
}
