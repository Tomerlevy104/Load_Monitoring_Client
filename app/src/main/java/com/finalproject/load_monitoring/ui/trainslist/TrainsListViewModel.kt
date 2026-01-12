package com.finalproject.load_monitoring.ui.trainslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finalproject.load_monitoring.di.RepositoryProvider
import com.finalproject.load_monitoring.models.TrainModel
import com.finalproject.load_monitoring.repositories.TrainRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TrainsListViewModel : ViewModel() {
    private val trainRepository: TrainRepository = RepositoryProvider.trainRepository

    private val _trainsList =
        MutableStateFlow<List<TrainModel>>(emptyList())// Internal mutable state - real data. This should only be modified inside the ViewModel


    val trainsList: StateFlow<List<TrainModel>> =
        _trainsList // The object exposed to the Fragment (read-only). The Fragment can observe this state but cannot modify it

    // Loads trains list by origin and destination.
    fun loadTrainsListByOriginAndDestination(origin: String, destination: String) {
        viewModelScope.launch {
            _trainsList.value = trainRepository.searchTrainsByOriginAndDest(origin, destination)
        }
    }

    // Loads all trains
    fun loadAllTrains() {
        viewModelScope.launch {
            _trainsList.value = trainRepository.getAllTrains()
        }
    }
}