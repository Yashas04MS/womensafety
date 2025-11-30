package com.example.womensafetyapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.womensafetyapp.ui.auth.*
import com.example.womensafetyapp.ui.home.HomeScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Forgot : Screen("forgot")
    object VerifyOTP : Screen("verify_otp")
    object ResetPassword : Screen("reset_pass")
    object Home : Screen("home")
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {

        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.Register.route) { RegisterScreen(navController) }
        composable(Screen.Forgot.route) { ForgotPasswordScreen(navController) }
        composable(Screen.VerifyOTP.route) { VerifyOTPScreen(navController) }
        composable(Screen.ResetPassword.route) { ResetPasswordScreen(navController) }
        composable(Screen.Home.route) {
            HomeScreen()
        }
    }
}
