package com.finalproject.load_monitoring.models

import java.util.Date

data class CarriageModel(
    val carriageID: String, // Carriage Id
    val trainID: String, // Train ID
    val carriageNumber: Int, //  Carriage number
    var occupancy: Int, // Current capacity number
    var maxCapacity: Int, // Maximum capacity
    var lastDataUpdate: String, // Last data update

    var occupancyStatus: OccupancyLevel, // Occupancy level
)
