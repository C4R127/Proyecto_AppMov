# 🎯 Guía de Implementación de Picasso - Tu Proyecto de Reservas

## ⚠️ IMPORTANTE: Lee Esto Primero

**Picasso YA está instalado en tu proyecto Android** ✅  
**NO necesitas instalar nada en tu servicio web** ❌

### ¿Qué hace cada parte?

```
┌─────────────────────────────────────────────────────────────────┐
│                    TU ARQUITECTURA ACTUAL                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  📱 APP ANDROID (Proyecto_AppMoviles_TA2)                       │
│  ├── ✅ Picasso YA instalado (version 2.8)                      │
│  ├── ✅ Retrofit consume tu API                                │
│  └── 🔄 Necesitas: Actualizar modelos y usar Picasso en UI     │
│                                                                 │
│                           ↕️ (JSON con datos)                   │
│                                                                 │
│  🌐 WEB SERVICE (Tu Backend Spring Boot)                        │
│  ├── Actualmente devuelve: Restaurante, Mesa, Reserva          │
│  ├── 🔄 Necesitas: Agregar campos de URL de imágenes           │
│  └── ❌ NO necesita Picasso (es librería de Android)           │
│                                                                 │
│                           ↕️ (URLs apuntan a)                   │
│                                                                 │
│  ☁️ SERVIDOR DE IMÁGENES (Para empezar)                         │
│  └── Unsplash, Pexels, Pixabay (URLs gratuitas para pruebas)  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 📋 Checklist General

### Backend (Servicio Web Spring Boot)
- [ ] Agregar campos de imagen en clases Entity
- [ ] Actualizar DTOs si usas
- [ ] Reiniciar el servicio web
- [ ] Probar endpoints y verificar que devuelven URLs

### Android (Esta App - Proyecto_AppMoviles_TA2)
- [ ] ✅ Picasso ya está en `build.gradle.kts`
- [ ] Actualizar modelos de datos (Restaurante, Mesa)
- [ ] Modificar RestauranteAdapter para usar Picasso
- [ ] Modificar MesaAdapter para usar Picasso
- [ ] Actualizar layouts XML para las imágenes
- [ ] ✅ Permiso INTERNET ya está en Manifest
- [ ] Probar la app

---

## 🔧 PARTE 1: Actualizar tu Servicio Web (Backend)

### Paso 1.1: Actualizar la Entidad Restaurante

Busca tu clase `Restaurante.java` en el backend y agrega estos campos:

```java
@Entity
@Table(name = "restaurantes")
public class Restaurante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private String nombre;
    private String direccion;
    private String telefono;
    private String descripcion;
    
    // ⬇️ AGREGAR ESTOS CAMPOS
    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;
    
    @Column(name = "imagen_thumbnail_url", length = 500)
    private String imagenThumbnailUrl;
    
    // ... getters y setters
    
    public String getImagenUrl() {
        return imagenUrl;
    }
    
    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }
    
    public String getImagenThumbnailUrl() {
        return imagenThumbnailUrl;
    }
    
    public void setImagenThumbnailUrl(String imagenThumbnailUrl) {
        this.imagenThumbnailUrl = imagenThumbnailUrl;
    }
}
```

### Paso 1.2: Actualizar la Entidad Mesa

```java
@Entity
@Table(name = "mesas")
public class Mesa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private Integer numero;
    private String numeroMesa;
    private Integer capacidad;
    private String estado;
    private Boolean disponible;
    
    @Column(name = "restaurante_id")
    private Integer restauranteId;
    
    // ⬇️ AGREGAR ESTE CAMPO
    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;
    
    // ... getters y setters
    
    public String getImagenUrl() {
        return imagenUrl;
    }
    
    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }
}
```

### Paso 1.3: Actualizar la Base de Datos

Abre **phpMyAdmin** y ejecuta:

```sql
-- Agregar columnas para imágenes en restaurantes
ALTER TABLE restaurantes 
ADD COLUMN imagen_url VARCHAR(500) NULL,
ADD COLUMN imagen_thumbnail_url VARCHAR(500) NULL;

-- Agregar columna para imágenes en mesas
ALTER TABLE mesas 
ADD COLUMN imagen_url VARCHAR(500) NULL;

