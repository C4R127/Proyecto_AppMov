package com.example.primeravance.model

data class ReservaRequest(
    val mesaId: Int,
    val nombreCliente: String,
    val telefonoCliente: String,
    val emailCliente: String,
    val fechaReserva: String,       // Formato: "yyyy-MM-dd"
    val horaInicio: String,          // Formato: "HH:mm:ss"
    val horaFin: String,             // Formato: "HH:mm:ss"
    val numeroPersonas: Int,
    val precio: Double? = 0.0,
    val observaciones: String? = null,
    val usuarioId: Int? = null
)
