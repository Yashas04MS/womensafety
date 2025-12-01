package com.example.womensafetyapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.womensafetyapp.network.apiClient
import com.example.womensafetyapp.network.models.AuthenticationRequestBody
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    fun login(
        email: String,
        password: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = apiClient.api.login(
                    AuthenticationRequestBody(
                        email = email,
                        password = password
                    )
                )

                onSuccess(response.message ?: "Login success")

            } catch (e: Exception) {
                onError(e.message ?: "Something went wrong")
            }
        }
    }
}
