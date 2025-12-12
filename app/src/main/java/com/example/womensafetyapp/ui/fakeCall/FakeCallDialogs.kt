package com.example.womensafetyapp.ui.fakeCall

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPresetDialog(
    preset: FakeCallPreset?,
    onDismiss: () -> Unit,
    onSave: (PresetData) -> Unit
) {
    var callerName by remember { mutableStateOf(preset?.callerName ?: "") }
    var callerPhone by remember { mutableStateOf(preset?.callerPhone ?: "") }
    var presetName by remember { mutableStateOf(preset?.presetName ?: "") }
    var callDuration by remember { mutableStateOf((preset?.callDurationSeconds ?: 120) / 60) }
    var autoAnswer by remember { mutableStateOf(preset?.autoAnswerDelaySeconds != null) }
    var autoAnswerDelay by remember { mutableStateOf(preset?.autoAnswerDelaySeconds ?: 5) }
    var vibrateEnabled by remember { mutableStateOf(preset?.vibrateEnabled ?: true) }
    var callType by remember { mutableStateOf(preset?.callType ?: "VOICE_CALL") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (preset != null) "Edit Preset" else "Create Fake Call Preset",
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
                // Preset Name
                OutlinedTextField(
                    value = presetName,
                    onValueChange = { presetName = it },
                    label = { Text("Preset Name (Optional)") },
                    placeholder = { Text("e.g., Mom Emergency, Boss Call") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Label, null) }
                )

                // Caller Name
                OutlinedTextField(
                    value = callerName,
                    onValueChange = { callerName = it },
                    label = { Text("Caller Name *") },
                    placeholder = { Text("e.g., Mom, John, Boss") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Person, null) }
                )

                // Caller Phone
                OutlinedTextField(
                    value = callerPhone,
                    onValueChange = { callerPhone = it },
                    label = { Text("Phone Number *") },
                    placeholder = { Text("+1234567890") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Phone, null) }
                )

                Divider()

                // Call Type
                Text(
                    "Call Type",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = callType == "VOICE_CALL",
                        onClick = { callType = "VOICE_CALL" },
                        label = { Text("Voice Call") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Call,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                    FilterChip(
                        selected = callType == "VIDEO_CALL",
                        onClick = { callType = "VIDEO_CALL" },
                        label = { Text("Video Call") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Videocam,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }

                // Call Duration
                Text(
                    "Call Duration: $callDuration minutes",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Slider(
                    value = callDuration.toFloat(),
                    onValueChange = { callDuration = it.toInt() },
                    valueRange = 1f..10f,
                    steps = 8
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("1 min", fontSize = 12.sp, color = Color.Gray)
                    Text("10 min", fontSize = 12.sp, color = Color.Gray)
                }

                Divider()

                // Auto Answer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Auto-answer", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "Automatically answer after delay",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    Switch(
                        checked = autoAnswer,
                        onCheckedChange = { autoAnswer = it }
                    )
                }

                if (autoAnswer) {
                    Text(
                        "Auto-answer after: $autoAnswerDelay seconds",
                        fontSize = 14.sp
                    )
                    Slider(
                        value = autoAnswerDelay.toFloat(),
                        onValueChange = { autoAnswerDelay = it.toInt() },
                        valueRange = 2f..15f,
                        steps = 12
                    )
                }

                // Vibrate
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Vibrate", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Switch(
                        checked = vibrateEnabled,
                        onCheckedChange = { vibrateEnabled = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (callerName.isNotBlank() && callerPhone.isNotBlank()) {
                        onSave(
                            PresetData(
                                callerName = callerName,
                                callerPhone = callerPhone,
                                presetName = presetName.takeIf { it.isNotBlank() },
                                callDurationSeconds = callDuration * 60,
                                autoAnswerDelaySeconds = if (autoAnswer) autoAnswerDelay else null,
                                vibrateEnabled = vibrateEnabled,
                                callType = callType
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6366F1)
                ),
                enabled = callerName.isNotBlank() && callerPhone.isNotBlank()
            ) {
                Text(if (preset != null) "Update" else "Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFF666666))
            }
        }
    )
}

@Composable
fun QuickTriggerDialog(
    presets: List<FakeCallPreset>,
    onDismiss: () -> Unit,
    onTrigger: (Long) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Phone,
                contentDescription = null,
                tint = Color(0xFF10B981),
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                "Quick Trigger",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Select a preset to trigger immediately:",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (presets.isEmpty()) {
                    Text(
                        "No presets available. Create one first!",
                        fontSize = 14.sp,
                        color = Color(0xFFEF4444)
                    )
                } else {
                    presets.take(5).forEach { preset ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable() {
                                    onTrigger(preset.id)
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFF3F4F6)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    if (preset.callType == "VIDEO_CALL")
                                        Icons.Default.Videocam
                                    else
                                        Icons.Default.Call,
                                    contentDescription = null,
                                    tint = Color(0xFF6366F1)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        preset.presetName ?: preset.callerName,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        preset.callerPhone,
                                        fontSize = 13.sp,
                                        color = Color.Gray
                                    )
                                }
                                Icon(
                                    Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    tint = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

data class PresetData(
    val callerName: String,
    val callerPhone: String,
    val presetName: String?,
    val callDurationSeconds: Int,
    val autoAnswerDelaySeconds: Int?,
    val vibrateEnabled: Boolean,
    val callType: String
)