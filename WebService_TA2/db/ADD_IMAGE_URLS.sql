-- =====================================================
-- AGREGAR SOPORTE PARA URLs DE IMÁGENES
-- Para uso con Picasso en la aplicación móvil
-- Fecha: 2025-11-15
-- =====================================================

USE reservas_simple;

-- Agregar columna de imagen a restaurantes
ALTER TABLE `restaurantes` 
ADD COLUMN `imagen_url` VARCHAR(500) AFTER `email`,
ADD COLUMN `imagen_thumbnail_url` VARCHAR(500) AFTER `imagen_url`;

-- Agregar columna de imagen a mesas
ALTER TABLE `mesas` 
ADD COLUMN `imagen_url` VARCHAR(500) AFTER `disponible`;

-- =====================================================
-- ACTUALIZAR DATOS EXISTENTES CON URLs DE EJEMPLO
-- Puedes reemplazar estas URLs con tus propias imágenes
-- =====================================================

-- URLs de ejemplo para restaurantes (usando placeholders de ejemplo)
UPDATE `restaurantes` 
SET 
    `imagen_url` = 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=800',
    `imagen_thumbnail_url` = 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=200'
WHERE `id` = 1; -- La Pizzería Italiana

UPDATE `restaurantes` 
SET 
    `imagen_url` = 'https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=800',
    `imagen_thumbnail_url` = 'https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=200'
WHERE `id` = 2; -- El Asador Criollo

UPDATE `restaurantes` 
SET 
    `imagen_url` = 'https://images.unsplash.com/photo-1579584425555-c3ce17fd4351?w=800',
    `imagen_thumbnail_url` = 'https://images.unsplash.com/photo-1579584425555-c3ce17fd4351?w=200'
WHERE `id` = 3; -- Sushi House

-- URLs de ejemplo para mesas (opcional)
UPDATE `mesas` 
SET `imagen_url` = 'https://images.unsplash.com/photo-1466978913421-dad2ebd01d17?w=400'
WHERE `restaurante_id` = 1; -- Mesas de La Pizzería Italiana

UPDATE `mesas` 
SET `imagen_url` = 'https://images.unsplash.com/photo-1424847651672-bf20a4b0982b?w=400'
WHERE `restaurante_id` = 2; -- Mesas de El Asador Criollo

UPDATE `mesas` 
SET `imagen_url` = 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=400'
WHERE `restaurante_id` = 3; -- Mesas de Sushi House

-- =====================================================
-- VERIFICACIÓN
-- =====================================================

-- Ver restaurantes con sus URLs
SELECT id, nombre, imagen_url, imagen_thumbnail_url 
FROM restaurantes;

-- Ver mesas con sus URLs
SELECT m.id, r.nombre AS restaurante, m.numero_mesa, m.imagen_url 
FROM mesas m
JOIN restaurantes r ON m.restaurante_id = r.id
ORDER BY r.id, m.numero_mesa;

COMMIT;

-- =====================================================
-- NOTAS IMPORTANTES:
-- =====================================================
-- 1. Las URLs de ejemplo usan Unsplash (servicio gratuito de imágenes)
-- 2. Puedes reemplazarlas con:
--    - URLs de tu propio servidor
--    - URLs de servicios como Cloudinary, AWS S3, Firebase Storage
--    - URLs de imágenes locales si configuras carpeta estática
-- 3. imagen_thumbnail_url es opcional, para versiones optimizadas
-- 4. Picasso manejará el caché y optimización en el cliente

