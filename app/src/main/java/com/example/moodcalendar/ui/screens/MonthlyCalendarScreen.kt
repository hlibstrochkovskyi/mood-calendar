package com.example.moodcalendar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moodcalendar.data.DayEntry
import com.example.moodcalendar.ui.util.getBlendedColor
import com.example.moodcalendar.viewmodel.CalendarViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

/**
 * 1. ГЛАВНЫЙ ЭКРАН
 */
@Composable
fun MonthlyCalendarScreen(
    navController: NavController,
    viewModel: CalendarViewModel
) {
    val currentMonth by viewModel.currentYearMonth.collectAsState()

    val entriesFlow = viewModel.entriesForMonth
    val entries by entriesFlow.collectAsState(initial = emptyList())
    val entriesMap = entries.associateBy { it.date }

    val monthName = currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        .replaceFirstChar { it.titlecase(Locale.getDefault()) }
    val year = currentMonth.year.toString()

    Scaffold(
        topBar = {
            CalendarTopAppBar(
                monthName = monthName,
                year = year,
                onYearClick = { navController.navigate("year") },
                onSearchClick = { /* TODO: Search */ },
                onSettingsClick = { /* TODO: Settings */ }
            )
        },
        floatingActionButton = {
            RateDayButton(
                date = "31 October", // TODO
                onClick = { /* TODO */ }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            CalendarGrid(
                yearMonth = currentMonth,
                entriesMap = entriesMap,
                onDayClick = { date ->
                    // TODO: Открыть модальное окно для этой даты
                }
            )
        }
    }
}

/**
 * 2. ВЕРХНЯЯ ПАНЕЛЬ (TopAppBar)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarTopAppBar(
    monthName: String,
    year: String,
    onYearClick: () -> Unit,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = monthName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onYearClick) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go to Year View"
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(text = year, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
            }
        },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            }
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            }
        }
    )
}

/**
 * 3. НИЖНЯЯ КНОПКА (Rate Day)
 */
@Composable
private fun RateDayButton(
    date: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(0.8f) // 80% ширины
        ) {
            Text(
                text = "Rate Day $date",
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * 4. СЕТКА КАЛЕНДАРЯ
 */
@Composable
fun CalendarGrid(
    yearMonth: YearMonth,
    entriesMap: Map<LocalDate, DayEntry>,
    onDayClick: (LocalDate) -> Unit
) {
    val daysOfWeek = listOf("M", "T", "W", "T", "F", "S", "S")

    val firstDayOfMonth = yearMonth.atDay(1)
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value // 1 (Пн) - 7 (Вс)
    val daysInMonth = yearMonth.lengthOfMonth()

    val emptyCells = (firstDayOfWeek - 1)

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.padding(4.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(7)
        ) {
            items(emptyCells) {
                Box(modifier = Modifier.aspectRatio(1f))
            }

            items(daysInMonth) { dayIndex ->
                val day = dayIndex + 1
                val date = yearMonth.atDay(day)
                val entry = entriesMap[date]

                DayCell(
                    day = day.toString(),
                    entry = entry,
                    onClick = { onDayClick(date) }
                )
            }
        }
    }
}

/**
 * 5. ЯЧЕЙКА ДНЯ
 */
@Composable
fun DayCell(
    day: String,
    entry: DayEntry?,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .clip(CircleShape)
            .background(
                color = if (entry != null) {
                    getBlendedColor(entry.morningRating, entry.afternoonRating, entry.eveningRating)
                } else {
                    Color.LightGray
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}