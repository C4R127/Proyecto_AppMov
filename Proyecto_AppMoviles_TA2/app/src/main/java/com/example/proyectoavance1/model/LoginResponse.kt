package com.example.primeravance.model

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val usuario: Usuario?
)
