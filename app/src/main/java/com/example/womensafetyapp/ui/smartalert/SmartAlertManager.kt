package com.example.womensafetyapp.ui.smartalert

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlin.math.min

class SmartAlertManager(private val context: Context) {

    enum class ActivityType {
        FALL,
        RUNNING,
        SHAKING,
        VOICE_DISTRESS,
        IMPACT,
        UNKNOWN
    }

    data class AlertEvent(
        val activityType: ActivityType,
        val intensity: Float,
        val riskLevel: String
    )

    private val _alertEvent = MutableLiveData<AlertEvent>()
    val alertEvent: LiveData<AlertEvent> = _alertEvent

    private var monitoring = false

    fun startMonitoring() {
        monitoring = true
    }

    fun stopMonitoring() {
        monitoring = false
    }

    fun triggerActivity(
        activityType: ActivityType,
        intensity: Float
    ) {
        if (!monitoring) return

        val riskScore = calculateRisk(activityType, intensity)
        val riskLevel = when {
            riskScore >= 0.85f -> "CRITICAL"
            riskScore >= 0.7f -> "HIGH"
            riskScore >= 0.4f -> "MEDIUM"
            else -> "LOW"
        }

        if (riskScore >= 0.7f) {
            _alertEvent.postValue(
                AlertEvent(activityType, intensity, riskLevel)
            )
        }
    }

    fun triggerManualAlert() {
        _alertEvent.postValue(
            AlertEvent(ActivityType.UNKNOWN, 1.0f, "CRITICAL")
        )
    }

    private fun calculateRisk(
        activityType: ActivityType,
        intensity: Float
    ): Float {
        var score = intensity

        score += when (activityType) {
            ActivityType.FALL,
            ActivityType.IMPACT -> 0.3f
            ActivityType.VOICE_DISTRESS -> 0.35f
            ActivityType.SHAKING -> 0.25f
            ActivityType.RUNNING -> 0.15f
            else -> 0f
        }

        return min(score, 1.0f)
    }
}
