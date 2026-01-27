package com.finalproject.load_monitoring.di

import com.finalproject.load_monitoring.repositories.RemoteTrainRepository
import com.finalproject.load_monitoring.repositories.TrainRepository

object RepositoryProvider {

    /**
     * Here we decide where the data will come from
     */

    val trainRepository: TrainRepository by lazy {
        RemoteTrainRepository()
    }
}
