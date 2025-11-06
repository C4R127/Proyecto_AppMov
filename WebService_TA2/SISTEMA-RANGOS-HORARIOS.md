# ✅ Sistema de Reservas con RANGOS HORARIOS

## 🎯 Cambios Implementados

### 📅 **Nuevo Sistema de Reservas por Rangos de Tiempo**

Antes usabas: `fechaHoraReserva` (un solo campo DateTime)  
Ahora usas: **3 campos separados:**
- `fechaReserva` (DATE) - Ejemplo: `"2025-10-21"`
- `horaInicio` (TIME) - Ejemplo: `"14:00:00"`  
- `horaFin` (TIME) - Ejemplo: `"15:00:00"`

---

## 📋 APIs Disponibles para Reservas

### 1️⃣ **CREAR Reserva** ✅
```http
POST http://localhost:8080/api/reservas
Content-Type: application/json

{
  "mesaId": 3,
  "nombreCliente": "Carlos López",
  "telefonoCliente": "555-3333",
  "emailCliente": "carlos@example.com",
  "fechaReserva": "2025-10-21",
  "horaInicio": "14:00:00",
  "horaFin": "15:00:00",
  "numeroPersonas": 4,
  "precio": 150.00,
  "observaciones": "Celebración"
}
```

**Validaciones automáticas:**
- ✅ Mesa existe y está disponible
- ✅ Capacidad suficiente
- ✅ Fecha futura
- ✅ HoraFin > HoraInicio
- ✅ **No hay conflictos de horario con otras reservas**

---

### 2️⃣ **ACTUALIZAR Reserva** ✅ (NUEVO)
```http
PUT http://localhost:8080/api/reservas/{id}
Content-Type: application/json

{
  "mesaId": 5,
  "nombreCliente": "Carlos López Actualizado",
  "telefonoCliente": "555-3333",
  "emailCliente": "carlos@example.com",
  "fechaReserva": "2025-10-22",
  "horaInicio": "18:00:00",
  "horaFin": "19:00:00",
  "numeroPersonas": 6,
  "precio": 200.00,
  "observaciones": "Cambio de horario"
}
```

**Restricciones:**
- ❌ No se puede modificar si está COMPLETADA o CANCELADA
- ✅ Todos los campos son opcionales (solo envía los que quieres cambiar)
- ✅ Valida conflictos excluyendo la reserva actual

---

### 3️⃣ **ELIMINAR Reserva** ✅
```http
DELETE http://localhost:8080/api/reservas/{id}
```

---

### 4️⃣ **CONSULTAR Reservas**
```http
# Todas las reservas
GET http://localhost:8080/api/reservas

# Por ID
GET http://localhost:8080/api/reservas/1

# Por mesa
GET http://localhost:8080/api/reservas/mesa/3

# Por estado (PENDIENTE, CONFIRMADA, CANCELADA, COMPLETADA)
GET http://localhost:8080/api/reservas/estado/PENDIENTE

# Por restaurante
GET http://localhost:8080/api/reservas/restaurante/1
```

---

### 5️⃣ **Cambiar Estado** ✅
```http
PATCH http://localhost:8080/api/reservas/1/estado?estado=CONFIRMADA
```

Estados válidos: `PENDIENTE`, `CONFIRMADA`, `CANCELADA`, `COMPLETADA`

---

### 6️⃣ **Cancelar Reserva** ✅
```http
PATCH http://localhost:8080/api/reservas/1/cancelar
```

---

## 🔍 Validación de Conflictos de Horarios

El sistema valida automáticamente que **NO** haya traslape de horarios:

### Ejemplo de conflicto:
```
Reserva existente: 14:00 - 15:00
Nueva reserva:     14:30 - 15:30  ❌ CONFLICTO (se traslapa)

Reserva existente: 14:00 - 15:00
Nueva reserva:     15:00 - 16:00  ✅ PERMITIDO (sin traslape)
```

**Lógica de traslape:**  
Dos rangos se traslapan si: `(inicio1 < fin2) AND (fin1 > inicio2)`

---

## 🗄️ Cambios en la Base de Datos

