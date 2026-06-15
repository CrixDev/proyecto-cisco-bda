/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidad;


import java.io.Serializable;

public class Software implements Serializable {
    private int id;
    private String nombre;
    private String description;

    public Software() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return description; }
    public void setDescripcion(String descripcion) { this.description = descripcion; }
}
