package com.finalproject.load_monitoring.network

import com.finalproject.load_monitoring.dto.StationDTO
import com.finalproject.load_monitoring.dto.TrainDTO
import retrofit2.http.GET
import retrofit2.http.Path

interface PassengerApi {

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // get all trains
    // GET /api/passengers
    @GET("api/passengers")
    suspend fun getAllTrains(): List<TrainDTO>

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Get train bu id
    // GET /api/passengers/{id}
    @GET("api/passengers/{id}")
    suspend fun getTrainById(@Path("id") id: Long): TrainDTO

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Search train bu origin and destination
    // GET /api/passengers/search/{origin}/{destination}
    @GET("api/passengers/search/{origin}/{destination}")
    suspend fun searchTrains(
        @Path("origin") origin: String,
        @Path("destination") destination: String
    ): List<TrainDTO>

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Get all stations
    @GET("api/stations")
    suspend fun getAllStations(): List<StationDTO>
}
