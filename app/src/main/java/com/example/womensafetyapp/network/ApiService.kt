package com.example.womensafetyapp.network

import com.example.womensafetyapp.network.models.*
import com.example.womensafetyapp.ui.emergency.EmergencyContact
import com.example.womensafetyapp.ui.emergency.EmergencyContactDTO
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

    @POST("api/emergency/contacts")
    suspend fun addEmergencyContact(
        @Header("Authorization") token: String,
        contact: EmergencyContactDTO
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