package com.example.womensafetyapp.models

data class Group(
    val id: String = "",
    val title: String = "",
    val members: List<String> = emptyList()
)
