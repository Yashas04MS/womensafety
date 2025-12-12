package com.example.womensafetyapp.ui.emergency

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SOSEmergencyScreen(
    viewModel: SOSEmergencyViewModel = viewModel(),
    onNavigateToContacts: () -> Unit = {}
) {
    val context = LocalContext.current
    val alertState by viewModel.alertState.collectAsState()
    val isTriggering by viewModel.isTriggering.collectAsState()
    val countdown by viewModel.countdown.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    var showConfirmDialog by remember { mutableStateOf(false) }
    var locationPermissionGranted by remember { mutableStateOf(false) }

    // Location permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        locationPermissionGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (locationPermissionGranted && showConfirmDialog) {
            // User granted permission and dialog is still showing
            // Trigger will happen when they click confirm
        }
    }

    // Check location permission on start
    LaunchedEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        locationPermissionGranted = hasPermission
    }

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
                text = "Emergency SOS 🚨",
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

        Spacer(modifier = Modifier.height(20.dp))

        // Success Message
        if (successMessage.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFD1FAE5)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = successMessage,
                        fontSize = 15.sp,
                        color = Color(0xFF065F46),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Error Message
        if (errorMessage.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFEBEE)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        tint = Color(0xFFFF3B30),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = errorMessage,
                            fontSize = 15.sp,
                            color = Color(0xFFB91C1C),
                            fontWeight = FontWeight.Medium
                        )

                        // Show "Add Contacts" button if no contacts error
                        if (errorMessage.contains("emergency contact", ignoreCase = true)) {
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(
                                onClick = onNavigateToContacts,
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = Color(0xFFFF3B30)
                                )
                            ) {
                                Text("Add Emergency Contacts →", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Location status indicator
        if (!locationPermissionGranted && errorMessage.isEmpty() && successMessage.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF3CD)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Location permission needed for accurate alerts",
                        fontSize = 13.sp,
                        color = Color(0xFF92400E)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

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
                        // Check location permission before showing dialog
                        if (!locationPermissionGranted) {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
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
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$countdown",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Sending alert...",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
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
                        Text("CANCEL", color = Color(0xFFFF3B30), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Status text
        Text(
            text = when {
                isTriggering -> "🚨 Triggering emergency alert..."
                successMessage.isNotEmpty() -> ""
                errorMessage.isNotEmpty() -> ""
                else -> "Tap SOS button for instant emergency help"
            },
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = when {
                isTriggering -> Color(0xFFFF3B30)
                else -> Color(0xFF666666)
            },
            fontWeight = if (isTriggering) FontWeight.Bold else FontWeight.Normal
        )

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
            icon = {
                Text("🚨", fontSize = 48.sp)
            },
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
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("📍 Your current location", fontSize = 14.sp)
                    Text("💬 Emergency message", fontSize = 14.sp)
                    Text("🏥 Nearby police & hospitals", fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    if (!locationPermissionGranted) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFFF3CD)
                            )
                        ) {
                            Text(
                                "⚠️ Location unavailable - alert will be sent without location",
                                fontSize = 12.sp,
                                color = Color(0xFF92400E),
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Text(
                        "Alert will be sent in 5 seconds unless you cancel.",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
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
                    Text("TRIGGER SOS", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancel", color = Color(0xFF666666))
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