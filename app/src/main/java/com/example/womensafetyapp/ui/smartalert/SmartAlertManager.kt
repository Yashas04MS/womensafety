//package com.example.womensafetyapp.ui.smartalert
//
//import android.content.Context
//import android.hardware.Sensor
//import android.hardware.SensorEvent
//import android.hardware.SensorEventListener
//import android.hardware.SensorManager
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import kotlin.math.min
//import kotlin.math.sqrt
//
///**
// * Handles local device sensor monitoring and detection
// * Works in conjunction with SmartAlertViewModel for API communication
// */
//class SmartAlertManager(private val context: Context) : SensorEventListener {
//
//    enum class ActivityType {
//        FALL,
//        RUNNING,
//        SHAKING,
//        VOICE_DISTRESS,
//        IMPACT,
//        UNKNOWN
//    }
//
//    data class SmartAlertEvent(
//        val activityType: ActivityType,
//        val intensity: Float,
//        val riskLevel: String,
//        val shouldTriggerApi: Boolean = true
//    )
//
//    private val _alertEvent = MutableLiveData<SmartAlertEvent>()
//    val alertEvent: LiveData<SmartAlertEvent> = _alertEvent
//
//    private val sensorManager: SensorManager =
//        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
//
//    private val accelerometer: Sensor? =
//        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
//
//    private var monitoring = false
//    private var lastShakeTime = 0L
//    private val shakeThreshold = 15f // m/s²
//    private val fallThreshold = 2f // Low acceleration indicates free fall
//
//    fun startMonitoring() {
//        monitoring = true
//        accelerometer?.let {
//            sensorManager.registerListener(
//                this,
//                it,
//                SensorManager.SENSOR_DELAY_NORMAL
//            )
//        }
//    }
//
//    fun stopMonitoring() {
//        monitoring = false
//        sensorManager.unregisterListener(this)
//    }
//
//    override fun onSensorChanged(event: SensorEvent?) {
//        if (!monitoring || event == null) return
//
//        when (event.sensor.type) {
//            Sensor.TYPE_ACCELEROMETER -> {
//                val x = event.values[0]
//                val y = event.values[1]
//                val z = event.values[2]
//
//                val acceleration = sqrt(x * x + y * y + z * z)
//
//                // Detect shake
//                if (acceleration > shakeThreshold) {
//                    val currentTime = System.currentTimeMillis()
//                    if (currentTime - lastShakeTime > 1000) { // Debounce
//                        lastShakeTime = currentTime
//                        triggerActivity(ActivityType.SHAKING, acceleration / 20f)
//                    }
//                }
//
//                // Detect fall (sudden drop in acceleration)
//                if (acceleration < fallThreshold) {
//                    triggerActivity(ActivityType.FALL, 0.9f)
//                }
//
//                // Detect impact (sudden spike)
//                if (acceleration > 25f) {
//                    triggerActivity(ActivityType.IMPACT, acceleration / 30f)
//                }
//            }
//        }
//    }
//
//    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
//        // Not needed for this implementation
//    }
//
//    fun triggerActivity(
//        activityType: ActivityType,
//        intensity: Float
//    ) {
//        if (!monitoring) return
//
//        val riskScore = calculateRisk(activityType, intensity)
//        val riskLevel = when {
//            riskScore >= 0.85f -> "CRITICAL"
//            riskScore >= 0.7f -> "HIGH"
//            riskScore >= 0.4f -> "MEDIUM"
//            else -> "LOW"
//        }
//
//        // Only trigger alert for HIGH and CRITICAL risks
//        if (riskScore >= 0.7f) {
//            _alertEvent.postValue(
//                SmartAlertEvent(
//                    activityType = activityType,
//                    intensity = intensity,
//                    riskLevel = riskLevel,
//                    shouldTriggerApi = true
//                )
//            )
//        }
//    }
//
//    fun triggerManualAlert() {
//        _alertEvent.postValue(
//            SmartAlertEvent(
//                activityType = ActivityType.UNKNOWN,
//                intensity = 1.0f,
//                riskLevel = "CRITICAL",
//                shouldTriggerApi = true
//            )
//        )
//    }
//
//    private fun calculateRisk(
//        activityType: ActivityType,
//        intensity: Float
//    ): Float {
//        var score = intensity
//
//        // Add weight based on activity type
//        score += when (activityType) {
//            ActivityType.FALL,
//            ActivityType.IMPACT -> 0.3f
//            ActivityType.VOICE_DISTRESS -> 0.35f
//            ActivityType.SHAKING -> 0.25f
//            ActivityType.RUNNING -> 0.15f
//            else -> 0f
//        }
//
//        return min(score, 1.0f)
//    }
//
//    fun cleanup() {
//        stopMonitoring()
//    }
//}

package com.example.womensafetyapp.ui.smartalert

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.min
import kotlin.math.sqrt

class SmartAlertManager(private val context: Context) : SensorEventListener {

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer =
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val _alertEvent = MutableStateFlow<AlertEvent?>(null)
    val alertEvent: StateFlow<AlertEvent?> = _alertEvent

    private var isMonitoring = false
    private var lastShakeTime = 0L

    enum class ActivityType {
        FALL, SHAKING, IMPACT, RUNNING, VOICE_DISTRESS, MANUAL, UNKNOWN
    }

    data class AlertEvent(
        val activityType: ActivityType,
        val intensity: Float,
        val riskLevel: String,
        val shouldTriggerApi: Boolean,
        val timestamp: Long = System.currentTimeMillis()
    )

    fun startMonitoring() {
        if (!isMonitoring) {
            isMonitoring = true
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    fun stopMonitoring() {
        if (isMonitoring) {
            isMonitoring = false
            sensorManager.unregisterListener(this)
        }
    }

    fun triggerActivity(activityType: ActivityType, intensity: Float) {
        val riskLevel = when {
            intensity >= 0.9f -> "CRITICAL"
            intensity >= 0.7f -> "HIGH"
            intensity >= 0.5f -> "MEDIUM"
            else -> "LOW"
        }

        _alertEvent.value = AlertEvent(
            activityType,
            intensity,
            riskLevel,
            shouldTriggerApi = true
        )
    }

    fun triggerManualAlert() {
        triggerActivity(ActivityType.MANUAL, 1f)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type != Sensor.TYPE_ACCELEROMETER) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val acceleration =
            sqrt((x * x + y * y + z * z).toDouble()).toFloat()
        val normalized = acceleration / SensorManager.GRAVITY_EARTH

        if (normalized > 2.5f) {
            val now = System.currentTimeMillis()
            if (now - lastShakeTime > 1000) {
                lastShakeTime = now
                triggerActivity(ActivityType.SHAKING, 0.8f)
            }
        }

        if (normalized < 0.3f) {
            triggerActivity(ActivityType.FALL, 0.9f)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
