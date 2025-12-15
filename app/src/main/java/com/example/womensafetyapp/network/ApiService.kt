package com.example.womensafetyapp.network

import com.example.womensafetyapp.network.models.*
import com.example.womensafetyapp.ui.emergency.EmergencyContact
import com.example.womensafetyapp.ui.emergency.EmergencyContactDTO
import com.example.womensafetyapp.ui.fakeCall.EndFakeCallDTO
import com.example.womensafetyapp.ui.fakeCall.FakeCallPresetDTO
import com.example.womensafetyapp.ui.fakeCall.TriggerFakeCallDTO
import com.example.womensafetyapp.ui.scheduled.ScheduledLocationSharingRequest
import com.example.womensafetyapp.ui.scheduled.ScheduledLocationSharingResponse
import retrofit2.http.*

interface ApiService {

    // ==================== AUTHENTICATION ====================

    @POST("api/auth/login")
    suspend fun login(
        @Body request: AuthenticationRequestBody
    ): AuthenticationResponseBody

    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): AuthenticationResponseBody

    @GET("api/auth/send-email-verification-token")
    suspend fun sendEmailVerificationToken(
        @Header("Authorization") token: String
    ): String

    @PUT("api/auth/validate-email-verification-token")
    suspend fun verifyEmailToken(
        @Query("token") token: String,
        @Header("Authorization") authToken: String
    ): Map<String, String>

    @PUT("api/auth/send-password-reset-token")
    suspend fun sendPasswordResetToken(
        @Query("email") email: String
    ): String

    @PUT("api/auth/reset-password")
    suspend fun resetPassword(
        @Query("email") email: String,
        @Query("newPassword") newPassword: String,
        @Query("token") token: String
    ): String

    @GET("api/auth/profile")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): AuthUser

    @PUT("api/auth/profile")
    suspend fun updateProfile(
        @Query("firstName") firstName: String?,
        @Query("lastName") lastName: String?,
        @Query("phoneNumber") phoneNumber: String?,
        @Header("Authorization") token: String
    ): Map<String, Any>

    // ==================== EMERGENCY CONTACTS ====================

    @GET("api/emergency/contacts")
    suspend fun getEmergencyContacts(
        @Header("Authorization") token: String
    ): List<EmergencyContact>

