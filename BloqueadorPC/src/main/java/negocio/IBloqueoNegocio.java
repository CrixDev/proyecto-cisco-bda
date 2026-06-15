/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package negocio;

import dto.SesionDTO;
import dto.UbicacionDTO;

/**
 *
 * @author Dylan
 */
public interface IBloqueoNegocio {

    /**
     * Paso 1: identifica el número de equipo y el centro al que pertenece.
     */
    UbicacionDTO identificarUbicacion() throws NegocioException;

    /**
     * Paso 2: obtiene el apartado activo (alumno) registrado para el equipo.
     */
    SesionDTO verificarApartado(int idComputadora) throws NegocioException;

    /**
     * Paso 3: valida la contraseña del alumno para desbloquear el equipo.
     */
    boolean validarContrasena(int idAlumno, String contrasena) throws NegocioException;

    /**
     * Paso 4: libera el apartado y deja la computadora disponible de nuevo.
     */
    void liberarApartado(int idComputadora) throws NegocioException;
}
