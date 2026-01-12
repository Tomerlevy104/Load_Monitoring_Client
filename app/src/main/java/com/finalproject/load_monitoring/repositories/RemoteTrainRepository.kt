package com.finalproject.load_monitoring.repositories

import com.finalproject.load_monitoring.models.TrainModel
import com.finalproject.load_monitoring.network.ApiClient
import com.finalproject.load_monitoring.utils.converters.TrainConverter

class RemoteTrainRepository : TrainRepository {

    private val api = ApiClient.passengerApi

    // Get train by Id
    override suspend fun getTrainDetailsByTrainID(trainID: String): TrainModel {
        val dto = api.getTrainById(trainID.toLong())
        return TrainConverter.fromDtoToModel(dto)
    }

    // Get train by origin and destination
    override suspend fun searchTrainsByOriginAndDest(
        origin: String,
        destination: String
    ): List<TrainModel> {

        // להוריד את זה מהערה כשיהיה endpoint
        // val trainDtoList = api.searchTrains(origin = origin, destination = destination)
        // return trainDtoList.map { TrainConverter.fromDtoToModel(it) }

        //למחוק את זה בהמשך
        return getAllTrains()
            .filter { it.originStation == origin && it.destinationStation == destination }
            .sortedBy { it.departureTime }
    }

    // Get all trains
    override suspend fun getAllTrains(): List<TrainModel> {
        return api.getAllTrains().map { TrainConverter.fromDtoToModel(it) }
    }
}
