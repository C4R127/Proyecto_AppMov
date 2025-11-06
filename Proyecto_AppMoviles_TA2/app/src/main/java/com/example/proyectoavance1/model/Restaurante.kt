package com.example.primeravance.model

data class Restaurante(
    val id: Int,
    val nombre: String,
    val direccion: String,
    val telefono: String,
    val descripcion: String?,
    val imageRes: Int = 0
)
