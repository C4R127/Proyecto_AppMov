# 📋 Resumen del Sistema de Reservas - Sprint Planning Punto 2

## ✅ COMPLETADO - APIs Implementadas

### 1️⃣ **API de Consultas de Tablas** ✅

#### 📌 **USUARIOS**
- `GET /api/usuarios` - Listar todos los usuarios
- `GET /api/usuarios/{id}` - Consultar usuario por ID
- `GET /api/auth/verificar/{username}` - Verificar si existe un usuario

#### 📌 **RESTAURANTES**
- `GET /api/restaurantes` - Listar todos los restaurantes
- `GET /api/restaurantes/{id}` - Consultar restaurante por ID

#### 📌 **MESAS**
- `GET /api/mesas/restaurante/{restauranteId}` - Listar mesas de un restaurante
- `GET /api/mesas/restaurante/{restauranteId}/disponibles` - Mesas disponibles
- `GET /api/mesas/restaurante/{restauranteId}/capacidad/{capacidad}` - Mesas por capacidad
- `GET /api/mesas/{id}` - Consultar mesa por ID

#### 📌 **RESERVAS**
- `GET /api/reservas` - Listar todas las reservas
- `GET /api/reservas/{id}` - Consultar reserva por ID
- `GET /api/reservas/mesa/{mesaId}` - Reservas de una mesa
- `GET /api/reservas/estado/{estado}` - Reservas por estado (PENDIENTE, CONFIRMADA, CANCELADA, COMPLETADA)
- `GET /api/reservas/restaurante/{restauranteId}` - Reservas de un restaurante
- `GET /api/reservas/rango-fechas` - Reservas por rango de fechas

---

### 2️⃣ **API para Login** ✅

#### 🔐 **Autenticación**
- `POST /api/auth/login` - Iniciar sesión
  ```json
  {
    "username": "admin",
    "password": "123456"
  }
  ```
  **Respuesta exitosa:**
  ```json
  {
    "success": true,
    "message": "Login exitoso",
    "usuario": {
      "id": 1,
      "username": "admin",
      "nombre": "Administrador Sistema",
      "email": "admin@reservas.com",
      "telefono": "555-0001",
      "rol": "ADMIN",
      "activo": true,
      "fechaCreacion": "2025-10-14T00:00:00",
      "ultimoAcceso": "2025-10-14T01:00:00"
    }
  }
  ```

- `POST /api/auth/registro` - Registrar nuevo usuario
  ```json
  {
    "username": "nuevousuario",
    "password": "123456",
    "nombre": "Nombre Completo",
    "email": "email@example.com",
    "telefono": "555-0000"
  }
  ```

#### 👥 **Roles de Usuario**
- **ADMIN**: Administrador del sistema
- **EMPLEADO**: Personal del restaurante
- **CLIENTE**: Clientes que hacen reservas

---

### 3️⃣ **API para Actualizar/Eliminar** ✅

#### 🔄 **USUARIOS**
- `PUT /api/usuarios/{id}` - Actualizar datos de usuario
- `PATCH /api/usuarios/{id}/password` - Cambiar contraseña
- `PATCH /api/usuarios/{id}/desactivar` - Desactivar usuario
- `DELETE /api/usuarios/{id}` - Eliminar usuario

#### 🔄 **RESTAURANTES**
- `POST /api/restaurantes` - Crear restaurante
- `PUT /api/restaurantes/{id}` - Actualizar restaurante
- `DELETE /api/restaurantes/{id}` - Eliminar restaurante

#### 🔄 **MESAS**
- `POST /api/mesas` - Crear mesa
- `PUT /api/mesas/{id}` - Actualizar mesa
- `PATCH /api/mesas/{id}/disponibilidad?disponible=true/false` - Cambiar disponibilidad
- `DELETE /api/mesas/{id}` - Eliminar mesa

