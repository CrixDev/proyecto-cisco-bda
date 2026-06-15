/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistencia;

import entidad.Alumno;
import entidad.Computadora;
import entidad.Laboratorio;
import dto.SesionDTO;

/**
 *
 * @author Dylan
 */
public interface IBloqueoDAO {

    /**
     * Identifica la computadora física en la que corre este programa (por su
     * dirección IP registrada en la BD).
     */
    Computadora identificarComputadora() throws PersistenciaException;

    /**
     * Obtiene el laboratorio (y el instituto al que pertenece) de una
     * computadora.
     */
    Laboratorio obtenerLaboratorio(int idLaboratorio) throws PersistenciaException;

    /**
     * Busca un préstamo (apartado) activo -fin_prestamo IS NULL- para la
     * computadora indicada y devuelve los datos del alumno que la reservó.
     * Devuelve null si la computadora no tiene apartado activo.
     */
    SesionDTO obtenerApartadoActivo(int idComputadora) throws PersistenciaException;

    /**
     * Obtiene los datos completos del alumno (incluida su contraseña) para
     * poder validar el desbloqueo.
     */
    Alumno obtenerAlumno(int idAlumno) throws PersistenciaException;

    /**
     * Marca como finalizado (fin_prestamo = NOW()) el préstamo activo de la
     * computadora y la regresa al estatus "Disponible".
     */
    void liberarApartado(int idComputadora) throws PersistenciaException;
}
