package com.finalproject.load_monitoring.dto

data class OccupancyLogDTO(
    val logId: Long,
    val carriageId: Long,
    val cameraCount: Int,
    val irCount: Int,
    val calculatedOccupancy: Int,
    val calculatedUncertainty: Double?,
    val cameraStatus: String?, // "ok" / "unavailable"
    val irStatus: String?,     // "ok" / "unavailable"
    val timestamp: String,
)