# Sistema de Reservas de Restaurante - API REST

## Descripción
API REST para gestionar reservas de mesas en restaurantes, desarrollada con Spring Boot 3 y MySQL.

## Tecnologías Utilizadas
- Java 17
- Spring Boot 3.4
- Spring Data JPA
- MySQL 8.0 (usando XAMPP)
- Lombok
- Maven

## Configuración de Base de Datos

### Requisitos Previos
- **XAMPP** instalado y ejecutándose
- MySQL debe estar activo en XAMPP (puerto 3306)
- Aplicación corriendo en puerto **8080**

### Configurar application.properties
Ya está configurado en `src/main/resources/application.properties` para usar XAMPP:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/reservas_simple
spring.datasource.username=root
spring.datasource.password=
server.port=8080
```

### Crear la Base de Datos
**Usando phpMyAdmin (XAMPP):**
1. Abre phpMyAdmin en `http://localhost/phpmyadmin`
2. Ve a la pestaña "SQL"
3. Ejecuta el script SQL ubicado en `db/ReservasDB_Complete.sql`

**O crea la base de datos manualmente:**
```sql
CREATE DATABASE reservas_simple;
```

## Estructura del Proyecto

```
src/main/java/com/webmovil/demo/
├── entity/          # Entidades JPA
│   ├── Restaurante.java
│   ├── Mesa.java
│   └── Reserva.java
├── repository/      # Repositorios JPA
│   ├── RestauranteRepository.java
│   ├── MesaRepository.java
│   └── ReservaRepository.java
├── service/         # Lógica de negocio
│   ├── RestauranteService.java
│   ├── MesaService.java
│   └── ReservaService.java
├── controller/      # Controladores REST
│   ├── RestauranteController.java
│   ├── MesaController.java
│   └── ReservaController.java
└── dto/            # Objetos de transferencia de datos
    ├── RestauranteDTO.java
    ├── MesaDTO.java
    ├── ReservaDTO.java
    └── CrearReservaRequest.java
```

## Endpoints de la API

### Restaurantes

#### Listar todos los restaurantes
```http
GET /api/restaurantes
```

#### Obtener restaurante por ID
```http
GET /api/restaurantes/{id}
```

#### Crear restaurante
```http
POST /api/restaurantes
Content-Type: application/json

{
  "nombre": "Restaurante El Buen Sabor",
  "direccion": "Calle Principal 123",
  "telefono": "555-1234",
  "email": "info@elbuensabor.com",
  "horaApertura": "10:00",
  "horaCierre": "22:00"
}
```

#### Actualizar restaurante
```http
PUT /api/restaurantes/{id}
Content-Type: application/json

{
  "nombre": "Restaurante El Buen Sabor",
  "direccion": "Calle Principal 123",
  "telefono": "555-1234",
  "email": "info@elbuensabor.com",
  "horaApertura": "10:00",
  "horaCierre": "23:00"
}
```

#### Eliminar restaurante
```http
DELETE /api/restaurantes/{id}
```

### Mesas

#### Listar mesas de un restaurante
```http
GET /api/mesas/restaurante/{restauranteId}
```

#### Listar mesas disponibles
```http
GET /api/mesas/restaurante/{restauranteId}/disponibles
```

#### Buscar mesas por capacidad
```http
GET /api/mesas/restaurante/{restauranteId}/capacidad/{capacidad}
```

#### Obtener mesa por ID
```http
GET /api/mesas/{id}
```

#### Crear mesa
```http
POST /api/mesas
Content-Type: application/json

{
  "restauranteId": 1,
  "numeroMesa": "M-01",
  "capacidad": 4,
  "disponible": true
}
```

#### Actualizar mesa
```http
PUT /api/mesas/{id}
Content-Type: application/json

{
  "numeroMesa": "M-01",
  "capacidad": 6,
  "disponible": true
}
```

#### Cambiar disponibilidad de mesa
```http
PATCH /api/mesas/{id}/disponibilidad?disponible=false
```

#### Eliminar mesa
```http
DELETE /api/mesas/{id}
```

### Reservas

#### Listar todas las reservas
```http
GET /api/reservas
```

#### Obtener reserva por ID
```http
GET /api/reservas/{id}
```

#### Listar reservas de una mesa
```http
GET /api/reservas/mesa/{mesaId}
```

#### Listar reservas por estado
```http
GET /api/reservas/estado/{estado}
```
Estados disponibles: PENDIENTE, CONFIRMADA, CANCELADA, COMPLETADA

#### Listar reservas de un restaurante
```http
GET /api/reservas/restaurante/{restauranteId}?desde=2025-10-06T10:00:00
```

#### Listar reservas por rango de fechas
```http
GET /api/reservas/rango-fechas?fechaInicio=2025-10-06T10:00:00&fechaFin=2025-10-07T23:59:59
```

