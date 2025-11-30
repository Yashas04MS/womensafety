package com.example.womensafetyapp.models   // your package name

data class AuthUser(
    val id: String? = null,
    val username: String,
    val email: String,
    val password: String,
    val role: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
