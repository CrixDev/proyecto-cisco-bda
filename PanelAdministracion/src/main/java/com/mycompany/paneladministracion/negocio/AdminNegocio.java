package com.mycompany.paneladministracion.negocio;

import com.mycompany.paneladministracion.dao.AlumnoDAO;
import com.mycompany.paneladministracion.dao.ApartadoDAO;
import com.mycompany.paneladministracion.dao.BloqueoDAO;
import com.mycompany.paneladministracion.dao.CarreraDAO;
import com.mycompany.paneladministracion.dao.ComputadoraDAO;
import com.mycompany.paneladministracion.dao.InstitutoDAO;
import com.mycompany.paneladministracion.dao.LaboratorioDAO;
import com.mycompany.paneladministracion.dao.SoftwareDAO;
import com.mycompany.paneladministracion.entidades.Alumno;
import com.mycompany.paneladministracion.entidades.Apartado;
import com.mycompany.paneladministracion.entidades.Bloqueo;
import com.mycompany.paneladministracion.entidades.Carrera;
import com.mycompany.paneladministracion.entidades.Computadora;
import com.mycompany.paneladministracion.entidades.Instituto;
import com.mycompany.paneladministracion.entidades.Laboratorio;
import com.mycompany.paneladministracion.entidades.Software;
import java.time.LocalDate;
import java.util.List;
import persistencia.PersistenciaException;
import util.Encriptador;

/**
 * Fachada de negocio del panel: agrupa los DAOs, aplica las validaciones y
 * encripta las contraseñas antes de persistir.
 */
public class AdminNegocio {

    private final AlumnoDAO alumnoDAO;
    private final ComputadoraDAO computadoraDAO;
    private final CarreraDAO carreraDAO;
    private final LaboratorioDAO laboratorioDAO;
    private final InstitutoDAO institutoDAO;
    private final SoftwareDAO softwareDAO;
    private final BloqueoDAO bloqueoDAO;
    private final ApartadoDAO apartadoDAO;

    public AdminNegocio(AlumnoDAO alumnoDAO, ComputadoraDAO computadoraDAO, CarreraDAO carreraDAO,
            LaboratorioDAO laboratorioDAO, InstitutoDAO institutoDAO, SoftwareDAO softwareDAO,
            BloqueoDAO bloqueoDAO, ApartadoDAO apartadoDAO) {
        this.alumnoDAO = alumnoDAO;
        this.computadoraDAO = computadoraDAO;
        this.carreraDAO = carreraDAO;
        this.laboratorioDAO = laboratorioDAO;
        this.institutoDAO = institutoDAO;
        this.softwareDAO = softwareDAO;
        this.bloqueoDAO = bloqueoDAO;
        this.apartadoDAO = apartadoDAO;
    }

    private static boolean vacio(String s) {
        return s == null || s.trim().isEmpty();
    }

    // ───────────────────────── CARRERAS ─────────────────────────
    public List<Carrera> listarCarreras() throws NegocioException {
        try { return carreraDAO.listarTodas(); }
        catch (PersistenciaException e) { throw new NegocioException(e.getMessage(), e); }
    }

    public void guardarCarrera(Carrera c, boolean esNuevo) throws NegocioException {
        if (vacio(c.getNombre())) throw new NegocioException("El nombre de la carrera no puede estar vacío.");
        if (c.getTiempoLimiteDiario() == null) throw new NegocioException("Indica el tiempo límite diario.");
        try {
            if (carreraDAO.existeNombre(c.getNombre(), esNuevo ? 0 : c.getId()))
                throw new NegocioException("Ya existe una carrera con ese nombre.");
            if (esNuevo) carreraDAO.insertar(c); else carreraDAO.actualizar(c);
        } catch (PersistenciaException e) { throw new NegocioException(e.getMessage(), e); }
    }

    public void eliminarCarrera(int id) throws NegocioException {
        try { carreraDAO.eliminar(id); }
        catch (PersistenciaException e) { throw new NegocioException(e.getMessage(), e); }
    }

    // ───────────────────────── SOFTWARE ─────────────────────────
    public List<Software> listarSoftware() throws NegocioException {
        try { return softwareDAO.listarTodos(); }
        catch (PersistenciaException e) { throw new NegocioException(e.getMessage(), e); }
    }

    public void guardarSoftware(Software s, boolean esNuevo) throws NegocioException {
        if (vacio(s.getNombre())) throw new NegocioException("El nombre del software no puede estar vacío.");
        try {
            if (softwareDAO.existeNombre(s.getNombre(), esNuevo ? 0 : s.getId()))
                throw new NegocioException("Ya existe un software con ese nombre.");
            if (esNuevo) softwareDAO.insertar(s); else softwareDAO.actualizar(s);
        } catch (PersistenciaException e) { throw new NegocioException(e.getMessage(), e); }
    }

