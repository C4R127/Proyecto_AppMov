package com.example.primeravance.network

import com.example.primeravance.model.Mesa
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MesaApiService {
    @GET("api/mesas/restaurante/{restauranteId}")
    suspend fun getMesasByRestaurante(@Path("restauranteId") restauranteId: Int): Response<List<Mesa>>
    
    @GET("api/mesas/restaurante/{restauranteId}/disponibles")
    suspend fun getMesasDisponiblesPorHorario(
        @Path("restauranteId") restauranteId: Int,
        @Query("fecha") fecha: String,
        @Query("horaInicio") horaInicio: String,
        @Query("horaFin") horaFin: String
    ): Response<List<Mesa>>
}
