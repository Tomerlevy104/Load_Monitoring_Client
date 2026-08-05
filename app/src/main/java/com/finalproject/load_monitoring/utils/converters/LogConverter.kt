package com.finalproject.load_monitoring.utils.converters

import com.finalproject.load_monitoring.dto.OccupancyLogDTO
import com.finalproject.load_monitoring.models.OccupancyLogModel

class LogConverter {
    companion object {
        fun fromDTOToModel(dto: OccupancyLogDTO): OccupancyLogModel {
            return OccupancyLogModel(
                logId = dto.logId,
                carriageId = dto.carriageId,
                cameraCount = dto.cameraCount,
                irCount = dto.irCount,
                calculatedOccupancy = dto.calculatedOccupancy,
                calculatedUncertainty = dto.calculatedUncertainty,
                cameraStatus = dto.cameraStatus,
                irStatus = dto.irStatus,
                timestamp = dto.timestamp
            )
        }

        fun fromModelToDTO(
            model: OccupancyLogModel
        ): OccupancyLogDTO {
            return OccupancyLogDTO(
                logId = model.logId,
                carriageId = model.carriageId,
                cameraCount = model.cameraCount,
                irCount = model.irCount,
                calculatedOccupancy = model.calculatedOccupancy,
                calculatedUncertainty = model.calculatedUncertainty,
                cameraStatus = model.cameraStatus,
                irStatus = model.irStatus,
                timestamp = model.timestamp
            )
        }
    }
}