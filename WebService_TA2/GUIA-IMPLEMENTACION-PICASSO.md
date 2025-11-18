# 📸 Guía de Implementación: Soporte de Imágenes con Picasso

## ✅ ¿Qué se implementó en el Servicio Web?

### 1. **Modificaciones en Base de Datos**
Se agregaron las siguientes columnas:

#### Tabla `restaurantes`:
- `imagen_url` (VARCHAR 500): URL de la imagen principal del restaurante
- `imagen_thumbnail_url` (VARCHAR 500): URL de la miniatura (opcional, para optimización)

#### Tabla `mesas`:
- `imagen_url` (VARCHAR 500): URL de la imagen de la mesa

### 2. **Entidades Actualizadas**
✅ `Restaurante.java` - Campos `imagenUrl` y `imagenThumbnailUrl` agregados
✅ `Mesa.java` - Campo `imagenUrl` agregado

### 3. **DTOs Actualizados**
✅ `RestauranteDTO.java` - Incluye URLs de imágenes
✅ `MesaDTO.java` - Incluye URL de imagen

### 4. **Servicios Actualizados**
✅ `RestauranteService.java` - Mapea URLs en conversiones
✅ `MesaService.java` - Mapea URLs en conversiones

---

## 🚀 Pasos para Completar la Implementación

### Paso 1: Ejecutar el Script SQL
```bash
# Abre tu cliente MySQL y ejecuta:
mysql -u root -p < db/ADD_IMAGE_URLS.sql
```

O ejecuta manualmente desde MySQL Workbench:
1. Abre el archivo `db/ADD_IMAGE_URLS.sql`
2. Ejecuta todo el contenido
3. Verifica que las columnas se agregaron correctamente

### Paso 2: Reiniciar el Servicio Web
```bash
# Detén la aplicación actual
# Luego ejecuta:
mvnw clean install
mvnw spring-boot:run
```

### Paso 3: Verificar que las APIs Devuelven las URLs

#### Ejemplo de respuesta esperada para GET /api/restaurantes:
```json
[
  {
    "id": 1,
    "nombre": "La Pizzería Italiana",
    "direccion": "Av. Principal 123",
    "telefono": "555-1234",
    "email": "info@pizzeria.com",
    "imagenUrl": "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=800",
    "imagenThumbnailUrl": "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=200",
    "horaApertura": "11:00",
    "horaCierre": "23:00"
  }
]
```

#### Ejemplo de respuesta esperada para GET /api/mesas/restaurante/1:
```json
[
  {
    "id": 1,
    "restauranteId": 1,
    "numeroMesa": "M-01",
    "capacidad": 2,
    "disponible": true,
    "imagenUrl": "https://images.unsplash.com/photo-1466978913421-dad2ebd01d17?w=400"
  }
]
```

---

## 📱 Cómo Usar en tu App Android con Picasso

### 1. Verificar Dependencia (YA ESTÁ INSTALADA)
En tu `build.gradle.kts`:
```kotlin
implementation("com.squareup.picasso:picasso:2.8")
```

### 2. Agregar Permisos de Internet
En tu `AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

### 3. Cargar Imágenes en tu Adapter

#### Ejemplo para Lista de Restaurantes:
```kotlin
// En tu RestauranteAdapter.kt
class RestauranteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val imageView: ImageView = view.findViewById(R.id.ivRestaurante)
    private val tvNombre: TextView = view.findViewById(R.id.tvNombre)
    
    fun bind(restaurante: Restaurante) {
        tvNombre.text = restaurante.nombre
        
        // Cargar imagen con Picasso
        Picasso.get()
            .load(restaurante.imagenUrl)
            .placeholder(R.drawable.ic_restaurant_placeholder) // Mientras carga
            .error(R.drawable.ic_restaurant_error)            // Si falla
            .resize(800, 600)                                 // Redimensionar
            .centerCrop()                                     // Ajustar
            .into(imageView)
    }
}
```

#### Ejemplo para Lista de Mesas:
```kotlin
// En tu MesaAdapter.kt
class MesaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val imageView: ImageView = view.findViewById(R.id.ivMesa)
    
    fun bind(mesa: Mesa) {
        Picasso.get()
            .load(mesa.imagenUrl)
            .placeholder(R.drawable.ic_table_placeholder)
            .error(R.drawable.ic_table_error)
            .fit()
            .centerInside()
            .into(imageView)
    }
}
```

### 4. Transformaciones Avanzadas (Opcional)

#### Imágenes Circulares:
```kotlin
Picasso.get()
    .load(restaurante.imagenUrl)
    .transform(CircleTransform())
    .into(imageView)
```

#### Con Caché Personalizado:
```kotlin
Picasso.get()
    .load(restaurante.imagenUrl)
    .networkPolicy(NetworkPolicy.OFFLINE) // Primero intenta desde caché
    .into(imageView, object : Callback {
        override fun onSuccess() {
            // Imagen cargada desde caché
        }
        
        override fun onError(e: Exception?) {
            // Si falla, cargar desde red
            Picasso.get()
                .load(restaurante.imagenUrl)
                .into(imageView)
        }
    })
