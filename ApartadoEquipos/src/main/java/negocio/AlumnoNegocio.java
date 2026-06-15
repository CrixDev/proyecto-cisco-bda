/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package negocio;

import dto.AlumnoDTO;
import entidad.Alumno;
import persistencia.IAlumnoDAO;
import persistencia.IApartadoDAO;
import persistencia.PersistenciaException;

public class AlumnoNegocio implements IAlumnoNegocio {

    private final IAlumnoDAO alumnoDAO;
    private final IApartadoDAO apartadoDAO;

    public AlumnoNegocio(IAlumnoDAO alumnoDAO, IApartadoDAO apartadoDAO) {
        this.alumnoDAO  = alumnoDAO;
        this.apartadoDAO = apartadoDAO;
    }

    @Override
    public AlumnoDTO identificarAlumno(String numeroControl) throws NegocioException {
        if (numeroControl == null || numeroControl.isBlank())
            throw new NegocioException("El número de control no puede estar vacío.");

        Alumno alumno;
        try {
            alumno = alumnoDAO.buscarPorNumeroControl(numeroControl.trim());
        } catch (PersistenciaException e) {
            throw new NegocioException("Error al buscar alumno en la base de datos.", e);
        }

        if (alumno == null)
            throw new NegocioException("No se encontró ningún alumno con el número de control: " + numeroControl);

        if (!alumno.isInscrito())
            throw new NegocioException("El alumno no se encuentra inscrito actualmente.");

        // Esta validación lee de la tabla Bloqueos (coordinado con el backend de tu compañero)
        if (alumno.isBloqueado())
            throw new NegocioException("El alumno tiene bloqueado el acceso a los laboratorios.\nMotivo: " + alumno.getMotivobloqueo());

        try {
            if (apartadoDAO.tieneApartadoActivo(numeroControl))
                throw new NegocioException("El alumno ya tiene un apartado activo en este momento.");
        } catch (PersistenciaException e) {
            throw new NegocioException("Error al verificar apartados activos.", e);
        }

        int minutosUsados;
        try {
            minutosUsados = apartadoDAO.obtenerMinutosUsadosHoy(numeroControl);
        } catch (PersistenciaException e) {
            throw new NegocioException("Error al calcular tiempo de uso diario.", e);
        }

        int limiteMinutos = alumno.getCarrera().getTiempoLimiteMinutos();
        if (minutosUsados >= limiteMinutos)
            throw new NegocioException("El alumno ha alcanzado su límite diario de uso (" + limiteMinutos + " min) para su carrera.");

        // Llenamos el DTO transparente para la interfaz gráfica
        AlumnoDTO dto = new AlumnoDTO();
        dto.setNumeroControl(alumno.getNumeroControl());
        dto.setNombreCompleto(alumno.getNombreCompleto());
        dto.setCarrera(alumno.getCarrera().getNombre());
        dto.setTiempoUsadoHoyMinutos(minutosUsados);
        dto.setTiempeLimiteMinutos(limiteMinutos);
        return dto;
    }
}
