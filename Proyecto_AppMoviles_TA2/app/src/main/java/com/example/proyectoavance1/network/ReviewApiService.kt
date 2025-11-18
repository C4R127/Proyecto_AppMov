package com.example.primeravance.network

import com.example.primeravance.model.Review
import com.example.primeravance.model.ReviewRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ReviewApiService {
    @GET("api/restaurantes/{restauranteId}/reviews")
    suspend fun getReviews(@Path("restauranteId") restauranteId: Int): Response<List<Review>>

    @POST("api/restaurantes/{restauranteId}/reviews")
    suspend fun submitReview(
        @Path("restauranteId") restauranteId: Int,
        @Body request: ReviewRequest
    ): Response<Review>
}
