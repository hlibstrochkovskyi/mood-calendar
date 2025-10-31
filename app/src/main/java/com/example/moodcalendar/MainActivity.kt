package com.example.moodcalendar


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.moodcalendar.ui.screens.MonthlyCalendarScreen
import com.example.moodcalendar.ui.screens.YearlyOverviewScreen
import com.example.moodcalendar.ui.theme.MoodCalendarTheme
import com.example.moodcalendar.viewmodel.CalendarViewModel
import com.example.moodcalendar.viewmodel.CalendarViewModelFactory

/**
 * The main and only activity for the Mood Calendar application.
 *
 * This activity serves as the entry point for the app. It sets up the Compose UI,
 * initializes the database and ViewModel, and configures the overall application theme
 * and edge-to-edge display.
 */
class MainActivity : ComponentActivity() {
    /**
     * Called when the activity is first created.
     *
     * This method handles the initial setup of the application. It enables edge-to-edge
     * display, sets the content to the main Composable theme, retrieves the DAO from the
     * Application class, creates the [CalendarViewModel] using its factory, and launches
     * the main app Composable, [MoodCalendarApp].
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     *                           being shut down, this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle).
     *                           Otherwise, it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoodCalendarTheme {
                // Retrieve the DAO from the custom Application instance.
                val application = (application as MoodCalendarApplication)
                val dao = application.dao

                // Instantiate the ViewModel using a factory to pass the DAO dependency.
                val viewModel: CalendarViewModel = viewModel(
                    factory = CalendarViewModelFactory(dao)
                )

                // Launch the main app composable, passing the shared ViewModel.
                MoodCalendarApp(viewModel)
            }
        }
    }
}

/**
 * The root Composable function that defines the app's structure and navigation.
 *
 * This function sets up the main layout using a [Scaffold] and configures the
 * navigation graph with a [NavHost]. It defines the different screens (routes)
 * of the application, such as the monthly and yearly views, and provides them
 * with the necessary [NavController] and [CalendarViewModel].
 *
 * @param viewModel The shared [CalendarViewModel] instance that provides state
 *                  and logic to all screens within the navigation graph.
 */
@Composable
fun MoodCalendarApp(viewModel: CalendarViewModel) {
    // Create and remember a NavController to manage navigation between screens.
    val navController = rememberNavController()

    // Scaffold provides a standard layout structure (e.g., for top bars, bottom bars).
    // It also automatically handles system screen insets (like the status bar)
    // and provides them as padding values.
    Scaffold { innerPadding ->

        // NavHost is the container for all navigation destinations.
        NavHost(
            navController = navController,
            startDestination = "month", // The first screen to be displayed.
            modifier = Modifier.padding(innerPadding) // Apply padding to avoid system bars.
        ) {
            // Defines the destination for the "month" route.
            composable(route = "month") {
                MonthlyCalendarScreen(
                    navController = navController,
                    viewModel = viewModel
                )
            }

            // Defines the destination for the "year" route.
            composable(route = "year") {
                YearlyOverviewScreen(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }
    }
}

