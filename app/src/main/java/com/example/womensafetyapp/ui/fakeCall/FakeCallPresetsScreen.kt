package com.example.womensafetyapp.ui.fakeCall

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FakeCallPresetsScreen(
    onBack: () -> Unit = {},
    onTriggerCall: (Long) -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel: FakeCallViewModel = viewModel(
        factory = FakeCallViewModelFactory(context)
    )

    val presets by viewModel.presets.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingPreset by remember { mutableStateOf<FakeCallPreset?>(null) }
    var showQuickTriggerDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchPresets()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Fake Call Presets",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${presets.size}/10 presets",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6366F1),
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Quick Trigger FAB
                FloatingActionButton(
                    onClick = { showQuickTriggerDialog = true },
                    containerColor = Color(0xFFEF4444),
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Phone, "Quick Call")
                }

                // Add Preset FAB
                if (presets.size < 10) {
                    FloatingActionButton(
                        onClick = { showAddDialog = true },
                        containerColor = Color(0xFF6366F1)
                    ) {
                        Icon(Icons.Default.Add, "Add Preset", tint = Color.White)
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFF0F4FF), Color(0xFFE0E7FF))
                    )
                )
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF6366F1)
                    )
                }

                errorMessage.isNotEmpty() -> {
                    ErrorStateView(
                        message = errorMessage,
                        onRetry = { viewModel.fetchPresets() },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                presets.isEmpty() -> {
                    EmptyPresetsView(
                        onCreateFirst = { showAddDialog = true },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Info Card
                        item {
                            InfoCard()
                        }

                        // Preset Cards
                        items(presets) { preset ->
                            PresetCard(
                                preset = preset,
                                onTrigger = {
                                    viewModel.triggerFakeCall(preset.id)
                                    onTriggerCall(preset.id)
                                },
                                onEdit = {
                                    editingPreset = preset
                                    showAddDialog = true
                                },
                                onDelete = {
                                    viewModel.deletePreset(preset.id)
                                }
                            )
                        }

                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }

    // Add/Edit Dialog
    if (showAddDialog) {
        AddEditPresetDialog(
            preset = editingPreset,
            onDismiss = {
                showAddDialog = false
                editingPreset = null
            },
            onSave = { presetData ->
                if (editingPreset != null) {
                    viewModel.updatePreset(editingPreset!!.id, presetData)
                } else {
                    viewModel.createPreset(presetData)
                }
                showAddDialog = false
                editingPreset = null
            }
        )
    }

    // Quick Trigger Dialog
    if (showQuickTriggerDialog) {
        QuickTriggerDialog(
            presets = presets,
            onDismiss = { showQuickTriggerDialog = false },
            onTrigger = { presetId ->
                viewModel.triggerFakeCall(presetId)
                onTriggerCall(presetId)
                showQuickTriggerDialog = false
            }
        )
    }
}

@Composable
fun InfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFDEEBFF)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                tint = Color(0xFF6366F1),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                "Simulate incoming calls to safely exit uncomfortable situations",
                fontSize = 14.sp,
                color = Color(0xFF1E40AF)
            )
        }
    }
}
// In PresetCard component, update the Trigger Call button:

@Composable
fun PresetCard(
    preset: FakeCallPreset,
    onTrigger: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isTriggering by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        if (preset.callType == "VIDEO_CALL")
                            Icons.Default.Videocam
                        else
                            Icons.Default.Call,
                        contentDescription = null,
                        tint = Color(0xFF6366F1),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            preset.presetName ?: preset.callerName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A)
                        )
                        Text(
                            preset.callerPhone,
                            fontSize = 14.sp,
                            color = Color(0xFF666666)
                        )
                    }
                }

                if (preset.triggerCount > 0) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFE0E7FF)
                    ) {
                        Text(
                            "Used ${preset.triggerCount}x",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF6366F1)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DetailChip(
                    icon = Icons.Default.Timer,
                    text = "${preset.callDurationSeconds / 60}m"
                )
                if (preset.autoAnswerDelaySeconds != null) {
                    DetailChip(
                        icon = Icons.Default.PhoneCallback,
                        text = "Auto ${preset.autoAnswerDelaySeconds}s"
                    )
                }
                if (preset.vibrateEnabled) {
                    DetailChip(
                        icon = Icons.Default.Vibration,
                        text = "Vibrate"
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color(0xFF6366F1)
                        )
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFEF4444)
                        )
                    }
                }

                Button(
                    onClick = {
                        isTriggering = true
                        onTrigger()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF10B981)
                    ),
                    enabled = !isTriggering
                ) {
                    if (isTriggering) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            Icons.Default.Phone,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Trigger Call")
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(48.dp)
                )
            },
            title = { Text("Delete Preset?", fontWeight = FontWeight.Bold) },
            text = {
                Text("Are you sure you want to delete \"${preset.presetName ?: preset.callerName}\"?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF4444)
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
@Composable
fun DetailChip(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFF3F4F6)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = Color(0xFF666666),
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text,
                fontSize = 12.sp,
                color = Color(0xFF666666)
            )
        }
    }
}

@Composable
fun EmptyPresetsView(
    onCreateFirst: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("📞", fontSize = 80.sp)
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "No Presets Yet",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Create fake call presets for quick access in emergencies",
            fontSize = 15.sp,
            color = Color.Gray,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onCreateFirst,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6366F1)
            )
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Create First Preset")
        }
    }
}

@Composable
fun ErrorStateView(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.Error,
            contentDescription = null,
            tint = Color(0xFFEF4444),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Error Loading Presets",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            message,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6366F1)
            )
        ) {
            Text("Retry")
        }
    }
}

// Data class for presets
data class FakeCallPreset(
    val id: Long,
    val callerName: String,
    val callerPhone: String,
    val callerPhotoUrl: String?,
    val ringtoneName: String?,
    val vibrateEnabled: Boolean,
    val autoAnswerDelaySeconds: Int?,
    val callDurationSeconds: Int,
    val callType: String,
    val presetName: String?,
    val triggerCount: Int,
    val lastTriggeredAt: String?
)