#### 🔄 **RESERVAS**
- `POST /api/reservas` - Crear reserva
- `PATCH /api/reservas/{id}/estado?estado=CONFIRMADA` - Cambiar estado
- `PATCH /api/reservas/{id}/cancelar` - Cancelar reserva
- `DELETE /api/reservas/{id}` - Eliminar reserva

---

## 🗄️ Base de Datos

### Tablas creadas:
1. ✅ **usuarios** (con ENUM para roles: ADMIN, EMPLEADO, CLIENTE)
2. ✅ **restaurantes**
3. ✅ **mesas**
4. ✅ **reservas** (con campo precio DECIMAL y ENUM para estado)

### Campos importantes en `reservas`:
- ✅ `fecha_hora_reserva` (DATETIME) - Campo de fecha
- ✅ `precio` (DECIMAL(10,2)) - Campo de precio
- ✅ `estado` (ENUM) - Campo de tipo/estado con 4 opciones

---

## 🚀 Usuarios de Prueba

| Username   | Password | Rol      | Descripción           |
|------------|----------|----------|-----------------------|
| admin      | 123456   | ADMIN    | Administrador         |
| empleado1  | 123456   | EMPLEADO | Empleado restaurante  |
| cliente1   | 123456   | CLIENTE  | Juan Pérez            |
| cliente2   | 123456   | CLIENTE  | María García          |
| cliente3   | 123456   | CLIENTE  | Ana Martínez          |

---

## 📱 Para Integrar con tu App Kotlin

### 1. URL Base de tu API:
- **Emulador Android**: `http://10.0.2.2:8080`
- **Dispositivo físico**: `http://[TU_IP]:8080` (ejemplo: `http://192.168.1.100:8080`)
- **Localhost (pruebas)**: `http://localhost:8080`

### 2. Dependencias recomendadas para Kotlin:
```kotlin
// Retrofit para consumir APIs REST
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")

// OkHttp para logging
implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

// Coroutines para operaciones asíncronas
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
```

### 3. Archivos SQL necesarios:
- `ReservasDB_Complete.sql` - Script completo de la base de datos
- `migration_add_precio_campo.sql` - Migración para agregar precio (si ya tienes la BD)
- `usuarios_tabla.sql` - Solo tabla de usuarios (si ya tienes las demás tablas)

---

## ✅ Resumen de Cumplimiento

| Requerimiento                                  | Estado |
|-----------------------------------------------|--------|
| Base de datos con campo fecha (DATETIME)     | ✅     |
| Base de datos con campo precio (DECIMAL)     | ✅     |
| Base de datos con campo estado (ENUM)        | ✅     |
| API de consultas de tablas                    | ✅     |
| API para login                                | ✅     |
| API para actualizar/eliminar                  | ✅     |

---

## 📝 Próximos Pasos

1. **Ejecutar el script SQL** para crear/actualizar la base de datos
2. **Iniciar la aplicación Spring Boot** (debería correr en puerto 8080)
3. **Probar los endpoints** usando el archivo `API-ENDPOINTS.http`
4. **Compartir el código de tu app Kotlin** para integrar el consumo de APIs

---

## 📞 Endpoints más importantes para tu App Móvil

```http
# Login
POST http://localhost:8080/api/auth/login
Body: {"username": "cliente1", "password": "123456"}

# Listar restaurantes
GET http://localhost:8080/api/restaurantes

# Mesas disponibles de un restaurante
GET http://localhost:8080/api/mesas/restaurante/1/disponibles

# Crear una reserva
POST http://localhost:8080/api/reservas
Body: {
  "mesaId": 1,
  "nombreCliente": "Juan",
  "telefonoCliente": "555-1111",
  "emailCliente": "juan@example.com",
  "fechaHoraReserva": "2025-10-20T19:30:00",
  "numeroPersonas": 2,
  "precio": 50.00,
  "observaciones": "Mesa junto a la ventana"
}

# Ver mis reservas (filtrar por estado)
GET http://localhost:8080/api/reservas/estado/PENDIENTE
```

---

¡Todo listo para integrar con tu aplicación móvil Kotlin! 🎉
