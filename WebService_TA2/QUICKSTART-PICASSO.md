# 🚀 Quick Start - Implementación Picasso

## ✅ Cambios Realizados en el Backend

### Archivos Modificados:
1. ✅ `db/ADD_IMAGE_URLS.sql` - Script SQL para agregar columnas de imágenes
2. ✅ `entity/Restaurante.java` - Agregados campos `imagenUrl` y `imagenThumbnailUrl`
3. ✅ `entity/Mesa.java` - Agregado campo `imagenUrl`
4. ✅ `dto/RestauranteDTO.java` - Incluye URLs de imágenes
5. ✅ `dto/MesaDTO.java` - Incluye URL de imagen
6. ✅ `service/RestauranteService.java` - Mapeo de URLs actualizado
7. ✅ `service/MesaService.java` - Mapeo de URLs actualizado
8. ✅ `API-ENDPOINTS.http` - Ejemplos actualizados con imágenes

---

## 📝 Pasos Rápidos

### 1. Ejecutar Migración SQL
```bash
# En MySQL Workbench o línea de comandos:
mysql -u root -p < db/ADD_IMAGE_URLS.sql
```

### 2. Reiniciar Servicio Web
```bash
mvnw clean spring-boot:run
```

### 3. Probar Endpoint
```bash
GET http://localhost:8080/api/restaurantes
```

**Respuesta esperada:**
```json
{
  "id": 1,
  "nombre": "La Pizzería Italiana",
  "imagenUrl": "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=800",
  "imagenThumbnailUrl": "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=200",
  ...
}
```

---

## 📱 Uso en Android

### Modelo de Datos (Actualizar):
```kotlin
data class Restaurante(
    val id: Int,
    val nombre: String,
    val direccion: String,
    val telefono: String,
    val email: String,
    val imagenUrl: String?,           // NUEVO
    val imagenThumbnailUrl: String?,  // NUEVO
    val horaApertura: String,
    val horaCierre: String
)

data class Mesa(
    val id: Int,
    val restauranteId: Int,
    val numeroMesa: String,
    val capacidad: Int,
    val disponible: Boolean,
    val imagenUrl: String?  // NUEVO
)
```

### Cargar Imagen con Picasso:
```kotlin
// En tu Adapter
Picasso.get()
    .load(restaurante.imagenUrl)
    .placeholder(R.drawable.ic_placeholder)
    .error(R.drawable.ic_error)
    .into(imageView)
```

---

## 🎨 URLs de Ejemplo Incluidas

Las siguientes URLs de Unsplash están pre-configuradas:
- **Restaurante 1**: Pizza/Café - `photo-1517248135467-4c7edcad34c4`
- **Restaurante 2**: Restaurant interior - `photo-1414235077428-338989a2e8c0`
- **Restaurante 3**: Sushi - `photo-1579584425555-c3ce17fd4351`
- **Mesas**: Diferentes tipos según restaurante

⚠️ **Nota**: Unsplash es solo para pruebas. Para producción, usa Firebase Storage, Cloudinary o tu propio servidor.

---

## 📚 Documentación Completa

Lee `GUIA-IMPLEMENTACION-PICASSO.md` para:
- Configuración avanzada de Picasso
- Opciones de hosting de imágenes
- Transformaciones y optimizaciones
- Solución de problemas
- Ejemplos completos de código Android

---

## ✅ Checklist

- [ ] Ejecutar script SQL
- [ ] Reiniciar servicio web
- [ ] Probar endpoint /api/restaurantes
- [ ] Actualizar modelos en Android
- [ ] Implementar Picasso en adapters
- [ ] Probar app completa

**¡Listo para usar!** 🎉
