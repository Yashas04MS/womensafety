package com.example.womensafetyapp.ui.auth

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.womensafetyapp.network.apiClient
import com.example.womensafetyapp.network.models.AuthenticationRequestBody
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf("")

    fun login(
        email: String,
        password: String,
        context: Context,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                isLoading = true
                errorMessage = ""

                val response = apiClient.api.login(
                    AuthenticationRequestBody(
                        email = email,
                        password = password
                    )
                )

                // Save token to SharedPreferences
                response.token?.let { token ->
                    saveToken(context, token)
                }

                onSuccess(response.message ?: "Login success")

            } catch (e: Exception) {
                val error = e.message ?: "Something went wrong"
                errorMessage = error
                onError(error)
            } finally {
                isLoading = false
            }
        }
    }

    private fun saveToken(context: Context, token: String) {
        val prefs = context.getSharedPreferences("user_token", Context.MODE_PRIVATE)
        prefs.edit().putString("jwt", token).apply()
    }
}