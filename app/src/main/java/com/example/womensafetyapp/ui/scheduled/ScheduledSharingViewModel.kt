package com.example.womensafetyapp.ui.scheduled

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.womensafetyapp.network.apiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ScheduledSharingViewModel(
    private val appContext: Context
) : ViewModel() {

    private val _sessions = MutableStateFlow<List<ScheduledSession>>(emptyList())
    val sessions: StateFlow<List<ScheduledSession>> = _sessions

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    private val _successMessage = MutableStateFlow("")
    val successMessage: StateFlow<String> = _successMessage

    private fun getToken(): String {
        val token = appContext.getSharedPreferences("user_token", Context.MODE_PRIVATE)
            .getString("jwt", "") ?: ""
        return "Bearer $token"
    }

    fun fetchSessions() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = ""

                val response = apiClient.api.getUserScheduledSharing(getToken())

                _sessions.value = response.map { dto ->
                    ScheduledSession(
                        id = dto.id ?: 0L,
                        sessionName = dto.sessionName ?: "Unnamed Session",
                        startTime = dto.startTime ?: "",
                        endTime = dto.endTime ?: "",
                        durationMinutes = dto.durationMinutes ?: 60,
                        status = dto.status ?: "SCHEDULED",
                        destinationAddress = dto.destinationAddress,
                        destinationLatitude = dto.destinationLatitude,
                        destinationLongitude = dto.destinationLongitude,
                        notifyContactsOnStart = dto.notifyContactsOnStart ?: true,
                        notifyContactsOnArrival = dto.notifyContactsOnArrival ?: true
                    )
                }

            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Failed to fetch sessions"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createSession(data: CreateSessionData) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = ""

                val request = ScheduledLocationSharingRequest(
                    sessionName = data.sessionName,
                    startTime = data.startTime.toString(),
                    durationMinutes = data.durationMinutes,
                    updateIntervalSeconds = 30,
                    destinationAddress = data.destinationAddress,
                    notifyContactsOnStart = data.notifyContactsOnStart,
                    notifyContactsOnArrival = data.notifyContactsOnArrival,
                    notifyContactsOnDelay = data.notifyContactsOnDelay,
                    autoAlertIfNotArrived = data.autoAlertIfNotArrived
                )

                apiClient.api.createScheduledSharing(request, getToken())

                _successMessage.value = "Session created successfully!"
                fetchSessions() // Refresh the list

            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Failed to create session"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun markArrived(sessionId: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = ""

                apiClient.api.markArrived(sessionId, getToken())

                _successMessage.value = "Marked as arrived!"
                fetchSessions() // Refresh the list

            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Failed to mark arrival"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun cancelSession(sessionId: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = ""

                apiClient.api.cancelScheduledSharing(sessionId, getToken())

                _successMessage.value = "Session cancelled"
                fetchSessions() // Refresh the list

            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Failed to cancel session"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearMessages() {
        _errorMessage.value = ""
        _successMessage.value = ""
    }
}

class ScheduledSharingViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScheduledSharingViewModel::class.java)) {
            return ScheduledSharingViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

// Request/Response DTOs
data class ScheduledLocationSharingRequest(
    val sessionName: String?,
    val startTime: String,
    val durationMinutes: Int,
    val updateIntervalSeconds: Int?,
    val destinationAddress: String?,
    val notifyContactsOnStart: Boolean?,
    val notifyContactsOnArrival: Boolean?,
    val notifyContactsOnDelay: Boolean?,
    val autoAlertIfNotArrived: Boolean?
)

data class ScheduledLocationSharingResponse(
    val id: Long?,
    val sessionName: String?,
    val startTime: String?,
    val endTime: String?,
    val durationMinutes: Int?,
    val status: String?,
    val destinationAddress: String?,
    val destinationLatitude: Double?,
    val destinationLongitude: Double?,
    val notifyContactsOnStart: Boolean?,
    val notifyContactsOnArrival: Boolean?
)