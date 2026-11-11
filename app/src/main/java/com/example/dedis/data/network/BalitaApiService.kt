package com.example.dedis.data.network

import com.example.dedis.data.model.ChildDto
import com.example.dedis.data.model.StoreMeasurementRequestDto
import com.example.dedis.data.model.StoreMeasurementResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface BalitaApiService {

    @GET("children")
    suspend fun getChildren(): Response<List<ChildDto>>

    @GET("children/{id}")
    suspend fun getChildDetail(@Path("id") id: Long): Response<ChildDto>

    @POST("children/{id}/measurements")
    suspend fun storeMeasurement(
        @Path("id") childId: Long,
        @Body request: StoreMeasurementRequestDto
    ): Response<StoreMeasurementResponseDto>
}

