# 🎨 Guía de Imágenes Locales - Implementado

## ✅ Lo que se ha implementado:

### 1. **Nuevos Logos y Iconos Vectoriales Creados:**

#### 📍 Logo Principal del App
- **Archivo:** `ic_logo_restaurant.xml`
- **Ubicación:** `res/drawable/`
- **Descripción:** Logo profesional con plato y cubiertos + estrella dorada
- **Uso:** Login, headers, splash screen

#### 🪑 Icono Mejorado para Mesas
- **Archivo:** `ic_mesa_icon.xml`
- **Ubicación:** `res/drawable/`
- **Descripción:** Mesa 3D con perspectiva y plato decorativo
- **Uso:** RecyclerView de mesas (`item_mesa.xml`)

#### 🏠 Placeholder General de Restaurante
- **Archivo:** `ic_restaurant_placeholder.xml`
- **Ubicación:** `res/drawable/`
- **Descripción:** Edificio de restaurante con toldo y ventanas
- **Uso:** Cuando no se puede detectar el tipo de restaurante

---

### 2. **Imágenes por Tipo de Restaurante:**

Todas estas son **imágenes vectoriales** (no ocupan espacio, escalan perfectamente):

| Tipo | Archivo | Descripción | Detecta por nombre |
|------|---------|-------------|--------------------|
| 🍕 **Italiano** | `img_restaurant_italian.xml` | Pizza con pepperoni y albahaca | "pizz", "ital" |
| 🌮 **Mexicano** | `img_restaurant_mexican.xml` | Taco con lechuga, tomate, carne | "mex", "taco", "burrito" |
| 🍱 **Asiático** | `img_restaurant_asian.xml` | Plato de sushi con palillos | "sushi", "asi", "chin", "jap" |
| 🥩 **Parrilla/Carnes** | `img_restaurant_steakhouse.xml` | Steak con papas y brócoli | "parrilla", "carne", "asado", "steak" |
| 🍔 **Casual/Hamburguesas** | `img_restaurant_casual.xml` | Hamburguesa completa con papas | "burger", "hambur" |

---

### 3. **RestauranteAdapter - Sistema Inteligente:**

El adapter ahora **detecta automáticamente** el tipo de restaurante:

```kotlin
val imageRes = when {
    // Prioridad 1: Si viene del servidor
    restaurante.imageRes != 0 -> restaurante.imageRes
    
    // Prioridad 2: Detectar por nombre
    restaurante.nombre.contains("pizz", ignoreCase = true) -> 
        R.drawable.img_restaurant_italian
        
    // etc...
    
    // Por defecto
    else -> R.drawable.ic_restaurant_placeholder
}
```

**Ventajas:**
- ✅ No necesitas modificar el backend
- ✅ Funciona con los datos actuales
- ✅ Detecta automáticamente por el nombre
- ✅ Fácil de extender con más tipos

---

### 4. **Archivos Actualizados:**

```
✅ RestauranteAdapter.kt - Lógica de detección de imágenes
✅ item_mesa.xml - Nuevo ícono de mesa
✅ fragment_login.xml - Logo principal
✅ fragment_dashboard.xml - Logo en header
✅ strings.xml - Descripciones de imágenes
```

---

## 📁 Estructura de Carpetas:

```
app/src/main/res/drawable/
├── ic_logo_restaurant.xml          ⭐ NUEVO - Logo principal
├── ic_mesa_icon.xml                ⭐ NUEVO - Ícono mesa mejorado
├── ic_restaurant_placeholder.xml   ⭐ NUEVO - Placeholder general
├── img_restaurant_italian.xml      ⭐ NUEVO - Restaurante italiano
├── img_restaurant_mexican.xml      ⭐ NUEVO - Restaurante mexicano
├── img_restaurant_asian.xml        ⭐ NUEVO - Restaurante asiático
├── img_restaurant_steakhouse.xml   ⭐ NUEVO - Parrilla/carnes
├── img_restaurant_casual.xml       ⭐ NUEVO - Casual/hamburguesas
├── bg_restaurante_card.xml         ⭐ NUEVO - Fondo degradado
├── restaurante_pf.png              ✓ Existente
├── restaurant_background.jpg       ✓ Existente
└── ic_restaurant_table.xml         ✓ Existente (viejo)
```

