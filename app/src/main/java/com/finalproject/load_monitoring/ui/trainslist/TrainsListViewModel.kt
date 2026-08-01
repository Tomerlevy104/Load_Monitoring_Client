package com.finalproject.load_monitoring.ui.trainslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finalproject.load_monitoring.di.RepositoryProvider
import com.finalproject.load_monitoring.models.TrainModel
import com.finalproject.load_monitoring.repositories.TrainRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class TrainsListViewModel : ViewModel() {
    private val trainRepository: TrainRepository = RepositoryProvider.trainRepository

    // Mutable internal state, writable only inside the ViewModel
    private val _trainsList = MutableStateFlow<List<TrainModel>>(emptyList())

    // Read-only state exposed to the Fragment
    val trainsList: StateFlow<List<TrainModel>> = _trainsList

    fun loadTrainsListByOriginAndDestination(origin: String, destination: String) {
        viewModelScope.launch {
            _trainsList.value = trainRepository.searchTrainsByOriginAndDest(origin, destination)
        }
    }

    fun loadTrainsListByOriginDestAndDate(origin: String, destination: String, date: LocalDateTime) {
        viewModelScope.launch {
            _trainsList.value =
                trainRepository.searchTrainsByOriginDestAndDate(origin, destination, date)
        }
    }

    fun loadAllTrains() {
        viewModelScope.launch {
            _trainsList.value = trainRepository.getAllTrains()
        }
    }
}