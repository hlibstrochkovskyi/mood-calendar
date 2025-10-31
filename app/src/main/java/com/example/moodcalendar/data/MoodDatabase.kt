    package com.example.moodcalendar.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [DayEntry::class], version = 1)
@TypeConverters(Converters::class)
abstract class MoodDatabase : RoomDatabase() {

    abstract fun dayEntryDao(): DayEntryDao

    // This code (Companion object) ensures that we only have
    // one instance of the database in the application.
    companion object {
        @Volatile
        private var INSTANCE: MoodDatabase? = null

        fun getDatabase(context: Context): MoodDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MoodDatabase::class.java,
                    "mood_database"
                )
                    // We won't be doing migrations for now, so if
                    // the schema changes, the DB will just be rebuilt.
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}