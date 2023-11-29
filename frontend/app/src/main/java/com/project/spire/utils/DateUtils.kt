package com.project.spire.utils

import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateUtils {

    private const val OFFSET_TIME = -3L

    fun formatTime(time: String): String {
        val dateTime: LocalDateTime = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME)
        val now: LocalDateTime = LocalDateTime.now()
        val diff: Duration = Duration.between(dateTime, now) - Duration.ofHours(OFFSET_TIME)

        return if (diff.toMinutes() < 1) {
            "Just now"
        } else if (diff.toMinutes().toInt() == 1) {
            "1 minute ago"
        } else if (diff.toHours() < 1) {
            "${diff.toMinutes()} minutes ago"
        } else if (diff.toHours().toInt() == 1) {
            "1 hour ago"
        } else if (diff.toDays() < 1) {
            "${diff.toHours()} hours ago"
        } else if (diff.toDays().toInt() == 1) {
            "1 day ago"
        } else {
            "${diff.toDays()} days ago"
        }
    }
}