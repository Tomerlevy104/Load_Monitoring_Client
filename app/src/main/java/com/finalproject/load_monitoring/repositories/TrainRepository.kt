package com.finalproject.load_monitoring.repositories

import com.finalproject.load_monitoring.models.TrainDetailsModel

interface TrainRepository {

    /**
     * Returns full details about a specific train.
     */
    suspend fun getTrainDetailsByTrainID(trainID: String): TrainDetailsModel

    /**
     * (TODO) Search trains by origin & destination
     */
    suspend fun searchTrainsByOriginAndDest(
        origin: String,
        destination: String
    ): List<TrainDetailsModel>
}
