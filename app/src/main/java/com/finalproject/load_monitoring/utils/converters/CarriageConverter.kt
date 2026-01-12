package com.finalproject.load_monitoring.utils.converters

import com.finalproject.load_monitoring.dto.CarriageDTO
import com.finalproject.load_monitoring.models.CarriageModel
import com.finalproject.load_monitoring.models.OccupancyLevel

class CarriageConverter {

    companion object {

        // From DTO To Model
        fun fromDtoToModel(dto: CarriageDTO): CarriageModel {
            val occupancyLevel = calculateOccupancyLevel(
                current = dto.occupancy,
                max = dto.maxCapacity
            )

            return CarriageModel(
                carriageID = dto.carriageId.toString(),
                trainID = dto.trainId.toString(),
                carriageNumber = dto.carriageNumber,
                occupancy = dto.occupancy,
                maxCapacity = dto.maxCapacity,
                lastDataUpdate = dto.lastUpdated,
                occupancyStatus = occupancyLevel
            )
        }

        // From Model To DTO
        fun fromModelToDto(model: CarriageModel): CarriageDTO {
            return CarriageDTO(
                carriageId = model.carriageID.toLong(),
                trainId = model.trainID.toLong(),
                carriageNumber = model.carriageNumber,
                occupancy = model.occupancy,
                maxCapacity = model.maxCapacity,
                lastUpdated = model.lastDataUpdate
            )
        }

        // Calculate occupancy level based on current and max capacity
        private fun calculateOccupancyLevel(
            current: Int,
            max: Int
        ): OccupancyLevel {
            if (max <= 0) return OccupancyLevel.UNKNOWN

            val ratio = current.toDouble() / max.toDouble()

            return when {
                ratio < 0.4 -> OccupancyLevel.LOW
                ratio < 0.7 -> OccupancyLevel.MEDIUM
                else -> OccupancyLevel.HIGH
            }
        }
    }
}
