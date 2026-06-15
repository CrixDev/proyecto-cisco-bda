/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package negocio;

import dto.SesionDTO;
import dto.UbicacionDTO;
import entidad.Alumno;
import entidad.Computadora;
import entidad.Laboratorio;
import persistencia.IBloqueoDAO;
import persistencia.PersistenciaException;
import util.Encriptador;

/**
 *
 * @author Dylan
 */
public class BloqueoNegocio implements IBloqueoNegocio {

    private final IBloqueoDAO bloqueoDAO;

    public BloqueoNegocio(IBloqueoDAO bloqueoDAO) {
        this.bloqueoDAO = bloqueoDAO;
    }

    @Override
    public UbicacionDTO identificarUbicacion() throws NegocioException {
        try {
            Computadora pc = bloqueoDAO.identificarComputadora();
            Laboratorio lab = bloqueoDAO.obtenerLaboratorio(pc.getIdLaboratorio());

            UbicacionDTO dto = new UbicacionDTO();
            dto.setIdComputadora(pc.getId());
            dto.setIdLaboratorio(pc.getIdLaboratorio());
            dto.setNumeroMaquina(pc.getNumeroMaquina());

            if (lab != null) {
                dto.setNombreLaboratorio(lab.getNombre());
                dto.setNombreCentro(lab.getInstituto() != null ? lab.getInstituto().getNombreOficial() : "");
            } else {
                dto.setNombreLaboratorio("Sin asignar");
                dto.setNombreCentro("Sin asignar");
            }
            return dto;
        } catch (PersistenciaException e) {
            throw new NegocioException("No fue posible identificar la ubicación del equipo.", e);
        }
    }

    @Override
    public SesionDTO verificarApartado(int idComputadora) throws NegocioException {
        try {
            return bloqueoDAO.obtenerApartadoActivo(idComputadora);
        } catch (PersistenciaException e) {
            throw new NegocioException("No fue posible verificar el apartado del equipo.", e);
        }
    }

    @Override
    public boolean validarContrasena(int idAlumno, String contrasena) throws NegocioException {
        if (contrasena == null || contrasena.isBlank()) {
            throw new NegocioException("Debes ingresar tu contraseña.");
        }
        try {
            Alumno alumno = bloqueoDAO.obtenerAlumno(idAlumno);
            if (alumno == null) {
                throw new NegocioException("No se encontró información del alumno.");
            }
            // La contraseña se guarda encriptada (SHA-256); se compara por hash.
            return Encriptador.coincide(contrasena, alumno.getContrasena());
        } catch (PersistenciaException e) {
            throw new NegocioException("Error al validar la contraseña.", e);
        }
    }

    @Override
    public boolean validarContrasenaMaestra(int idLaboratorio, String contrasena) throws NegocioException {
        if (contrasena == null || contrasena.isBlank()) {
            throw new NegocioException("Debes ingresar la contraseña maestra.");
        }
        try {
            Laboratorio lab = bloqueoDAO.obtenerLaboratorio(idLaboratorio);
            if (lab == null) {
                throw new NegocioException("No se encontró información del laboratorio.");
            }
            // La contraseña maestra se guarda encriptada (SHA-256); se compara por hash.
            return Encriptador.coincide(contrasena, lab.getContrasenaMaestra());
        } catch (PersistenciaException e) {
            throw new NegocioException("Error al validar la contraseña maestra.", e);
        }
    }

    @Override
    public void liberarApartado(int idComputadora) throws NegocioException {
        try {
            bloqueoDAO.liberarApartado(idComputadora);
        } catch (PersistenciaException e) {
            throw new NegocioException("No fue posible liberar el apartado.", e);
        }
    }
}
