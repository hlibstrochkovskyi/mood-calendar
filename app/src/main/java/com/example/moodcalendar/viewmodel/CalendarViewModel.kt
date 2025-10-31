package com.example.moodcalendar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.moodcalendar.data.DayEntry
import com.example.moodcalendar.data.DayEntryDao
import com.example.moodcalendar.data.MoodRating
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

// This ViewModel receives a DAO as a parameter.
class CalendarViewModel(private val dao: DayEntryDao) : ViewModel() {

    // _currentYearMonth is a private state that we can modify.
    // .now() means that at startup, it will show the current month.
    private val _currentYearMonth = MutableStateFlow(YearMonth.now())

    // currentYearMonth is a public state that the UI can observe.
    val currentYearMonth = _currentYearMonth.asStateFlow()

    // This is a reactive data flow.
    // When _currentYearMonth changes, flatMapLatest automatically
    // cancels the previous database query and starts a new one.
    val entriesForMonth = _currentYearMonth.flatMapLatest { yearMonth ->
        val startDate = yearMonth.atDay(1)
        val endDate = yearMonth.atEndOfMonth()
        dao.getEntriesForMonth(startDate, endDate)
    }

    // Function for the UI to change the displayed month.
    fun setCurrentMonth(yearMonth: YearMonth) {
        _currentYearMonth.value = yearMonth
    }

    // Function for the UI to insert or update a day entry.
    fun upsertDayEntry(date: LocalDate, morning: MoodRating?, afternoon: MoodRating?, evening: MoodRating?, happy: String?, sad: String?) {
        // viewModelScope is a safe coroutine scope
        // for running asynchronous operations (like saving to the DB).
        viewModelScope.launch {
            val entry = DayEntry(
                date = date,
                morningRating = morning,
                afternoonRating = afternoon,
                eveningRating = evening,
                happyNote = happy,
                sadNote = sad
            )
            dao.upsert(entry)
        }
    }
}

// --- FACTORY ---
// We need a factory to tell Android how to create our ViewModel,
// because it has a constructor parameter (dao).
class CalendarViewModelFactory(private val dao: DayEntryDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CalendarViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
