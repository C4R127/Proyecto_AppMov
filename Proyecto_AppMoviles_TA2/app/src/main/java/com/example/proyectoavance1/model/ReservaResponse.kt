package com.example.primeravance.model

data class ReservaResponse(
    val success: Boolean,
    val message: String,
    val reserva: Reserva?
)

data class Reserva(
    val id: Int,
    val mesaId: Int,
    val nombreCliente: String,
    val telefonoCliente: String,
    val emailCliente: String,
    val fechaReserva: String,
    val horaInicio: String,
    val horaFin: String,
    val numeroPersonas: Int,
    val estado: String,
    val precio: Double? = null,
    val observaciones: String? = null,
    // Campos opcionales que pueden venir del backend
    val usuarioId: Int? = null,
    val restauranteNombre: String? = null,
    val mesaNumero: String? = null
)
