# 🎯 Pasos Exactos para Implementar Picasso

## ⚠️ IMPORTANTE: Entiende Esto Primero

**Picasso NO se implementa en el servicio web (backend)**  
**Picasso YA está implementado en tu proyecto Android (frontend)**

### ¿Qué hace cada parte?

```
┌─────────────────────────────────────────────────────────────────┐
│                    TU ARQUITECTURA                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  📱 APP ANDROID (Tu Proyecto Móvil)                             │
│  ├── Picasso YA está instalado ✅                               │
│  ├── Retrofit consume tu API                                   │
│  └── Picasso descarga y muestra imágenes desde URLs            │
│                                                                 │
│                           ↕️ (JSON con URLs)                    │
│                                                                 │
│  🌐 WEB SERVICE (Este Proyecto - Backend)                       │
│  ├── Devuelve JSONs con URLs de imágenes                       │
│  ├── NO almacena las imágenes (solo URLs)                      │
│  └── NO necesita Picasso (es una librería de Android)          │
│                                                                 │
│                           ↕️ (URLs apuntan a)                   │
│                                                                 │
│  ☁️ SERVIDOR DE IMÁGENES                                        │
│  ├── Unsplash (temporal/pruebas)                               │
│  ├── Firebase Storage (recomendado)                            │
│  ├── Cloudinary (alternativa)                                  │
│  └── Tu propio servidor (opcional)                             │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🔧 PARTE 1: Configurar el Web Service (Backend)

### Paso 1.1: Ejecutar Script SQL
1. Abre XAMPP
2. Inicia Apache y MySQL
3. Abre phpMyAdmin: http://localhost/phpmyadmin
4. Selecciona la base de datos `reservas_simple`
5. Ve a la pestaña "SQL"
6. Copia y pega TODO el contenido de `db/ADD_IMAGE_URLS.sql`
7. Haz clic en "Continuar"
8. ✅ Verifica que las columnas se agregaron:
   - `restaurantes` debe tener: `imagen_url` y `imagen_thumbnail_url`
   - `mesas` debe tener: `imagen_url`

### Paso 1.2: Reiniciar el Servicio Web
```bash
# Detén la aplicación si está corriendo (Ctrl+C)
# Luego ejecuta:
mvnw clean spring-boot:run
```

### Paso 1.3: Probar que Funciona
Abre Postman o Thunder Client y ejecuta:

```http
GET http://localhost:8080/api/restaurantes
```

**Deberías ver algo como:**
```json
[
  {
    "id": 1,
    "nombre": "La Pizzería Italiana",
    "imagenUrl": "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=800",
    "imagenThumbnailUrl": "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=200",
    ...
  }
]
```

Si ves `imagenUrl` y `imagenThumbnailUrl` en la respuesta: **✅ Backend listo!**

---

## 📱 PARTE 2: Actualizar tu App Android

### Paso 2.1: Verificar que Picasso está Instalado

En tu `build.gradle.kts` (del módulo app), busca:
```kotlin
implementation("com.squareup.picasso:picasso:2.8")
```

Si NO está, agrégalo y sincroniza el proyecto.

### Paso 2.2: Actualizar tus Modelos de Datos

#### Antes:
```kotlin
data class Restaurante(
    val id: Int,
    val nombre: String,
    val direccion: String,
    val telefono: String,
    val email: String,
    val horaApertura: String,
    val horaCierre: String
)
```

#### Después (AGREGA estos campos):
```kotlin
data class Restaurante(
    val id: Int,
    val nombre: String,
    val direccion: String,
    val telefono: String,
    val email: String,
    val imagenUrl: String?,           // ⬅️ NUEVO
    val imagenThumbnailUrl: String?,  // ⬅️ NUEVO
    val horaApertura: String,
    val horaCierre: String
)
```

#### Para Mesa:
```kotlin
data class Mesa(
    val id: Int,
    val restauranteId: Int,
    val numeroMesa: String,
    val capacidad: Int,
    val disponible: Boolean,
    val imagenUrl: String?  // ⬅️ NUEVO
)
```

### Paso 2.3: Usar Picasso en tu Adapter

Busca tu `RestauranteAdapter` (o como se llame) y actualiza:

#### Antes:
```kotlin
override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val restaurante = restaurantes[position]
    holder.tvNombre.text = restaurante.nombre
    holder.tvDireccion.text = restaurante.direccion
    // ... sin imagen
}
```

#### Después:
```kotlin
override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val restaurante = restaurantes[position]
    holder.tvNombre.text = restaurante.nombre
    holder.tvDireccion.text = restaurante.direccion
    
    // ⬇️ AGREGAR ESTO
    Picasso.get()
        .load(restaurante.imagenUrl)
        .placeholder(R.drawable.ic_placeholder)  // Imagen mientras carga
        .error(R.drawable.ic_error)              // Imagen si falla
        .into(holder.ivRestaurante)              // Tu ImageView
}
```

### Paso 2.4: Asegurarte de Tener Permisos

En `AndroidManifest.xml`, dentro de `<manifest>` (no dentro de `<application>`):
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

### Paso 2.5: Agregar ImageView a tu Layout

En tu `item_restaurante.xml` (o como se llame):
```xml
<ImageView
    android:id="@+id/ivRestaurante"
    android:layout_width="100dp"
    android:layout_height="100dp"
    android:scaleType="centerCrop"
    android:contentDescription="@string/restaurant_image" />