#### Crear reserva
```http
POST /api/reservas
Content-Type: application/json

{
  "mesaId": 1,
  "nombreCliente": "Juan Pérez",
  "telefonoCliente": "555-9876",
  "emailCliente": "juan@example.com",
  "fechaHoraReserva": "2025-10-07T19:30:00",
  "numeroPersonas": 4,
  "observaciones": "Celebración de cumpleaños"
}
```

#### Cambiar estado de reserva
```http
PATCH /api/reservas/{id}/estado?estado=CONFIRMADA
```

#### Cancelar reserva
```http
PATCH /api/reservas/{id}/cancelar
```

#### Eliminar reserva
```http
DELETE /api/reservas/{id}
```

## Reglas de Negocio

### Reservas
1. **Validación de fecha**: La fecha de reserva debe ser futura
2. **Validación de capacidad**: La mesa debe tener capacidad suficiente para el número de personas
3. **Validación de disponibilidad**: La mesa debe estar marcada como disponible
4. **Validación de conflictos**: No pueden existir dos reservas activas en la misma mesa con menos de 2 horas de diferencia
5. **Estados de reserva**:
   - PENDIENTE: Reserva creada, pendiente de confirmación
   - CONFIRMADA: Reserva confirmada por el restaurante
   - CANCELADA: Reserva cancelada
   - COMPLETADA: Reserva completada (cliente ya asistió)

### Mesas
1. Cada mesa tiene un número único por restaurante
2. La capacidad indica el número máximo de personas
3. El estado "disponible" indica si la mesa puede recibir reservas

## Ejecutar la Aplicación

### Requisitos Previos
- JDK 17 o superior
- **XAMPP** con MySQL activo (puerto 3306)
- Maven 3.6 o superior

### Pasos
1. **Inicia XAMPP** y asegúrate de que MySQL esté ejecutándose
2. **Crea la base de datos** `reservas_simple` usando phpMyAdmin o ejecutando el script SQL
3. **Ejecuta la aplicación**:
```bash
mvnw spring-boot:run
```
O en Windows:
```cmd
mvnw.cmd spring-boot:run
```

La aplicación estará disponible en: **`http://localhost:8080`**

## Probar la API

Puedes usar herramientas como:
- **Postman**: Importa los endpoints descritos arriba (recomendado para pruebas completas)
- **cURL**: Usa los comandos de ejemplo
- **Thunder Client** (extensión de VS Code)
- **Navegador web**: Para peticiones GET

### Ejemplo con cURL
```bash
# Crear un restaurante
curl -X POST http://localhost:8080/api/restaurantes \
  -H "Content-Type: application/json" \
  -d '{"nombre":"La Pizzería","direccion":"Av. Italia 456","telefono":"555-5555","email":"info@pizzeria.com","horaApertura":"11:00","horaCierre":"23:00"}'

# Crear una mesa
curl -X POST http://localhost:8080/api/mesas \
  -H "Content-Type: application/json" \
  -d '{"restauranteId":1,"numeroMesa":"M-01","capacidad":4,"disponible":true}'

# Crear una reserva
curl -X POST http://localhost:8080/api/reservas \
  -H "Content-Type: application/json" \
  -d '{"mesaId":1,"nombreCliente":"María García","telefonoCliente":"555-7777","emailCliente":"maria@example.com","fechaHoraReserva":"2025-10-07T20:00:00","numeroPersonas":4,"observaciones":"Mesa junto a la ventana"}'
```

## Notas sobre la Base de Datos

### Configuración con XAMPP
- **Puerto MySQL**: 3306 (por defecto en XAMPP)
- **Usuario**: root
- **Contraseña**: (vacía por defecto)
- **Puerto Aplicación**: 8080

La base de datos original (`db/ReservasDB.sql`) solo contiene la tabla `mesas`. El sistema ha sido extendido con:
- Tabla `restaurantes`: Información de los restaurantes
- Tabla `reservas`: Gestión de reservas

**Recomendación:** Ejecuta el script completo `db/ReservasDB_Complete.sql` en phpMyAdmin para crear todas las tablas con datos de ejemplo.

Alternativamente, Hibernate creará automáticamente estas tablas al ejecutar la aplicación (configurado con `spring.jpa.hibernate.ddl-auto=update`).

## Características Adicionales

- **CORS habilitado**: La API acepta peticiones desde cualquier origen
- **Validaciones**: Todas las operaciones incluyen validaciones de negocio
- **Manejo de errores**: Respuestas apropiadas para errores comunes
- **Logging SQL**: Las consultas SQL se muestran en consola para debugging

## Autor
Sistema creado para gestión de reservas de restaurante.
