package com.finalproject.load_monitoring.models

import java.util.Date

data class CarriageModel(
    val trainID: String, // Train ID
    val carriageNumber: Int, //  Carriage number
    var occupancy: OccupancyLevel, // Occupancy level
    var lastDataUpdate: Date // Last data update
)