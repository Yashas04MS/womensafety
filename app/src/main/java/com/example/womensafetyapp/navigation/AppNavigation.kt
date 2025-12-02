package com.example.womensafetyapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.womensafetyapp.ui.auth.*
import com.example.womensafetyapp.ui.home.HomeScreen
import com.example.womensafetyapp.ui.emergency.EmergencyContactsScreen
import com.example.womensafetyapp.ui.emergency.SOSEmergencyScreen
import com.example.womensafetyapp.ui.scheduled.ScheduledSharingScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Forgot : Screen("forgot")
    object VerifyOTP : Screen("verify_otp/{email}/{token}") {
        fun createRoute(email: String, token: String) = "verify_otp/$email/$token"
    }
    object ResetPassword : Screen("reset_pass")
    object Home : Screen("home")
    object EmergencyContacts : Screen("emergency_contacts")
    object SOSEmergency : Screen("sos_emergency")

    object ScheduledSharing : Screen("scheduled_sharing")
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {

        composable(Screen.Login.route) {
            LoginScreen(navController)
        }

        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }

        composable(Screen.Forgot.route) {
            ForgotPasswordScreen(navController)
        }

        composable("verify_otp/{email}/{token}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val token = backStackEntry.arguments?.getString("token") ?: ""
            VerifyOtpScreen(navController, email, token)
        }

        composable(Screen.ResetPassword.route) {
            ResetPasswordScreen(navController)
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToSOS = {
                    navController.navigate(Screen.SOSEmergency.route)
                },
                onNavigateToContacts = {
                    navController.navigate(Screen.EmergencyContacts.route)
                },
                onNavigateToScheduledSharing = { // ADD THIS BLOCK
                    navController.navigate(Screen.ScheduledSharing.route)
                }
            )
        }

        // New Emergency Features
        composable(Screen.SOSEmergency.route) {
            SOSEmergencyScreen(
                onNavigateToContacts = {
                    navController.navigate(Screen.EmergencyContacts.route)
                }
            )
        }

        composable(Screen.EmergencyContacts.route) {
            EmergencyContactsScreen()
        }

        composable(Screen.ScheduledSharing.route) {
            ScheduledSharingScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}