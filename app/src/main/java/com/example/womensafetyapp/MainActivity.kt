package com.example.womensafetyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.womensafetyapp.navigation.AppNavigation
import com.example.womensafetyapp.ui.theme.WomenSafetyAppTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WomenSafetyAppTheme {
                val navController = rememberNavController()
                AppNavigation(navController)
            }
        }
    }
}