package com.example.primeravance.model

data class Mesa(
    val id: Int,
    val numero: Int,
    val numeroMesa: String? = null,  // Por si el backend usa este campo
    val capacidad: Int,
    val estado: String? = null,
    val disponible: Boolean = true,  // Estado general de la mesa
    val restauranteId: Int,
    val ocupada: Boolean = false  // Si está ocupada en el horario consultado
)
