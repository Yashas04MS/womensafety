package com.example.womensafetyapp.network.models

data class OTPVerifyRequest(
    val email: String,
    val otp: String
)
