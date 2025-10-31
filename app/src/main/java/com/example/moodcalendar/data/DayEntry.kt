package com.example.moodcalendar.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "day_entries")
data class DayEntry(
    @PrimaryKey
    val date: LocalDate, // key

    // Ratings by day parts
    val morningRating: MoodRating?,
    val afternoonRating: MoodRating?,
    val eveningRating: MoodRating?,

    // Notes from a micro-journal
    val happyNote: String?,
    val sadNote: String?
)