-- =====================================================
-- SISTEMA DE RESERVAS DE RESTAURANTES
-- Base de datos completa y actualizada
-- Fecha: 2025-10-14
-- =====================================================

DROP DATABASE IF EXISTS reservas_simple;
CREATE DATABASE reservas_simple CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE reservas_simple;

-- =====================================================
-- TABLA: usuarios
-- Sistema de autenticación y gestión de usuarios
-- =====================================================
CREATE TABLE `usuarios` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `nombre` VARCHAR(100) NOT NULL,
  `email` VARCHAR(100),
  `telefono` VARCHAR(20),
  `rol` ENUM('ADMIN', 'EMPLEADO', 'CLIENTE') NOT NULL DEFAULT 'CLIENTE',
  `activo` TINYINT(1) NOT NULL DEFAULT 1,
  `fecha_creacion` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ultimo_acceso` DATETIME,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_username` (`username`),
  UNIQUE KEY `unique_email` (`email`),
  KEY `idx_usuarios_username` (`username`),
  KEY `idx_usuarios_rol` (`rol`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- =====================================================
-- TABLA: restaurantes
-- Información de los restaurantes
-- =====================================================
CREATE TABLE `restaurantes` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(100) NOT NULL,
  `direccion` VARCHAR(200),
  `telefono` VARCHAR(20),
  `email` VARCHAR(100),
  `hora_apertura` VARCHAR(5),
  `hora_cierre` VARCHAR(5),
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_nombre` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- =====================================================
-- TABLA: mesas
-- Mesas disponibles en cada restaurante
-- =====================================================
CREATE TABLE `mesas` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `restaurante_id` INT(11) NOT NULL,
  `numero_mesa` VARCHAR(10) NOT NULL,
  `capacidad` INT(11) NOT NULL,
  `disponible` TINYINT(1) DEFAULT 1,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_mesa_restaurante` (`restaurante_id`, `numero_mesa`),
  KEY `idx_mesas_restaurante` (`restaurante_id`),
  CONSTRAINT `mesas_ibfk_1` FOREIGN KEY (`restaurante_id`) REFERENCES `restaurantes` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- =====================================================
-- TABLA: reservas
-- Reservas realizadas por los clientes
-- =====================================================
CREATE TABLE `reservas` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `mesa_id` INT(11) NOT NULL,
  `nombre_cliente` VARCHAR(100) NOT NULL,
  `telefono_cliente` VARCHAR(20) NOT NULL,
  `email_cliente` VARCHAR(100),
  `fecha_reserva` DATE NOT NULL,
  `hora_inicio` TIME NOT NULL,
  `hora_fin` TIME NOT NULL,
  `numero_personas` INT(11) NOT NULL,
  `precio` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  `estado` ENUM('PENDIENTE', 'CONFIRMADA', 'CANCELADA', 'COMPLETADA') NOT NULL DEFAULT 'PENDIENTE',
  `observaciones` VARCHAR(500),
  `fecha_creacion` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_actualizacion` DATETIME ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_reservas_mesa` (`mesa_id`),
  KEY `idx_reservas_fecha` (`fecha_reserva`),
  KEY `idx_reservas_estado` (`estado`),
  CONSTRAINT `reservas_ibfk_1` FOREIGN KEY (`mesa_id`) REFERENCES `mesas` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- =====================================================
-- DATOS DE PRUEBA
-- =====================================================

-- Usuarios de ejemplo (password: 123456 para todos)
INSERT INTO `usuarios` (`username`, `password`, `nombre`, `email`, `telefono`, `rol`, `activo`) VALUES
('admin', '123456', 'Administrador Sistema', 'admin@reservas.com', '555-0001', 'ADMIN', 1),
('empleado1', '123456', 'Carlos Empleado', 'carlos@reservas.com', '555-0002', 'EMPLEADO', 1),
('cliente1', '123456', 'Juan Pérez', 'juan@example.com', '555-1111', 'CLIENTE', 1),
('cliente2', '123456', 'María García', 'maria@example.com', '555-2222', 'CLIENTE', 1),
('cliente3', '123456', 'Ana Martínez', 'ana@example.com', '555-4444', 'CLIENTE', 1);

-- Restaurantes de ejemplo
INSERT INTO `restaurantes` (`nombre`, `direccion`, `telefono`, `email`, `hora_apertura`, `hora_cierre`) VALUES
('La Pizzería Italiana', 'Av. Principal 123', '555-1234', 'info@pizzeria.com', '11:00', '23:00'),
('El Asador Criollo', 'Calle Comercio 456', '555-5678', 'contacto@asador.com', '12:00', '00:00'),
('Sushi House', 'Boulevard Central 789', '555-9012', 'reservas@sushihouse.com', '12:00', '22:00');

-- Mesas para La Pizzería Italiana (restaurante_id = 1)
INSERT INTO `mesas` (`restaurante_id`, `numero_mesa`, `capacidad`, `disponible`) VALUES
(1, 'M-01', 2, 1),
(1, 'M-02', 2, 1),
(1, 'M-03', 4, 1),
(1, 'M-04', 4, 1),
(1, 'M-05', 6, 1),
(1, 'M-06', 8, 1);

-- Mesas para El Asador Criollo (restaurante_id = 2)
INSERT INTO `mesas` (`restaurante_id`, `numero_mesa`, `capacidad`, `disponible`) VALUES
(2, 'A-01', 2, 1),
(2, 'A-02', 4, 1),
(2, 'A-03', 4, 1),
(2, 'A-04', 6, 1),
(2, 'A-05', 8, 1);

-- Mesas para Sushi House (restaurante_id = 3)
INSERT INTO `mesas` (`restaurante_id`, `numero_mesa`, `capacidad`, `disponible`) VALUES
(3, 'S-01', 2, 1),
(3, 'S-02', 2, 1),
(3, 'S-03', 4, 1),
(3, 'S-04', 4, 1),
(3, 'S-05', 6, 1);

-- Reservas de ejemplo (con rangos horarios de 1 hora)
INSERT INTO `reservas` (`mesa_id`, `nombre_cliente`, `telefono_cliente`, `email_cliente`, `fecha_reserva`, `hora_inicio`, `hora_fin`, `numero_personas`, `precio`, `estado`, `observaciones`) VALUES
(1, 'Juan Pérez', '555-1111', 'juan@example.com', '2025-10-20', '19:00:00', '20:00:00', 2, 50.00, 'CONFIRMADA', 'Mesa para dos, cena romántica'),
(3, 'María García', '555-2222', 'maria@example.com', '2025-10-21', '20:00:00', '21:00:00', 4, 120.00, 'PENDIENTE', 'Cumpleaños, mesa decorada'),
(7, 'Carlos López', '555-3333', 'carlos@example.com', '2025-10-22', '14:00:00', '15:00:00', 4, 100.00, 'CONFIRMADA', NULL),
(11, 'Ana Martínez', '555-4444', 'ana@example.com', '2025-10-23', '12:00:00', '13:00:00', 2, 45.00, 'PENDIENTE', 'Preferencia por ventana');

-- =====================================================
-- VERIFICACIÓN
-- =====================================================

-- Verificar que las tablas se crearon correctamente
SHOW TABLES;

-- Contar registros en cada tabla
SELECT 'usuarios' AS tabla, COUNT(*) AS registros FROM usuarios
UNION ALL
SELECT 'restaurantes' AS tabla, COUNT(*) AS registros FROM restaurantes
UNION ALL
SELECT 'mesas' AS tabla, COUNT(*) AS registros FROM mesas
UNION ALL
SELECT 'reservas' AS tabla, COUNT(*) AS registros FROM reservas;

COMMIT;

-- =====================================================
-- FIN DEL SCRIPT
-- =====================================================
