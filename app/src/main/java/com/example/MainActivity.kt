package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AchievementsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DietTrackerScreen
import com.example.ui.screens.LandingScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.PrayerTrackerScreen
import com.example.ui.screens.UserProfileScreen
import com.example.ui.screens.WorkoutTrackerScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.NurViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val viewModel: NurViewModel = viewModel()
      val appState by viewModel.appState.collectAsState()
      val isDarkMode = appState?.isDarkMode ?: false

      MyApplicationTheme(darkTheme = isDarkMode) {
        val currentRoute by viewModel.currentRoute.collectAsState()

        Scaffold(
          modifier = Modifier.fillMaxSize(),
          bottomBar = {
            // Render Bottom Bar only when not on Login or Landing screen
            if (currentRoute != "login" && currentRoute != "landing") {
              BottomNavBar(
                currentRoute = currentRoute,
                onNavigate = { route -> viewModel.navigateTo(route) }
              )
            }
          }
        ) { innerPadding ->
          Box(
            modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding)
          ) {
            Crossfade(targetState = currentRoute, label = "screen_transition") { route ->
              when (route) {
                "login" -> LoginScreen(viewModel = viewModel)
                "landing" -> LandingScreen(viewModel = viewModel)
                "dashboard" -> DashboardScreen(viewModel = viewModel)
                "prayer_tracker" -> PrayerTrackerScreen(viewModel = viewModel)
                "workout_tracker" -> WorkoutTrackerScreen(viewModel = viewModel)
                "diet_tracker" -> DietTrackerScreen(viewModel = viewModel)
                "achievements" -> AchievementsScreen(viewModel = viewModel)
                "user_profile" -> UserProfileScreen(viewModel = viewModel)
                else -> DashboardScreen(viewModel = viewModel)
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun BottomNavBar(
  currentRoute: String,
  onNavigate: (String) -> Unit
) {
  // Translucent bottom navigation bar with Apple-like Glassmorphism look
  Card(
    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    modifier = Modifier
      .fillMaxWidth()
      .navigationBarsPadding()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(72.dp)
        .padding(horizontal = 16.dp),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Prayer Tab
      BottomNavItem(
        icon = Icons.Default.CalendarToday,
        label = "Prayer",
        isSelected = currentRoute == "prayer_tracker",
        onClick = { onNavigate("prayer_tracker") }
      )

      // Workout Tab
      BottomNavItem(
        icon = Icons.Default.QueryStats,
        label = "Workout",
        isSelected = currentRoute == "workout_tracker",
        onClick = { onNavigate("workout_tracker") }
      )

      // Diet Tab
      BottomNavItem(
        icon = Icons.Default.Restaurant,
        label = "Diet",
        isSelected = currentRoute == "diet_tracker",
        onClick = { onNavigate("diet_tracker") }
      )

      // Achievements Tab
      BottomNavItem(
        icon = Icons.Default.EmojiEvents,
        label = "Achievements",
        isSelected = currentRoute == "achievements",
        onClick = { onNavigate("achievements") }
      )

      // Account Tab
      BottomNavItem(
        icon = Icons.Default.Person,
        label = "Account",
        isSelected = currentRoute == "user_profile",
        onClick = { onNavigate("user_profile") }
      )
    }
  }
}

@Composable
fun BottomNavItem(
  icon: ImageVector,
  label: String,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .size(48.dp)
      .clip(CircleShape)
      .background(
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
      )
      .clickable(onClick = onClick),
    contentAlignment = Alignment.Center
  ) {
    Icon(
      imageVector = icon,
      contentDescription = label,
      tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.size(24.dp)
    )
  }
}
