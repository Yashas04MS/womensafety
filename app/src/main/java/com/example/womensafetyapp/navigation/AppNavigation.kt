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
import com.example.womensafetyapp.ui.fakeCall.*
import com.example.womensafetyapp.ui.scheduled.ScheduledSharingScreen
import com.example.womensafetyapp.ui.smartalert.SmartAlertScreen

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
    object SmartAlert : Screen("smart_alert")
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

        composable(Screen.VerifyOTP.route) { backStackEntry ->
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
                },
                onNavigateToSmartAlert = {
                    navController.navigate(Screen.SmartAlert.route)
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
            EmergencyContactsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ScheduledSharing.route) {
            ScheduledSharingScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.FakeCallPresets.route) {
            FakeCallPresetsScreen(
                onBack = { navController.popBackStack() },
                onTriggerCall = { presetId ->
                    navController.navigate(Screen.IncomingFakeCall.createRoute(presetId))
                }
            )
        }

        composable(Screen.IncomingFakeCall.route) { backStackEntry ->
            val presetId = backStackEntry.arguments
                ?.getString("presetId")
                ?.toLongOrNull() ?: 0L

            val context = LocalContext.current
            val viewModel: FakeCallViewModel = viewModel(
                factory = FakeCallViewModelFactory(context)
            )

            LaunchedEffect(presetId) {
                if (presetId > 0) {
                    viewModel.triggerFakeCall(presetId)
                }
            }

            val callResponse by viewModel.callResponse.collectAsState()

            callResponse?.let { response ->
                IncomingFakeCallScreen(
                    callResponse = response,
                    onAnswer = {},
                    onDecline = {
                        viewModel.endFakeCall(
                            callLogId = response.callLogId,
                            wasAnswered = false,
                            actualDuration = 0
                        )
                        navController.popBackStack()
                    },
                    onCallEnded = {
                        navController.popBackStack()
                    }
                )
            }
        }

        composable(Screen.SmartAlert.route) {
            SmartAlertScreen( onBack = { navController.popBackStack() }
            )
        }
    }
}
