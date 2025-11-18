package com.example.primeravance.network

import com.example.primeravance.model.Reserva
import com.example.primeravance.model.ReservaRequest
import com.example.primeravance.model.ReservaResponse
import retrofit2.Response
import retrofit2.http.*

interface ReservaApiService {
    // Crear nueva reserva - El backend devuelve el objeto Reserva directamente con código 201
    @POST("api/reservas")
    suspend fun crearReserva(@Body request: ReservaRequest): Response<Reserva>
    
    // Modificar reserva existente - El backend devuelve el objeto Reserva actualizado
    @PUT("api/reservas/{id}")
    suspend fun modificarReserva(
        @Path("id") reservaId: Int,
        @Body request: ReservaRequest
    ): Response<Reserva>
    
    // Eliminar reserva - El backend devuelve texto plano
    @DELETE("api/reservas/{id}")
    suspend fun eliminarReserva(@Path("id") reservaId: Int): Response<String>
    
    // Obtener reserva por ID - El backend devuelve el objeto Reserva
    @GET("api/reservas/{id}")
    suspend fun obtenerReserva(@Path("id") reservaId: Int): Response<Reserva>
    
    // Obtener todas las reservas
    @GET("api/reservas")
    suspend fun obtenerTodasReservas(): Response<List<Reserva>>
    
    @GET("api/reservas/usuario/{usuarioId}")
    suspend fun obtenerReservasPorUsuario(@Path("usuarioId") usuarioId: Int): Response<List<Reserva>>

    // Obtener reservas por mesa
    @GET("api/reservas/mesa/{mesaId}")
    suspend fun obtenerReservasPorMesa(@Path("mesaId") mesaId: Int): Response<List<Reserva>>
    
    // Obtener reservas por estado
    @GET("api/reservas/estado/{estado}")
    suspend fun obtenerReservasPorEstado(@Path("estado") estado: String): Response<List<Reserva>>
    
    // Cambiar estado de reserva - El backend devuelve texto plano
    @PATCH("api/reservas/{id}/estado")
    suspend fun cambiarEstadoReserva(
        @Path("id") reservaId: Int,
        @Query("estado") estado: String
    ): Response<String>
    
    // Cancelar reserva - El backend devuelve texto plano "Reserva cancelada exitosamente"
    @PATCH("api/reservas/{id}/cancelar")
    suspend fun cancelarReserva(@Path("id") reservaId: Int): Response<String>
}
