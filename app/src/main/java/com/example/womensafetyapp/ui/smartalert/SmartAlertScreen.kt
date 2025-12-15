package com.example.womensafetyapp.ui.smartalert

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartAlertScreen(onBack: () -> Unit) {

    val context = LocalContext.current
    val manager = remember { SmartAlertManager(context) }

    var isMonitoring by remember { mutableStateOf(false) }
    var lastActivity by remember { mutableStateOf("None") }
    var alertLevel by remember { mutableStateOf("SAFE") }
    var showOverlay by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val observer = Observer<SmartAlertManager.AlertEvent> { event ->
            alertLevel = event.riskLevel
            lastActivity = event.activityType.name
            showOverlay = true
        }

        manager.alertEvent.observeForever(observer)

        onDispose {
            manager.alertEvent.removeObserver(observer)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Smart Alert") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        Box(Modifier.padding(padding)) {

            SmartAlertContent(
                isMonitoring = isMonitoring,
                lastActivity = lastActivity,
                alertLevel = alertLevel,
                onToggleMonitoring = {
                    isMonitoring = !isMonitoring
                    if (isMonitoring) manager.startMonitoring()
                    else manager.stopMonitoring()
                },
                onTriggerAlert = {
                    manager.triggerManualAlert()
                }
            )

            if (showOverlay) {
                AlertTriggeredOverlay()
            }
        }
    }
}
