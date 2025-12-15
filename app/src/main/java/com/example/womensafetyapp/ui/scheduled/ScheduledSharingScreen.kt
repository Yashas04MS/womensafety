package com.example.womensafetyapp.ui.scheduled

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduledSharingScreen(
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel: ScheduledSharingViewModel = viewModel(
        factory = ScheduledSharingViewModelFactory(context)
    )

    val sessions by viewModel.sessions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedSession by remember { mutableStateOf<ScheduledSession?>(null) }

    LaunchedEffect(Unit) {
        viewModel.fetchSessions()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Scheduled Location Sharing",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF8B5CF6),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = Color(0xFF8B5CF6)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create", tint = Color.White)
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }

                errorMessage.isNotEmpty() -> {
                    ErrorState(
                        message = errorMessage,
                        onRetry = { viewModel.fetchSessions() },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                sessions.isEmpty() -> {
                    EmptyState(
                        onCreateClick = { showCreateDialog = true },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item { InfoCard() }

                        items(sessions) { session ->
                            SessionCard(
                                session = session,
                                onMarkArrived = { viewModel.markArrived(session.id) },
                                onCancel = { viewModel.cancelSession(session.id) },
                                onClick = { selectedSession = session }
                            )
                        }

                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateSessionDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = {
                viewModel.createSession(it)
                showCreateDialog = false
            }
        )
    }

    selectedSession?.let {
        SessionDetailsDialog(
            session = it,
            onDismiss = { selectedSession = null }
        )
    }
}

@Composable
fun InfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEEE9FF))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Info, null, tint = Color(0xFF6D28D9))
            Spacer(Modifier.width(12.dp))
            Text(
                "Schedule location sharing for trips or walks. Contacts are notified automatically.",
                fontSize = 14.sp,
                color = Color(0xFF4C1D95)
            )
        }
    }
}

@Composable
fun SessionCard(
    session: ScheduledSession,
    onMarkArrived: () -> Unit,
    onCancel: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(Modifier.weight(1f)) {
                    Text(session.sessionName, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text(formatDateTime(session.startTime), fontSize = 13.sp, color = Color.Gray)
                }
                StatusChip(session.status)
            }

            Spacer(Modifier.height(8.dp))
            Text("Duration: ${session.durationMinutes} min", fontSize = 14.sp)

            session.destinationAddress?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, fontSize = 14.sp, color = Color.DarkGray, maxLines = 1)
            }

            if (session.status == "ACTIVE") {
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFEF4444)
                        )
                    ) { Text("Cancel") }

                    Button(
                        onClick = onMarkArrived,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                    ) { Text("I Arrived") }
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val color = when (status) {
        "SCHEDULED" -> Color(0xFFFEF3C7)
        "ACTIVE" -> Color(0xFFD1FAE5)
        "COMPLETED" -> Color(0xFFDDEAFF)
        "CANCELLED" -> Color(0xFFFEE2E2)
        else -> Color.LightGray
    }

    Surface(shape = RoundedCornerShape(12.dp), color = color) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun EmptyState(onCreateClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.padding(40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("📅", fontSize = 64.sp)
        Spacer(Modifier.height(12.dp))
        Text("No Scheduled Sessions", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Button(onClick = onCreateClick) {
            Icon(Icons.Default.Add, null)
            Spacer(Modifier.width(8.dp))
            Text("Create Session")
        }
    }
}

@Composable
fun ErrorState(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.padding(40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.Error, null, tint = Color.Red, modifier = Modifier.size(48.dp))
        Spacer(Modifier.height(12.dp))
        Text(message, color = Color.Gray)
        Spacer(Modifier.height(12.dp))
        Button(onClick = onRetry) { Text("Retry") }
    }
}

fun formatDateTime(dateTime: String): String =
    try {
        LocalDateTime.parse(dateTime)
            .format(DateTimeFormatter.ofPattern("MMM dd, hh:mm a"))
    } catch (e: Exception) {
        dateTime
    }

data class ScheduledSession(
    val id: Long,
    val sessionName: String,
    val startTime: String,
    val endTime: String,
    val durationMinutes: Int,
    val status: String,
    val destinationAddress: String?,
    val destinationLatitude: Double?,
    val destinationLongitude: Double?,
    val notifyContactsOnStart: Boolean,
    val notifyContactsOnArrival: Boolean
)
