//package com.example.womensafetyapp.ui.auth
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavHostController
//
////@Composable
////fun VerifyOTPScreen(navController: NavHostController, email: String) {
////
////    var otp by remember { mutableStateOf("") }
////
////    Column(
////        modifier = Modifier
////            .fillMaxSize()
////            .padding(24.dp)
////    ) {
////
////        Text(text = "Verify OTP", style = MaterialTheme.typography.headlineMedium)
////
////        Spacer(modifier = Modifier.height(16.dp))
////
////        OutlinedTextField(
////            value = otp,
////            onValueChange = { otp = it },
////            label = { Text("Enter OTP") },
////            modifier = Modifier.fillMaxWidth()
////        )
////
////        Spacer(modifier = Modifier.height(24.dp))
////
////        Button(
////            onClick = {
////                navController.navigate("reset_pass")
////            },
////            modifier = Modifier.fillMaxWidth()
////        ) {
////            Text("Verify OTP")
////        }
////
////        Spacer(modifier = Modifier.height(12.dp))
////
////        TextButton(onClick = { navController.popBackStack() }) {
////            Text("Back")
////        }
////    }
////}
//@Composable
//fun VerifyOTPScreen(navController: NavHostController) {
//
//    var otp by remember { mutableStateOf("") }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(24.dp)
//    ) {
//
//        Text(text = "Verify OTP", style = MaterialTheme.typography.headlineMedium)
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        OutlinedTextField(
//            value = otp,
//            onValueChange = { otp = it },
//            label = { Text("Enter OTP") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Spacer(modifier = Modifier.height(24.dp))
//
//        Button(
//            onClick = {
//                navController.navigate("reset_pass")
//            },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text("Verify OTP")
//        }
//
//        Spacer(modifier = Modifier.height(12.dp))
//
//        TextButton(onClick = { navController.popBackStack() }) {
//            Text("Back")
//        }
//    }
//}


package com.example.womensafetyapp.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
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
import androidx.navigation.NavController
import com.example.womensafetyapp.R
import androidx.compose.foundation.shape.RoundedCornerShape


@Composable
fun VerifyOtpScreen(
    navController: NavController,
    email: String           // Pass email from register screen
) {
    val vm = remember { VerifyOtpViewModel() }

    var otp by remember { mutableStateOf("") }

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

        // Center content
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
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(34.dp)
                    )
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    "Verify OTP",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    "Enter the 6-digit code sent to",
                    fontSize = 14.sp,
                    color = Color.White
                )

                Text(
                    email,
                    fontSize = 14.sp,
                    color = Color(0xFFFFC66C)
                )

                Spacer(Modifier.height(20.dp))

                OutlinedTextField(
                    value = otp,
                    onValueChange = { if (it.length <= 6) otp = it },
                    label = { Text("OTP", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.White,
                        cursorColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(Modifier.height(22.dp))

                // VERIFY BUTTON
                Button(
                    onClick = {
                        vm.verifyOtp(
                            email = email,
                            otp = otp,
                            onSuccess = {
                                navController.navigate("home") {
                                    popUpTo("register") { inclusive = true }
                                }
                            }
                        )
                    }
                )
            },
                modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xCDFF7B07),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text("Verify")
                }

                Spacer(Modifier.height(10.dp))

                if (vm.errorMessage.isNotEmpty()) {
                    Text(vm.errorMessage, color = Color.Red)
                }
            }
        }
    }
}

