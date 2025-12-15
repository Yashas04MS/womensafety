package com.example.womensafetyapp.ui.smartalert

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SmartAlertContent(
    isMonitoring: Boolean,
    lastActivity: String,
    alertLevel: String,
    onToggleMonitoring: () -> Unit,
    onTriggerAlert: () -> Unit
) {
    Column(Modifier.padding(16.dp)) {
        Text("Status: $alertLevel", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("Last Activity: $lastActivity")

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onToggleMonitoring,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isMonitoring) "Stop Monitoring" else "Start Monitoring")
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = onTriggerAlert,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("Trigger Alert", color = Color.White)
        }
    }
}

@Composable
fun AlertTriggeredOverlay() {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center
    ) {
        Card(shape = RoundedCornerShape(20.dp)) {
            Column(
                Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.Warning, null, tint = Color.Red, modifier = Modifier.size(48.dp))
                Text("ALERT TRIGGERED", fontWeight = FontWeight.Bold)
            }
        }
    }
}
