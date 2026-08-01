package com.finalproject.load_monitoring.di

import com.finalproject.load_monitoring.repositories.RemoteTrainRepository
import com.finalproject.load_monitoring.repositories.TrainRepository

object RepositoryProvider {
    val trainRepository: TrainRepository by lazy {
        RemoteTrainRepository()
    }
}
