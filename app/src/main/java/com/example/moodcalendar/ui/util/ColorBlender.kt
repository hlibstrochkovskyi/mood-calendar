package com.example.moodcalendar.ui.util

import androidx.compose.ui.graphics.Color
import com.example.moodcalendar.data.MoodRating
import kotlin.math.pow

fun getBlendedColor(
    morning: MoodRating?,
    afternoon: MoodRating?,
    evening: MoodRating?
): Color {
    val ratings = listOfNotNull(morning, afternoon, evening)
    if (ratings.size < 3) { // Do not blend until all 3 time periods are rated
        return Color.LightGray // Neutral color
    }

    val colorMap = mapOf(
        MoodRating.GOOD to 1,
        MoodRating.AVERAGE to 0,
        MoodRating.BAD to -1
    )

    val values = ratings.map { colorMap[it]!! }
    val avg = values.average()
    val variance = values.sumOf { (it - avg).pow(2) } / 3.0

    // High variance = mixed/muddy colors
    if (variance > 0.4) {
        return when {
            avg > 0 -> Color(0xFFA3A380) // "Dirty green"
            avg < 0 -> Color(0xFFA37A6E) // "Dirty red"
            else -> Color(0xFF9CA3AF)    // Gray
        }
    }

    // Low variance = clear colors
    return when {
        avg > 0.6 -> Color(0xFF22C55E)  // Bright green
        avg > 0.3 -> Color(0xFF84CC16)  // Light green
        avg > 0 -> Color(0xFFBEF264)    // Lime
        avg > -0.3 -> Color(0xFFFBBF24) // Yellow
        avg > -0.6 -> Color(0xFFF97316) // Orange
        else -> Color(0xFFEF4444)       // Red
    }
}