    public void eliminarSoftware(int id) throws NegocioException {
        try { softwareDAO.eliminar(id); }
        catch (PersistenciaException e) { throw new NegocioException(e.getMessage(), e); }
    }

    // ───────────────────────── INSTITUTOS ─────────────────────────
    public List<Instituto> listarInstitutos() throws NegocioException {
        try { return institutoDAO.listarTodos(); }
        catch (PersistenciaException e) { throw new NegocioException(e.getMessage(), e); }
    }

    // ───────────────────────── LABORATORIOS ─────────────────────────
    public List<Laboratorio> listarLaboratorios() throws NegocioException {
        try { return laboratorioDAO.listarTodos(); }
        catch (PersistenciaException e) { throw new NegocioException(e.getMessage(), e); }
    }

    public void guardarLaboratorio(Laboratorio l, String contrasenaPlano, boolean esNuevo) throws NegocioException {
        if (vacio(l.getNombre())) throw new NegocioException("El nombre del laboratorio no puede estar vacío.");
        if (l.getHoraInicio() == null || l.getHoraFin() == null) throw new NegocioException("Indica el horario de servicio.");
        if (!l.getHoraInicio().isBefore(l.getHoraFin())) throw new NegocioException("La hora de inicio debe ser anterior a la hora de fin.");
        if (l.getIdInstituto() <= 0) throw new NegocioException("Selecciona la unidad académica.");
        try {
            if (esNuevo) {
                if (vacio(contrasenaPlano)) throw new NegocioException("Indica la contraseña maestra.");
                l.setContrasenaMaestra(Encriptador.encriptar(contrasenaPlano));
                laboratorioDAO.insertar(l);
            } else {
                String hash = vacio(contrasenaPlano) ? null : Encriptador.encriptar(contrasenaPlano);
                laboratorioDAO.actualizar(l, hash);
            }
        } catch (PersistenciaException e) { throw new NegocioException(e.getMessage(), e); }
    }

    public void eliminarLaboratorio(int id) throws NegocioException {
        try { laboratorioDAO.eliminar(id); }
        catch (PersistenciaException e) { throw new NegocioException(e.getMessage(), e); }
    }

    // ───────────────────────── ALUMNOS ─────────────────────────
    public List<Alumno> listarAlumnos() throws NegocioException {
        try { return alumnoDAO.listarTodos(); }
        catch (PersistenciaException e) { throw new NegocioException(e.getMessage(), e); }
    }

    public void guardarAlumno(Alumno a, String contrasenaPlano, boolean esNuevo) throws NegocioException {
        if (vacio(a.getNombre()) || vacio(a.getApellido())) throw new NegocioException("Nombre y apellido son obligatorios.");
        if (vacio(a.getEstatusInscripcion())) throw new NegocioException("Indica el estatus de inscripción.");
        if (a.getIdCarrera() <= 0) throw new NegocioException("Selecciona la carrera.");
        try {
            if (alumnoDAO.existeDuplicado(a.getNombre(), a.getApellido(), esNuevo ? 0 : a.getId()))
                throw new NegocioException("Ya existe un alumno con ese nombre y apellido.");
            if (esNuevo) {
                if (vacio(contrasenaPlano)) throw new NegocioException("Indica la contraseña del alumno.");
                a.setContrasena(Encriptador.encriptar(contrasenaPlano));
                alumnoDAO.insertar(a);
            } else {
                String hash = vacio(contrasenaPlano) ? null : Encriptador.encriptar(contrasenaPlano);
                alumnoDAO.actualizar(a, hash);
            }
        } catch (PersistenciaException e) { throw new NegocioException(e.getMessage(), e); }
    }

    public void eliminarAlumno(int id) throws NegocioException {
        try { alumnoDAO.eliminar(id); }
        catch (PersistenciaException e) { throw new NegocioException(e.getMessage(), e); }
    }

    // ───────────────────────── COMPUTADORAS ─────────────────────────
    public List<Computadora> listarComputadoras() throws NegocioException {
        try { return computadoraDAO.listarTodas(); }
        catch (PersistenciaException e) { throw new NegocioException(e.getMessage(), e); }
    }

    public void guardarComputadora(Computadora c, boolean esNuevo) throws NegocioException {
        if (c.getNumeroMaquina() <= 0) throw new NegocioException("El número de máquina debe ser mayor a cero.");
        if (vacio(c.getDireccionIp())) throw new NegocioException("La dirección IP no puede estar vacía.");
        if (!c.getDireccionIp().matches("^(\\d{1,3}\\.){3}\\d{1,3}$"))
            throw new NegocioException("La dirección IP no tiene un formato válido (ej. 192.168.1.10).");
        if (c.getIdLaboratorio() <= 0) throw new NegocioException("Selecciona el laboratorio.");
        try {
            if (computadoraDAO.existeIp(c.getDireccionIp(), esNuevo ? 0 : c.getId()))
                throw new NegocioException("Ya existe una computadora con esa dirección IP.");
            if (esNuevo) computadoraDAO.insertar(c); else computadoraDAO.actualizar(c);
        } catch (PersistenciaException e) { throw new NegocioException(e.getMessage(), e); }
    }

