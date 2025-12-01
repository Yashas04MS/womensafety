package com.example.womensafetyapp.ui.emergency

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.womensafetyapp.network.EmergencyAlertRequest
import com.example.womensafetyapp.network.apiClient
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SOSEmergencyViewModel : ViewModel() {

    private val _isTriggering = MutableStateFlow(false)
    val isTriggering: StateFlow<Boolean> = _isTriggering

    private val _countdown = MutableStateFlow(5)
    val countdown: StateFlow<Int> = _countdown

    private val _alertState = MutableStateFlow<String?>(null)
    val alertState: StateFlow<String?> = _alertState

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation

    private var countdownJob: Job? = null

    fun triggerSOSAlert() {
        viewModelScope.launch {
            try {
                _isTriggering.value = true
                _errorMessage.value = ""
                _countdown.value = 5

                // Start countdown
                countdownJob = launch {
                    for (i in 5 downTo 1) {
                        _countdown.value = i
                        delay(1000)
                    }

                    // After countdown, send alert
                    sendEmergencyAlert()
                }

            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Failed to trigger alert"
                _isTriggering.value = false
            }
        }
    }

    private suspend fun sendEmergencyAlert() {
        try {
            val location = _currentLocation.value

            val alertRequest = EmergencyAlertRequest(
                alertMessage = "EMERGENCY! I need immediate help!",
                latitude = location?.latitude,
                longitude = location?.longitude,
                locationAddress = null // Backend will geocode
            )

            val response = apiClient.api.triggerEmergencyAlert(alertRequest, getToken())

            _alertState.value = response.alertStatus
            _errorMessage.value = ""

            // Keep alert state for 5 seconds then reset
            delay(5000)
            _isTriggering.value = false

        } catch (e: Exception) {
            _errorMessage.value = when {
                e.message?.contains("No emergency contacts") == true ->
                    "Please add emergency contacts first!"
                else -> e.localizedMessage ?: "Failed to send alert"
            }
            _isTriggering.value = false
        }
    }

    fun cancelAlert() {
        countdownJob?.cancel()
        _isTriggering.value = false
        _countdown.value = 5
        _errorMessage.value = "Alert cancelled"

        // Clear error after 3 seconds
        viewModelScope.launch {
            delay(3000)
            _errorMessage.value = ""
        }
    }

    fun updateLocation(location: Location) {
        _currentLocation.value = location
    }

    private fun getToken(): String {
        // TODO: Get token from SharedPreferences
        // For now returning empty, you should implement proper token management
        return "Bearer YOUR_TOKEN_HERE"
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}