### Tabla `reservas` actualizada:
```sql
CREATE TABLE `reservas` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `mesa_id` INT(11) NOT NULL,
  `nombre_cliente` VARCHAR(100) NOT NULL,
  `telefono_cliente` VARCHAR(20) NOT NULL,
  `email_cliente` VARCHAR(100),
  `fecha_reserva` DATE NOT NULL,           -- ⬅ CAMBIO
  `hora_inicio` TIME NOT NULL,             -- ⬅ NUEVO
  `hora_fin` TIME NOT NULL,                -- ⬅ NUEVO
  `numero_personas` INT(11) NOT NULL,
  `precio` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  `estado` ENUM('PENDIENTE', 'CONFIRMADA', 'CANCELADA', 'COMPLETADA') NOT NULL DEFAULT 'PENDIENTE',
  `observaciones` VARCHAR(500),
  `fecha_creacion` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_actualizacion` DATETIME ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;
```

---

## 📱 Para tu App Kotlin

### Modelo de datos sugerido:
```kotlin
data class ReservaRequest(
    val mesaId: Int,
    val nombreCliente: String,
    val telefonoCliente: String,
    val emailCliente: String?,
    val fechaReserva: String,        // "2025-10-21"
    val horaInicio: String,          // "14:00:00"
    val horaFin: String,             // "15:00:00"
    val numeroPersonas: Int,
    val precio: Double,
    val observaciones: String?
)

data class ReservaResponse(
    val id: Int,
    val mesaId: Int,
    val numeroMesa: String,
    val nombreCliente: String,
    val telefonoCliente: String,
    val emailCliente: String?,
    val fechaReserva: String,
    val horaInicio: String,
    val horaFin: String,
    val numeroPersonas: Int,
    val precio: Double,
    val estado: String,              // PENDIENTE, CONFIRMADA, etc.
    val observaciones: String?,
    val fechaCreacion: String
)
```

### Ejemplo de uso con Retrofit:
```kotlin
interface ReservasApi {
    @POST("api/reservas")
    suspend fun crearReserva(@Body request: ReservaRequest): ReservaResponse
    
    @PUT("api/reservas/{id}")
    suspend fun actualizarReserva(
        @Path("id") id: Int, 
        @Body request: ReservaRequest
    ): ReservaResponse
    
    @DELETE("api/reservas/{id}")
    suspend fun eliminarReserva(@Path("id") id: Int): ResponseBody
    
    @GET("api/reservas")
    suspend fun obtenerReservas(): List<ReservaResponse>
    
    @GET("api/reservas/mesa/{mesaId}")
    suspend fun obtenerReservasPorMesa(@Path("mesaId") mesaId: Int): List<ReservaResponse>
}
```

---

## ⏰ Rangos Horarios Sugeridos (10 AM - 10 PM)

```kotlin
val rangosHorarios = listOf(
    "10:00:00" to "11:00:00",
    "11:00:00" to "12:00:00",
    "12:00:00" to "13:00:00",
    "13:00:00" to "14:00:00",
    "14:00:00" to "15:00:00",
    "15:00:00" to "16:00:00",
    "16:00:00" to "17:00:00",
    "17:00:00" to "18:00:00",
    "18:00:00" to "19:00:00",
    "19:00:00" to "20:00:00",
    "20:00:00" to "21:00:00",
    "21:00:00" to "22:00:00"
)
```

---

## 🚀 Pasos para Implementar

1. **Ejecuta el script SQL actualizado:**
   ```bash
   DATABASE_COMPLETE.sql
   ```

2. **Reinicia tu aplicación Spring Boot** (los cambios ya están aplicados)

3. **Prueba los endpoints** con el archivo `API-ENDPOINTS.http`

4. **Integra en tu app Kotlin** usando los modelos de datos sugeridos

---

## ✅ Resumen de Funcionalidades

| Funcionalidad | Endpoint | Estado |
|--------------|----------|--------|
| Crear reserva | POST /api/reservas | ✅ |
| Actualizar reserva | PUT /api/reservas/{id} | ✅ NUEVO |
| Eliminar reserva | DELETE /api/reservas/{id} | ✅ |
| Consultar reservas | GET /api/reservas | ✅ |
| Cambiar estado | PATCH /api/reservas/{id}/estado | ✅ |
| Cancelar reserva | PATCH /api/reservas/{id}/cancelar | ✅ |
| Validación de conflictos | Automática | ✅ |

---

¡Todo listo para trabajar con rangos horarios! 🎉
