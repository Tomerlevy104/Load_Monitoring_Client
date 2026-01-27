package com.finalproject.load_monitoring.utils.converters

import com.finalproject.load_monitoring.dto.TrainDTO
import com.finalproject.load_monitoring.models.TrainModel

class TrainConverter {

    companion object {

        // From DTO To Model
        fun fromDtoToModel(
            dto: TrainDTO,
            originPlatformDefault: Int = 2,
            destinationPlatformDefault: Int = 1
        ): TrainModel {
            return TrainModel(
                trainID = dto.trainId.toString(),
                originStation = dto.originStation,
                destinationStation = dto.destinationStation,
                departureTime = dto.departureTime,
                arrivalTime = dto.arrivalTime,
                currentStation = dto.currentStation,
                lastUpdated = dto.lastUpdated,
                carriageList = dto.carriages.map { CarriageConverter.fromDtoToModel(it) },
                originPlatform = originPlatformDefault,
                destinationPlatform = destinationPlatformDefault
            )
        }

        // From Model To DTO
        fun fromModelToDto(model: TrainModel): TrainDTO {
            return TrainDTO(
                trainId = model.trainID.toLong(),
                originStation = model.originStation,
                destinationStation = model.destinationStation,
                departureTime = model.departureTime,
                arrivalTime = model.arrivalTime,
                currentStation = model.currentStation,
                lastUpdated = model.lastUpdated,
                carriages = model.carriageList.map { CarriageConverter.fromModelToDto(it) }
            )
        }
    }
}
