package com.example.womensafetyapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.womensafetyapp.ui.auth.*
import com.example.womensafetyapp.ui.home.HomeScreen
import com.example.womensafetyapp.ui.emergency.EmergencyContactsScreen
import com.example.womensafetyapp.ui.emergency.SOSEmergencyScreen
import com.example.womensafetyapp.ui.fakeCall.FakeCallPresetsScreen
import com.example.womensafetyapp.ui.fakeCall.FakeCallViewModel
import com.example.womensafetyapp.ui.fakeCall.FakeCallViewModelFactory
import com.example.womensafetyapp.ui.scheduled.ScheduledSharingScreen
import com.example.womensafetyapp.ui.fakeCall.IncomingFakeCallScreen

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
    object FakeCallPresets : Screen("fake_call_presets")
    object IncomingFakeCall : Screen("incoming_fake_call/{presetId}") {
        fun createRoute(presetId: Long) = "incoming_fake_call/$presetId"
    }
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
                onNavigateToScheduledSharing = {
                    navController.navigate(Screen.ScheduledSharing.route)
                },
                onNavigateToFakeCall = {
                    navController.navigate(Screen.FakeCallPresets.route)
                }
            )
        }

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

        composable(Screen.FakeCallPresets.route) {
            FakeCallPresetsScreen(
                onBack = { navController.popBackStack() },
                onTriggerCall = { presetId ->
                    // Navigate with preset ID
                    navController.navigate(Screen.IncomingFakeCall.createRoute(presetId))
                }
            )
        }

        composable("incoming_fake_call/{presetId}") { backStackEntry ->
            val presetId = backStackEntry.arguments?.getString("presetId")?.toLongOrNull() ?: 0L
            val context = LocalContext.current
            val viewModel: FakeCallViewModel = viewModel(
                factory = FakeCallViewModelFactory(context)
            )

            // Trigger the fake call when entering this screen
            LaunchedEffect(presetId) {
                if (presetId > 0) {
                    viewModel.triggerFakeCall(presetId)
                }
            }

            val callResponse by viewModel.callResponse.collectAsState()

            // Show incoming call screen when response is available
            callResponse?.let { response ->
                IncomingFakeCallScreen(
                    callResponse = response,
                    onAnswer = {
                        // Handle answer - call is now active
                    },
                    onDecline = {
                        // End the call as declined
                        viewModel.endFakeCall(
                            callLogId = response.callLogId,
                            wasAnswered = false,
                            actualDuration = 0
                        )
                        navController.popBackStack()
                    },
                    onCallEnded = {
                        // Calculate duration and end call
                        // For now, just navigate back
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}