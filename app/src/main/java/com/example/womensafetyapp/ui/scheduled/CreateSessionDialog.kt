package com.example.womensafetyapp.ui.scheduled

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSessionDialog(
    onDismiss: () -> Unit,
    onCreate: (CreateSessionData) -> Unit
) {
    var sessionName by remember { mutableStateOf("") }
    var selectedDateTime by remember { mutableStateOf<LocalDateTime?>(null) }
    var durationMinutes by remember { mutableStateOf(60) }
    var destinationAddress by remember { mutableStateOf("") }
    var notifyOnStart by remember { mutableStateOf(true) }
    var notifyOnArrival by remember { mutableStateOf(true) }
    var notifyOnDelay by remember { mutableStateOf(true) }
    var autoAlert by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Schedule Location Sharing",
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
                // Session Name
                OutlinedTextField(
                    value = sessionName,
                    onValueChange = { sessionName = it },
                    label = { Text("Session Name") },
                    placeholder = { Text("e.g., First Date, Uber Ride") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Default.Event, contentDescription = null)
                    }
                )

                // Start Time
                OutlinedCard(
                    onClick = {
                        val now = Calendar.getInstance()
                        DatePickerDialog(
                            context,
                            { _, year, month, day ->
                                TimePickerDialog(
                                    context,
                                    { _, hour, minute ->
                                        selectedDateTime = LocalDateTime.of(
                                            year, month + 1, day, hour, minute
                                        )
                                    },
                                    now.get(Calendar.HOUR_OF_DAY),
                                    now.get(Calendar.MINUTE),
                                    false
                                ).show()
                            },
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)
                        ).apply {
                            datePicker.minDate = System.currentTimeMillis()
                        }.show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            tint = Color(0xFF2563EB)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                "Start Time",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Text(
                                selectedDateTime?.format(
                                    DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a")
                                ) ?: "Select date and time",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Duration
                Column {
                    Text(
                        "Duration: $durationMinutes minutes",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Slider(
                        value = durationMinutes.toFloat(),
                        onValueChange = { durationMinutes = it.toInt() },
                        valueRange = 15f..480f,
                        steps = 30,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF2563EB),
                            activeTrackColor = Color(0xFF2563EB)
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("15 min", fontSize = 12.sp, color = Color.Gray)
                        Text("8 hours", fontSize = 12.sp, color = Color.Gray)
                    }
                }

                // Destination (Optional)
                OutlinedTextField(
                    value = destinationAddress,
                    onValueChange = { destinationAddress = it },
                    label = { Text("Destination (Optional)") },
                    placeholder = { Text("e.g., Coffee Shop on Main St") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Default.LocationOn, contentDescription = null)
                    }
                )

                Divider()

                // Notification Settings
                Text(
                    "Notification Settings",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Notify contacts when starting", fontSize = 14.sp)
                    Switch(
                        checked = notifyOnStart,
                        onCheckedChange = { notifyOnStart = it }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Notify contacts on arrival", fontSize = 14.sp)
                    Switch(
                        checked = notifyOnArrival,
                        onCheckedChange = { notifyOnArrival = it }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Notify if delayed", fontSize = 14.sp)
                    Switch(
                        checked = notifyOnDelay,
                        onCheckedChange = { notifyOnDelay = it }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Auto-alert if not arrived", fontSize = 14.sp)
                        Text(
                            "Trigger SOS if destination not reached",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    Switch(
                        checked = autoAlert,
                        onCheckedChange = { autoAlert = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (sessionName.isNotBlank() && selectedDateTime != null) {
                        onCreate(
                            CreateSessionData(
                                sessionName = sessionName,
                                startTime = selectedDateTime!!,
                                durationMinutes = durationMinutes,
                                destinationAddress = destinationAddress.takeIf { it.isNotBlank() },
                                notifyContactsOnStart = notifyOnStart,
                                notifyContactsOnArrival = notifyOnArrival,
                                notifyContactsOnDelay = notifyOnDelay,
                                autoAlertIfNotArrived = autoAlert
                            )
                        )
                    }
                },
                enabled = sessionName.isNotBlank() && selectedDateTime != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2563EB)
                )
            ) {
                Text("Create Session")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

data class CreateSessionData(
    val sessionName: String,
    val startTime: LocalDateTime,
    val durationMinutes: Int,
    val destinationAddress: String?,
    val notifyContactsOnStart: Boolean,
    val notifyContactsOnArrival: Boolean,
    val notifyContactsOnDelay: Boolean,
    val autoAlertIfNotArrived: Boolean
)
