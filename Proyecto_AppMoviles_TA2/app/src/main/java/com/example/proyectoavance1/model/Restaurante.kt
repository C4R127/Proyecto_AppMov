package com.example.primeravance.model

data class Restaurante(
    val id: Int,
    val nombre: String,
    val direccion: String,
    val telefono: String,
    val descripcion: String?,
    val imageRes: Int = 0,
    val imagenUrl: String? = null,
    val miniaturaUrl: String? = null,
    val ratingPromedio: Double? = null,
    val totalResenas: Int? = null
)
