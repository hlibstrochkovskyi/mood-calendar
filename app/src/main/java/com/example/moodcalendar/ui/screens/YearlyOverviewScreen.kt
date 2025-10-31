package com.example.moodcalendar.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moodcalendar.data.DayEntry
import com.example.moodcalendar.ui.util.getBlendedColor
import com.example.moodcalendar.viewmodel.CalendarViewModel
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

/**
 * A Composable function that displays a full-year overview of mood entries.
 *
 * This screen presents a calendar view for the entire year, with each month
 * represented as a grid of "pixels," where each pixel corresponds to a day.
 * The color of the pixel indicates the blended mood for that day. It includes a
 * top app bar for navigation and actions.
 *
 * @param navController The NavController used for navigating back to the monthly view.
 * @param viewModel The [CalendarViewModel] instance that provides state and handles business logic.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YearlyOverviewScreen(
    navController: NavController,
    viewModel: CalendarViewModel
) {
    // Collect state from the ViewModel
    val currentYear by viewModel.currentYear.collectAsState()
    val entries by viewModel.entriesForYear.collectAsState(initial = emptyList())
    val entriesMap = entries.associateBy { it.date }

    // Get the current month for the back navigation button
    val currentMonth by viewModel.currentYearMonth.collectAsState()
    val monthShortName = currentMonth.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())

    Scaffold(
        topBar = {
            YearTopAppBar(
                monthShortName = monthShortName,
                onMonthClick = {
                    navController.popBackStack() // Navigate back to the month screen
                },
                onSearchClick = { /* TODO: Implement search functionality */ },
                onSettingsClick = { /* TODO: Implement settings navigation */ }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // "2025" Title
            Text(
                text = currentYear.value.toString(),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Grid of "pixels"
            YearAsPixelsGrid(
                year = currentYear.value,
                entriesMap = entriesMap,
                onMonthClick = { yearMonth ->
                    viewModel.setCurrentMonth(yearMonth) // Update ViewModel with the selected month
                    navController.popBackStack() // Navigate back
                }
            )
        }
    }
}

/**
 * Displays a grid containing all 12 months of a given year.
 *
 * This Composable arranges the months in a 3-column vertical grid. Each item
 * in the grid is a [MonthPixelGrid] representing a single month.
 *
 * @param year The integer value of the year to display.
 * @param entriesMap A map of [LocalDate] to [DayEntry] for quick lookup of mood data.
 * @param onMonthClick A callback function that is invoked with the selected [YearMonth]
 *                     when a month's grid is clicked.
 */
@Composable
private fun YearAsPixelsGrid(
    year: Int,
    entriesMap: Map<LocalDate, DayEntry>,
    onMonthClick: (YearMonth) -> Unit
) {
    // A grid with 3 fixed columns
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(Month.values()) { month ->
            MonthPixelGrid(
                yearMonth = YearMonth.of(year, month),
                entriesMap = entriesMap,
                onMonthClick = { onMonthClick(YearMonth.of(year, month)) }
            )
        }
    }
}


/**
 * Renders a single month as a small grid of colored pixels, where each pixel is a day.
 *
 * This Composable displays the short name of the month above a 7-column grid
 * representing the days. The color of each day is determined by the corresponding
 * [DayEntry]. Empty cells are used for padding at the beginning of the month.
 *
 * @param yearMonth The [YearMonth] to be displayed.
 * @param entriesMap A map of [LocalDate] to [DayEntry] containing the mood data.
 * @param onMonthClick A lambda function that is executed when the month grid is clicked.
 */
@Composable
private fun MonthPixelGrid(
    yearMonth: YearMonth,
    entriesMap: Map<LocalDate, DayEntry>,
    onMonthClick: () -> Unit
) {
    val firstDayOfMonth = yearMonth.atDay(1)
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value // 1 (Mon) - 7 (Sun)
    val daysInMonth = yearMonth.lengthOfMonth()
    val emptyCells = (firstDayOfWeek - 1)

    Column(
        modifier = Modifier.clickable(onClick = onMonthClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Month name (Jan, Feb...)
        Text(
            text = yearMonth.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // 7-column pixel grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            userScrollEnabled = false,
            modifier = Modifier.height(60.dp) // 6 rows * 10dp
        ) {
            items(emptyCells) {
                Box(Modifier.size(8.dp)) // Empty space for alignment
            }
            items(daysInMonth) { dayIndex ->
                val date = yearMonth.atDay(dayIndex + 1)
                val entry = entriesMap[date]

                // Draw a colored "pixel" for the day
                Box(
                    modifier = Modifier
                        .size(8.dp) // Total cell size 8x8
                        .padding(1.dp) // Padding to make the circle 6x6
                        .background(
                            color = if (entry != null) {
                                getBlendedColor(
                                    entry.morningRating,
                                    entry.afternoonRating,
                                    entry.eveningRating
                                )
                            } else {
                                Color.LightGray
                            },
                            shape = CircleShape
                        )
                )
            }
        }
    }
}


/**
 * A custom top app bar for the yearly overview screen.
 *
 * It features a navigation icon that acts as a back button to the monthly view,
 * displaying the name of the current month. It also includes action icons for
 * search and settings.
 *
 * @param monthShortName The short name of the current month (e.g., "Oct") to be displayed
 *                       next to the back arrow.
 * @param onMonthClick A lambda function to be executed when the navigation icon area is clicked.
 * @param onSearchClick A lambda function to be executed when the search icon is clicked.
 * @param onSettingsClick A lambda function to be executed when the settings icon is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun YearTopAppBar(
    monthShortName: String,
    onMonthClick: () -> Unit,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = { /* No title */ },
        // "< Oct" button
        navigationIcon = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable(onClick = onMonthClick)
                    .padding(start = 12.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go to Month View"
                )
                Spacer(Modifier.padding(horizontal = 2.dp))
                Text(
                    text = monthShortName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
            }
        },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Default.Search, "Search")
            }
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, "Settings")
            }
        }
    )
}