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

    // SEND EMAIL VERIFICATION TOKEN (requires JWT token in header)
    @GET("api/auth/send-email-verification-token")
    suspend fun sendEmailVerificationToken(
        @Header("Authorization") token: String
    ): String

    // VERIFY EMAIL TOKEN (requires JWT token in header)
    @PUT("api/auth/validate-email-verification-token")
    suspend fun verifyEmailToken(
        @Query("token") token: String,
        @Header("Authorization") authToken: String
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

    // GET PROFILE (requires JWT token)
    @GET("api/auth/profile")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): AuthUser

    // UPDATE PROFILE (requires JWT token)
    @PUT("api/auth/profile")
    suspend fun updateProfile(
        @Query("firstName") firstName: String?,
        @Query("lastName") lastName: String?,
        @Query("phoneNumber") phoneNumber: String?,
        @Header("Authorization") token: String
    ): Map<String, Any>
}