//    @POST("api/emergency/contacts")
//    suspend fun addEmergencyContact(
//        @Header("Authorization") token: String,
//        contact: EmergencyContactDTO
//    ): Map<String, Any>

    @POST("api/emergency/contacts")
    suspend fun addEmergencyContact(
        @Body contact: EmergencyContactDTO,
        @Header("Authorization") token: String
    ): Map<String, Any>

    @PUT("api/emergency/contacts/{contactId}")
    suspend fun updateEmergencyContact(
        @Path("contactId") contactId: Long,
        @Body contact: EmergencyContactDTO,
        @Header("Authorization") token: String
    ): Map<String, Any>

    @DELETE("api/emergency/contacts/{contactId}")
    suspend fun deleteEmergencyContact(
        @Path("contactId") contactId: Long,
        @Header("Authorization") token: String
    ): Map<String, String>

    // ==================== EMERGENCY ALERTS ====================

    @POST("api/emergency/alerts")
    suspend fun triggerEmergencyAlert(
        @Body request: EmergencyAlertRequest,
        @Header("Authorization") token: String
    ): EmergencyAlertResponse

    @POST("api/emergency/alert/quick")
    suspend fun quickEmergencyAlert(
        @Query("latitude") latitude: Double?,
        @Query("longitude") longitude: Double?,
        @Query("locationAddress") locationAddress: String?,
        @Header("Authorization") token: String
    ): EmergencyAlertResponse

    @GET("api/emergency/alerts")
    suspend fun getEmergencyAlerts(
        @Header("Authorization") token: String
    ): List<EmergencyAlertResponse>

    @POST("api/emergency/test-sms")
    suspend fun testSms(
        @Query("phoneNumber") phoneNumber: String,
        @Query("message") message: String?,
        @Header("Authorization") token: String
    ): Map<String, String>

    // ==================== SCHEDULED LOCATION SHARING ====================

    @POST("api/location/scheduled-sharing")
    suspend fun createScheduledSharing(
        @Body request: ScheduledLocationSharingRequest,
        @Header("Authorization") token: String
    ): ScheduledLocationSharingResponse

    @GET("api/location/scheduled-sharing")
    suspend fun getUserScheduledSharing(
        @Header("Authorization") token: String
    ): List<ScheduledLocationSharingResponse>

    @PUT("api/location/scheduled-sharing/{sharingId}/arrived")
    suspend fun markArrived(
        @Path("sharingId") sharingId: Long,
        @Header("Authorization") token: String
    ): ScheduledLocationSharingResponse

    @PUT("api/location/scheduled-sharing/{sharingId}/cancel")
    suspend fun cancelScheduledSharing(
        @Path("sharingId") sharingId: Long,
        @Header("Authorization") token: String
    ): ScheduledLocationSharingResponse


    @GET("api/fake-call/presets")
    suspend fun getFakeCallPresets(
        @Header("Authorization") token: String
    ): List<FakeCallPresetDTO>

    @POST("api/fake-call/presets")
    suspend fun createFakeCallPreset(
        @Body preset: FakeCallPresetDTO,
        @Header("Authorization") token: String
    ): Map<String, Any>

    @PUT("api/fake-call/presets/{presetId}")
    suspend fun updateFakeCallPreset(
        @Path("presetId") presetId: Long,
        @Body preset: FakeCallPresetDTO,
        @Header("Authorization") token: String
    ): Map<String, Any>

    @DELETE("api/fake-call/presets/{presetId}")
    suspend fun deleteFakeCallPreset(
        @Path("presetId") presetId: Long,
        @Header("Authorization") token: String
    ): Map<String, String>

    @POST("api/fake-call/trigger")
    suspend fun triggerFakeCall(
        @Body request: TriggerFakeCallDTO,
        @Header("Authorization") token: String
    ): FakeCallResponseDTO

    @POST("api/fake-call/quick-trigger")
    suspend fun quickTriggerFakeCall(
        @Query("triggerMethod") triggerMethod: String,
        @Header("Authorization") token: String
    ): FakeCallResponseDTO

    @POST("api/fake-call/end")
    suspend fun endFakeCall(
        @Body request: EndFakeCallDTO,
        @Header("Authorization") token: String
    ): Map<String, Any>

////////smartalert///////////
    @GET("api/smart-alert/settings")
    suspend fun getSmartAlertSettings(
        @Header("Authorization") token: String
    ): SmartAlertSettings

    @PUT("api/smart-alert/settings")
    suspend fun updateSmartAlertSettings(
        @Body settings: SmartAlertSettingsUpdate,
        @Header("Authorization") token: String
    ): Map<String, Any>

    // Activity Logging
    @POST("api/smart-alert/activity/log")
    suspend fun logSuspiciousActivity(
        @Body activity: SuspiciousActivityRequest,
        @Header("Authorization") token: String
    ): Map<String, Any>

    @GET("api/smart-alert/activity/log")
    suspend fun getUserActivityLog(
        @Query("recentMinutes") recentMinutes: Int?,
        @Header("Authorization") token: String
    ): List<SuspiciousActivity>

    // Voice Commands
    @POST("api/smart-alert/voice/process")
    suspend fun processVoiceCommand(
        @Body command: VoiceCommandRequest,
        @Header("Authorization") token: String
    ): Map<String, Any>

    // Quick Detection Actions
    @POST("api/smart-alert/detect/shake")
    suspend fun reportPhoneShake(
        @Query("intensity") intensity: Double,
        @Query("latitude") latitude: Double?,
        @Query("longitude") longitude: Double?,
        @Header("Authorization") token: String
    ): Map<String, Any>

    @POST("api/smart-alert/detect/rapid-movement")
    suspend fun reportRapidMovement(
        @Query("speed") speed: Double,
        @Query("latitude") latitude: Double?,
        @Query("longitude") longitude: Double?,
        @Header("Authorization") token: String
    ): Map<String, Any>

    @POST("api/smart-alert/detect/fall")
    suspend fun reportFall(
        @Query("latitude") latitude: Double?,
        @Query("longitude") longitude: Double?,
        @Header("Authorization") token: String
    ): Map<String, Any>

    @POST("api/smart-alert/voice/help")
    suspend fun voiceHelp(
        @Query("latitude") latitude: Double?,
        @Query("longitude") longitude: Double?,
        @Header("Authorization") token: String
    ): Map<String, Any>
}


