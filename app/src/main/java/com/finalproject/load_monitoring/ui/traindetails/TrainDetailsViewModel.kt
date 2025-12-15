package com.finalproject.load_monitoring.ui.traindetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finalproject.load_monitoring.models.TrainDetailsModel
import com.finalproject.load_monitoring.repositories.FakeTrainRepository
import com.finalproject.load_monitoring.repositories.TrainRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TrainDetailsViewModel : ViewModel() {
    // TODO: replace with a server connection to a real repository (Retrofit).
    private val trainRepository: TrainRepository =
        FakeTrainRepository() // Repository responsible for fetching train data.

    private val _trainDetails =
        MutableStateFlow<TrainDetailsModel?>(null) // Internal mutable state - real data. This should only be modified inside the ViewModel

    val trainDetails: StateFlow<TrainDetailsModel?> =
        _trainDetails // The object exposed to the Fragment (read-only). The Fragment can observe this state but cannot modify it

    // Loads train details for a given train ID.
    // This function is called by the Fragment.
    fun loadTrainDetails(trainId: String) {

        // viewModelScope launches a coroutine that is tied to the ViewModel lifecycle.
        // When the ViewModel is cleared, all coroutines in this scope are automatically cancelled.
        viewModelScope.launch {

            // Fetch train details from the repository
            _trainDetails.value = trainRepository.getTrainDetailsByTrainID(trainId)
        }
    }
}
