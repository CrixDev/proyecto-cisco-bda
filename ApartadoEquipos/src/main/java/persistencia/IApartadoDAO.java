/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistencia;

import entidad.Apartado;
import entidad.Computadora;
import java.util.List;

public interface IApartadoDAO {
    Apartado guardar(Apartado apartado) throws PersistenciaException;
    List<Computadora> obtenerComputadorasPorCentro(Long laboratorioId) throws PersistenciaException;
    boolean tieneApartadoActivo(String numeroControl) throws PersistenciaException;
    int obtenerMinutosUsadosHoy(String numeroControl) throws PersistenciaException;
    Apartado obtenerApartadoActivo(String numeroControl) throws PersistenciaException;
    // Agrega la firma en la interfaz y este método en tu ApartadoDAO
    public void terminarPrestamo(String numeroControl) throws PersistenciaException;
}
