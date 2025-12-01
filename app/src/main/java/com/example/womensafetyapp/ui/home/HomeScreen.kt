package com.example.womensafetyapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color

@Composable
fun HomeScreen(
    onNavigateToSOS: () -> Unit = {},
    onNavigateToContacts: () -> Unit = {},
    onShareLocation: () -> Unit = {},
    onCallPolice: () -> Unit = {},
    onProfile: () -> Unit = {}
) {
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
                    .clickable { onProfile() },
                contentAlignment = Alignment.Center
            ) {
                Text("U", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // 🚨 SOS Button
        Box(
            modifier = Modifier
                .size(220.dp)
                .clip(CircleShape)
                .background(Color(0xFFFF3B30))
                .clickable { onNavigateToSOS() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "SOS",
                color = Color.White,
                fontSize = 60.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Tap SOS for instant emergency help",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = Color(0xFF444444)
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
                onClick = onShareLocation
            )
            QuickActionCard(
                title = "Call\nPolice",
                color = Color(0xFF16A34A),
                onClick = onCallPolice
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
            Text(
                "Manage Emergency Contacts",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Feature Cards
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Safety Features",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
                Spacer(modifier = Modifier.height(12.dp))

                FeatureItem("📱", "Emergency Contacts", "Manage trusted contacts")
                FeatureItem("🚨", "SOS Alert", "One-tap emergency notification")
                FeatureItem("📍", "Live Location", "Real-time location sharing")
                FeatureItem("🔔", "Smart Alerts", "AI-powered danger detection")
            }
        }
    }
}

@Composable
fun QuickActionCard(title: String, color: Color, onClick: () -> Unit) {
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

@Composable
fun FeatureItem(icon: String, title: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            fontSize = 24.sp,
            modifier = Modifier.padding(end = 12.dp)
        )
        Column {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )
            Text(
                text = description,
                fontSize = 14.sp,
                color = Color(0xFF666666)
            )
        }
    }
}