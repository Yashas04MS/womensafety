package com.example.womensafetyapp.network.models

data class AuthenticationResponseBody(
    val token: String?,
    val message: String? = null,
    val user: AuthUser? = null
)
