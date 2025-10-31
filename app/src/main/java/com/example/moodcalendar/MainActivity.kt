package com.example.moodcalendar


import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.moodcalendar.ui.screens.MonthlyCalendarScreen
import com.example.moodcalendar.ui.screens.YearlyOverviewScreen
import com.example.moodcalendar.ui.theme.MoodCalendarTheme
import com.example.moodcalendar.viewmodel.CalendarViewModel
import com.example.moodcalendar.viewmodel.CalendarViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoodCalendarTheme {
                // Get our DAO from the Application instance
                val application = (application as MoodCalendarApplication)
                val dao = application.dao

                // Create the ViewModel using our Factory
                val viewModel: CalendarViewModel = viewModel(
                    factory = CalendarViewModelFactory(dao)
                )

                // Launch the main app composable
                MoodCalendarApp(viewModel)
            }
        }
    }
}

// This is our main Composable that manages navigation
@Composable
fun MoodCalendarApp(viewModel: CalendarViewModel) {
    // NavController acts as the "remote control" for navigating between screens
    val navController = rememberNavController()

    // 1. Add a Scaffold (layout structure)
    // It automatically applies system insets (innerPadding)
    Scaffold { innerPadding ->

        // NavHost is the "container" that displays the appropriate screen
        NavHost(
            navController = navController,
            startDestination = "month", // Start with the monthly screen

            // 2. Apply padding from Scaffold to the NavHost
            modifier = Modifier.padding(innerPadding)
        ) {
            // First screen: "month"
            composable(route = "month") {
                MonthlyCalendarScreen() // Now it's placed within the padding area
            }

            // Second screen: "year"
            composable(route = "year") {
                YearlyOverviewScreen()
            }
        }
    }
}

