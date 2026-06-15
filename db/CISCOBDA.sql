-- ========================================================
-- ========================================================

CREATE DATABASE IF NOT EXISTS CISCOBDA;
USE CISCOBDA;

-- ========================================================
--  TABLAS SIN DEPENDENCIAS
-- ========================================================

CREATE TABLE IF NOT EXISTS Institutos (
    id_instituto INT AUTO_INCREMENT PRIMARY KEY,
    nombre_oficial VARCHAR(255) NOT NULL,
    nombre_abreviado VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS Carreras (
    id_carrera INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    tiempo_limite_diario TIME NOT NULL,
);

CREATE TABLE IF NOT EXISTS SOFTWARE (
    id_software INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(250)
);

-- ========================================================
--  TABLAS CON DEPENDENCIAS DIRECTAS
-- ========================================================

CREATE TABLE IF NOT EXISTS Alumnos (
    id_alumno INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    apellido VARCHAR(50) NOT NULL,
    estatus_inscripcion VARCHAR(255) NOT NULL,
    contrasena VARCHAR(255) NOT NULL,          -- SHA-256 (hex)
    id_carrera INT,
    FOREIGN KEY (id_carrera) REFERENCES Carreras(id_carrera)
);

CREATE TABLE IF NOT EXISTS Laboratorios (
    id_laboratorio INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    contrasena_maestra VARCHAR(255) NOT NULL,  -- SHA-256 (hex)
    hora_inicio TIME NOT NULL,                 -- horario de servicio del centro
    hora_fin TIME NOT NULL,
    id_instituto INT,
    FOREIGN KEY (id_instituto) REFERENCES Institutos(id_instituto)
);

CREATE TABLE IF NOT EXISTS Bloqueos (
    id_bloqueo INT AUTO_INCREMENT PRIMARY KEY,
    fecha_bloqueo DATE NOT NULL,
    fecha_desbloqueo DATE NULL,
    motivo VARCHAR(255),
    id_alumno INT,
    FOREIGN KEY (id_alumno) REFERENCES Alumnos(id_alumno)
);

-- ========================================================
--  TABLAS CON DEPENDENCIAS MÚLTIPLES
-- ========================================================

CREATE TABLE IF NOT EXISTS PrestamosPD (
    id_prestamosPD INT AUTO_INCREMENT PRIMARY KEY,
    hora_Inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    fecha DATE NOT NULL,
    id_laboratorio INT,
    FOREIGN KEY (id_laboratorio) REFERENCES Laboratorios(id_laboratorio)
);

CREATE TABLE IF NOT EXISTS Computadoras (
    id_computadora INT AUTO_INCREMENT PRIMARY KEY,
    numero_maquina INT NOT NULL,
    direccion_ip VARCHAR(15) UNIQUE NOT NULL,
    estatus VARCHAR(255) NOT NULL DEFAULT 'Disponible',
    tipo_computadora ENUM('Windows', 'Mac', 'Linux') NOT NULL DEFAULT 'Windows',
    id_laboratorio INT,
    FOREIGN KEY (id_laboratorio) REFERENCES Laboratorios(id_laboratorio)
);

-- ========================================================
--  TABLAS PUENTE Y TRANSACCIONALES
-- ========================================================

CREATE TABLE IF NOT EXISTS computadorasoftware (
    id_software INT,
    id_computadora INT,
    PRIMARY KEY (id_software, id_computadora),
    FOREIGN KEY (id_software) REFERENCES SOFTWARE(id_software),
    FOREIGN KEY (id_computadora) REFERENCES Computadoras(id_computadora)
);

CREATE TABLE IF NOT EXISTS Prestamos (
    id_prestamo INT AUTO_INCREMENT PRIMARY KEY,
    inicio_prestamo DATETIME NOT NULL,
    fin_prestamo DATETIME NULL,
    id_alumno INT NOT NULL,
    id_computadora INT NOT NULL,
    id_prestamosPD INT NOT NULL,
    FOREIGN KEY (id_alumno) REFERENCES Alumnos(id_alumno),
    FOREIGN KEY (id_computadora) REFERENCES Computadoras(id_computadora),
    FOREIGN KEY (id_prestamosPD) REFERENCES PrestamosPD(id_prestamosPD)
);
