package com.example.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.viewmodel.NurViewModel
import com.example.viewmodel.FakeRepository
import com.example.data.NurAppState

/**
 * Collects @Preview composables for the main UI screens.
 * Open this file in Android Studio and the Compose Preview pane will render each preview.
 */

@Preview(showBackground = true, name = "Landing Screen Preview")
@Composable
fun LandingScreenPreview() {
    // Dummy view‑model with test data
    val viewModel = NurViewModel(FakeRepository())
    viewModel.setAppState(
        NurAppState(
            userName = "Amina",
            profilePicUri = null,
            xp = 200,
            streakDays = 3,
            waterIntakeMl = 1500f,
            workoutActiveMin = 20
        )
    )
    LandingScreen(viewModel = viewModel)
}

@Preview(showBackground = true, name = "User Profile Preview")
@Composable
fun UserProfileScreenPreview() {
    val viewModel = NurViewModel(FakeRepository())
    viewModel.setAppState(
        NurAppState(
            userName = "Amina",
            profilePicUri = null,
            xp = 350,
            streakDays = 7
        )
    )
    UserProfileScreen(viewModel = viewModel, onLogout = {})
}

@Preview(showBackground = true, name = "Achievements Preview")
@Composable
fun AchievementsScreenPreview() {
    // If AchievementsScreen needs a view‑model, provide a dummy one here.
    AchievementsScreen(onBack = {})
}
