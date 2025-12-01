package com.example.womensafetyapp.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

fun sendAlertSMS(context: Context, numbers: List<String>) {
    val message = "🚨 SOS Alert!\nI need help immediately.\nPlease contact me ASAP!"

    numbers.forEach { number ->
        val uri = Uri.parse("smsto:$number")
        val intent = Intent(Intent.ACTION_SENDTO, uri)
        intent.putExtra("sms_body", message)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }
}
