package com.example.womensafetyapp.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.womensafetyapp.network.apiClient
import kotlinx.coroutines.launch

class VerifyOtpViewModel : ViewModel() {

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf("")
    var successMessage by mutableStateOf("")

    fun verifyOtp(
        otp: String,
        token: String,
        onSuccess: () -> Unit
    ) {
        if (otp.length != 6) {
            errorMessage = "Enter valid 6-digit OTP"
            return
        }

        viewModelScope.launch {
            try {
                isLoading = true
                errorMessage = ""

                // Call backend API with token in header
                val response = apiClient.api.verifyEmailToken(
                    token = otp,
                    authToken = "Bearer $token"
                )

                successMessage = response["Message"] ?: "Email verified successfully"
                onSuccess()

            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Invalid OTP"
            } finally {
                isLoading = false
            }
        }
    }

    fun resendOtp(
        token: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                isLoading = true
                errorMessage = ""

                // Call backend to resend OTP
                val response = apiClient.api.sendEmailVerificationToken("Bearer $token")

                successMessage = "OTP sent successfully"
                onSuccess()

            } catch (e: Exception) {
                val error = e.localizedMessage ?: "Failed to resend OTP"
                errorMessage = error
                onError(error)
            } finally {
                isLoading = false
            }
        }
    }
}