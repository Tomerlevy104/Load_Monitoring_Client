package com.finalproject.load_monitoring.dto

data class TrainDTO(
    val trainId: Long,
    val originStation: String,
    val destinationStation: String,
    val departureTime: String,
    val arrivalTime: String,
    val lastUpdated: String,
    val carriages: List<CarriageDTO>
)
