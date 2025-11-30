package com.example.womensafetyapp.ui.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.womensafetyapp.network.models.LoginRequest
import com.example.womensafetyapp.network.apiClient
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
                    LoginRequest(email, password)
                )

                onSuccess(response.token ?: "")

            } catch (e: Exception) {
                onError(e.message ?: "Something went wrong")
            }
        }
    }

    fun saveToken(context: Context, token: String) {
        val shared = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
        shared.edit()
            .putString("token", token)
            .apply()
    }
}
