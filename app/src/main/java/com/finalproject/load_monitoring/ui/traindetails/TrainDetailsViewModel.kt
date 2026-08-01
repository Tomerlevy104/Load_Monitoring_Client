package com.finalproject.load_monitoring.ui.traindetails

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finalproject.load_monitoring.di.RepositoryProvider
import com.finalproject.load_monitoring.models.OccupancyLogModel
import com.finalproject.load_monitoring.models.TrainModel
import com.finalproject.load_monitoring.repositories.TrainRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TrainDetailsViewModel : ViewModel() {
    private val trainRepository: TrainRepository = RepositoryProvider.trainRepository

    // Mutable internal state, writable only inside the ViewModel
    private val _trainDetails = MutableStateFlow<TrainModel?>(null)

    // Read-only state exposed to the Fragment
    val trainDetails: StateFlow<TrainModel?> = _trainDetails.asStateFlow()

    private val _occupancyLog = MutableStateFlow<OccupancyLogModel?>(null)
    val occupancyLog: StateFlow<OccupancyLogModel?> = _occupancyLog.asStateFlow()

    fun loadTrainDetails(trainId: String) {
        viewModelScope.launch {
            try {
                _trainDetails.value = trainRepository.getTrainDetailsByTrainID(trainId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getOccupancyLogByCarriageId(carriageId: Long) {
        viewModelScope.launch {
            try {
                _occupancyLog.value = trainRepository.getLogByCarriageId(carriageId)
                Log.d("TrainDetailsViewModel", "Occupancy log fetched: ${_occupancyLog.value}")
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("TrainDetailsViewModel", "Error fetching occupancy log: ${e.message}")
            }
        }
    }

    fun clearOccupancyLog() {
        _occupancyLog.value = null
    }
}