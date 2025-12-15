package com.example.womensafetyapp.ui.smartalert

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.womensafetyapp.network.SuspiciousActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartAlertScreen(onBack: () -> Unit = {}) {

    val context = LocalContext.current
    val viewModel: SmartAlertViewModel = viewModel(
        factory = SmartAlertViewModelFactory(context)
    )

    val isMonitoring by viewModel.isMonitoring.collectAsState()
    val activityLog by viewModel.activityLog.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val lastAnalysis by viewModel.lastAnalysis.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Smart Alert System", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF59E0B),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->

        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
        ) {
            when {
                isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))

                errorMessage.isNotEmpty() ->
                    ErrorState(errorMessage, onRetry = viewModel::clearMessages)

                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item { SmartAlertInfoCard() }

                    item {
                        RiskOverviewCard(
                            riskLevel = lastAnalysis?.riskLevel ?: "LOW",
                            riskScore = lastAnalysis?.overallRiskScore ?: 0.0,
                            isMonitoring = isMonitoring
                        )
                    }

                    item {
                        SmartActionsCard(
                            enabled = isMonitoring,
                            onManualAlert = { viewModel.sendManualAlert() }
                        )
                    }

                    items(activityLog.take(10)) {
                        ActivityLogCard(it)
                    }
                }
            }
        }
    }
}

/* ---------------- COMPONENTS ---------------- */

@Composable
fun SmartActionsCard(enabled: Boolean, onManualAlert: () -> Unit) {
    Card {
        Column(Modifier.padding(16.dp)) {
            Text("Quick Actions", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onManualAlert,
                enabled = enabled,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
            ) {
                Icon(Icons.Default.Warning, null)
                Spacer(Modifier.width(8.dp))
                Text("Send Emergency Alert")
            }
        }
    }
}

@Composable
fun RiskOverviewCard(riskLevel: String, riskScore: Double, isMonitoring: Boolean) {
    Card(shape = RoundedCornerShape(12.dp)) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Column {
                    Text("Current Risk", fontWeight = FontWeight.Bold)
                    Text(riskLevel, color = getRiskColor(riskLevel), fontSize = 20.sp)
                }
                StatusChip(riskLevel)
            }
            if (isMonitoring) {
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = riskScore.toFloat().coerceIn(0f, 1f)
                )
            }
        }
    }
}

@Composable
fun ActivityLogCard(activity: SuspiciousActivity) {
    Card {
        Column(Modifier.padding(12.dp)) {
            Text(activity.activityType, fontWeight = FontWeight.Bold)
            Text(activity.timestamp, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun StatusChip(status: String) {
    Surface(
        color = getRiskColor(status).copy(alpha = 0.2f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(status, Modifier.padding(8.dp), fontSize = 12.sp)
    }
}

fun getRiskColor(level: String) = when (level) {
    "CRITICAL" -> Color.Red
    "HIGH" -> Color(0xFFF97316)
    "MEDIUM" -> Color(0xFFFACC15)
    "LOW" -> Color(0xFF22C55E)
    else -> Color.Gray
}

@Composable
fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        Modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Error, null, tint = Color.Red)
        Spacer(Modifier.height(12.dp))
        Text(message)
        Spacer(Modifier.height(12.dp))
        Button(onClick = onRetry) { Text("Retry") }
    }
}
@Composable
fun SmartAlertInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEECB9C))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = Color(0xFF794F03)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = "Automatically detects falls, shakes, and distress situations.",
                fontSize = 14.sp,
                color = Color(0xFF794F03)
            )
        }
    }
}
