package com.finalproject.load_monitoring.models


data class OccupancyLogModel(
    val logId: Long,
    val carriageId: Long,
    val cameraCount: Int,
    val irCount: Int,
    val calculatedOccupancy: Int,
    val timestamp: String,
)