-- Insertar URLs de ejemplo para restaurantes existentes
UPDATE restaurantes SET 
    imagen_url = 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=800',
    imagen_thumbnail_url = 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=200'
WHERE id = 1;

UPDATE restaurantes SET 
    imagen_url = 'https://images.unsplash.com/photo-1514933651103-005eec06c04b?w=800',
    imagen_thumbnail_url = 'https://images.unsplash.com/photo-1514933651103-005eec06c04b?w=200'
WHERE id = 2;

UPDATE restaurantes SET 
    imagen_url = 'https://images.unsplash.com/photo-1552566626-52f8b828add9?w=800',
    imagen_thumbnail_url = 'https://images.unsplash.com/photo-1552566626-52f8b828add9?w=200'
WHERE id = 3;

-- URLs de ejemplo para mesas
UPDATE mesas SET imagen_url = 'https://images.unsplash.com/photo-1466978913421-dad2ebd01d17?w=400' WHERE capacidad = 2;
UPDATE mesas SET imagen_url = 'https://images.unsplash.com/photo-1559339352-11d035aa65de?w=400' WHERE capacidad = 4;
UPDATE mesas SET imagen_url = 'https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=400' WHERE capacidad >= 6;
```

### Paso 1.4: Reiniciar el Servicio Web

En tu terminal del backend:

```bash
# Si usas Maven
mvn clean spring-boot:run

# O si usas el wrapper de Maven
./mvnw clean spring-boot:run

# En Windows
mvnw.cmd clean spring-boot:run
```

### Paso 1.5: Probar el Backend

Usa **Postman** o **Thunder Client** para verificar:

**GET** `http://localhost:8080/api/restaurantes`

**Respuesta esperada:**
```json
[
  {
    "id": 1,
    "nombre": "La Pizzería Italiana",
    "direccion": "Av. Principal 123",
    "telefono": "555-1234",
    "descripcion": "Auténtica comida italiana",
    "imagenUrl": "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=800",
    "imagenThumbnailUrl": "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=200",
    "imageRes": 0
  }
]
```

**GET** `http://localhost:8080/api/mesas/restaurante/1`

**Respuesta esperada:**
```json
[
  {
    "id": 1,
    "numero": 1,
    "numeroMesa": "Mesa 1",
    "capacidad": 4,
    "disponible": true,
    "restauranteId": 1,
    "imagenUrl": "https://images.unsplash.com/photo-1559339352-11d035aa65de?w=400"
  }
]
```

✅ **Si ves los campos `imagenUrl` y `imagenThumbnailUrl`: Backend listo!**

---

## 📱 PARTE 2: Actualizar tu App Android

### Paso 2.1: Verificar Picasso (Ya está ✅)

Tu archivo `build.gradle.kts` ya tiene:
```kotlin
implementation("com.squareup.picasso:picasso:2.8")
```

### Paso 2.2: Actualizar Modelo Restaurante

**Archivo:** `app/src/main/java/com/example/proyectoavance1/model/Restaurante.kt`

**Antes:**
```kotlin
data class Restaurante(
    val id: Int,
    val nombre: String,
    val direccion: String,
    val telefono: String,
    val descripcion: String?,
    val imageRes: Int = 0
)
```

**Después:**
```kotlin
data class Restaurante(
    val id: Int,
    val nombre: String,
    val direccion: String,
    val telefono: String,
    val descripcion: String?,
    val imagenUrl: String? = null,           // ⬅️ NUEVO
    val imagenThumbnailUrl: String? = null,  // ⬅️ NUEVO
    val imageRes: Int = 0                    // Mantener para compatibilidad
)
```

### Paso 2.3: Actualizar Modelo Mesa

**Archivo:** `app/src/main/java/com/example/proyectoavance1/model/Mesa.kt`

**Antes:**
```kotlin
data class Mesa(
    val id: Int,
    val numero: Int,
    val numeroMesa: String? = null,
    val capacidad: Int,
    val estado: String? = null,
    val disponible: Boolean = true,
    val restauranteId: Int,
    val ocupada: Boolean = false
)
```