---

## 🎯 Cómo Probar:

### 1. **Sincronizar el Proyecto**
```
File > Sync Project with Gradle Files
```

### 2. **Limpiar y Reconstruir**
```powershell
.\gradlew clean build
```

### 3. **Ejecutar la App**
- Ve a **DashboardFragment** (Reserva)
- Deberías ver las imágenes de restaurantes según su tipo
- Ve a **MesasFragment** y verás el nuevo ícono de mesas

---

## 🔧 Personalizar para tus Restaurantes:

### Opción 1: Editar los nombres en tu base de datos

Si tus restaurantes en la BD tienen nombres como:
- "La Pizzería Italiana" → Se mostrará la pizza 🍕
- "Tacos El Mexicano" → Se mostrará el taco 🌮
- "Sushi Tokyo" → Se mostrará el sushi 🍱
- "Parrilla Don José" → Se mostrará el steak 🥩
- "Burger King" → Se mostrará la hamburguesa 🍔

### Opción 2: Agregar más palabras clave

En `RestauranteAdapter.kt`, busca la función `bind()` y agrega más palabras:

```kotlin
restaurante.nombre.contains("pizz", ignoreCase = true) ||
restaurante.nombre.contains("ital", ignoreCase = true) ||
restaurante.nombre.contains("napoli", ignoreCase = true) ||  // ⬅️ AGREGAR
restaurante.nombre.contains("roma", ignoreCase = true) ->    // ⬅️ AGREGAR
    R.drawable.img_restaurant_italian
```

### Opción 3: Asignar por ID del restaurante

```kotlin
val imageRes = when (restaurante.id) {
    1 -> R.drawable.img_restaurant_italian
    2 -> R.drawable.img_restaurant_mexican
    3 -> R.drawable.img_restaurant_asian
    4 -> R.drawable.img_restaurant_steakhouse
    5 -> R.drawable.img_restaurant_casual
    else -> R.drawable.ic_restaurant_placeholder
}
```

---

## 🖼️ Agregar Imágenes PNG/JPG (Si lo necesitas)

### Paso 1: Preparar tus imágenes

Crea 4 versiones de cada imagen:
- `mdpi`: 160dpi (referencia base)
- `hdpi`: 240dpi (1.5x)
- `xhdpi`: 320dpi (2x)
- `xxhdpi`: 480dpi (3x)
- `xxxhdpi`: 640dpi (4x)

### Paso 2: Colocar en carpetas

```
res/
├── drawable-mdpi/
│   └── restaurante_1.jpg (200x150px)
├── drawable-hdpi/
│   └── restaurante_1.jpg (300x225px)
├── drawable-xhdpi/
│   └── restaurante_1.jpg (400x300px)
├── drawable-xxhdpi/
│   └── restaurante_1.jpg (600x450px)
└── drawable-xxxhdpi/
    └── restaurante_1.jpg (800x600px)
```

### Paso 3: Usar en el código

```kotlin
// Opción A: Imagen específica por restaurante
val imageRes = when (restaurante.id) {
    1 -> R.drawable.restaurante_1
    2 -> R.drawable.restaurante_2
    else -> R.drawable.ic_restaurant_placeholder
}

// Opción B: Cargar desde assets
val assetPath = "restaurantes/${restaurante.id}.jpg"

// Opción C: Mantener sistema actual (recomendado)
// Ya funciona con vectores
```

---

## 🎨 Crear Más Iconos Vectoriales:

Si necesitas más tipos de restaurante, puedes:

