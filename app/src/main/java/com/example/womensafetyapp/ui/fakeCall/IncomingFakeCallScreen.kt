package com.example.womensafetyapp.ui.fakeCall

import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun IncomingFakeCallScreen(
    callResponse: FakeCallResponse,
    onAnswer: () -> Unit,
    onDecline: () -> Unit,
    onCallEnded: () -> Unit
) {
    val context = LocalContext.current
    var isCallActive by remember { mutableStateOf(false) }
    var callDuration by remember { mutableStateOf(0) }
    var autoAnswerCountdown by remember { mutableStateOf(callResponse.autoAnswerDelaySeconds) }

    // Store ringtone and vibrator references
    var ringtone by remember { mutableStateOf<Ringtone?>(null) }
    var vibrator by remember { mutableStateOf<Vibrator?>(null) }

    // Start vibration and ringtone
    LaunchedEffect(Unit) {
        try {
            // Start vibration
            if (callResponse.vibrateEnabled) {
                vibrator = context.getSystemService(android.content.Context.VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val pattern = longArrayOf(0, 500, 1000)
                    vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(longArrayOf(0, 500, 1000), 0)
                }
            }

            // Start ringtone
            val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            ringtone = RingtoneManager.getRingtone(context, ringtoneUri)
            ringtone?.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Stop ringtone and vibration when call becomes active
    LaunchedEffect(isCallActive) {
        if (isCallActive) {
            try {
                ringtone?.stop()
                vibrator?.cancel()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Cleanup on dispose
    DisposableEffect(Unit) {
        onDispose {
            try {
                ringtone?.stop()
                vibrator?.cancel()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Auto-answer countdown
    LaunchedEffect(autoAnswerCountdown) {
        if (autoAnswerCountdown != null && autoAnswerCountdown!! > 0 && !isCallActive) {
            delay(1000)
            autoAnswerCountdown = autoAnswerCountdown!! - 1
            if (autoAnswerCountdown == 0) {
                onAnswer()
                isCallActive = true
            }
        }
    }

    // Call duration counter
    LaunchedEffect(isCallActive) {
        if (isCallActive) {
            while (callDuration < callResponse.callDurationSeconds) {
                delay(1000)
                callDuration++
            }
            // Auto end call after duration
            onCallEnded()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = if (isCallActive) {
                        listOf(Color(0xFF10B981), Color(0xFF059669))
                    } else {
                        listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                    }
                )
            )
    ) {
        if (!isCallActive) {
            // Incoming Call UI
            IncomingCallUI(
                callResponse = callResponse,
                autoAnswerCountdown = autoAnswerCountdown,
                onAnswer = {
                    isCallActive = true
                    onAnswer()
                },
                onDecline = {
                    // Stop ringtone and vibration on decline
                    try {
                        ringtone?.stop()
                        vibrator?.cancel()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    onDecline()
                }
            )
        } else {
            // Active Call UI
            ActiveCallUI(
                callResponse = callResponse,
                callDuration = callDuration,
                onEndCall = onCallEnded
            )
        }
    }
}

@Composable
fun IncomingCallUI(
    callResponse: FakeCallResponse,
    autoAnswerCountdown: Int?,
    onAnswer: () -> Unit,
    onDecline: () -> Unit
) {
    // Pulsing animation for caller avatar
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Caller Avatar with pulsing effect
            Box(
                modifier = Modifier
                    .size((120 * scale).dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Caller Name
            Text(
                callResponse.callerName,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Caller Phone
            Text(
                callResponse.callerPhone,
                fontSize = 18.sp,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Call Type
            Surface(
                shape = MaterialTheme.shapes.small,
                color = Color.White.copy(alpha = 0.2f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (callResponse.callType == "VIDEO_CALL")
                            Icons.Default.Videocam
                        else
                            Icons.Default.Phone,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (callResponse.callType == "VIDEO_CALL") "Video Call" else "Voice Call",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }

            // Auto-answer countdown
            if (autoAnswerCountdown != null && autoAnswerCountdown > 0) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "Auto-answering in $autoAnswerCountdown...",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Decline Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FloatingActionButton(
                    onClick = onDecline,
                    containerColor = Color(0xFFEF4444),
                    modifier = Modifier.size(70.dp)
                ) {
                    Icon(
                        Icons.Default.CallEnd,
                        contentDescription = "Decline",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Decline", color = Color.White, fontSize = 14.sp)
            }

            // Answer Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FloatingActionButton(
                    onClick = onAnswer,
                    containerColor = Color(0xFF10B981),
                    modifier = Modifier.size(70.dp)
                ) {
                    Icon(
                        Icons.Default.Call,
                        contentDescription = "Answer",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Answer", color = Color.White, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun ActiveCallUI(
    callResponse: FakeCallResponse,
    callDuration: Int,
    onEndCall: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Caller Avatar
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                callResponse.callerName,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Call Duration
            Text(
                formatDuration(callDuration),
                fontSize = 20.sp,
                color = Color.White.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Call in progress...",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
        }

        // Control Buttons
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Mute, Speaker, etc. (optional)
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.padding(bottom = 40.dp)
            ) {
                IconButton(
                    onClick = { /* Mute */ },
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(
                        Icons.Default.Mic,
                        contentDescription = "Mute",
                        tint = Color.White
                    )
                }

                IconButton(
                    onClick = { /* Speaker */ },
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(
                        Icons.Default.VolumeUp,
                        contentDescription = "Speaker",
                        tint = Color.White
                    )
                }

                if (callResponse.callType == "VIDEO_CALL") {
                    IconButton(
                        onClick = { /* Video */ },
                        modifier = Modifier
                            .size(60.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(
                            Icons.Default.Videocam,
                            contentDescription = "Video",
                            tint = Color.White
                        )
                    }
                }
            }

            // End Call Button
            FloatingActionButton(
                onClick = onEndCall,
                containerColor = Color(0xFFEF4444),
                modifier = Modifier.size(70.dp)
            ) {
                Icon(
                    Icons.Default.CallEnd,
                    contentDescription = "End Call",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("End Call", color = Color.White, fontSize = 14.sp)
        }
    }
}

fun formatDuration(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", mins, secs)
}