package com.example.womensafetyapp.ui.emergency

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay

@Composable
fun SOSEmergencyScreen(
    viewModel: SOSEmergencyViewModel = viewModel(),
    onNavigateToContacts: () -> Unit = {}
) {
    val alertState by viewModel.alertState.collectAsState()
    val isTriggering by viewModel.isTriggering.collectAsState()
    val countdown by viewModel.countdown.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var showConfirmDialog by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF2F2))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Greeting + Profile
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Welcome Back 👋",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFB7185))
                    .clickable { /* Navigate to profile */ },
                contentAlignment = Alignment.Center
            ) {
                Text("U", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // SOS Button with countdown overlay
        Box(
            contentAlignment = Alignment.Center
        ) {
            // Main SOS Button
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .clip(CircleShape)
                    .background(
                        if (isTriggering) Color(0xFFFF6B6B) else Color(0xFFFF3B30)
                    )
                    .clickable(enabled = !isTriggering) {
                        showConfirmDialog = true
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "SOS",
                        color = Color.White,
                        fontSize = 60.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    if (isTriggering && countdown > 0) {
                        Text(
                            text = "Triggering in $countdown...",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Cancel overlay if triggering
            if (isTriggering) {
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .padding(20.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Button(
                        onClick = {
                            viewModel.cancelAlert()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("CANCEL", color = Color(0xFFFF3B30))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Status text
        Text(
            text = when {
                isTriggering -> "🚨 Alert triggering! Tap CANCEL to stop"
                alertState == "ACTIVE" -> "✅ Alert sent to emergency contacts"
                else -> "Tap SOS for instant emergency help"
            },
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = when {
                isTriggering -> Color(0xFFFF3B30)
                alertState == "ACTIVE" -> Color(0xFF16A34A)
                else -> Color(0xFF444444)
            },
            fontWeight = if (isTriggering) FontWeight.Bold else FontWeight.Normal
        )

        // Error message
        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFEBEE)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = errorMessage,
                    color = Color(0xFFFF3B30),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(45.dp))

        // Quick Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            QuickActionCard(
                title = "Share\nLocation",
                color = Color(0xFF2563EB),
                onClick = { /* Share location */ }
            )
            QuickActionCard(
                title = "Call\nPolice",
                color = Color(0xFF16A34A),
                onClick = { /* Call police */ }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Emergency Contacts Button
        OutlinedButton(
            onClick = onNavigateToContacts,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFFFF3B30)
            )
        ) {
            Text("Manage Emergency Contacts")
        }
    }

    // Confirmation Dialog
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = {
                Text(
                    "Trigger SOS Alert?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            text = {
                Column {
                    Text(
                        "This will immediately notify all your emergency contacts with:",
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("• Your current location")
                    Text("• Emergency message")
                    Text("• Nearby police & hospitals")
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "Alert will be sent in 5 seconds unless you cancel.",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        viewModel.triggerSOSAlert()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF3B30)
                    )
                ) {
                    Text("TRIGGER SOS")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun QuickActionCard(
    title: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(150.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(color)
            .clickable { onClick() }
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}