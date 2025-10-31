package com.example.moodcalendar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moodcalendar.data.DayEntry
import com.example.moodcalendar.data.MoodRating
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Color constants for mood ratings
val colorGood = Color(0xFF4CAF50)
val colorAverage = Color(0xFFFFC328)
val colorBad = Color(0xFFEC3B3B)

/**
 * A modal bottom sheet for rating a specific day.
 *
 * This Composable function provides a user interface for selecting mood ratings
 * for the morning, afternoon, and evening, as well as for adding notes about
 * what made the user happy or sad. It is designed to be displayed from the bottom
 * of the screen. The sheet is pre-populated with existing data if an entry for the
 * selected date already exists.
 *
 * @param selectedDate The [LocalDate] that the user is rating.
 * @param existingEntry An optional [DayEntry] containing pre-existing data for the selected date.
 * @param onDismiss A lambda function that is invoked when the user dismisses the bottom sheet.
 * @param onSave A callback function that is invoked when the user clicks the "Save" button.
 *               It passes the selected mood ratings and notes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RateDaySheet(
    selectedDate: LocalDate,
    existingEntry: DayEntry?,
    onDismiss: () -> Unit,
    onSave: (morning: MoodRating?, afternoon: MoodRating?, evening: MoodRating?, happy: String, sad: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Internal state for tracking user selections and inputs.
    var morning by remember { mutableStateOf(existingEntry?.morningRating) }
    var afternoon by remember { mutableStateOf(existingEntry?.afternoonRating) }
    var evening by remember { mutableStateOf(existingEntry?.eveningRating) }
    var happyNote by remember { mutableStateOf(existingEntry?.happyNote ?: "") }
    var sadNote by remember { mutableStateOf(existingEntry?.sadNote ?: "") }

    // This effect updates the sheet's state if the selected date or entry changes
    // while the sheet is composed.
    LaunchedEffect(selectedDate, existingEntry) {
        morning = existingEntry?.morningRating
        afternoon = existingEntry?.afternoonRating
        evening = existingEntry?.eveningRating
        happyNote = existingEntry?.happyNote ?: ""
        sadNote = existingEntry?.sadNote ?: ""
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Title (e.g., "Rate October 31")
            Text(
                text = "Rate ${selectedDate.format(DateTimeFormatter.ofPattern("MMMM d"))}",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(24.dp))

            // Mood selectors for different parts of the day.
            DayPartSelector(
                partName = "Morning",
                selectedRating = morning,
                onRatingSelected = { morning = it }
            )
            Spacer(Modifier.height(16.dp))
            DayPartSelector(
                partName = "Afternoon",
                selectedRating = afternoon,
                onRatingSelected = { afternoon = it }
            )
            Spacer(Modifier.height(16.dp))
            DayPartSelector(
                partName = "Evening",
                selectedRating = evening,
                onRatingSelected = { evening = it }
            )
            Spacer(Modifier.height(24.dp))

            // Text fields for notes.
            OutlinedTextField(
                value = happyNote,
                onValueChange = { happyNote = it },
                label = { Text("What made you happy 😊") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = sadNote,
                onValueChange = { sadNote = it },
                label = { Text("What made you sad 😔") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))

            // Save button
            Button(
                onClick = {
                    onSave(morning, afternoon, evening, happyNote, sadNote)
                    onDismiss() // Close the sheet after saving.
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Save", fontSize = 18.sp)
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

/**
 * A row component for selecting a mood rating for a specific part of the day.
 *
 * This Composable displays a label for the part of the day (e.g., "Morning")
 * followed by three [RatingButton] instances for "Good," "Average," and "Bad" moods.
 *
 * @param partName The name of the time period (e.g., "Morning", "Afternoon").
 * @param selectedRating The currently selected [MoodRating] for this part of the day.
 * @param onRatingSelected A callback function that is invoked with the new [MoodRating]
 *                         when a rating button is clicked.
 */
@Composable
private fun DayPartSelector(
    partName: String,
    selectedRating: MoodRating?,
    onRatingSelected: (MoodRating) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(partName, fontSize = 18.sp, fontWeight = FontWeight.Medium)
        Row {
            RatingButton(
                rating = MoodRating.GOOD,
                color = colorGood,
                isSelected = selectedRating == MoodRating.GOOD,
                onClick = { onRatingSelected(MoodRating.GOOD) }
            )
            Spacer(Modifier.padding(8.dp))
            RatingButton(
                rating = MoodRating.AVERAGE,
                color = colorAverage,
                isSelected = selectedRating == MoodRating.AVERAGE,
                onClick = { onRatingSelected(MoodRating.AVERAGE) }
            )
            Spacer(Modifier.padding(8.dp))
            RatingButton(
                rating = MoodRating.BAD,
                color = colorBad,
                isSelected = selectedRating == MoodRating.BAD,
                onClick = { onRatingSelected(MoodRating.BAD) }
            )
        }
    }
}

/**
 * A circular, colored button used for selecting a mood rating.
 *
 * This Composable displays a colored circle that represents a mood. It shows a
 * border to indicate when it is selected.
 *
 * @param rating The [MoodRating] this button represents.
 * @param color The background [Color] of the button.
 * @param isSelected A boolean indicating whether this button is currently selected.
 * @param onClick A lambda function that is executed when the button is clicked.
 */
@Composable
private fun RatingButton(
    rating: MoodRating,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(color)
            .clickable(onClick = onClick)
            .border(
                width = if (isSelected) 3.dp else 0.dp,
                color = Color.Black, // Border color when selected
                shape = CircleShape
            )
    )
}