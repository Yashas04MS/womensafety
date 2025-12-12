package com.example.womensafetyapp.ui.fakeCall

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.womensafetyapp.network.apiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FakeCallViewModel(
    private val appContext: Context
) : ViewModel() {

    private val _presets = MutableStateFlow<List<FakeCallPreset>>(emptyList())
    val presets: StateFlow<List<FakeCallPreset>> = _presets

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    private val _callResponse = MutableStateFlow<FakeCallResponse?>(null)
    val callResponse: StateFlow<FakeCallResponse?> = _callResponse

    private fun getToken(): String {
        val token = appContext.getSharedPreferences("user_token", Context.MODE_PRIVATE)
            .getString("jwt", "") ?: ""
        return "Bearer $token"
    }

    fun fetchPresets() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = ""

                val response = apiClient.api.getFakeCallPresets(getToken())

                _presets.value = response.map { dto ->
                    FakeCallPreset(
                        id = dto.id ?: 0L,
                        callerName = dto.callerName ?: "Unknown",
                        callerPhone = dto.callerPhone ?: "",
                        callerPhotoUrl = dto.callerPhotoUrl,
                        ringtoneName = dto.ringtoneName,
                        vibrateEnabled = dto.vibrateEnabled ?: true,
                        autoAnswerDelaySeconds = dto.autoAnswerDelaySeconds,
                        callDurationSeconds = dto.callDurationSeconds ?: 120,
                        callType = dto.callType ?: "VOICE_CALL",
                        presetName = dto.presetName,
                        triggerCount = dto.triggerCount ?: 0,
                        lastTriggeredAt = dto.lastTriggeredAt
                    )
                }

            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Failed to fetch presets"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createPreset(presetData: PresetData) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = ""

                val dto = FakeCallPresetDTO(
                    callerName = presetData.callerName,
                    callerPhone = presetData.callerPhone,
                    presetName = presetData.presetName,
                    callDurationSeconds = presetData.callDurationSeconds,
                    autoAnswerDelaySeconds = presetData.autoAnswerDelaySeconds,
                    vibrateEnabled = presetData.vibrateEnabled,
                    callType = presetData.callType,
                    isPreset = true
                )

                apiClient.api.createFakeCallPreset(dto, getToken())
                fetchPresets() // Refresh list

            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Failed to create preset"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updatePreset(presetId: Long, presetData: PresetData) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = ""

                val dto = FakeCallPresetDTO(
                    callerName = presetData.callerName,
                    callerPhone = presetData.callerPhone,
                    presetName = presetData.presetName,
                    callDurationSeconds = presetData.callDurationSeconds,
                    autoAnswerDelaySeconds = presetData.autoAnswerDelaySeconds,
                    vibrateEnabled = presetData.vibrateEnabled,
                    callType = presetData.callType,
                    isPreset = true
                )

                apiClient.api.updateFakeCallPreset(presetId, dto, getToken())
                fetchPresets() // Refresh list

            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Failed to update preset"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deletePreset(presetId: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = ""

                apiClient.api.deleteFakeCallPreset(presetId, getToken())
                fetchPresets() // Refresh list

            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Failed to delete preset"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun triggerFakeCall(presetId: Long?) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = ""

                val triggerDTO = TriggerFakeCallDTO(
                    presetId = presetId,
                    triggerMethod = "IN_APP_BUTTON"
                )

                val response = apiClient.api.triggerFakeCall(triggerDTO, getToken())

                _callResponse.value = FakeCallResponse(
                    callLogId = response.callLogId ?: 0L,
                    callerName = response.callerName ?: "Unknown",
                    callerPhone = response.callerPhone ?: "",
                    callType = response.callType ?: "VOICE_CALL",
                    autoAnswerDelaySeconds = response.autoAnswerDelaySeconds,
                    callDurationSeconds = response.callDurationSeconds ?: 120,
                    vibrateEnabled = response.vibrateEnabled ?: true
                )

            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Failed to trigger fake call"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun quickTrigger() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = ""

                val response = apiClient.api.quickTriggerFakeCall("IN_APP_BUTTON", getToken())

                _callResponse.value = FakeCallResponse(
                    callLogId = response.callLogId ?: 0L,
                    callerName = response.callerName ?: "Unknown",
                    callerPhone = response.callerPhone ?: "",
                    callType = response.callType ?: "VOICE_CALL",
                    autoAnswerDelaySeconds = response.autoAnswerDelaySeconds,
                    callDurationSeconds = response.callDurationSeconds ?: 120,
                    vibrateEnabled = response.vibrateEnabled ?: true
                )

            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Failed to trigger quick call"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun endFakeCall(callLogId: Long, wasAnswered: Boolean, actualDuration: Int) {
        viewModelScope.launch {
            try {
                val endDTO = EndFakeCallDTO(
                    callLogId = callLogId,
                    wasAnswered = wasAnswered,
                    actualDurationSeconds = actualDuration
                )

                apiClient.api.endFakeCall(endDTO, getToken())
                _callResponse.value = null

            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Failed to end call"
            }
        }
    }

    fun clearCallResponse() {
        _callResponse.value = null
    }
}

class FakeCallViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FakeCallViewModel::class.java)) {
            return FakeCallViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

// DTOs
data class FakeCallPresetDTO(
    val id: Long? = null,
    val callerName: String,
    val callerPhone: String,
    val callerPhotoUrl: String? = null,
    val ringtoneName: String? = null,
    val vibrateEnabled: Boolean? = null,
    val autoAnswerDelaySeconds: Int? = null,
    val callDurationSeconds: Int? = null,
    val callType: String? = null,
    val isPreset: Boolean? = null,
    val presetName: String? = null,
    val triggerCount: Int? = null,
    val lastTriggeredAt: String? = null
)

data class TriggerFakeCallDTO(
    val presetId: Long? = null,
    val callerName: String? = null,
    val callerPhone: String? = null,
    val callType: String? = null,
    val autoAnswerDelaySeconds: Int? = null,
    val callDurationSeconds: Int? = null,
    val triggerMethod: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)

data class EndFakeCallDTO(
    val callLogId: Long,
    val wasAnswered: Boolean? = null,
    val wasDeclined: Boolean? = null,
    val actualDurationSeconds: Int? = null,
    val notes: String? = null
)

data class FakeCallResponse(
    val callLogId: Long,
    val callerName: String,
    val callerPhone: String,
    val callType: String,
    val autoAnswerDelaySeconds: Int?,
    val callDurationSeconds: Int,
    val vibrateEnabled: Boolean
)