// Emergency Alert DTOs
data class EmergencyAlertRequest(
    val alertMessage: String?,
    val latitude: Double?,
    val longitude: Double?,
    val locationAddress: String?
)

data class EmergencyAlertResponse(
    val id: Long,
    val alertMessage: String?,
    val latitude: Double?,
    val longitude: Double?,
    val locationAddress: String?,
    val alertStatus: String,
    val createdAt: String,
    val resolvedAt: String?,
    val contactsNotifiedCount: Int?,
    val message: String?
)

// Response DTO for fake call trigger
data class FakeCallResponseDTO(
    val callLogId: Long?,
    val presetId: Long?,
    val callerName: String?,
    val callerPhone: String?,
    val callerPhotoUrl: String?,
    val callType: String?,
    val autoAnswerDelaySeconds: Int?,
    val callDurationSeconds: Int?,
    val ringtoneName: String?,
    val vibrateEnabled: Boolean?,
    val message: String?,
    val instruction: String?
)

// Data models
data class SmartAlertSettings(
    val id: Long? = null,
    val shakeDetectionEnabled: Boolean = true,
    val shakeSensitivity: Double = 0.7,
    val shakeDurationSeconds: Int = 3,
    val runningDetectionEnabled: Boolean = true,
    val runningDurationSeconds: Int = 10,
    val fallDetectionEnabled: Boolean = true,
    val impactDetectionEnabled: Boolean = true,
    val voiceActivationEnabled: Boolean = true,
    val voiceKeywords: String = "help,emergency,police,danger",
    val screamDetectionEnabled: Boolean = false,
    val autoTriggerEnabled: Boolean = false,
    val confirmationDelaySeconds: Int = 10,
    val silentMode: Boolean = false,
    val autoEnableAtNight: Boolean = false,
    val nightStartHour: Int = 22,
    val nightEndHour: Int = 6,
    val autoEnableInDangerZones: Boolean = true
)

data class SmartAlertSettingsUpdate(
    val shakeDetectionEnabled: Boolean? = null,
    val shakeSensitivity: Double? = null,
    val shakeDurationSeconds: Int? = null,
    val runningDetectionEnabled: Boolean? = null,
    val runningDurationSeconds: Int? = null,
    val fallDetectionEnabled: Boolean? = null,
    val impactDetectionEnabled: Boolean? = null,
    val voiceActivationEnabled: Boolean? = null,
    val voiceKeywords: String? = null,
    val screamDetectionEnabled: Boolean? = null,
    val autoTriggerEnabled: Boolean? = null,
    val confirmationDelaySeconds: Int? = null,
    val silentMode: Boolean? = null,
    val autoEnableAtNight: Boolean? = null,
    val nightStartHour: Int? = null,
    val nightEndHour: Int? = null,
    val autoEnableInDangerZones: Boolean? = null
)

data class SuspiciousActivity(
    val id: Long,
    val activityType: String,
    val intensityLevel: Double?,
    val confidenceScore: Double?,
    val latitude: Double?,
    val longitude: Double?,
    val timestamp: String,
    val alertTriggered: Boolean,
    val falsePositive: Boolean
)

data class SuspiciousActivityRequest(
    val activityType: String,
    val intensityLevel: Double,
    val confidenceScore: Double? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val deviceMotionData: String? = null,
    val notes: String? = null
)

data class VoiceCommandRequest(
    val commandText: String,
    val audioUrl: String? = null,
    val commandType: String? = null,
    val confidenceScore: Double? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)

data class SmartAlertAnalysis(
    val shouldTriggerAlert: Boolean,
    val overallRiskScore: Double,
    val riskLevel: String,
    val recommendation: String?,
    val detectedActivities: Map<String, Any>?,
    val recentActivityCount: Int?,
    val requiresImmediateAction: Boolean?
)
