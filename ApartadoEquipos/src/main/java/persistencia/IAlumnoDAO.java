/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistencia;

import entidad.Alumno;

public interface IAlumnoDAO {
    Alumno buscarPorNumeroControl(String numeroControl) throws PersistenciaException;
}
