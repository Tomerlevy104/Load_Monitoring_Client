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
    private val trainRepository: TrainRepository =
        RepositoryProvider.trainRepository // Repository responsible for fetching train data

    private val _trainDetails =
        MutableStateFlow<TrainModel?>(null) // Internal mutable state - real data. This should only be modified inside the ViewModel

    // trainDetails is like a "pointer" to _trainDetails
    val trainDetails: StateFlow<TrainModel?> =
        _trainDetails.asStateFlow() // The object exposed to the Fragment (read-only). The Fragment can observe this state but cannot modify it


    private val _occupancyLog = MutableStateFlow<OccupancyLogModel?>(null)
    val occupancyLog: StateFlow<OccupancyLogModel?> = _occupancyLog.asStateFlow()
    // Loads train details for a given train ID.
    // This function is called by the Fragment.
    fun loadTrainDetails(trainId: String) {

        // viewModelScope launches a coroutine that is tied to the ViewModel lifecycle.
        // When the ViewModel is cleared, all coroutines in this scope are automatically cancelled.
        viewModelScope.launch {
            try {
                // Fetch train details from the repository
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
