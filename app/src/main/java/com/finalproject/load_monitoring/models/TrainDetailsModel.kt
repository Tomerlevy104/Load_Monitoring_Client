package com.finalproject.load_monitoring.models

import java.util.Date

data class TrainDetailsModel(
    val trainID: String, // Train ID
    val carriageList: List<CarriageModel>, // Carriage list
    val originStation: String, // Origin station
    val destinationStation: String, // Destination station
    var departureTime: Date, // Departure time
    var arrivalTime: Date, // Arrival time
    val departurePlatform: Int // Departure platform

)



