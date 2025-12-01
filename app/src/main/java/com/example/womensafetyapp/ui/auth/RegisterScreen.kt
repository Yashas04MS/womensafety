package com.example.womensafetyapp.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.womensafetyapp.R

@Composable
fun RegisterScreen(navController: NavController) {

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val vm: RegisterViewModel = viewModel()

    Box(modifier = Modifier.fillMaxSize()) {

        // Blurred background
        Image(
            painter = painterResource(id = R.drawable.login_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .blur(20.dp)
        )

        // Centered card
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            Column(
                modifier = Modifier
                    .width(330.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                Color(0x60FFFFFF),
                                Color(0x60FFFFFF)
                            )
                        ),
                        shape = RoundedCornerShape(26.dp)
                    )
                    .padding(26.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Icon
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(Color(0xAA1A122D)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(34.dp)
                    )
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    "Create Account",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(Modifier.height(20.dp))

                // Input fields
                RegisterInputField(
                    label = "Full Name",
                    value = name,
                    onValueChange = { name = it }
                )

                Spacer(Modifier.height(14.dp))

                RegisterInputField(
                    label = "Email",
                    value = email,
                    onValueChange = { email = it }
                )

                Spacer(Modifier.height(14.dp))

                RegisterInputField(
                    label = "Phone Number",
                    value = phone,
                    onValueChange = { phone = it }
                )

                Spacer(Modifier.height(14.dp))

                RegisterInputField(
                    label = "Password",
                    value = password,
                    onValueChange = { password = it }
                )

                Spacer(Modifier.height(14.dp))

                RegisterInputField(
                    label = "Confirm Password",
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it }
                )

                Spacer(Modifier.height(22.dp))

                // Register button
                Button(
                    onClick = {
                        if (password != confirmPassword) {
                            vm.errorMessage = "Passwords do not match"
                            return@Button
                        }

                        vm.register(
                            name = name,
                            email = email,
                            phone = phone,
                            password = password
                        ) { userEmail, jwtToken ->
                            // Navigate to OTP verification with email and token
                            navController.navigate("verify_otp/$userEmail/$jwtToken")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xCDFF7B07),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(18.dp),
                    enabled = !vm.isLoading
                ) {
                    if (vm.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Text("Register")
                    }
                }

                Spacer(Modifier.height(10.dp))

                if (vm.errorMessage.isNotEmpty()) {
                    Text(vm.errorMessage, color = Color.Red)
                }

                Spacer(Modifier.height(12.dp))

                TextButton(
                    onClick = { navController.navigate("login") }
                ) {
                    Text("Already have an account? Login", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun RegisterInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.White) },
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color.White,
            cursorColor = Color.White,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        )
    )
}