```

---

## 🎨 Opciones para Hospedar Imágenes

### Opción 1: Unsplash (Ya configurado - Solo para pruebas)
- ✅ Gratuito
- ✅ Alta calidad
- ⚠️ **Solo para desarrollo/pruebas**
- ❌ No es apropiado para producción

### Opción 2: Firebase Storage (Recomendado)
```kotlin
// 1. Subir imagen
val storageRef = Firebase.storage.reference
val imageRef = storageRef.child("restaurantes/${restaurante.id}.jpg")

imageRef.putFile(imageUri).addOnSuccessListener {
    // 2. Obtener URL
    imageRef.downloadUrl.addOnSuccessListener { url ->
        // 3. Guardar URL en tu API
        actualizarRestaurante(restaurante.copy(imagenUrl = url.toString()))
    }
}
```

### Opción 3: Cloudinary
```kotlin
// Configuración simple
MediaManager.init(this, mapOf(
    "cloud_name" to "tu-cloud-name",
    "api_key" to "tu-api-key",
    "api_secret" to "tu-api-secret"
))

// URL automática
val url = MediaManager.get().url()
    .transformation(Transformation()
        .width(800)
        .height(600)
        .crop("fill"))
    .generate("restaurante_${id}.jpg")
```

### Opción 4: Servidor Propio (Con Spring Boot)
Puedes agregar un endpoint para subir imágenes:

```java
@RestController
@RequestMapping("/api/imagenes")
public class ImagenController {
    
    @PostMapping("/upload")
    public ResponseEntity<String> subirImagen(@RequestParam("file") MultipartFile file) {
        // Guardar en disco o cloud storage
        String filename = storageService.guardar(file);
        String url = "https://tu-servidor.com/imagenes/" + filename;
        return ResponseEntity.ok(url);
    }
}
```

---

## 🧪 Testing

### Probar desde Postman/Thunder Client:

#### 1. Obtener restaurantes con imágenes:
```http
GET http://localhost:8080/api/restaurantes
```

#### 2. Crear restaurante con imagen:
```http
POST http://localhost:8080/api/restaurantes
Content-Type: application/json

{
  "nombre": "Nuevo Restaurante",
  "direccion": "Calle Nueva 123",
  "telefono": "555-9999",
  "email": "nuevo@restaurant.com",
  "imagenUrl": "https://example.com/mi-imagen.jpg",
  "imagenThumbnailUrl": "https://example.com/mi-imagen-thumb.jpg",
  "horaApertura": "10:00",
  "horaCierre": "22:00"
}
```

---

## 📊 Ventajas de esta Arquitectura

### ✅ Rendimiento Optimizado
- Las imágenes se cachean automáticamente en el dispositivo
- Reduce consumo de datos móviles
- Carga más rápida en siguientes visitas

### ✅ Escalabilidad
- El backend solo almacena URLs livianas
- Las imágenes pueden estar en CDN
- No sobrecarga el servidor de aplicación

### ✅ Flexibilidad
- Puedes cambiar URLs sin modificar código
- Fácil integración con servicios cloud
- Soporte para múltiples tamaños (thumbnails, full)

### ✅ Mantenimiento
- Separación de responsabilidades
- Fácil actualizar imágenes
- Compatible con cualquier fuente de imágenes

---

## 🐛 Solución de Problemas Comunes

### Problema: "Las imágenes no cargan"
**Solución:**
1. Verifica que tengas permiso de INTERNET en AndroidManifest.xml
2. Comprueba que las URLs sean accesibles desde el navegador
3. Revisa los logs de Picasso: `Picasso.get().setLoggingEnabled(true)`

### Problema: "Imágenes muy grandes/lentas"
**Solución:**
```kotlin
Picasso.get()
    .load(url)
    .resize(800, 600)  // Redimensiona antes de mostrar
    .onlyScaleDown()   // Solo reduce, no agranda
    .into(imageView)
```

### Problema: "OutOfMemoryError"
**Solución:**
```kotlin
// En Application class
val picasso = Picasso.Builder(this)
    .memoryCache(LruCache(30))  // Limita caché a 30MB
    .build()
Picasso.setSingletonInstance(picasso)
```

---

## 📚 Recursos Adicionales

- [Documentación Picasso](https://square.github.io/picasso/)
- [Unsplash API](https://unsplash.com/developers)
- [Firebase Storage](https://firebase.google.com/docs/storage)
- [Cloudinary Android SDK](https://cloudinary.com/documentation/android_integration)

---

## 🎯 Próximos Pasos Recomendados

1. ✅ Ejecutar el script SQL de migración
2. ✅ Reiniciar el servicio web
3. ✅ Probar endpoints con Postman
4. 📱 Actualizar modelos de datos en Android
5. 📱 Implementar Picasso en tus adapters
6. 🎨 Decidir dónde hospedarás las imágenes finales
7. 📤 Implementar funcionalidad de subida de imágenes (opcional)

---

**¡Implementación completada!** 🎉

Ahora tu servicio web está listo para proporcionar URLs de imágenes que tu app Android puede consumir eficientemente con Picasso.