**Después:**
```kotlin
data class Mesa(
    val id: Int,
    val numero: Int,
    val numeroMesa: String? = null,
    val capacidad: Int,
    val estado: String? = null,
    val disponible: Boolean = true,
    val restauranteId: Int,
    val ocupada: Boolean = false,
    val imagenUrl: String? = null  // ⬅️ NUEVO
)
```

### Paso 2.4: Actualizar RestauranteAdapter

**Archivo:** `app/src/main/java/com/example/proyectoavance1/adapter/RestauranteAdapter.kt`

**Reemplaza la función `bind`:**

```kotlin
fun bind(restaurante: Restaurante) {
    tvNombre.text = restaurante.nombre
    tvDireccion.text = restaurante.direccion
    tvTelefono.text = restaurante.telefono

    // ⬇️ REEMPLAZAR ESTE BLOQUE
    // Priorizar imagenUrl del servidor sobre imageRes local
    when {
        !restaurante.imagenUrl.isNullOrEmpty() -> {
            // Cargar imagen desde URL con Picasso
            Picasso.get()
                .load(restaurante.imagenUrl)
                .placeholder(R.drawable.restaurante_pf)  // Mientras carga
                .error(R.drawable.ic_restaurant_dish)    // Si falla
                .fit()
                .centerCrop()
                .into(imgRestaurante)
        }
        restaurante.imageRes != 0 -> {
            // Fallback a recurso local
            Picasso.get()
                .load(restaurante.imageRes)
                .into(imgRestaurante)
        }
        else -> {
            // Imagen por defecto
            imgRestaurante.setImageResource(R.drawable.restaurante_pf)
        }
    }

    // Descripción opcional
    if (restaurante.descripcion.isNullOrEmpty()) {
        tvDescripcion.visibility = View.GONE
    } else {
        tvDescripcion.visibility = View.VISIBLE
        tvDescripcion.text = restaurante.descripcion
    }

    // Acción al hacer clic
    itemView.setOnClickListener {
        onItemClick(restaurante)
    }
}
```

### Paso 2.5: Actualizar MesaAdapter (Opcional pero recomendado)

**Archivo:** `app/src/main/java/com/example/proyectoavance1/adapter/MesaAdapter.kt`

Primero, agrega un `ImageView` en el ViewHolder:

```kotlin
inner class MesaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val cardMesa: CardView = itemView.findViewById(R.id.cardMesa)
    private val tvNumero: TextView = itemView.findViewById(R.id.tvMesaNumero)
    private val tvCapacidad: TextView = itemView.findViewById(R.id.tvMesaCapacidad)
    private val tvEstado: TextView = itemView.findViewById(R.id.tvMesaEstado)
    private val viewEstado: View = itemView.findViewById(R.id.viewEstado)
    private val imgMesa: ImageView = itemView.findViewById(R.id.imgMesa)  // ⬅️ NUEVO
    
    // ... resto del código
}
```

Luego actualiza la función `bind`:

```kotlin
fun bind(mesa: Mesa, position: Int) {
    val numeroMesaDisplay = mesa.numeroMesa ?: "Mesa ${mesa.numero}"
    tvNumero.text = numeroMesaDisplay
    tvCapacidad.text = "Capacidad: ${mesa.capacidad} personas"
    
    // ⬇️ AGREGAR: Cargar imagen de la mesa si existe
    if (!mesa.imagenUrl.isNullOrEmpty()) {
        imgMesa.visibility = View.VISIBLE
        Picasso.get()
            .load(mesa.imagenUrl)
            .placeholder(R.drawable.ic_restaurant_table)
            .error(R.drawable.ic_restaurant_table)
            .resize(80, 80)
            .centerCrop()
            .into(imgMesa)
    } else {
        imgMesa.visibility = View.GONE
    }
    
    // ... resto del código existente
}
```

### Paso 2.6: Actualizar Layout de Mesa (item_mesa.xml)

**Archivo:** `app/src/main/res/layout/item_mesa.xml`

Agrega el ImageView después del ícono existente:

