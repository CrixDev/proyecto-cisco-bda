/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistencia;

import java.sql.*;
import util.Encriptador;

/**
 * Siembra automáticamente el catálogo del ITSON (unidades y carreras de
 * licenciatura) más algunos datos demo. Las contraseñas se guardan
 * encriptadas con SHA-256. Es idempotente: si ya hay institutos, no hace nada.
 */
public class DatosIniciales {

    private final IConexionBD conexion;

    public DatosIniciales(IConexionBD conexion) {
        this.conexion = conexion;
    }

    // Unidades académicas del ITSON: {nombre_oficial, nombre_abreviado}
    private static final String[][] UNIDADES = {
        {"Unidad Náinari", "UN"},
        {"Unidad Centro", "UC"},
        {"Unidad Guaymas", "UG"},
        {"Unidad Navojoa", "UNAV"},
        {"Unidad Empalme", "UE"}
    };

    // Carreras de nivel licenciatura del ITSON: {nombre, tiempo_limite_diario, telefono_academia}
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
            // Candado para evitar duplicación en cada arranque
            String checkSql = "SELECT COUNT(*) FROM Institutos;";
            try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(checkSql)) {
                if (rs.next() && rs.getInt(1) > 0) return;
            }

            con.setAutoCommit(false);

            // 1. Insertar TODAS las unidades del ITSON (catálogo automático)
            int idPrimeraUnidad = 0;
            String insInst = "INSERT INTO Institutos (nombre_oficial, nombre_abreviado) VALUES (?, ?);";
            try (PreparedStatement ps = con.prepareStatement(insInst, Statement.RETURN_GENERATED_KEYS)) {
                for (String[] u : UNIDADES) {
                    ps.setString(1, u[0]);
                    ps.setString(2, u[1]);
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next() && idPrimeraUnidad == 0) idPrimeraUnidad = rs.getInt(1);
                    }
                }
            }

            // 2. Insertar TODAS las carreras (licenciatura) del ITSON (catálogo automático)
            int idCarreraSoftware = 0;
            String insCar = "INSERT INTO Carreras (nombre, tiempo_limite_diario, telefono_academia) VALUES (?, ?, ?);";
            try (PreparedStatement ps = con.prepareStatement(insCar, Statement.RETURN_GENERATED_KEYS)) {
                for (String[] c : CARRERAS) {
                    ps.setString(1, c[0]);
                    ps.setTime(2, Time.valueOf(c[1]));
                    ps.setString(3, c[2]);
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next() && c[0].equals("Ingeniería en Software")) idCarreraSoftware = rs.getInt(1);
                    }
                }
            }

            // 3. Laboratorio demo (con horario de servicio y contraseña maestra encriptada)
            int idLaboratorio = 0;
            String insLab = "INSERT INTO Laboratorios (nombre, contrasena_maestra, hora_inicio, hora_fin, id_instituto) VALUES (?, ?, ?, ?, ?);";
            try (PreparedStatement ps = con.prepareStatement(insLab, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, "Laboratorio de Cómputo A");
                ps.setString(2, Encriptador.encriptar("admin123"));
                ps.setTime(3, Time.valueOf("07:00:00"));
                ps.setTime(4, Time.valueOf("21:00:00"));
                ps.setInt(5, idPrimeraUnidad);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) idLaboratorio = rs.getInt(1); }
            }

            // 4. Alumno demo (contraseña encriptada)
            String insAl = "INSERT INTO Alumnos (nombre, apellido, estatus_inscripcion, contrasena, id_carrera) VALUES (?, ?, ?, ?, ?);";
            try (PreparedStatement ps = con.prepareStatement(insAl)) {
                ps.setString(1, "Juan");
                ps.setString(2, "Pérez");
                ps.setString(3, "INSCRITO");
                ps.setString(4, Encriptador.encriptar("alumno123"));
                ps.setInt(5, idCarreraSoftware);
                ps.executeUpdate();
            }

            // 5. Computadoras demo
            String insComp = "INSERT INTO Computadoras (numero_maquina, direccion_ip, estatus, tipo_computadora, id_laboratorio) VALUES (?, ?, ?, ?, ?);";
            String[] ips = {"192.168.1.1", "192.168.1.2", "192.168.1.3"};
            try (PreparedStatement ps = con.prepareStatement(insComp)) {
                for (int i = 0; i < 3; i++) {
                    ps.setInt(1, i + 1);
                    ps.setString(2, ips[i]);
                    ps.setString(3, "Disponible");
                    ps.setString(4, "Windows");
                    ps.setInt(5, idLaboratorio);
                    ps.executeUpdate();
                }
            }

            // 6. Catálogo de software informativo
            int idSoftware1 = 0, idSoftware2 = 0;
            String insSw = "INSERT INTO SOFTWARE (nombre, descripcion) VALUES (?, ?);";
            try (PreparedStatement ps = con.prepareStatement(insSw, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, "NetBeans IDE");
                ps.setString(2, "Entorno de desarrollo para Java");
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) idSoftware1 = rs.getInt(1); }
            }
            try (PreparedStatement ps = con.prepareStatement(insSw, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, "Visual Studio Code");
                ps.setString(2, "Editor de código ligero multipropósito");
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) idSoftware2 = rs.getInt(1); }
            }

            // 7. Asociar software a la Computadora 1
            String insRel = "INSERT INTO computadorasoftware (id_software, id_computadora) VALUES (?, ?);";
            try (PreparedStatement ps = con.prepareStatement(insRel)) {
                ps.setInt(1, idSoftware1);
                ps.setInt(2, 1);
                ps.executeUpdate();
                ps.setInt(1, idSoftware2);
                ps.setInt(2, 1);
                ps.executeUpdate();
            }

            con.commit();
            System.out.println("[DatosIniciales] Catálogo del ITSON y datos demo cargados en CISCOBDA.");
        } catch (SQLException e) {
            System.err.println("Error al inyectar datos iniciales: " + e.getMessage());
        }
    }
}
