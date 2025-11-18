package com.example.primeravance.network

import com.example.primeravance.model.ForgotPasswordRequest
import com.example.primeravance.model.ForgotPasswordResponse
import com.example.primeravance.model.LoginRequest
import com.example.primeravance.model.LoginResponse
import com.example.primeravance.model.RegisterRequest
import com.example.primeravance.model.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<ForgotPasswordResponse>
}
