package com.example.womensafetyapp.network.models

data class RegisterRequest(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val role: String = "USER"
)

