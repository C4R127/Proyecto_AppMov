package com.example.primeravance.network

import com.example.primeravance.model.Restaurante
import retrofit2.Response
import retrofit2.http.GET

interface RestauranteApiService {
    @GET("api/restaurantes")
    suspend fun getRestaurantes(): Response<List<Restaurante>>
}
