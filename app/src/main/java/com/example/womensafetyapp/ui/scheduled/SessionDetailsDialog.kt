package com.example.womensafetyapp.ui.scheduled

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SessionDetailsDialog(
    session: ScheduledSession,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                session.sessionName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Status
                StatusChip(status = session.status)

                Divider()

                // Time Details
                DetailRow(
                    icon = Icons.Default.Schedule,
                    label = "Start Time",
                    value = formatDateTime(session.startTime)
                )

                DetailRow(
                    icon = Icons.Default.Timer,
                    label = "Duration",
                    value = "${session.durationMinutes} minutes"
                )

                DetailRow(
                    icon = Icons.Default.EventAvailable,
                    label = "End Time",
                    value = formatDateTime(session.endTime)
                )

                // Destination
                if (session.destinationAddress != null) {
                    Divider()
                    DetailRow(
                        icon = Icons.Default.LocationOn,
                        label = "Destination",
                        value = session.destinationAddress
                    )
                }

                Divider()

                // Notification Settings
                Text(
                    "Notifications",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                NotificationStatus(
                    icon = Icons.Default.PlayArrow,
                    label = "On Start",
                    enabled = session.notifyContactsOnStart
                )

                NotificationStatus(
                    icon = Icons.Default.CheckCircle,
                    label = "On Arrival",
                    enabled = session.notifyContactsOnArrival
                )

                // Additional Info
                if (session.status == "ACTIVE") {
                    Divider()
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFD1FAE5)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFF10B981)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Your emergency contacts are being notified with live location updates",
                                fontSize = 13.sp,
                                color = Color(0xFF065F46)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color(0xFF666666),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                label,
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun NotificationStatus(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    enabled: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (enabled) Color(0xFF10B981) else Color.Gray,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                label,
                fontSize = 14.sp,
                color = if (enabled) Color.Black else Color.Gray
            )
        }

        if (enabled) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                tint = Color(0xFF10B981),
                modifier = Modifier.size(18.dp)
            )
        } else {
            Icon(
                Icons.Default.Close,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
