package com.finalproject.load_monitoring.utils.converters

import com.finalproject.load_monitoring.dto.StationDTO
import com.finalproject.load_monitoring.models.StationModel

class StationConverter {

    companion object {

        ////////////////////////////////////////////////////////////////////////////////////////////////
        // From DTO To Model
        fun fromDtoToModel(dto: StationDTO): StationModel {
            return StationModel(
                stationId = dto.stationId,
                stationName = dto.stationName
            )
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////
        // From Model To DTO
        fun fromModelToDto(model: StationModel): StationDTO {
            return StationDTO(
                stationId = model.stationId,
                stationName = model.stationName
            )
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////
        // From DTO List To Model List
        fun fromDtoListToModelList(dtoList: List<StationDTO>): List<StationModel> {
            return dtoList.map { fromDtoToModel(it) }
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////
        // From Model List To DTO List
        fun fromModelListToDtoList(modelList: List<StationModel>): List<StationDTO> {
            return modelList.map { fromModelToDto(it) }
        }
    }
}
