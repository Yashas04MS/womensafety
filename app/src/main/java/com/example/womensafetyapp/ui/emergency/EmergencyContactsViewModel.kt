package com.example.womensafetyapp.ui.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onNavigateToSOS: () -> Unit = {},
    onNavigateToContacts: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var currentLocation by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var showLocationDialog by remember { mutableStateOf(false) }
    var locationPermissionGranted by remember { mutableStateOf(false) }

    // Location permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        locationPermissionGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (locationPermissionGranted) {
            getCurrentLocation(context) { lat, lon ->
                currentLocation = Pair(lat, lon)
            }
        }
    }

    // Check location permission on start
    LaunchedEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            locationPermissionGranted = true
            getCurrentLocation(context) { lat, lon ->
                currentLocation = Pair(lat, lon)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF2F2))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with greeting
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Stay Safe",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                    Text(
                        text = "We're here to protect you",
                        fontSize = 14.sp,
                        color = Color(0xFF666666)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF3B30)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // SOS Emergency Button
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clickable { onNavigateToSOS() },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFF3B30)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🚨",
                            fontSize = 64.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "EMERGENCY SOS",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Tap to alert emergency contacts",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Action Buttons Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionButton(
                    modifier = Modifier.weight(1f),
                    title = "Emergency\nContacts",
                    icon = Icons.Default.Contacts,
                    color = Color(0xFF2563EB),
                    onClick = onNavigateToContacts
                )

                QuickActionButton(
                    modifier = Modifier.weight(1f),
                    title = "Call\nPolice",
                    icon = Icons.Default.Phone,
                    color = Color(0xFF16A34A),
                    onClick = {
                        callPolice(context)
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionButton(
                    modifier = Modifier.weight(1f),
                    title = "Share\nLocation",
                    icon = Icons.Default.LocationOn,
                    color = Color(0xFFEA580C),
                    onClick = {
                        if (locationPermissionGranted) {
                            if (currentLocation != null) {
                                showLocationDialog = true
                            } else {
                                getCurrentLocation(context) { lat, lon ->
                                    currentLocation = Pair(lat, lon)
                                    showLocationDialog = true
                                }
                            }
                        } else {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    }
                )

                QuickActionButton(
                    modifier = Modifier.weight(1f),
                    title = "Live\nTracking",
                    icon = Icons.Default.MyLocation,
                    color = Color(0xFF7C3AED),
                    onClick = {
                        // TODO: Implement live tracking
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Safety Status Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = Color(0xFF16A34A),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Safety Status",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "All systems active",
                                fontSize = 14.sp,
                                color = Color(0xFF16A34A)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Divider()

                    Spacer(modifier = Modifier.height(16.dp))

                    SafetyFeatureItem(
                        icon = Icons.Default.ContactEmergency,
                        title = "Emergency Contacts",
                        description = "Manage trusted contacts"
                    )

                    SafetyFeatureItem(
                        icon = Icons.Default.Notifications,
                        title = "Smart Alerts",
                        description = "AI-powered detection"
                    )

                    SafetyFeatureItem(
                        icon = Icons.Default.Map,
                        title = "Location Sharing",
                        description = "Real-time tracking"
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Emergency Numbers Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF3CD)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Emergency Numbers",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    EmergencyNumberItem("Police", "100")
                    EmergencyNumberItem("Ambulance", "108")
                    EmergencyNumberItem("Women Helpline", "1091")
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    // Location Share Dialog
    if (showLocationDialog && currentLocation != null) {
        LocationShareDialog(
            location = currentLocation!!,
            onDismiss = { showLocationDialog = false },
            onShare = { shareLocation(context, currentLocation!!) }
        )
    }
}

@Composable
fun QuickActionButton(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(110.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun SafetyFeatureItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF666666),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = description,
                fontSize = 13.sp,
                color = Color(0xFF666666)
            )
        }
    }
}

@Composable
fun EmergencyNumberItem(name: String, number: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = number,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFF3B30)
        )
    }
}

@Composable
fun LocationShareDialog(
    location: Pair<Double, Double>,
    onDismiss: () -> Unit,
    onShare: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Share Your Location",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text("Your current location:")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "📍 ${String.format("%.4f", location.first)}, ${String.format("%.4f", location.second)}",
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onShare()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2563EB)
                )
            ) {
                Text("Share")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Helper Functions
private fun getCurrentLocation(
    context: Context,
    onLocationReceived: (Double, Double) -> Unit
) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    try {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                onLocationReceived(it.latitude, it.longitude)
            }
        }
    } catch (e: SecurityException) {
        e.printStackTrace()
    }
}

private fun callPolice(context: Context) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:100")
    }
    context.startActivity(intent)
}

private fun shareLocation(context: Context, location: Pair<Double, Double>) {
    val googleMapsUrl = "https://maps.google.com/?q=${location.first},${location.second}"
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "My Location - Emergency")
        putExtra(Intent.EXTRA_TEXT, "I'm sharing my location with you: $googleMapsUrl")
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share location via"))
}