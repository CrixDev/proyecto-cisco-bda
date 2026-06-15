/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistencia;

import java.sql.*;

public class DatosIniciales {

    private final IConexionBD conexion;

    public DatosIniciales(IConexionBD conexion) {
        this.conexion = conexion;
    }

    public void insertar() {
        try (Connection con = conexion.crearConexion()) {
            // Candado para evitar duplicación en cada arranque
            String checkSql = "SELECT COUNT(*) FROM Institutos;";
            try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(checkSql)) {
                if (rs.next() && rs.getInt(1) > 0) return; 
            }

            con.setAutoCommit(false);

            // 1. Insertar Instituto 
            int idInstituto = 0;
            String insInst = "INSERT INTO Institutos (nombre_oficial, nombre_abreviado) VALUES (?, ?);";
            try (PreparedStatement ps = con.prepareStatement(insInst, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, "Unidad Cajeme");
                ps.setString(2, "UC");
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) idInstituto = rs.getInt(1); }
            }

            // 2. Insertar Laboratorio
            int idLaboratorio = 0;
            String insLab = "INSERT INTO Laboratorios (nombre, contrasena_maestra, id_instituto) VALUES (?, ?, ?);";
            try (PreparedStatement ps = con.prepareStatement(insLab, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, "Laboratorio A");
                ps.setString(2, "admin123"); 
                ps.setInt(3, idInstituto);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) idLaboratorio = rs.getInt(1); }
            }

            // 3. Insertar Carrera Resumida
            int idCarrera = 0;
            String insCar = "INSERT INTO Carreras (nombre, tiempo_limite_diario) VALUES (?, ?);";
            try (PreparedStatement ps = con.prepareStatement(insCar, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, "Ing. en Software"); // Nombre resumido y estandarizado
                ps.setTime(2, Time.valueOf("03:00:00")); // 180 minutos de límite diario
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) idCarrera = rs.getInt(1); }
            }

            // 4. Insertar Alumno Demo (ID autoincremental: 1)
            String insAl = "INSERT INTO Alumnos (nombre, apellido, estatus_inscripcion, contrasena, id_carrera) VALUES (?, ?, ?, ?, ?);";
            try (PreparedStatement ps = con.prepareStatement(insAl)) {
                ps.setString(1, "Juan");
                ps.setString(2, "Pérez");
                ps.setString(3, "INSCRITO");
                ps.setString(4, "alumno123"); 
                ps.setInt(5, idCarrera);
                ps.executeUpdate();
            }

            // 5. Insertar Computadoras 
            String insComp = "INSERT INTO Computadoras (numero_maquina, direccion_ip, estatus, id_laboratorio) VALUES (?, ?, ?, ?);";
            String[] ips = {"192.168.1.1", "192.168.1.2", "192.168.1.3"};
            try (PreparedStatement ps = con.prepareStatement(insComp)) {
                for (int i = 0; i < 3; i++) {
                    ps.setInt(1, i + 1);
                    ps.setString(2, ips[i]);
                    ps.setString(3, "Disponible");
                    ps.setInt(4, idLaboratorio);
                    ps.executeUpdate();
                }
            }

            // 6. Insertar Catálogo de Software Informativo
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

            // 7. Asociar el software informativo a la Computadora 1
            String insRel = "INSERT INTO computadorasoftware (id_software, id_computadora) VALUES (?, ?);";
            try (PreparedStatement ps = con.prepareStatement(insRel)) {
                ps.setInt(1, idSoftware1);
                ps.setInt(2, 1); // ID de la máquina 1
                ps.executeUpdate();
                
                ps.setInt(1, idSoftware2);
                ps.setInt(2, 1); 
                ps.executeUpdate();
            }

            con.commit();
            System.out.println("🚀 [DatosIniciales] Base de datos CISCO actualizada y lista.");
        } catch (SQLException e) {
            System.err.println("❌ Error al inyectar datos iniciales: " + e.getMessage());
        }
    }
}
