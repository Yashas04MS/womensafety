package com.example.womensafetyapp.ui.emergency

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.womensafetyapp.network.EmergencyAlertRequest
import com.example.womensafetyapp.network.apiClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SOSEmergencyViewModel(application: Application) : AndroidViewModel(application) {

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

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)

    // Get token from SharedPreferences
    private fun getToken(): String {
        val token = getApplication<Application>().getSharedPreferences("user_token", Context.MODE_PRIVATE)
            .getString("jwt", "") ?: ""
        return "Bearer $token"
    }

    fun triggerSOSAlert() {
        viewModelScope.launch {
            try {
                _isTriggering.value = true
                _errorMessage.value = ""
                _countdown.value = 5

                // Fetch location first
                fetchCurrentLocation()

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

    private suspend fun fetchCurrentLocation() {
        try {
            val context = getApplication<Application>().applicationContext

            // Check permissions
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.w("SOSViewModel", "Location permission not granted")
                return
            }

            // Try to get last known location first (faster)
            val lastLocation = fusedLocationClient.lastLocation.await()
            if (lastLocation != null) {
                _currentLocation.value = lastLocation
                Log.d("SOSViewModel", "Got last known location: ${lastLocation.latitude}, ${lastLocation.longitude}")
                return
            }

            // If last location is null, get current location
            val cancellationTokenSource = CancellationTokenSource()
            val currentLocation = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).await()

            if (currentLocation != null) {
                _currentLocation.value = currentLocation
                Log.d("SOSViewModel", "Got current location: ${currentLocation.latitude}, ${currentLocation.longitude}")
            } else {
                Log.w("SOSViewModel", "Could not fetch location")
            }

        } catch (e: Exception) {
            Log.e("SOSViewModel", "Error fetching location: ${e.message}", e)
        }
    }

    private suspend fun sendEmergencyAlert() {
        try {
            val location = _currentLocation.value

            Log.d("SOSViewModel", "Sending alert with location: ${location?.latitude}, ${location?.longitude}")

            val alertRequest = EmergencyAlertRequest(
                alertMessage = "EMERGENCY! I need immediate help!",
                latitude = location?.latitude,
                longitude = location?.longitude,
                locationAddress = null // Backend will geocode
            )

            val response = apiClient.api.triggerEmergencyAlert(alertRequest, getToken())

            _alertState.value = response.alertStatus
            _errorMessage.value = ""

            Log.d("SOSViewModel", "Alert sent successfully: ${response.message}")

            // Keep alert state for 5 seconds then reset
            delay(5000)
            _isTriggering.value = false
            _alertState.value = null // Clear alert state after showing success

        } catch (e: Exception) {
            Log.e("SOSViewModel", "Error sending alert: ${e.message}", e)
            _errorMessage.value = when {
                e.message?.contains("No emergency contacts") == true ->
                    "Please add emergency contacts first!"
                else -> e.localizedMessage ?: "Failed to send alert"
            }
            _isTriggering.value = false
            _alertState.value = null // Clear alert state on error
        }
    }

    fun cancelAlert() {
        countdownJob?.cancel()
        _isTriggering.value = false
        _countdown.value = 5
        _alertState.value = null // Clear any previous success state
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

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}