package com.example.womensafetyapp.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.womensafetyapp.network.apiClient
import com.example.womensafetyapp.network.models.RegisterRequest
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    var errorMessage by mutableStateOf("")
    var successMessage by mutableStateOf("")
    var isLoading by mutableStateOf(false)

    // Store JWT token after registration
    var jwtToken by mutableStateOf("")

    fun register(
        name: String,
        email: String,
        phone: String,
        password: String,
        onSuccess: (String, String) -> Unit // Pass email and token
    ) {
        val parts = name.split(" ", limit = 2)
        val first = parts.getOrNull(0) ?: ""
        val last = parts.getOrNull(1) ?: ""

        viewModelScope.launch {
            try {
                isLoading = true
                errorMessage = ""

                val body = RegisterRequest(
                    email = email,
                    password = password,
                    firstName = first,
                    lastName = last,
                    phoneNumber = phone.trim(),
                    role = "USER"
                )

                val response = apiClient.api.register(body)

                // Store token
                jwtToken = response.token ?: ""
                successMessage = response.message ?: "Registration successful"

                // Navigate to OTP verification with email and token
                onSuccess(email, jwtToken)

            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Registration failed"
            } finally {
                isLoading = false
            }
        }
    }
}