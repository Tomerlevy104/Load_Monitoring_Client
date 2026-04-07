package com.finalproject.load_monitoring.repositories

import com.finalproject.load_monitoring.models.StationModel
import com.finalproject.load_monitoring.models.TrainModel
import java.time.LocalDateTime

interface TrainRepository {

    /**
     * Returns full details about a specific train.
     */
    suspend fun getTrainDetailsByTrainID(trainID: String): TrainModel

    // Search trains by origin & destination
    suspend fun searchTrainsByOriginAndDest(
        origin: String,
        destination: String
    ): List<TrainModel>

    // Search trains by origin & destination & date
    suspend fun searchTrainsByOriginDestAndDate(
        origin: String,
        destination: String,
        date: LocalDateTime): List<TrainModel>

    // Get all trains
    suspend fun getAllTrains(): List<TrainModel>

    // Get all stations
    suspend fun getAllStations(): List<StationModel>
    
}
