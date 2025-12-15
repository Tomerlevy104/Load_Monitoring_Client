package com.finalproject.load_monitoring.repositories

import com.finalproject.load_monitoring.models.CarriageModel
import com.finalproject.load_monitoring.models.OccupancyLevel
import com.finalproject.load_monitoring.models.TrainDetailsModel
import java.util.Date

class FakeTrainRepository : TrainRepository {

    override suspend fun getTrainDetailsByTrainID(trainID: String): TrainDetailsModel {
        val now = Date()

        val carriages = listOf(
            CarriageModel(trainID, 1, OccupancyLevel.HIGH, now),
            CarriageModel(trainID, 2, OccupancyLevel.MEDIUM, now),
            CarriageModel(trainID, 3, OccupancyLevel.HIGH, now),
            CarriageModel(trainID, 4, OccupancyLevel.LOW, now),
//            CarriageModel(trainID, 5, OccupancyLevel.MEDIUM, now)
        )

        return TrainDetailsModel(
            trainID = trainID,
            carriageList = carriages,
            originStation = "תל אביב סבידור",
            destinationStation = "חיפה חוף הכרמל",
            departureTime = now,
            arrivalTime = Date(now.time + 90 * 60 * 1000), // +90 דקות
            departurePlatform = 3
        )
    }

    override suspend fun searchTrainsByOriginAndDest(
        origin: String,
        destination: String
    ): List<TrainDetailsModel> {
        // כרגע לא רלוונטי – נחזיר רשימה ריקה
        return emptyList()
    }
}
