//package com.example.womensafetyapp.ui.auth
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.womensafetyapp.network.apiClient
//import com.example.womensafetyapp.network.models.RegisterRequest
//import kotlinx.coroutines.launch
//
//class RegisterViewModel : ViewModel() {
//
//    var errorMessage = ""
//    var successMessage = ""
//    var navigateToVerification: (() -> Unit)? = null
//
//    fun register(
//        name: String,
//        email: String,
//        phone: String,
//        password: String,
//    ) {
//        val names = name.split(" ", limit = 2)
//        val firstName = names.getOrNull(0) ?: ""
//        val lastName = names.getOrNull(1) ?: ""
//
//        viewModelScope.launch {
//            try {
//                val body = RegisterRequest(
//                    email = email,
//                    password = password,
//                    firstName = firstName,
//                    lastName = lastName,
//                    phoneNumber = phone.trim(),
//                    role = "USER"
//                )
//
//                val response = apiClient.api.register(body)
//
//                successMessage = response.message.orEmpty()
//
//                // 👉 Instead of going to Login, go to Verification
//                navigateToVerification?.invoke()
//
//            } catch (e: Exception) {
//                errorMessage = e.localizedMessage ?: "Registration failed"
//            }
//        }
//    }
//}
package com.example.womensafetyapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.womensafetyapp.network.apiClient
import com.example.womensafetyapp.network.models.RegisterRequest
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    var errorMessage = ""
    var successMessage = ""

    // Assigned from UI (Composable)
    var navigateToVerification: ((email: String) -> Unit)? = null

    fun register(
        name: String,
        email: String,
        phone: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        val parts = name.split(" ", limit = 2)
        val first = parts.getOrNull(0) ?: ""
        val last = parts.getOrNull(1) ?: ""

        viewModelScope.launch {
            try {
                val body = RegisterRequest(
                    email = email,
                    password = password,
                    firstName = first,
                    lastName = last,
                    phoneNumber = phone.trim(),
                    role = "USER"
                )

                val response = apiClient.api.register(body)
                successMessage = response.message.orEmpty()

                // 🔥 redirect directly to OTP verification screen
                navigateToVerification?.invoke(email)

            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Registration failed"
            }
        }
    }
}
