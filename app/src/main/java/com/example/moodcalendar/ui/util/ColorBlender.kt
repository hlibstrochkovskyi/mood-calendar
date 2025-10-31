package com.example.moodcalendar.ui.util

import androidx.compose.ui.graphics.Color
import com.example.moodcalendar.data.MoodRating

// A predefined color palette for mood combinations.
// These colors are used by the getBlendedColor function to represent
// different combinations of mood ratings throughout a day.

private val C_GGG = Color(0xFF4CAF50) // Bright Green: For three 'Good' ratings.
private val C_GGY = Color(0xFF9DCC1A) // Lime Green: For two 'Good' and one 'Average' rating.
private val C_GGR = Color(0xFFC8CC45) // Green-Yellow: For two 'Good' and one 'Bad' rating.
private val C_GYY = Color(0xFFC8CC45) // Light Yellow-Green: For one 'Good' and two 'Average' ratings.
private val C_GRR = Color(0xFFF99316) // Orange: For one 'Good' and two 'Bad' ratings.
private val C_GYR = Color(0xFFFFC328) // Amber/Mixed Yellow: For one of each 'Good', 'Average', and 'Bad' rating.
private val C_YYY = Color(0xFFFFC328) // Bright Yellow: For three 'Average' ratings.
private val C_YYR = Color(0xFFF99316) // Orange: For two 'Average' and one 'Bad' rating.
private val C_YRR = Color(0xFFF55D0B) // Dark Orange: For one 'Average' and two 'Bad' ratings.
private val C_RRR = Color(0xFFEC3B3B) // Bright Red: For three 'Bad' ratings.


/**
 * Determines a blended color based on mood ratings for a day using a lookup table.
 *
 * This function takes optional mood ratings for the morning, afternoon, and evening.
 * It counts the occurrences of 'Good', 'Average', and 'Bad' ratings and returns a
 * predefined color from a palette that corresponds to that specific combination.
 * This approach avoids color mixing at runtime and instead uses a fixed set of
 * 10 colors to represent all possible mood combinations for a fully rated day.
 *
 * @param morning The [MoodRating] for the morning, or null if not rated.
 * @param afternoon The [MoodRating] for the afternoon, or null if not rated.
 * @param evening The [MoodRating] for the evening, or null if not rated.
 * @return A specific [Color] from the predefined palette if all three parts of the day
 *         are rated. Returns [Color.LightGray] if any rating is missing, which serves
 *         as a default for incomplete or future dates.
 */
fun getBlendedColor(
    morning: MoodRating?,
    afternoon: MoodRating?,
    evening: MoodRating?
): Color {
    val ratings = listOfNotNull(morning, afternoon, evening)

    // Return a default color if the day is not fully rated.
    if (ratings.size < 3) {
        return Color.LightGray // Default color for incomplete or future days.
    }

    // 1. Count the number of 'Good', 'Average', and 'Bad' ratings.
    val greenCount = ratings.count { it == MoodRating.GOOD }
    val yellowCount = ratings.count { it == MoodRating.AVERAGE }
    val redCount = ratings.count { it == MoodRating.BAD }

    // 2. Use a 'when' statement as a lookup table to find the corresponding
    //    predefined color based on the counts of each rating type.
    return when {
        // Three identical ratings
        greenCount == 3 -> C_GGG
        yellowCount == 3 -> C_YYY
        redCount == 3 -> C_RRR

        // Combinations with two 'Good' ratings
        greenCount == 2 && yellowCount == 1 -> C_GGY // GGY
        greenCount == 2 && redCount == 1 -> C_GGR   // GGR

        // Combinations with two 'Average' ratings
        yellowCount == 2 && greenCount == 1 -> C_GYY // GYY
        yellowCount == 2 && redCount == 1 -> C_YYR   // YYR

        // Combinations with two 'Bad' ratings
        redCount == 2 && greenCount == 1 -> C_GRR // GRR
        redCount == 2 && yellowCount == 1 -> C_YRR // YRR

        // The most mixed combination: one of each
        greenCount == 1 && yellowCount == 1 && redCount == 1 -> C_GYR // GYR

        // A fallback case, though it should not be reached if logic is sound.
        else -> Color.LightGray
    }
}