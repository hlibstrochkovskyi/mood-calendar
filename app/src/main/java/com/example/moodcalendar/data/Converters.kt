package com.example.moodcalendar.data

import androidx.room.TypeConverter
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun fromMoodRating(rating: MoodRating?): String? {
        return rating?.name // we will save as good, avarage, bad
    }

    @TypeConverter
    fun toMoodRating(value: String?): MoodRating? {
        return value?.let { MoodRating.valueOf(it) }
    }
}