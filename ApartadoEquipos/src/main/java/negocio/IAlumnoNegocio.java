/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package negocio;

import dto.AlumnoDTO;

public interface IAlumnoNegocio {
    AlumnoDTO identificarAlumno(String numeroControl) throws NegocioException;
}
