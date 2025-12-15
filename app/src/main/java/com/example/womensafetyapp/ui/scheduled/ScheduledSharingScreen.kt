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
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
<<<<<<< HEAD
                    containerColor = Color(0xFF2563EB),
=======
                    containerColor = Color(0xFF8B5CF6),
>>>>>>> a9a0289 (Implemented smart alert)
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = Color(0xFF2563EB)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Create Session",
                    tint = Color.White
                )
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
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
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
                        item {
                            InfoCard()
                        }

                        items(sessions) { session ->
                            SessionCard(
                                session = session,
                                onMarkArrived = {
                                    viewModel.markArrived(session.id)
                                },
                                onCancel = {
                                    viewModel.cancelSession(session.id)
                                },
                                onClick = {
                                    selectedSession = session
                                }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateSessionDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { session ->
                viewModel.createSession(session)
                showCreateDialog = false
            }
        )
    }

    if (selectedSession != null) {
        SessionDetailsDialog(
            session = selectedSession!!,
            onDismiss = { selectedSession = null }
        )
    }
}

@Composable
fun InfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFDEEBFF)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                tint = Color(0xFF2563EB),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                "Schedule location sharing for dates, rides, or walks. Your contacts will be notified automatically.",
                fontSize = 14.sp,
                color = Color(0xFF1E40AF)
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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = session.sessionName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = formatDateTime(session.startTime),
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }

                StatusChip(status = session.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Duration
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Timer,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = Color(0xFF666666)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Duration: ${session.durationMinutes} minutes",
                    fontSize = 14.sp,
                    color = Color(0xFF444444)
                )
            }

            if (session.destinationAddress != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = Color(0xFF666666)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        session.destinationAddress,
                        fontSize = 14.sp,
                        color = Color(0xFF444444),
                        maxLines = 1
                    )
                }
            }

            // Action buttons for ACTIVE status
            if (session.status == "ACTIVE") {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFEF4444)
                        )
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = onMarkArrived,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF10B981)
                        )
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("I Arrived")
                    }
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val (color, text) = when (status) {
        "SCHEDULED" -> Color(0xFFFEF3C7) to "Scheduled"
        "ACTIVE" -> Color(0xFFD1FAE5) to "Active"
        "COMPLETED" -> Color(0xFFDDEAFF) to "Completed"
        "CANCELLED" -> Color(0xFFFEE2E2) to "Cancelled"
        else -> Color(0xFFF3F4F6) to status
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun EmptyState(
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "📅",
            fontSize = 64.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "No Scheduled Sessions",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Schedule location sharing for upcoming trips, dates, or walks",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onCreateClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2563EB)
            )
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Create Session")
        }
    }
}

@Composable
fun ErrorState(
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
            "Error Loading Sessions",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            message,
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2563EB)
            )
        ) {
            Text("Retry")
        }
    }
}

fun formatDateTime(dateTime: String): String {
    return try {
        val parsed = LocalDateTime.parse(dateTime)
        parsed.format(DateTimeFormatter.ofPattern("MMM dd, hh:mm a"))
    } catch (e: Exception) {
        dateTime
    }
}

// Data class for sessions
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