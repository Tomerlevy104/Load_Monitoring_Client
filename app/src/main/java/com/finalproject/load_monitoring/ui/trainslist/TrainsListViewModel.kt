package com.finalproject.load_monitoring.ui.trainslist

import android.util.Log
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
            try {
                _trainsList.value = trainRepository.searchTrainsByOriginAndDest(origin, destination)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("TrainsListViewModel", "Error fetching trains list: ${e.message}")
            }
        }
    }

    fun loadTrainsListByOriginDestAndDate(origin: String, destination: String, date: LocalDateTime) {
        viewModelScope.launch {
            try {
                _trainsList.value =
                    trainRepository.searchTrainsByOriginDestAndDate(origin, destination, date)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("TrainsListViewModel", "Error fetching trains list: ${e.message}")
            }
        }
    }

    fun loadAllTrains() {
        viewModelScope.launch {
            try {
                _trainsList.value = trainRepository.getAllTrains()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("TrainsListViewModel", "Error fetching all trains: ${e.message}")
            }
        }
    }
}