package com.example.womensafetyapp.network.models

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)
