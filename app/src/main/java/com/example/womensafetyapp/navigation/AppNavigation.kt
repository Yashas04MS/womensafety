package com.example.womensafetyapp.navigation

import androidx.compose.runtime.Composable
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
    object IncomingFakeCall : Screen("incoming_fake_call/{callLogId}") {
        fun createRoute(callLogId: Long) = "incoming_fake_call/$callLogId"
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
                onNavigateToScheduledSharing = { // ADD THIS BLOCK
                    navController.navigate(Screen.ScheduledSharing.route)
                },
                onNavigateToFakeCall ={
                    navController.navigate(Screen.FakeCallPresets.route)

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

        composable(Screen.FakeCallPresets.route) {
            FakeCallPresetsScreen(
                onBack = { navController.popBackStack() },
                onTriggerCall = { callLogId ->
                    navController.navigate(Screen.IncomingFakeCall.createRoute(callLogId))
                }
            )
        }

        composable("incoming_fake_call/{callLogId}") { backStackEntry ->
            val callLogId = backStackEntry.arguments?.getString("callLogId")?.toLongOrNull() ?: 0L
            val context = LocalContext.current
            val viewModel: FakeCallViewModel = viewModel(
                factory = FakeCallViewModelFactory(context)
            )
            val callResponse by viewModel.callResponse.collectAsState()

            callResponse?.let { response ->
                IncomingFakeCallScreen(
                    callResponse = response,
                    onAnswer = { /* Handle answer */ },
                    onDecline = {
                        viewModel.endFakeCall(callLogId, false, 0)
                        navController.popBackStack()
                    },
                    onCallEnded = {
                        // Calculate actual duration and end call
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}