package com.finalproject.load_monitoring.dto

data class CarriageDTO(
    val carriageId: Long,
    val trainId: Long,
    val carriageNumber: Int,
    val occupancy: Int,
    val maxCapacity: Int,
    val lastUpdated: String
)
