package com.finalproject.load_monitoring.dto

import java.time.LocalDateTime

data class OccupancyLogDTO(
    val logId: Long,
    val carriageId: Long,
    val cameraCount: Int,
    val irCount: Int,
    val calculatedOccupancy: Int,
    val timestamp: String,
)