```xml
<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    android:padding="16dp"
    android:gravity="center_vertical">

    <!-- Icono de Mesa existente -->
    <ImageView
        android:layout_width="40dp"
        android:layout_height="40dp"
        android:src="@drawable/ic_restaurant_table"
        android:layout_marginEnd="16dp"
        app:tint="@color/green_primary" />

    <!-- ⬇️ AGREGAR: Imagen real de la mesa -->
    <ImageView
        android:id="@+id/imgMesa"
        android:layout_width="60dp"
        android:layout_height="60dp"
        android:scaleType="centerCrop"
        android:layout_marginEnd="12dp"
        android:visibility="gone"
        android:contentDescription="@string/mesa_image" />

    <!-- Resto del layout existente... -->
</LinearLayout>
```

### Paso 2.7: Agregar String Resource

**Archivo:** `app/src/main/res/values/strings.xml`

```xml
<resources>
    <!-- Existentes... -->
    <string name="mesa_image">Imagen de la mesa</string>
</resources>
```

### Paso 2.8: Verificar Permisos (Ya está ✅)

Tu `AndroidManifest.xml` ya debería tener:
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

---

## 🧪 PARTE 3: Probar la Implementación

### Paso 3.1: Sincronizar el Proyecto

1. En Android Studio, haz clic en **File > Sync Project with Gradle Files**
2. Espera a que termine la sincronización

### Paso 3.2: Limpiar y Reconstruir

```powershell
# En la terminal de Android Studio
.\gradlew clean build
```

### Paso 3.3: Ejecutar la App

1. Asegúrate de que tu **servicio web esté corriendo** en `http://localhost:8080`
2. Ejecuta la app en el emulador o dispositivo físico
3. Ve al **DashboardFragment** (lista de restaurantes)
4. Deberías ver las imágenes cargando desde Unsplash

### Paso 3.4: Verificar que Funciona

✅ **Señales de éxito:**
- Las imágenes de restaurantes cargan correctamente
- Mientras cargan, ves el placeholder `restaurante_pf`
- Si hay error, ves el ícono de error
- Las imágenes se ven bien proporcionadas

❌ **Si algo falla:**
- Verifica que el backend esté corriendo
- Revisa Logcat para mensajes de error
- Confirma que las URLs están en la respuesta del API

---

## 🐛 Solución de Problemas

### Problema: "Unresolved reference: Picasso"

**Causa:** Falta sincronizar Gradle

**Solución:**
```kotlin
// En build.gradle.kts (Module: app) debe estar:
implementation("com.squareup.picasso:picasso:2.8")
```
Luego: **File > Sync Project with Gradle Files**

---

### Problema: Las imágenes no cargan

**Causa 1:** Backend no devuelve las URLs

**Solución:**
- Ve a Logcat y filtra por "Retrofit" o "OkHttp"
- Verifica la respuesta JSON
- Confirma que `imagenUrl` no es null

**Causa 2:** Permisos de internet

**Solución:**
- Verifica `AndroidManifest.xml` tiene `<uses-permission android:name="android.permission.INTERNET" />`

---

### Problema: "Cleartext HTTP traffic not permitted"

**Causa:** Android bloquea HTTP (no HTTPS) por defecto

**Solución temporal (solo para desarrollo):**

**Archivo:** `app/src/main/AndroidManifest.xml`

```xml
<application
    android:usesCleartextTraffic="true"
    ...>
```

---

### Problema: Las imágenes se ven distorsionadas

**Solución:** Ajusta los métodos de Picasso:

```kotlin
// Para llenar todo el espacio (puede recortar)
Picasso.get()
    .load(url)
    .fit()
    .centerCrop()
    .into(imageView)

// Para ajustar dentro sin recortar
Picasso.get()
    .load(url)
    .fit()
    .centerInside()
    .into(imageView)

// Para tamaño específico
Picasso.get()
    .load(url)
    .resize(300, 200)
    .centerCrop()
    .into(imageView)
```

---

## 🎨 Mejoras Opcionales

### 1. Imágenes Circulares

Agrega esta clase de transformación:

**Archivo:** `app/src/main/java/com/example/primeravance/util/CircleTransform.kt`

