package com.example.moodcalendar.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface DayEntryDao {

    // Upsert = "Update" or "Insert" if it doesn't exist
    @Upsert
    suspend fun upsert(entry: DayEntry)

    // Get a single entry by date
    @Query("SELECT * FROM day_entries WHERE date = :date")
    suspend fun getEntry(date: LocalDate): DayEntry?

    // Get all entries for a month (for CalendarGrid)
    // Flow<> means Compose will automatically update when data changes
    @Query("SELECT * FROM day_entries WHERE date >= :startDate AND date <= :endDate")
    fun getEntriesForMonth(startDate: LocalDate, endDate: LocalDate): Flow<List<DayEntry>>

    // Get all entries for a year (for Pixel View)
    @Query("SELECT * FROM day_entries WHERE date >= :startDate AND date <= :endDate")
    fun getEntriesForYear(startDate: LocalDate, endDate: LocalDate): Flow<List<DayEntry>>

    // Search through notes
    @Query("SELECT * FROM day_entries WHERE happyNote LIKE '%' || :query || '%' OR sadNote LIKE '%' || :query || '%'")
    fun searchNotes(query: String): Flow<List<DayEntry>>
}