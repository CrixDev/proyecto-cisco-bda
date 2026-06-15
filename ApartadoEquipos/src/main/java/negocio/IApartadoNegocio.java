/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package negocio;

import dto.ApartadoResultadoDTO;
import dto.ComputadoraDTO;
import java.util.List;

public interface IApartadoNegocio {
    List<ComputadoraDTO> obtenerComputadorasDelLaboratorio() throws NegocioException;
    ApartadoResultadoDTO realizarApartado(String numeroControl, Long computadoraId) throws NegocioException;
    public void liberarEquipo(String numeroControl) throws NegocioException ;
    String obtenerNombreLaboratorio() throws NegocioException;
}
