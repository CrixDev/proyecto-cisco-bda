package persistencia;

import java.sql.*;
import util.Encriptador;

/**
 * Siembra automáticamente el catálogo del ITSON (unidades y carreras de
 * licenciatura) y un laboratorio demo si la base de datos está vacía.
 * Es idempotente: si ya hay institutos, no hace nada.
 */
public class DatosIniciales {

    private final IConexionBD conexion;

    public DatosIniciales(IConexionBD conexion) {
        this.conexion = conexion;
    }

    // Unidades académicas del ITSON
    private static final String[][] UNIDADES = {
        {"Unidad Náinari", "UN"},
        {"Unidad Centro", "UC"},
        {"Unidad Guaymas", "UG"},
        {"Unidad Navojoa", "UNAV"},
        {"Unidad Empalme", "UE"}
    };

    // Carreras (licenciatura): {nombre, tiempo_limite_diario, telefono_academia}
    private static final String[][] CARRERAS = {
        {"Ingeniería en Software", "03:00:00", "(644) 410-0900"},
        {"Ingeniería Industrial y de Sistemas", "02:00:00", "(644) 410-0901"},
        {"Ingeniería Mecatrónica", "02:30:00", "(644) 410-0902"},
        {"Ingeniería Civil", "02:00:00", "(644) 410-0903"},
        {"Ingeniería Electrónica", "02:30:00", "(644) 410-0904"},
        {"Ingeniería Química", "02:00:00", "(644) 410-0905"},
        {"Ingeniería en Manufactura", "02:00:00", "(644) 410-0906"},
        {"Ingeniería Biomédica", "02:30:00", "(644) 410-0907"},
        {"Licenciatura en Administración", "01:30:00", "(644) 410-0908"},
        {"Licenciatura en Contaduría Pública", "01:30:00", "(644) 410-0909"},
        {"Licenciatura en Economía y Finanzas", "01:30:00", "(644) 410-0910"},
        {"Licenciatura en Negocios Internacionales", "01:30:00", "(644) 410-0911"},
        {"Licenciatura en Psicología", "02:00:00", "(644) 410-0912"},
        {"Licenciatura en Ciencias de la Educación", "02:00:00", "(644) 410-0913"},
        {"Licenciatura en Educación Infantil", "02:00:00", "(644) 410-0914"},
        {"Licenciatura en Diseño Gráfico", "04:00:00", "(644) 410-0915"},
        {"Licenciatura en Enfermería", "02:00:00", "(644) 410-0916"},
        {"Licenciatura en Médico Veterinario Zootecnista", "02:00:00", "(644) 410-0917"},
        {"Licenciatura en Tecnología de Alimentos", "02:00:00", "(644) 410-0918"},
        {"Licenciatura en Gestión del Turismo", "02:00:00", "(644) 410-0919"},
        {"Licenciatura en Ciencias del Ejercicio Físico", "02:00:00", "(644) 410-0920"},
        {"Licenciatura en Sistemas de Información Administrativa", "03:00:00", "(644) 410-0921"}
    };

    public void insertar() {
        try (Connection con = conexion.crearConexion()) {
            try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM Institutos;")) {
                if (rs.next() && rs.getInt(1) > 0) return;
            }

            con.setAutoCommit(false);

            int idPrimeraUnidad = 0;
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO Institutos (nombre_oficial, nombre_abreviado) VALUES (?, ?);",
                    Statement.RETURN_GENERATED_KEYS)) {
                for (String[] u : UNIDADES) {
                    ps.setString(1, u[0]);
                    ps.setString(2, u[1]);
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next() && idPrimeraUnidad == 0) idPrimeraUnidad = rs.getInt(1);
                    }
                }
            }

            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO Carreras (nombre, tiempo_limite_diario, telefono_academia) VALUES (?, ?, ?);")) {
                for (String[] c : CARRERAS) {
                    ps.setString(1, c[0]);
                    ps.setTime(2, Time.valueOf(c[1]));
                    ps.setString(3, c[2]);
                    ps.executeUpdate();
                }
            }

            // Laboratorio demo (contraseña maestra encriptada y horario de servicio)
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO Laboratorios (nombre, contrasena_maestra, hora_inicio, hora_fin, id_instituto) VALUES (?, ?, ?, ?, ?);")) {
                ps.setString(1, "Laboratorio de Cómputo A");
                ps.setString(2, Encriptador.encriptar("admin123"));
                ps.setTime(3, Time.valueOf("07:00:00"));
                ps.setTime(4, Time.valueOf("21:00:00"));
                ps.setInt(5, idPrimeraUnidad);
                ps.executeUpdate();
            }

            con.commit();
            System.out.println("[DatosIniciales] Catálogo del ITSON cargado en CISCOBDA.");
        } catch (SQLException e) {
            System.err.println("Error al inyectar datos iniciales: " + e.getMessage());
        }
    }
}