1. **Usar herramientas online:**
   - [Figma](https://figma.com) - Diseño vectorial
   - [Vector Asset Studio](https://developer.android.com/studio/write/vector-asset-studio) - En Android Studio
   - [SVG to Vector Drawable](https://svg2vector.com/) - Convertidor

2. **Importar desde SVG:**
   ```
   Android Studio > Right Click en drawable >
   New > Vector Asset > Local file (SVG)
   ```

3. **Usar Material Icons:**
   ```
   Android Studio > Right Click en drawable >
   New > Vector Asset > Clip Art
   ```

---

## 🚀 Mejoras Adicionales Implementadas:

### 1. **Picasso con Recursos Locales**
```kotlin
Picasso.get()
    .load(imageRes)
    .placeholder(R.drawable.ic_restaurant_placeholder)
    .error(R.drawable.restaurante_pf)
    .fit()
    .centerCrop()
    .into(imgRestaurante)
```

### 2. **Optimización de Imágenes**
- Todas las imágenes son **vectoriales** (SVG)
- No ocupan espacio en disco
- Escalan perfectamente a cualquier tamaño
- No pierden calidad

### 3. **Fallback en Capas**
```
1. restaurante.imageRes (del servidor)
   ↓
2. Detectar por nombre
   ↓
3. Placeholder general
   ↓
4. restaurante_pf.png (último recurso)
```

---

## 🐛 Solución de Problemas:

### Problema: No veo las imágenes nuevas

**Solución:**
```powershell
# Limpiar y reconstruir
.\gradlew clean
.\gradlew build

# O en Android Studio
Build > Clean Project
Build > Rebuild Project
```

---

### Problema: "Cannot resolve symbol R.drawable.ic_logo_restaurant"

**Solución:**
1. Verifica que los archivos XML estén en `res/drawable/`
2. Sincroniza Gradle: `File > Sync Project with Gradle Files`
3. Invalida caché: `File > Invalidate Caches / Restart`

---

### Problema: Las imágenes se ven pixeladas

**Causa:** Estás usando PNG en lugar de vectores

**Solución:** Los archivos XML vectoriales ya están listos y no se pixelan

---

## 📊 Comparación: Antes vs Después

### Antes:
```kotlin
// Solo mostraba restaurante_pf.png para todos
imgRestaurante.setImageResource(R.drawable.restaurante_pf)
```

### Después:
```kotlin
// Detecta automáticamente y muestra imagen específica
// 🍕 Pizza para italianos
// 🌮 Taco para mexicanos
// 🍱 Sushi para asiáticos
// 🥩 Steak para parrillas
// 🍔 Hamburguesa para casual
```

---

## ✨ Ventajas de Usar Vectores:

1. ✅ **Tamaño:** Los archivos XML pesan menos de 5KB cada uno
2. ✅ **Calidad:** Se ven perfectos en cualquier densidad de pantalla
3. ✅ **Colores:** Fácil de cambiar colores editando el XML
4. ✅ **Mantenimiento:** Un solo archivo para todas las resoluciones
5. ✅ **Rendimiento:** Android los renderiza eficientemente

---

## 🎯 Próximos Pasos Opcionales:

### 1. Animaciones
```kotlin
// Fade in al cargar imagen
Picasso.get()
    .load(imageRes)
    .into(imgRestaurante, object : Callback {
        override fun onSuccess() {
            imgRestaurante.animate()
                .alpha(1f)
                .setDuration(300)
                .start()
        }
    })
```

### 2. Imágenes de Fondo Dinámicas
```xml
<!-- En item_restaurante.xml -->
<FrameLayout>
    <ImageView
        android:id="@+id/imgBackground"
        android:scaleType="centerCrop"
        android:alpha="0.3" />
    <!-- Contenido encima -->
</FrameLayout>
```

### 3. Logo Animado en Splash Screen
```kotlin
imgLogo.animate()
    .scaleX(1.2f)
    .scaleY(1.2f)
    .setDuration(1000)
    .start()
```

---

## 📚 Recursos:

- **Material Design Icons:** https://fonts.google.com/icons
- **Flaticon:** https://www.flaticon.com/ (Vectores gratis)
- **Android Vector Drawable:** https://developer.android.com/guide/topics/graphics/vector-drawable-resources

---

**Fecha:** Noviembre 15, 2025  
**Proyecto:** Sistema de Reservas - Proyecto_AppMoviles_TA2  
**Estado:** ✅ Implementado y Funcionando