```kotlin
package com.example.primeravance.util

import android.graphics.*
import com.squareup.picasso.Transformation

class CircleTransform : Transformation {
    override fun transform(source: Bitmap): Bitmap {
        val size = Math.min(source.width, source.height)
        
        val x = (source.width - size) / 2
        val y = (source.height - size) / 2
        
        val squaredBitmap = Bitmap.createBitmap(source, x, y, size, size)
        if (squaredBitmap != source) {
            source.recycle()
        }
        
        val bitmap = Bitmap.createBitmap(size, size, source.config)
        
        val canvas = Canvas(bitmap)
        val paint = Paint()
        val shader = BitmapShader(squaredBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.shader = shader
        paint.isAntiAlias = true
        
        val r = size / 2f
        canvas.drawCircle(r, r, r, paint)
        
        squaredBitmap.recycle()
        return bitmap
    }
    
    override fun key(): String = "circle"
}
```

**Uso:**
```kotlin
Picasso.get()
    .load(url)
    .transform(CircleTransform())
    .into(imageView)
```

---

### 2. Indicador de Progreso

```kotlin
// En tu adapter o fragment
Picasso.get()
    .load(restaurante.imagenUrl)
    .placeholder(R.drawable.restaurante_pf)
    .error(R.drawable.ic_restaurant_dish)
    .into(imgRestaurante, object : com.squareup.picasso.Callback {
        override fun onSuccess() {
            // Imagen cargada con éxito
            progressBar.visibility = View.GONE
        }
        
        override fun onError(e: Exception?) {
            // Error al cargar
            progressBar.visibility = View.GONE
            Toast.makeText(context, "Error al cargar imagen", Toast.LENGTH_SHORT).show()
        }
    })
```

---

### 3. Caché Personalizado

En tu `Application` class:

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Configurar Picasso con caché personalizado
        val picasso = Picasso.Builder(this)
            .indicatorsEnabled(true)  // Mostrar indicadores de debug
            .loggingEnabled(true)     // Logs en Logcat
            .build()
        
        Picasso.setSingletonInstance(picasso)
    }
}
```

No olvides registrarlo en el Manifest:
```xml
<application
    android:name=".MyApplication"
    ...>
```

---

## 📚 URLs de Imágenes Gratuitas (Para Pruebas)

### Unsplash (Recomendado)
```
https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=800
https://images.unsplash.com/photo-1514933651103-005eec06c04b?w=800
https://images.unsplash.com/photo-1552566626-52f8b828add9?w=800
```

### Pexels
```
https://images.pexels.com/photos/262978/pexels-photo-262978.jpeg?w=800
```

### Pixabay
```
https://pixabay.com/get/g...
```

**Nota:** Para producción, considera usar:
- **Firebase Storage** (5GB gratis)
- **Cloudinary** (Optimización automática)
- **AWS S3** (Escalable)

---

## ✅ Checklist Final de Implementación

### Backend
- [x] Agregué campos `imagenUrl` en entidad Restaurante
- [x] Agregué campo `imagenUrl` en entidad Mesa
- [x] Actualicé la base de datos con `ALTER TABLE`
- [x] Inserté URLs de ejemplo
- [x] Reinicié el servicio web
- [x] Probé con Postman y veo las URLs

### Android
- [x] Picasso está en `build.gradle.kts`
- [x] Actualicé modelo `Restaurante.kt`
- [x] Actualicé modelo `Mesa.kt`
- [x] Modifiqué `RestauranteAdapter.kt`
- [x] Modifiqué `MesaAdapter.kt` (opcional)
- [x] Actualicé `item_mesa.xml` (opcional)
- [x] Sincronicé Gradle
- [x] Permiso INTERNET en Manifest
- [x] Probé la app y las imágenes cargan

---

## 🎉 ¡Listo!

Tu app ahora:
- ✅ Carga imágenes desde URLs remotas
- ✅ Usa caché automático de Picasso
- ✅ Muestra placeholders mientras carga
- ✅ Maneja errores gracefully
- ✅ Optimiza el rendimiento

**Recuerda:** El backend solo envía URLs, Picasso (en Android) hace todo el trabajo de descarga, caché y optimización.

---

## 📖 Recursos Adicionales

- **Picasso Docs:** https://square.github.io/picasso/
- **Retrofit + Picasso:** https://github.com/square/retrofit
- **Firebase Storage:** https://firebase.google.com/docs/storage
- **Unsplash API:** https://unsplash.com/developers

---

**Fecha:** Noviembre 15, 2025  
**Proyecto:** Sistema de Reservas - Proyecto_AppMoviles_TA2  
**Autor:** Generado por GitHub Copilot
