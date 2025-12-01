////package com.example.womensafetyapp.ui.auth
////
////import androidx.lifecycle.ViewModel
////import androidx.lifecycle.viewModelScope
////import com.example.womensafetyapp.network.apiClient
////import com.example.womensafetyapp.network.models.OTPVerifyRequest
////import kotlinx.coroutines.launch
////
////class VerifyOtpViewModel : ViewModel() {
////
////    var errorMessage = ""
////
////    fun verifyOtp(
////        email: String,
////        otp: String,
////        onSuccess: () -> Unit
////    ) {
////        viewModelScope.launch {
////            try {
////                val body = OTPVerifyRequest(
////                    email = email,
////                    otp = otp
////                )
////
////                val response = apiClient.api.verifyEmailToken(body)
////
////                if (response.success == true) {
////                    onSuccess()
////                } else {
////                    errorMessage = response.message ?: "Invalid OTP"
////                }
////
////            } catch (e: Exception) {
////                errorMessage = e.localizedMessage ?: "Verification failed"
////            }
////        }
////    }
////}
//package com.example.womensafetyapp.ui.auth
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.womensafetyapp.network.apiClient
//import com.example.womensafetyapp.network.models.OTPVerifyRequest
//import kotlinx.coroutines.launch
//
//class VerifyOtpViewModel : ViewModel() {
//
//    var errorMessage = ""
//
//    fun verifyOtp(
//        email: String,
//        otp: String,
//        onSuccess: () -> Unit
//    ) {
//        viewModelScope.launch {
//            try {
//                val body = OTPVerifyRequest(
//                    email = email,
//                    otp = otp
//                )
//
//                val response = apiClient.api.verifyEmailToken(body)
//
//                if (response.success == true) {
//                    onSuccess()
//                } else {
//                    errorMessage = response.message ?: "OTP verification failed"
//                }
//
//
//            } catch (e: Exception) {
//                errorMessage = e.localizedMessage ?: "Verification failed"
//            }
//        }
//    }
//}

package com.example.womensafetyapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.womensafetyapp.network.apiClient
import com.example.womensafetyapp.network.models.OTPVerifyRequest
import kotlinx.coroutines.launch

class VerifyOtpViewModel : ViewModel() {

    var isLoading = false
    var errorMessage = ""
    var successMessage = ""

    var navigateToHome: (() -> Unit)? = null

    fun verifyOtp(email: String, otp: String) {
        if (otp.length != 6) {
            errorMessage = "Enter valid 6-digit OTP"
            return
        }

        viewModelScope.launch {
            try {
                isLoading = true

                val request = OTPVerifyRequest(
                    email = email,
                    otp = otp
                )

                val response = apiClient.api.verifyOtp(request)

                successMessage = response.message ?: "Verified"
                navigateToHome?.invoke()

            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Invalid OTP"
            } finally {
                isLoading = false
            }
        }
    }
}
