package com.finalproject.load_monitoring.models

data class TrainModel(
    val trainID: String, // Train ID
    val originStation: String, // Origin station
    val destinationStation: String, // Destination station
    var departureTime: String, // Departure time
    var arrivalTime: String, // Arrival time
    var lastUpdated: String, // Last updated time
    val carriageList: List<CarriageModel>, // Carriage list
    val originPlatform: Int, // Departure platform
    val destinationPlatform: Int // Destination platform
)



