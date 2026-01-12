package com.finalproject.load_monitoring.repositories

import com.finalproject.load_monitoring.models.TrainModel

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

    // Get all trains
    suspend fun getAllTrains(): List<TrainModel>


}
