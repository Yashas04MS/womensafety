package com.example.womensafetyapp.network

import com.example.womensafetyapp.network.models.*
import retrofit2.http.*

interface ApiService {

    // LOGIN
    @POST("api/auth/login")
    suspend fun login(
        @Body request: AuthenticationRequestBody
    ): AuthenticationResponseBody

    // REGISTER
    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): AuthenticationResponseBody

    // SEND EMAIL VERIFICATION TOKEN
    @GET("api/auth/send-email-verification-token")
    suspend fun sendEmailVerificationToken(): String

    // VERIFY EMAIL TOKEN
    @PUT("api/auth/validate-email-verification-token")
    suspend fun verifyEmailToken(
        @Query("token") token: String
    ): Map<String, String>

    // SEND PASSWORD RESET TOKEN
    @PUT("api/auth/send-password-reset-token")
    suspend fun sendPasswordResetToken(
        @Query("email") email: String
    ): String

    // RESET PASSWORD
    @PUT("api/auth/reset-password")
    suspend fun resetPassword(
        @Query("email") email: String,
        @Query("newPassword") newPassword: String,
        @Query("token") token: String
    ): String

    // GET PROFILE
    @GET("api/auth/profile")
    suspend fun getProfile(): AuthUser   // <-- FIXED

    // UPDATE PROFILE
    @PUT("api/auth/profile")
    suspend fun updateProfile(
        @Query("firstName") firstName: String?,
        @Query("lastName") lastName: String?,
        @Query("phoneNumber") phoneNumber: String?
    ): Map<String, Any>

}
