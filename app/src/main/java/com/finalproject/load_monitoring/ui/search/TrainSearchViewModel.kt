package com.finalproject.load_monitoring.ui.search

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// The data class is like the state of the screen
data class TrainSearchUiState(
    val origin: String = "",
    val destination: String = "",
    val hour: Int = 8,
    val minute: Int = 0,
    val isSearchEnabled: Boolean = false
) {
    val timeFormatted: String
        get() = "%02d:%02d".format(hour, minute)
}

class TrainSearchViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TrainSearchUiState()) // The real state
    val uiState: StateFlow<TrainSearchUiState> = _uiState // The state that the Fragment observes

    fun onOriginChanged(newOrigin: String) {
        val current = _uiState.value
        val enabled =
            newOrigin.isNotBlank() && current.destination.isNotBlank()

        _uiState.value = current.copy(
            origin = newOrigin,
            isSearchEnabled = enabled
        )
    }

    fun onDestinationChanged(newDestination: String) {
        val current = _uiState.value
        val enabled =
            current.origin.isNotBlank() && newDestination.isNotBlank()

        _uiState.value = current.copy(
            destination = newDestination,
            isSearchEnabled = enabled
        )
    }

    fun onHourChanged(newHour: Int) {
        val current = _uiState.value
        _uiState.value = current.copy(
            hour = newHour
        )
    }

    fun onMinuteChanged(newMinute: Int) {
        val current = _uiState.value
        _uiState.value = current.copy(
            minute = newMinute
        )
    }

    fun onSwapStations() {
        val current = _uiState.value
        val newOrigin = current.destination
        val newDestination = current.origin

        val enabled =
            newOrigin.isNotBlank() && newDestination.isNotBlank()

        _uiState.value = current.copy(
            origin = newOrigin,
            destination = newDestination,
            isSearchEnabled = enabled
        )
    }
}

