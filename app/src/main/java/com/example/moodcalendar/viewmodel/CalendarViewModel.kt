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
import java.time.Year
import java.time.YearMonth

/**
 * A ViewModel responsible for managing and providing calendar-related data to the UI.
 *
 * This class serves as the bridge between the UI layer and the data layer (repository/DAO).
 * It holds the application's UI state for the selected month and year, fetches mood entries
 * from the database, and provides methods to update the state and persist data.
 *
 * @property dao The Data Access Object ([DayEntryDao]) for interacting with the database.
 */
class CalendarViewModel(private val dao: DayEntryDao) : ViewModel() {

    // --- MONTH-RELATED LOGIC ---

    /**
     * A private mutable state flow that holds the currently selected month and year.
     *
     * This is the internal state that can be updated within the ViewModel.
     */
    private val _currentYearMonth = MutableStateFlow(YearMonth.now())

    /**
     * A public, read-only state flow representing the currently selected month and year.
     *
     * UI components can collect this flow to react to changes in the selected month.
     */
    val currentYearMonth = _currentYearMonth.asStateFlow()

    /**
     * A flow that emits a list of [DayEntry] objects for the currently selected month.
     *
     * It uses `flatMapLatest` to automatically re-fetch data from the DAO whenever
     * the `_currentYearMonth` state changes, ensuring the UI always displays entries
     * for the correct month.
     */
    val entriesForMonth = _currentYearMonth.flatMapLatest { yearMonth ->
        val startDate = yearMonth.atDay(1)
        val endDate = yearMonth.atEndOfMonth()
        dao.getEntriesForMonth(startDate, endDate)
    }

    /**
     * Updates the current month state.
     *
     * This function should be called when the user navigates to a different month
     * in the UI (e.g., by swiping or using navigation arrows).
     *
     * @param yearMonth The new [YearMonth] to set as the current state.
     */
    fun setCurrentMonth(yearMonth: YearMonth) {
        _currentYearMonth.value = yearMonth
    }

    // --- YEAR-RELATED LOGIC ---

    /**
     * A private mutable state flow that holds the currently selected year.
     *
     * This is the internal state used for the yearly overview screen.
     */
    private val _currentYear = MutableStateFlow(Year.now())

    /**
     * A public, read-only state flow representing the currently selected year.
     *
     * UI components (like the yearly overview) can collect this flow to display
     * data for the correct year.
     */
    val currentYear = _currentYear.asStateFlow()

    /**
     * A flow that emits a list of all [DayEntry] objects for the currently selected year.
     *
     * It uses `flatMapLatest` to automatically re-fetch all entries for the year
     * from the DAO whenever the `_currentYear` state changes.
     */
    val entriesForYear = _currentYear.flatMapLatest { year ->
        val startDate = year.atDay(1)
        val endDate = year.atMonth(12).atEndOfMonth()
        dao.getEntriesForYear(startDate, endDate)
    }

    /**
     * Updates the current year state.
     *
     * This function can be called if the user navigates to a different year.
     *
     * @param year The new [Year] to set as the current state.
     */
    fun setCurrentYear(year: Year) {
        _currentYear.value = year
    }
    // --- END OF YEAR LOGIC ---


    /**
     * Creates or updates a mood entry for a specific date.
     *
     * This function constructs a [DayEntry] object from the provided data and
     * uses a coroutine to call the DAO's `upsert` method, which will either
     * insert a new entry or update an existing one for the given date.
     *
     * @param date The [LocalDate] of the entry.
     * @param morning The [MoodRating] for the morning, or null if not provided.
     * @param afternoon The [MoodRating] for the afternoon, or null if not provided.
     * @param evening The [MoodRating] for the evening, or null if not provided.
     * @param happy An optional string note about what made the user happy.
     * @param sad An optional string note about what made the user sad.
     */
    fun upsertDayEntry(date: LocalDate, morning: MoodRating?, afternoon: MoodRating?, evening: MoodRating?, happy: String?, sad: String?) {
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

/**
 * A factory for creating [CalendarViewModel] instances.
 *
 * This class is necessary because [CalendarViewModel] has a constructor dependency
 * on [DayEntryDao]. The ViewModel framework uses this factory to correctly
 * instantiate the ViewModel with its required dependencies.
 *
 * @property dao The [DayEntryDao] instance to be provided to the ViewModel.
 */
class CalendarViewModelFactory(private val dao: DayEntryDao) : ViewModelProvider.Factory {
    /**
     * Creates a new instance of the given `Class`.
     *
     * This method checks if the requested ViewModel is of type [CalendarViewModel]
     * and, if so, returns a new instance, passing the [dao] to its constructor.
     *
     * @param modelClass A `Class` whose instance is requested.
     * @return A newly created ViewModel.
     * @throws IllegalArgumentException if `modelClass` is not a subclass of [CalendarViewModel].
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CalendarViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}