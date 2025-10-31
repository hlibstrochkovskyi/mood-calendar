package com.example.moodcalendar

import android.app.Application
import com.example.moodcalendar.data.MoodDatabase

// This class is created once when the application starts.
class MoodCalendarApplication : Application() {

    // "lazy" means the database will be created
    // only when it is accessed for the first time.
    val database: MoodDatabase by lazy {
        MoodDatabase.getDatabase(this)
    }

    // The DAO is also created lazily from the database instance.
    val dao by lazy {
        database.dayEntryDao()
    }
}
