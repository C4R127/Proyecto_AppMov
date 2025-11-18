package com.example.primeravance.model

data class Usuario(
    val id: Int,
    val nombre: String,
    val email: String,
    val rol: String,
    val telefono: String? = null
)
