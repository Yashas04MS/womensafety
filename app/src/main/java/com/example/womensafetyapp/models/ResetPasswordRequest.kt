package com.example.womensafetyapp.network.models

data class ResetPasswordRequest(
    val email: String,
    val newPassword: String
)