    public void eliminarComputadora(int id) throws NegocioException {
        try { computadoraDAO.eliminar(id); }
        catch (PersistenciaException e) { throw new NegocioException(e.getMessage(), e); }
    }

    /** Habilita/deshabilita una computadora cambiando únicamente su estatus. */
    public void cambiarEstatusComputadora(Computadora c, String nuevoEstatus) throws NegocioException {
        if (c == null) throw new NegocioException("Selecciona un equipo.");
        if ("Apartada".equalsIgnoreCase(c.getEstatus()))
            throw new NegocioException("El equipo está ocupado por un apartado activo; cancélalo primero.");
        c.setEstatus(nuevoEstatus);
        try { computadoraDAO.actualizar(c); }
        catch (PersistenciaException e) { throw new NegocioException(e.getMessage(), e); }
    }

    public java.util.Set<Integer> idsSoftwareAsignado(int idComputadora) throws NegocioException {
        try { return computadoraDAO.idsSoftwareAsignado(idComputadora); }
        catch (PersistenciaException e) { throw new NegocioException(e.getMessage(), e); }
    }

    public void asignarSoftware(int idComputadora, List<Integer> idsSoftware) throws NegocioException {
        try { computadoraDAO.asignarSoftware(idComputadora, idsSoftware); }
        catch (PersistenciaException e) { throw new NegocioException(e.getMessage(), e); }
    }

    // ───────────────────────── BLOQUEOS ─────────────────────────
    public List<Bloqueo> listarBloqueosActivos() throws NegocioException {
        try { return bloqueoDAO.listarActivos(); }
        catch (PersistenciaException e) { throw new NegocioException(e.getMessage(), e); }
    }

    /**
     * Bloquea a un alumno y devuelve el mensaje de notificación telefónica a la
     * academia de su carrera.
     */
    public String bloquearAlumno(Alumno alumno, LocalDate fecha, String motivo) throws NegocioException {
        if (alumno == null) throw new NegocioException("Selecciona un alumno.");
        if (fecha == null) throw new NegocioException("Selecciona la fecha del bloqueo.");
        if (vacio(motivo)) throw new NegocioException("Indica el motivo del bloqueo.");
        try {
            if (bloqueoDAO.tieneBloqueoActivo(alumno.getId()))
                throw new NegocioException("El alumno ya tiene un bloqueo activo.");
            bloqueoDAO.bloquear(alumno.getId(), fecha, motivo.trim());
            return construirNotificacion(alumno);
        } catch (PersistenciaException e) { throw new NegocioException(e.getMessage(), e); }
    }

    public void desbloquearAlumno(int idAlumno) throws NegocioException {
        try {
            if (!bloqueoDAO.tieneBloqueoActivo(idAlumno))
                throw new NegocioException("El alumno no tiene un bloqueo activo.");
            bloqueoDAO.desbloquear(idAlumno);
        } catch (PersistenciaException e) { throw new NegocioException(e.getMessage(), e); }
    }

    private String construirNotificacion(Alumno alumno) throws PersistenciaException {
        String carreraNombre = alumno.getCarreraNombre();
        String telefono = "(no registrado)";
        for (Carrera c : carreraDAO.listarTodas()) {
            if (c.getId() == alumno.getIdCarrera()) {
                carreraNombre = c.getNombre();
                if (c.getTelefonoAcademia() != null && !c.getTelefonoAcademia().isBlank())
                    telefono = c.getTelefonoAcademia();
                break;
            }
        }
        return "Se notificó vía telefónica a la academia de la carrera \"" + carreraNombre
                + "\" al teléfono " + telefono + " sobre el bloqueo del alumno "
                + alumno.getNombreCompleto() + ".";
    }

    // ───────────────────────── APARTADOS ─────────────────────────
    public List<Apartado> listarApartados() throws NegocioException {
        try { return apartadoDAO.listarTodos(); }
        catch (PersistenciaException e) { throw new NegocioException(e.getMessage(), e); }
    }

    /** Cancela un apartado activo y libera la computadora asociada. */
    public void cancelarApartado(int idPrestamo) throws NegocioException {
        try { apartadoDAO.cancelar(idPrestamo); }
        catch (PersistenciaException e) { throw new NegocioException(e.getMessage(), e); }
    }
}
