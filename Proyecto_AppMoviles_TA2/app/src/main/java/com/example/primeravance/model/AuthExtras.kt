package com.example.primeravance.model

/**
 * Modelos auxiliares para registro y recuperación de contraseña.
 */
data class RegisterRequest(
    val nombre: String,
    val email: String,
    val username: String,
    val telefono: String,
    val password: String
)

data class RegisterResponse(
    val success: Boolean? = null,
    val message: String? = null,
    val usuario: Usuario? = null,
    val id: Int? = null,
    val username: String? = null,
    val nombre: String? = null,
    val email: String? = null,
    val telefono: String? = null,
    val rol: String? = null,
    val activo: Boolean? = null,
    val fechaCreacion: String? = null,
    val ultimoAcceso: String? = null
)

data class ForgotPasswordRequest(
    val email: String
)

data class ForgotPasswordResponse(
    val success: Boolean,
    val message: String
)

data class ReviewRequest(
    val restauranteId: Int,
    val usuarioId: Int,
    val comentario: String,
    val rating: Float
)

data class Review(
    val id: Int,
    val restauranteId: Int,
    val usuarioId: Int,
    val comentario: String,
    val rating: Float,
    val fecha: String,
    val usuarioNombre: String
)
