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

    private val _successMessage = MutableStateFlow("")
    val successMessage: StateFlow<String> = _successMessage

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
                _successMessage.value = ""
                _alertState.value = null
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
                _errorMessage.value = "Failed to trigger alert: ${e.localizedMessage}"
                _isTriggering.value = false
                _successMessage.value = ""
                Log.e("SOSViewModel", "Error triggering alert", e)
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
            _successMessage.value = "✅ Alert sent successfully to ${response.contactsNotifiedCount ?: 0} contact(s)!"

            Log.d("SOSViewModel", "Alert sent successfully: ${response.message}")

            // Keep success message for 8 seconds then reset
            delay(8000)
            _isTriggering.value = false
            _successMessage.value = ""
            _alertState.value = null

        } catch (e: Exception) {
            Log.e("SOSViewModel", "Error sending alert: ${e.message}", e)

            // Parse error message to provide user-friendly feedback
            val userFriendlyError = when {
                e.message?.contains("No emergency contacts", ignoreCase = true) == true ->
                    "No emergency contacts found. Please add emergency contacts first!"

                e.message?.contains("401") == true || e.message?.contains("Unauthorized") == true ->
                    "Session expired. Please login again."

                e.message?.contains("404") == true ->
                    "Service not found. Please check your connection."

                e.message?.contains("500") == true ->
                    "Server error. Please try again later."

                e.message?.contains("timeout", ignoreCase = true) == true ->
                    "Request timeout. Please check your internet connection."

                e.message?.contains("Unable to resolve host") == true ->
                    "No internet connection. Please check your network."

                else -> "Failed to send alert. Please try again."
            }

            _errorMessage.value = userFriendlyError
            _successMessage.value = ""
            _isTriggering.value = false
            _alertState.value = null

            // Auto-clear error after 10 seconds
            viewModelScope.launch {
                delay(10000)
                _errorMessage.value = ""
            }
        }
    }

    fun cancelAlert() {
        Log.d("SOSViewModel", "Alert cancelled by user")

        countdownJob?.cancel()
        _isTriggering.value = false
        _countdown.value = 5
        _alertState.value = null
        _successMessage.value = ""
        _errorMessage.value = "Alert cancelled"

        // Clear cancellation message after 3 seconds
        viewModelScope.launch {
            delay(3000)
            _errorMessage.value = ""
        }
    }

    fun updateLocation(location: Location) {
        _currentLocation.value = location
    }

    fun clearMessages() {
        _errorMessage.value = ""
        _successMessage.value = ""
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}