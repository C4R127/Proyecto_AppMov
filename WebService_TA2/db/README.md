# 🗄️ Base de Datos - Sistema de Reservas

## 📁 Archivo SQL Único

**`DATABASE_COMPLETE.sql`** - Script completo y consolidado con toda la base de datos.

---

## 📋 ¿Qué incluye este script?

### 🔹 **5 Tablas:**

1. **`usuarios`** - Sistema de login y autenticación
   - Campos: id, username, password, nombre, email, telefono, rol (ENUM), activo, fecha_creacion, ultimo_acceso
   - Roles: ADMIN, EMPLEADO, CLIENTE

2. **`restaurantes`** - Información de restaurantes
   - Campos: id, nombre, direccion, telefono, email, **imagen_url**, **imagen_thumbnail_url**, hora_apertura, hora_cierre

3. **`mesas`** - Mesas de cada restaurante
   - Campos: id, restaurante_id, numero_mesa, capacidad, disponible, **imagen_url**

4. **`reservas`** - Reservas de los clientes
   - Campos: id, mesa_id, **usuario_id (nullable)**, nombre_cliente, telefono_cliente, email_cliente, fecha_reserva, hora_inicio, hora_fin, numero_personas, **precio (DECIMAL)**, **estado (ENUM)**, observaciones, fecha_creacion, fecha_actualizacion
   - Estados: PENDIENTE, CONFIRMADA, CANCELADA, COMPLETADA

5. **`reviews`** - Comentarios/calificaciones asociados a restaurantes
   - Campos: id, restaurante_id, usuario_id, nombre_cliente, comentario, calificacion (1-5), fecha_creacion

### 🔹 **Datos de Prueba:**
- ✅ 5 usuarios (1 admin, 1 empleado, 3 clientes)
- ✅ 3 restaurantes (con URLs de imagen y miniatura)
- ✅ 45 mesas (15 por restaurante, todas con imagen de referencia)
- ✅ 4 reservas de ejemplo con asociación opcional a usuarios
- ✅ 4 reviews de ejemplo

---

## 🚀 ¿Cómo ejecutar el script?

### **Opción 1: Desde phpMyAdmin**
1. Abre phpMyAdmin
2. Ve a la pestaña "SQL"
3. Copia y pega el contenido de `DATABASE_COMPLETE.sql`
4. Haz clic en "Continuar"

### **Opción 2: Desde MySQL Workbench**
1. Abre MySQL Workbench
2. Conecta a tu servidor MySQL
3. Menú: File → Open SQL Script
4. Selecciona `DATABASE_COMPLETE.sql`
5. Ejecuta el script (⚡ botón)

### **Opción 3: Desde línea de comandos**
```bash
mysql -u root -p < DATABASE_COMPLETE.sql
```

---

## ✅ Verificación

Después de ejecutar el script, deberías tener:

```sql
-- Verificar tablas creadas
SHOW TABLES;
-- Resultado: usuarios, restaurantes, mesas, reservas, reviews

-- Verificar datos
SELECT COUNT(*) FROM usuarios;      -- 5 usuarios
SELECT COUNT(*) FROM restaurantes;  -- 3 restaurantes
SELECT COUNT(*) FROM mesas;         -- 45 mesas (15 por restaurante)
SELECT COUNT(*) FROM reservas;      -- 4 reservas
SELECT COUNT(*) FROM reviews;       -- 4 reviews
```

---

## 👥 Usuarios de Prueba

| Username   | Password | Rol      |
|------------|----------|----------|
| admin      | 123456   | ADMIN    |
| empleado1  | 123456   | EMPLEADO |
| cliente1   | 123456   | CLIENTE  |
| cliente2   | 123456   | CLIENTE  |
| cliente3   | 123456   | CLIENTE  |

---

## ⚠️ Importante

- Este script **ELIMINA** la base de datos `reservas_simple` si existe y la crea de nuevo
- Todas las contraseñas son `123456` (solo para desarrollo)
- En producción, las contraseñas deberían estar encriptadas con BCrypt

---

## 🔗 Conexión desde Spring Boot

Asegúrate de que tu `application.properties` tenga:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/reservas_simple?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
```

---

¡Listo! Con este archivo tienes todo lo necesario para tu sistema de reservas. 🎉
