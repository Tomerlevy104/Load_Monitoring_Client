package com.finalproject.load_monitoring.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateFormatUtils {
    private val timeFormatter =
        SimpleDateFormat("HH:mm", Locale.getDefault())

    fun formatTime(date: Date?): String {
        return if (date == null) "--:--" else timeFormatter.format(date)
    }

    fun formatStringTime(isoDateTime: String?): String {
        if (isoDateTime.isNullOrBlank()) return "--:--"

        return try {
            // חותכים לשעה ודקות
            isoDateTime.substring(11, 16) // HH:mm
        } catch (e: Exception) {
            "--:--"
        }
    }
}