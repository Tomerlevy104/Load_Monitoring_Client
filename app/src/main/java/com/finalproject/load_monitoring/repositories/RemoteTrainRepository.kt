package com.finalproject.load_monitoring.repositories

import com.finalproject.load_monitoring.models.StationModel
import com.finalproject.load_monitoring.models.TrainModel
import com.finalproject.load_monitoring.network.ApiClient
import com.finalproject.load_monitoring.utils.converters.StationConverter
import com.finalproject.load_monitoring.utils.converters.TrainConverter
import java.time.LocalDateTime

class RemoteTrainRepository : TrainRepository {

    private val api = ApiClient.passengerApi

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Get train by Id
    override suspend fun getTrainDetailsByTrainID(trainID: String): TrainModel {
        val dto = api.getTrainById(trainID.toLong())
        return TrainConverter.fromDtoToModel(dto)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Get train by origin and destination
    override suspend fun searchTrainsByOriginAndDest(
        origin: String,
        destination: String
    ): List<TrainModel> {

        val trainDtoList = api.searchTrains(origin = origin, destination = destination)
        return trainDtoList.map { TrainConverter.fromDtoToModel(it) }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Get train by origin and destination and date
    override suspend fun searchTrainsByOriginDestAndDate(
        origin: String,
        destination: String,
        date: LocalDateTime): List<TrainModel> {
        val trainDtoList = api.searchTrains(origin = origin, destination = destination, date = date.toString())
        return trainDtoList.map { TrainConverter.fromDtoToModel(it) }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Get all trains
    override suspend fun getAllTrains(): List<TrainModel> {
        return api.getAllTrains().map { TrainConverter.fromDtoToModel(it) }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Get all stations
    override suspend fun getAllStations(): List<StationModel> {
        return api.getAllStations().map { StationConverter.fromDtoToModel(it) }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
}
