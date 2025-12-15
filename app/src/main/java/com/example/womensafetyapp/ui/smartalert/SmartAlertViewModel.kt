package com.example.womensafetyapp.ui.smartalert

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.womensafetyapp.network.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SmartAlertViewModel(private val context: Context) : ViewModel() {

    private val _isMonitoring = MutableStateFlow(false)
    val isMonitoring: StateFlow<Boolean> = _isMonitoring

    private val _activityLog = MutableStateFlow<List<SuspiciousActivity>>(emptyList())
    val activityLog: StateFlow<List<SuspiciousActivity>> = _activityLog

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    private val _lastAnalysis = MutableStateFlow<SmartAlertAnalysis?>(null)
    val lastAnalysis: StateFlow<SmartAlertAnalysis?> = _lastAnalysis

    fun sendManualAlert() {
        _errorMessage.value = ""
        // Trigger SOS / backend / notification here
    }

    fun clearMessages() {
        _errorMessage.value = ""
    }
}

class SmartAlertViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        SmartAlertViewModel(context) as T
}