```

---

## ✅ Checklist de Implementación

### Backend (Web Service)
- [ ] ✅ Ejecuté el script `ADD_IMAGE_URLS.sql`
- [ ] ✅ Reinicié el servicio web
- [ ] ✅ Probé GET /api/restaurantes y veo las URLs

### Android (App Móvil)
- [ ] ✅ Verifiqué que Picasso está en `build.gradle.kts`
- [ ] ✅ Agregué `imagenUrl` a mis modelos de datos (Restaurante, Mesa)
- [ ] ✅ Agregué código de Picasso en mi Adapter
- [ ] ✅ Agregué `<uses-permission INTERNET />` en el Manifest
- [ ] ✅ Agregué `ImageView` a mis layouts
- [ ] ✅ Probé la app y las imágenes cargan correctamente

---

## 🐛 Solución de Problemas

### "No veo las URLs en la respuesta del API"
❌ No ejecutaste el script SQL o no reiniciaste el servicio web
✅ Ejecuta `ADD_IMAGE_URLS.sql` y reinicia con `mvnw spring-boot:run`

### "Las imágenes no cargan en mi app"
❌ No agregaste el permiso de INTERNET
✅ Agrega `<uses-permission android:name="android.permission.INTERNET" />` en el Manifest

### "Error: Unresolved reference: Picasso"
❌ Picasso no está instalado o no sincronizaste el proyecto
✅ Agrega `implementation("com.squareup.picasso:picasso:2.8")` y sincroniza

### "Las imágenes cargan pero se ven mal"
✅ Ajusta el `scaleType` del ImageView:
- `centerCrop` - Llena todo el espacio, puede cortar
- `centerInside` - Ajusta dentro sin cortar
- `fitXY` - Estira para llenar

---

## 🎨 Próximos Pasos (Opcional)

### 1. Cambiar las URLs de Ejemplo
Las URLs actuales son de Unsplash (solo para pruebas).

**Para producción, puedes usar:**
- **Firebase Storage** (recomendado) - Gratis hasta 5GB
- **Cloudinary** - Optimización automática de imágenes
- **Tu propio servidor** - Control total

### 2. Implementar Subida de Imágenes
Permite a los usuarios subir fotos de sus restaurantes.

### 3. Implementar Transformaciones
```kotlin
// Imagen circular
Picasso.get()
    .load(url)
    .transform(CircleTransform())
    .into(imageView)

// Imagen con bordes redondeados
Picasso.get()
    .load(url)
    .transform(RoundedCornersTransformation(30, 0))
    .into(imageView)
```

---

## 📚 Recursos

- **Documentación Picasso**: https://square.github.io/picasso/
- **Firebase Storage**: https://firebase.google.com/docs/storage
- **Unsplash API**: https://unsplash.com/developers

---

## ✨ ¡Resumen Final!

```
✅ Backend devuelve URLs en JSON
✅ Android tiene Picasso instalado
✅ Modelos de datos actualizados
✅ Picasso carga imágenes desde URLs
✅ ¡Todo funciona! 🎉
```

**Recuerda:** El backend NO necesita Picasso. Solo envía URLs. Picasso trabaja en el cliente (Android).
GitHub Copilot: Sí. Para que la app funcione debes exponer en tu backend los endpoints nuevos que usa el código: registro (POST /api/auth/register), recuperación (POST /api/auth/forgot-password), obtención de reservas por usuario con filtros (GET /api/reservas/usuario/{id}?estado=&desde=&hasta=), actualización de estados/cancelaciones si no existían, y reseñas (GET/POST /api/restaurantes/{id}/reviews). Además, devuelve en “login” un token o datos que permitan persistir la sesión (nombre, id, correo) para que el SessionManager pueda guardarlos.

GPT-5.1-Cod
