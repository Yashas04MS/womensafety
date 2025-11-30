package com.example.womensafetyapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object apiClient {

    private const val BASE_URL = "http://10.0.2.2:8080/"
    // 10.0.2.2 works for emulator → connects to localhost of your Mac

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
