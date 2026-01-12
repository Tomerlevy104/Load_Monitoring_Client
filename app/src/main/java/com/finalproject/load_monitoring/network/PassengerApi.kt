package com.finalproject.load_monitoring.network

import com.finalproject.load_monitoring.dto.TrainDTO
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PassengerApi {
    @GET("api/passengers")
    suspend fun getAllTrains(): List<TrainDTO>

    @GET("api/passengers/{id}")
    suspend fun getTrainById(@Path("id") id: Long): TrainDTO

    // GET /api/passengers/search?origin=...&destination=...
    @GET("api/passengers/search")
    suspend fun searchTrains(
        @Query("origin") origin: String,
        @Query("destination") destination: String
    ): List<TrainDTO>
}
