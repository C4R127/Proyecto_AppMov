-- =====================================================
-- SISTEMA DE RESERVAS DE RESTAURANTES
-- Base de datos completa y actualizada
-- Fecha: 2025-11-20
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
  `imagen_url` VARCHAR(500),
  `imagen_thumbnail_url` VARCHAR(500),
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
  `imagen_url` VARCHAR(500),
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
  `usuario_id` INT(11),
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
  KEY `idx_reservas_usuario` (`usuario_id`),
  CONSTRAINT `reservas_ibfk_1` FOREIGN KEY (`mesa_id`) REFERENCES `mesas` (`id`) ON DELETE CASCADE,
  CONSTRAINT `reservas_ibfk_2` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- =====================================================
-- TABLA: reviews
-- Comentarios y calificaciones de los clientes
-- =====================================================
CREATE TABLE `reviews` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `restaurante_id` INT(11) NOT NULL,
  `usuario_id` INT(11),
  `nombre_cliente` VARCHAR(100),
  `comentario` VARCHAR(500) NOT NULL,
  `calificacion` INT(1) NOT NULL,
  `fecha_creacion` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_reviews_restaurante` (`restaurante_id`),
  KEY `idx_reviews_usuario` (`usuario_id`),
  CONSTRAINT `reviews_ibfk_1` FOREIGN KEY (`restaurante_id`) REFERENCES `restaurantes` (`id`) ON DELETE CASCADE,
  CONSTRAINT `reviews_ibfk_2` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL
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
INSERT INTO `restaurantes` (
  `nombre`, `direccion`, `telefono`, `email`, `imagen_url`, `imagen_thumbnail_url`, `hora_apertura`, `hora_cierre`
) VALUES
('La Pizzería Italiana', 'Av. Principal 123', '555-1234', 'info@pizzeria.com', 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=800', 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=200', '11:00', '23:00'),
('El Asador Criollo', 'Calle Comercio 456', '555-5678', 'contacto@asador.com', 'https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=800', 'https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=200', '12:00', '00:00'),
('Sushi House', 'Boulevard Central 789', '555-9012', 'reservas@sushihouse.com', 'https://images.unsplash.com/photo-1579584425555-c3ce17fd4351?w=800', 'https://images.unsplash.com/photo-1579584425555-c3ce17fd4351?w=200', '12:00', '22:00');

-- Mesas para La Pizzería Italiana (restaurante_id = 1)
INSERT INTO `mesas` (`restaurante_id`, `numero_mesa`, `capacidad`, `disponible`, `imagen_url`) VALUES
(1, 'M-01', 2, 1, 'https://images.unsplash.com/photo-1466978913421-dad2ebd01d17?w=400'),
(1, 'M-02', 2, 1, 'https://images.unsplash.com/photo-1466978913421-dad2ebd01d17?w=400'),
(1, 'M-03', 4, 1, 'https://images.unsplash.com/photo-1466978913421-dad2ebd01d17?w=400'),
(1, 'M-04', 4, 1, 'https://images.unsplash.com/photo-1466978913421-dad2ebd01d17?w=400'),
(1, 'M-05', 6, 1, 'https://images.unsplash.com/photo-1466978913421-dad2ebd01d17?w=400'),
(1, 'M-06', 8, 1, 'https://images.unsplash.com/photo-1466978913421-dad2ebd01d17?w=400'),
(1, 'M-07', 2, 1, 'https://images.unsplash.com/photo-1466978913421-dad2ebd01d17?w=400'),
(1, 'M-08', 4, 1, 'https://images.unsplash.com/photo-1466978913421-dad2ebd01d17?w=400'),
(1, 'M-09', 6, 1, 'https://images.unsplash.com/photo-1466978913421-dad2ebd01d17?w=400'),
(1, 'M-10', 8, 1, 'https://images.unsplash.com/photo-1466978913421-dad2ebd01d17?w=400'),
(1, 'M-11', 2, 1, 'https://images.unsplash.com/photo-1466978913421-dad2ebd01d17?w=400'),
(1, 'M-12', 4, 1, 'https://images.unsplash.com/photo-1466978913421-dad2ebd01d17?w=400'),
(1, 'M-13', 6, 1, 'https://images.unsplash.com/photo-1466978913421-dad2ebd01d17?w=400'),
(1, 'M-14', 8, 1, 'https://images.unsplash.com/photo-1466978913421-dad2ebd01d17?w=400'),
(1, 'M-15', 10, 1, 'https://images.unsplash.com/photo-1466978913421-dad2ebd01d17?w=400');

-- Mesas para El Asador Criollo (restaurante_id = 2)
INSERT INTO `mesas` (`restaurante_id`, `numero_mesa`, `capacidad`, `disponible`, `imagen_url`) VALUES
(2, 'A-01', 2, 1, 'https://images.unsplash.com/photo-1424847651672-bf20a4b0982b?w=400'),
(2, 'A-02', 4, 1, 'https://images.unsplash.com/photo-1424847651672-bf20a4b0982b?w=400'),
(2, 'A-03', 4, 1, 'https://images.unsplash.com/photo-1424847651672-bf20a4b0982b?w=400'),
(2, 'A-04', 6, 1, 'https://images.unsplash.com/photo-1424847651672-bf20a4b0982b?w=400'),
(2, 'A-05', 8, 1, 'https://images.unsplash.com/photo-1424847651672-bf20a4b0982b?w=400'),
(2, 'A-06', 2, 1, 'https://images.unsplash.com/photo-1424847651672-bf20a4b0982b?w=400'),
(2, 'A-07', 4, 1, 'https://images.unsplash.com/photo-1424847651672-bf20a4b0982b?w=400'),
(2, 'A-08', 6, 1, 'https://images.unsplash.com/photo-1424847651672-bf20a4b0982b?w=400'),
(2, 'A-09', 8, 1, 'https://images.unsplash.com/photo-1424847651672-bf20a4b0982b?w=400'),
(2, 'A-10', 10, 1, 'https://images.unsplash.com/photo-1424847651672-bf20a4b0982b?w=400'),
(2, 'A-11', 2, 1, 'https://images.unsplash.com/photo-1424847651672-bf20a4b0982b?w=400'),
(2, 'A-12', 4, 1, 'https://images.unsplash.com/photo-1424847651672-bf20a4b0982b?w=400'),
(2, 'A-13', 6, 1, 'https://images.unsplash.com/photo-1424847651672-bf20a4b0982b?w=400'),
(2, 'A-14', 8, 1, 'https://images.unsplash.com/photo-1424847651672-bf20a4b0982b?w=400'),
(2, 'A-15', 10, 1, 'https://images.unsplash.com/photo-1424847651672-bf20a4b0982b?w=400');

-- Mesas para Sushi House (restaurante_id = 3)
INSERT INTO `mesas` (`restaurante_id`, `numero_mesa`, `capacidad`, `disponible`, `imagen_url`) VALUES
(3, 'S-01', 2, 1, 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=400'),
(3, 'S-02', 2, 1, 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=400'),
(3, 'S-03', 4, 1, 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=400'),
(3, 'S-04', 4, 1, 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=400'),
(3, 'S-05', 6, 1, 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=400'),
(3, 'S-06', 2, 1, 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=400'),
(3, 'S-07', 4, 1, 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=400'),
(3, 'S-08', 6, 1, 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=400'),
(3, 'S-09', 8, 1, 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=400'),
(3, 'S-10', 10, 1, 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=400'),
(3, 'S-11', 2, 1, 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=400'),
(3, 'S-12', 4, 1, 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=400'),
(3, 'S-13', 6, 1, 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=400'),
(3, 'S-14', 8, 1, 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=400'),
(3, 'S-15', 10, 1, 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=400');

-- Reservas de ejemplo (con rangos horarios de 1 hora)
INSERT INTO `reservas` (`mesa_id`, `usuario_id`, `nombre_cliente`, `telefono_cliente`, `email_cliente`, `fecha_reserva`, `hora_inicio`, `hora_fin`, `numero_personas`, `precio`, `estado`, `observaciones`) VALUES
(1, 3, 'Juan Pérez', '555-1111', 'juan@example.com', '2025-10-20', '19:00:00', '20:00:00', 2, 50.00, 'CONFIRMADA', 'Mesa para dos, cena romántica'),
(3, 4, 'María García', '555-2222', 'maria@example.com', '2025-10-21', '20:00:00', '21:00:00', 4, 120.00, 'PENDIENTE', 'Cumpleaños, mesa decorada'),
(7, NULL, 'Carlos López', '555-3333', 'carlos@example.com', '2025-10-22', '14:00:00', '15:00:00', 4, 100.00, 'CONFIRMADA', NULL),
(11, 5, 'Ana Martínez', '555-4444', 'ana@example.com', '2025-10-23', '12:00:00', '13:00:00', 2, 45.00, 'PENDIENTE', 'Preferencia por ventana');

-- Reviews de ejemplo
INSERT INTO `reviews` (`restaurante_id`, `usuario_id`, `nombre_cliente`, `comentario`, `calificacion`) VALUES
(1, 3, NULL, 'La pizza estuvo increíble y el servicio muy atento.', 5),
(1, NULL, 'Visitante Anónimo', 'Buena atención, aunque podría haber más opciones sin gluten.', 4),
(2, 4, NULL, 'La carne al punto perfecto, repetiré pronto.', 5),
(3, 5, NULL, 'El sushi estuvo fresco y delicioso.', 5);

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
SELECT 'reservas' AS tabla, COUNT(*) AS registros FROM reservas
UNION ALL
SELECT 'reviews' AS tabla, COUNT(*) AS registros FROM reviews;

COMMIT;

-- =====================================================
-- FIN DEL SCRIPT
-- =====================================================
