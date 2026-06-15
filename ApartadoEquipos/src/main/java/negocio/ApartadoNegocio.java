/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package negocio;

import dto.ApartadoResultadoDTO;
import dto.ComputadoraDTO;
import entidad.*;
import persistencia.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ApartadoNegocio implements IApartadoNegocio {

    private final IApartadoDAO apartadoDAO;
    private final IAlumnoDAO   alumnoDAO;
    private final CentroDAO    centroDAO;

    private static final int DURACION_MINUTOS = 120;

    public ApartadoNegocio(IApartadoDAO apartadoDAO, IAlumnoDAO alumnoDAO, CentroDAO centroDAO) {
        this.apartadoDAO = apartadoDAO;
        this.alumnoDAO   = alumnoDAO;
        this.centroDAO   = centroDAO;
    }

    @Override
    public List<ComputadoraDTO> obtenerComputadorasDelLaboratorio() throws NegocioException {
        try {
            Laboratorio centro = centroDAO.obtenerPrimero();
            if (centro == null) throw new NegocioException("No hay centros de laboratorio registrados.");
    
            List<Computadora> computadoras = apartadoDAO.obtenerComputadorasPorCentro((long) centro.getId());
            List<ComputadoraDTO> resultado = new ArrayList<>();
    
            // ── ESTE ES EL CICLO "FOR" ──
            for (Computadora c : computadoras) {
                ComputadoraDTO dto = new ComputadoraDTO();
                dto.setId((long) c.getId());
                dto.setNumeroMaquina(c.getNumeroMaquina());
                dto.setDireccionIp(c.getDireccionIp());
    
                // ── NUEVA LÓGICA DE ESTADOS ADAPTADA A TUS INSERTS ──
                if ("Deshabilitada".equalsIgnoreCase(c.getEstatus())) {
                    dto.setEstado("DESHABILITADA");
                    dto.setNombreAlumnoActual("Mantenimiento");
                } else if ("Apartada".equalsIgnoreCase(c.getEstatus()) || !"Disponible".equals(c.getNombreAlumnoActual())) {
                    dto.setEstado("OCUPADA");
                    // Si el LEFT JOIN no trajo alumno pero en MySQL dice 'Apartada', le ponemos "Reservada"
                    dto.setNombreAlumnoActual("Disponible".equals(c.getNombreAlumnoActual()) ? "Reservada" : c.getNombreAlumnoActual());
                } else {
                    dto.setEstado("LIBRE");
                    dto.setNombreAlumnoActual("Disponible");
                }
    
                // Mapea la lista de software de la base de datos al DTO
                List<String> listaNombresSoftware = new ArrayList<>();
                if (c.getSoftware() != null) {
                    for (Software s : c.getSoftware()) {
                        listaNombresSoftware.add(s.getNombre());
                    }
                }
                dto.setSoftwareInstalado(listaNombresSoftware);
    
                resultado.add(dto);
            }
            return resultado;
        } catch (PersistenciaException e) {
            throw new NegocioException("Error al procesar el software de los equipos.", e);
        }
    }

    @Override
    public ApartadoResultadoDTO realizarApartado(String numeroControl, Long computadoraId) throws NegocioException {
        try {
            if (apartadoDAO.tieneApartadoActivo(numeroControl))
                throw new NegocioException("El alumno ya tiene un apartado activo.");

            Alumno alumno = alumnoDAO.buscarPorNumeroControl(numeroControl);
            if (alumno == null) throw new NegocioException("Alumno no encontrado.");

            Laboratorio centro = centroDAO.obtenerPrimero();
            if (centro == null) throw new NegocioException("No hay centros de laboratorio registrados.");

            // Validar que estemos dentro del horario de servicio del laboratorio
            validarHorarioServicio(centro);

            List<Computadora> computadoras = apartadoDAO.obtenerComputadorasPorCentro((long) centro.getId());

            Computadora computadora = computadoras.stream()
                    .filter(c -> c.getId() == computadoraId.intValue()) // Comparación correcta de tipos primitivos
                    .findFirst()
                    .orElseThrow(() -> new NegocioException("Computadora no encontrada."));

            if (!computadora.isDisponible())
                throw new NegocioException("La computadora seleccionada está deshabilitada.");

            if (!"Disponible".equals(computadora.getNombreAlumnoActual())) {
                throw new NegocioException("La computadora ya está ocupada por otro alumno.");
            }

            Apartado apartado = new Apartado();
            apartado.setAlumno(alumno);
            apartado.setComputadora(computadora);
            apartado.setFechaHoraInicio(LocalDateTime.now());
            // El apartado queda ACTIVO (fin_prestamo = NULL); se cierra al liberar
            // la PC en el Bloqueador. Así el BloqueadorPC lo reconoce como vigente.
            apartado.setFechaHoraFin(null);

            apartadoDAO.guardar(apartado);

            ApartadoResultadoDTO resultado = new ApartadoResultadoDTO();
            resultado.setApartadoId((long) apartado.getId());
            resultado.setNumeroMaquina(computadora.getNumeroMaquina());
            resultado.setNombreAlumno(alumno.getNombreCompleto());
            resultado.setLaboratorio(centro.getNombre());
            resultado.setDuracionMinutos(DURACION_MINUTOS);
            return resultado;

        } catch (PersistenciaException e) {
            throw new NegocioException("Error al registrar el apartado.", e);
        }
    }
    public void liberarEquipo(String numeroControl) throws NegocioException {
    try {
        apartadoDAO.terminarPrestamo(numeroControl);
    } catch (PersistenciaException e) {
        throw new NegocioException("No se pudo completar la liberación del equipo.", e);
    }
}

    @Override
    public String obtenerNombreLaboratorio() throws NegocioException {
        try {
            Laboratorio centro = centroDAO.obtenerPrimero();
            return centro != null ? centro.getNombre() : "Sin asignar";
        } catch (PersistenciaException e) {
            throw new NegocioException("Error al obtener el laboratorio.", e);
        }
    }

    /** Verifica que la hora actual esté dentro del horario de servicio del centro. */
    private void validarHorarioServicio(Laboratorio centro) throws NegocioException {
        LocalTime inicio = centro.getHoraInicio();
        LocalTime fin = centro.getHoraFin();
        if (inicio == null || fin == null) return; // sin horario configurado, no se restringe

        LocalTime ahora = LocalTime.now();
        if (ahora.isBefore(inicio) || ahora.isAfter(fin)) {
            DateTimeFormatter f = DateTimeFormatter.ofPattern("HH:mm");
            throw new NegocioException("El laboratorio " + centro.getNombre()
                    + " presta servicio de " + inicio.format(f) + " a " + fin.format(f)
                    + ". No es posible apartar fuera de ese horario.");
        }
    }
}
