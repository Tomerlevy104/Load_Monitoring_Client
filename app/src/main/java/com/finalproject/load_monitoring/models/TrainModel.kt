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
) {
    val overallOccupancyLevel: OccupancyLevel
        get() {
            // אם אין קרונות ברשימה, אי אפשר לחשב
            if (carriageList.isEmpty()) return OccupancyLevel.UNKNOWN

            // סוכמים את כל הנוסעים בכל הקרונות
            val totalOccupancy = carriageList.sumOf { it.occupancy }
            // סוכמים את הקיבולת המקסימלית של כל הקרונות
            val totalCapacity = carriageList.sumOf { it.maxCapacity }

            // מניעת קריסה של חלוקה באפס (למקרה של נתונים שגויים בשרת)
            if (totalCapacity == 0) return OccupancyLevel.UNKNOWN

            // חישוב אחוז העומס הכולל
            val percentage = (totalOccupancy.toFloat() / totalCapacity.toFloat()) * 100

            // החזרת רמת העומס לפי האחוז (ניתן לשנות את המספרים לפי הצורך)
            return when {
                percentage <= 40f -> OccupancyLevel.LOW
                percentage <= 75f -> OccupancyLevel.MEDIUM
                else -> OccupancyLevel.HIGH
            }
        }
}



