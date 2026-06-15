/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidad;
import java.io.Serializable;
import java.time.LocalTime;

public class Carrera implements Serializable {
    private int id;
    private String nombre;
    private LocalTime tiempoLimiteDiario;

    public Carrera() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public LocalTime getTiempoLimiteDiario() { return tiempoLimiteDiario; }
    public void setTiempoLimiteDiario(LocalTime tiempo) { this.tiempoLimiteDiario = tiempo; }

    // Adaptador para que tu capa de negocio (AlumnoNegocio) siga funcionando sin cambios
    public int getTiempoLimiteMinutos() {
        if (tiempoLimiteDiario == null) return 0;
        return (tiempoLimiteDiario.getHour() * 60) + tiempoLimiteDiario.getMinute();
    }
}
