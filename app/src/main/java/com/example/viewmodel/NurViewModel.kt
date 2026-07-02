package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.NurActivityLog
import com.example.data.NurAppState
import com.example.data.NurDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.sin

class NurViewModel(application: Application) : AndroidViewModel(application) {

  private val database = Room.databaseBuilder(
    application,
    NurDatabase::class.java,
    "nur_database"
  ).fallbackToDestructiveMigration().build()

  private val dao = database.nurDao()

  // Navigation State
  private val _currentRoute = MutableStateFlow("prayer_tracker")
  val currentRoute: StateFlow<String> = _currentRoute.asStateFlow()

  // Image analysis states for meal capturing feature
  private val _isAnalyzingImage = MutableStateFlow(false)
  val isAnalyzingImage: StateFlow<Boolean> = _isAnalyzingImage.asStateFlow()

  private val _imageAnalysisError = MutableStateFlow<String?>(null)
  val imageAnalysisError: StateFlow<String?> = _imageAnalysisError.asStateFlow()

  private val _imageAnalysisSuccess = MutableStateFlow<String?>(null)
  val imageAnalysisSuccess: StateFlow<String?> = _imageAnalysisSuccess.asStateFlow()

  private val _prayerErrorMessage = MutableStateFlow<String?>(null)
  val prayerErrorMessage: StateFlow<String?> = _prayerErrorMessage.asStateFlow()

  fun clearPrayerErrorMessage() {
    _prayerErrorMessage.value = null
  }

  fun clearAnalysisMessages() {
    _imageAnalysisError.value = null
    _imageAnalysisSuccess.value = null
  }

  // Dynamic Prayer location and Date offset for Pakistan PK
  private val _selectedPrayerCity = MutableStateFlow("Karachi")
  val selectedPrayerCity: StateFlow<String> = _selectedPrayerCity.asStateFlow()

  private val _selectedPrayerDayOffset = MutableStateFlow(0)
  val selectedPrayerDayOffset: StateFlow<Int> = _selectedPrayerDayOffset.asStateFlow()

  fun setPrayerCity(city: String) {
    _selectedPrayerCity.value = city
  }

  fun setPrayerDayOffset(offset: Int) {
    _selectedPrayerDayOffset.value = offset
  }

  fun getCalculatedPrayerTimes(): List<PakistanPrayerInfo> {
    val calendar = java.util.Calendar.getInstance()
    calendar.add(java.util.Calendar.DAY_OF_YEAR, _selectedPrayerDayOffset.value)
    val dayOfYear = calendar.get(java.util.Calendar.DAY_OF_YEAR)
    return PakistanPrayerCalculator.getPrayerTimes(_selectedPrayerCity.value, dayOfYear)
  }

  // App state from database
  val appState: StateFlow<NurAppState?> = dao.getAppStateFlow()
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = null
    )

  // Recent logs
  val activityLogs: StateFlow<List<NurActivityLog>> = dao.getActivityLogsFlow()
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  init {
    viewModelScope.launch {
      // Initialize with default state if empty
      val currentState = dao.getAppState()
      if (currentState == null) {
        val defaultState = NurAppState(
          isDarkMode = false,
          userName = "Sarah Jenkins",
          userXp = 12450,
          userLevel = 12,
          streakDays = 12,
          profileStreakDays = 14,
          fajrCompleted = true,
          dhuhrCompleted = false,
          asrCompleted = false,
          maghribCompleted = false,
          ishaCompleted = false,
          waterIntakeMl = 1200f,
          quranPages = 5,
          studyHours = 2.5f,
          techProgress = 60,
          workoutActiveMin = 42,
          workoutCalories = 320,
          workoutHeartRate = 145,
          workoutDurationSelected = 30,
          workoutActivitySelected = "Running",
          currentMood = "Feeling Excellent",
          dietCalorieGoal = 2000,
          dietCaloriesConsumed = 1250,
          dietCarbsGoalG = 250,
          dietCarbsConsumedG = 145,
          dietProteinGoalG = 130,
          dietProteinConsumedG = 85,
          dietFatGoalG = 65,
          dietFatConsumedG = 38,
          userWeightKg = 72.0f,
          weatherTempC = 35.0f,
          fitnessGoal = "Maintain Weight"
        )
        dao.insertAppState(defaultState)

        // Prepopulate activity logs to match screenshot visual representation exactly
        dao.insertActivityLog(
          NurActivityLog(
            title = "Fajr Prayer",
            subtitle = "Today, 5:30 AM",
            xpReward = 50,
            type = "PRAYER",
            timestamp = System.currentTimeMillis() - 1000 * 60 * 120 // 2h ago
          )
        )
        dao.insertActivityLog(
          NurActivityLog(
            title = "Morning Workout",
            subtitle = "Today, 6:15 AM",
            xpReward = 100,
            type = "WORKOUT",
            timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 3 // 3h ago
          )
        )
        dao.insertActivityLog(
          NurActivityLog(
            title = "Read 5 Pages",
            subtitle = "Yesterday, 9:00 PM",
            xpReward = 25,
            type = "QURAN",
            timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 24 // 24h ago
          )
        )
        dao.insertActivityLog(
          NurActivityLog(
            title = "Daily Reflection",
            subtitle = "Yesterday, 9:30 PM",
            xpReward = 30,
            type = "MOOD",
            timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 24 - 1000 * 60 * 30 // 24.5h ago
          )
        )
      }
    }
  }

  fun navigateTo(route: String) {
    _currentRoute.value = route
  }

  fun toggleDarkMode() {
    viewModelScope.launch {
      val state = dao.getAppState() ?: return@launch
      dao.updateAppState(state.copy(isDarkMode = !state.isDarkMode))
    }
  }

  // Trend Chart Tracker Features
  private val _selectedTrendPeriod = MutableStateFlow(TrendPeriod.WEEKLY)
  val selectedTrendPeriod: StateFlow<TrendPeriod> = _selectedTrendPeriod.asStateFlow()

  fun selectTrendPeriod(period: TrendPeriod) {
    _selectedTrendPeriod.value = period
  }

  fun getTrendData(period: TrendPeriod, currentCalories: Float, currentWater: Float): List<TrendDataPoint> {
    return if (period == TrendPeriod.WEEKLY) {
      listOf(
        TrendDataPoint("Mon", 280f, 1500f),
        TrendDataPoint("Tue", 420f, 2200f),
        TrendDataPoint("Wed", 150f, 1000f),
        TrendDataPoint("Thu", 490f, 2800f),
        TrendDataPoint("Fri", 310f, 1800f),
        TrendDataPoint("Sat", 550f, 3100f),
        TrendDataPoint("Today", currentCalories, currentWater)
      )
    } else {
      val list = mutableListOf<TrendDataPoint>()
      val days = 30
      for (i in 1..days) {
        if (i == days) {
          list.add(TrendDataPoint("Today", currentCalories, currentWater))
        } else {
          val label = String.format("%02d", i)
          val kcalSeed = 200f + (sin(i * 0.5) * 120f).toFloat() + (i % 3) * 40f
          val waterSeed = 1200f + (sin(i * 0.4) * 800f).toFloat() + (i % 4) * 250f
          list.add(TrendDataPoint(label, kcalSeed.coerceAtLeast(50f), waterSeed.coerceAtLeast(300f)))
        }
      }
      list
    }
  }

  fun togglePrayer(prayerName: String) {
    viewModelScope.launch {
      val state = dao.getAppState() ?: return@launch
      val isNowCompleted: Boolean
      val updated = when (prayerName.lowercase()) {
        "fajr" -> {
          isNowCompleted = !state.fajrCompleted
          state.copy(fajrCompleted = isNowCompleted)
        }
        "dhuhr" -> {
          isNowCompleted = !state.dhuhrCompleted
          state.copy(dhuhrCompleted = isNowCompleted)
        }
        "asr" -> {
          isNowCompleted = !state.asrCompleted
          state.copy(asrCompleted = isNowCompleted)
        }
        "maghrib" -> {
          isNowCompleted = !state.maghribCompleted
          state.copy(maghribCompleted = isNowCompleted)
        }
        "isha" -> {
          isNowCompleted = !state.ishaCompleted
          state.copy(ishaCompleted = isNowCompleted)
        }
        else -> return@launch
      }

      // Time-based authentication if user is marking prayer as completed
      if (isNowCompleted) {
        val dayOffset = _selectedPrayerDayOffset.value
        val city = _selectedPrayerCity.value

        if (dayOffset > 0) {
          _prayerErrorMessage.value = "Cannot complete $prayerName prayer for a future day!"
          return@launch
        } else if (dayOffset == 0) {
          val calendar = java.util.Calendar.getInstance()
          val dayOfYear = calendar.get(java.util.Calendar.DAY_OF_YEAR)
          val prayerTimes = PakistanPrayerCalculator.getPrayerTimes(city, dayOfYear)
          val matchedPrayer = prayerTimes.firstOrNull { it.name.equals(prayerName, ignoreCase = true) }
          
          if (matchedPrayer != null) {
            val currentHour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
            val currentMin = calendar.get(java.util.Calendar.MINUTE)
            val currentTotalMin = currentHour * 60 + currentMin
            val prayerTotalMin = matchedPrayer.hour * 60 + matchedPrayer.minute

            if (currentTotalMin < prayerTotalMin) {
              _prayerErrorMessage.value = "Cannot tick $prayerName prayer before its scheduled time of ${matchedPrayer.formattedTime}!"
              return@launch
            }
          }
        }
      }

      // Clear any previous error if toggle succeeds!
      _prayerErrorMessage.value = null

      // Add/subtract XP rewards
      val xpChange = if (isNowCompleted) 50 else -50
      val finalXp = maxOf(0, updated.userXp + xpChange)
      val calculatedLevel = finalXp / 1000 + 1

      dao.updateAppState(
        updated.copy(
          userXp = finalXp,
          userLevel = calculatedLevel
        )
      )

      if (isNowCompleted) {
        dao.insertActivityLog(
          NurActivityLog(
            title = "$prayerName Prayer",
            subtitle = "Completed just now",
            xpReward = 50,
            type = "PRAYER"
          )
        )
      }
    }
  }

  fun incrementWater() {
    viewModelScope.launch {
      val state = dao.getAppState() ?: return@launch
      val newWater = (state.waterIntakeMl + 250f).coerceAtMost(4000f)
      dao.updateAppState(state.copy(waterIntakeMl = newWater))

      // Trigger a log when target hit or just simple milestone logs
      if (newWater == 3000f) {
        val updated = dao.getAppState() ?: return@launch
        dao.updateAppState(updated.copy(userXp = updated.userXp + 50))
        dao.insertActivityLog(
          NurActivityLog(
            title = "Hydration Milestone",
            subtitle = "Reached 3.0L Water Target",
            xpReward = 50,
            type = "STUDY"
          )
        )
      }
    }
  }

  fun resetWater() {
    viewModelScope.launch {
      val state = dao.getAppState() ?: return@launch
      dao.updateAppState(state.copy(waterIntakeMl = 0f))
    }
  }

  fun incrementQuranPages() {
    viewModelScope.launch {
      val state = dao.getAppState() ?: return@launch
      val newPages = state.quranPages + 1
      val updated = state.copy(
        quranPages = newPages,
        userXp = state.userXp + 10
      )
      dao.updateAppState(updated)

      dao.insertActivityLog(
        NurActivityLog(
          title = "Read Quran",
          subtitle = "Read page $newPages",
          xpReward = 10,
          type = "QURAN"
        )
      )
    }
  }

  fun addStudyHours(hours: Float) {
    viewModelScope.launch {
      val state = dao.getAppState() ?: return@launch
      val newHours = (state.studyHours + hours).coerceAtMost(12f)
      val updated = state.copy(
        studyHours = newHours,
        userXp = state.userXp + 20
      )
      dao.updateAppState(updated)

      dao.insertActivityLog(
        NurActivityLog(
          title = "Productive Study",
          subtitle = "Tracked +${hours}h Study",
          xpReward = 20,
          type = "STUDY"
        )
      )
    }
  }

  fun increaseTechProgress() {
    viewModelScope.launch {
      val state = dao.getAppState() ?: return@launch
      val newProg = (state.techProgress + 5).coerceAtMost(100)
      dao.updateAppState(state.copy(techProgress = newProg))
    }
  }

  fun selectWorkoutDuration(minutes: Int) {
    viewModelScope.launch {
      val state = dao.getAppState() ?: return@launch
      dao.updateAppState(state.copy(workoutDurationSelected = minutes))
    }
  }

  fun selectWorkoutActivity(activity: String) {
    viewModelScope.launch {
      val state = dao.getAppState() ?: return@launch
      dao.updateAppState(state.copy(workoutActivitySelected = activity))
    }
  }

  fun startWorkout() {
    viewModelScope.launch {
      val state = dao.getAppState() ?: return@launch
      val workoutMin = state.workoutDurationSelected
      
      // Calculate calories burned dynamically based on MET * weight * (hours)
      val met = when (state.workoutActivitySelected) {
        "Gym" -> 5.0f
        "Running" -> 9.8f
        "Yoga" -> 3.0f
        "Cycling" -> 7.5f
        else -> 6.0f
      }
      val workoutCal = (met * state.userWeightKg * (workoutMin / 60f)).toInt()
      val newActiveMin = state.workoutActiveMin + workoutMin
      val newCalories = state.workoutCalories + workoutCal

      val finalXp = state.userXp + 100
      val calculatedLevel = finalXp / 1000 + 1

      dao.updateAppState(
        state.copy(
          workoutActiveMin = newActiveMin,
          workoutCalories = newCalories,
          userXp = finalXp,
          userLevel = calculatedLevel
        )
      )

      dao.insertActivityLog(
        NurActivityLog(
          title = "Workout session",
          subtitle = "${state.workoutActivitySelected} for ${workoutMin}m",
          xpReward = 100,
          type = "WORKOUT"
        )
      )
    }
  }

  fun cycleMood() {
    viewModelScope.launch {
      val state = dao.getAppState() ?: return@launch
      val nextMood = when (state.currentMood) {
        "Feeling Excellent" -> "Feeling Peaceful"
        "Feeling Peaceful" -> "Feeling Normal"
        "Feeling Normal" -> "Feeling Tired"
        "Feeling Tired" -> "Feeling Radiant"
        else -> "Feeling Excellent"
      }
      dao.updateAppState(state.copy(currentMood = nextMood))

      dao.insertActivityLog(
        NurActivityLog(
          title = "Mood Logged",
          subtitle = nextMood,
          xpReward = 10,
          type = "MOOD"
        )
      )
    }
  }

  fun analyzeAndLogMeal(bitmap: android.graphics.Bitmap) {
    viewModelScope.launch {
      _isAnalyzingImage.value = true
      _imageAnalysisError.value = null
      _imageAnalysisSuccess.value = null

      try {
        val result = com.example.api.GeminiClient.analyzeMealPhoto(bitmap)
        if (result.isFood) {
          val foodName = result.foodName ?: "Meal"
          logMeal(
            mealName = foodName,
            calories = result.calories,
            carbs = result.carbs,
            protein = result.protein,
            fat = result.fat
          )
          _imageAnalysisSuccess.value = "Logged $foodName: +${result.calories} kcal!"
        } else {
          _imageAnalysisError.value = result.error ?: "This does not look like a food image. Please snap a picture of your food!"
        }
      } catch (e: Exception) {
        _imageAnalysisError.value = "Failed to analyze image: ${e.localizedMessage}"
      } finally {
        _isAnalyzingImage.value = false
      }
    }
  }

  fun logMeal(mealName: String, calories: Int, carbs: Int, protein: Int, fat: Int) {
    viewModelScope.launch {
      val state = dao.getAppState() ?: return@launch
      val newCalories = (state.dietCaloriesConsumed + calories).coerceAtMost(5000)
      val newCarbs = (state.dietCarbsConsumedG + carbs).coerceAtMost(500)
      val newProtein = (state.dietProteinConsumedG + protein).coerceAtMost(300)
      val newFat = (state.dietFatConsumedG + fat).coerceAtMost(200)

      val xpEarned = 40
      val finalXp = state.userXp + xpEarned
      val calculatedLevel = finalXp / 1000 + 1

      dao.updateAppState(
        state.copy(
          dietCaloriesConsumed = newCalories,
          dietCarbsConsumedG = newCarbs,
          dietProteinConsumedG = newProtein,
          dietFatConsumedG = newFat,
          userXp = finalXp,
          userLevel = calculatedLevel
        )
      )

      dao.insertActivityLog(
        NurActivityLog(
          title = "Logged Meal: $mealName",
          subtitle = "+$calories kcal, +${protein}g Protein, +${carbs}g Carbs",
          xpReward = xpEarned,
          type = "DIET"
        )
      )
    }
  }

  fun resetDiet() {
    viewModelScope.launch {
      val state = dao.getAppState() ?: return@launch
      dao.updateAppState(
        state.copy(
          dietCaloriesConsumed = 0,
          dietCarbsConsumedG = 0,
          dietProteinConsumedG = 0,
          dietFatConsumedG = 0
        )
      )
    }
  }

  fun resetAllData() {
    viewModelScope.launch {
      dao.clearActivityLogs()
      val defaultState = NurAppState(
        isDarkMode = false,
        userName = "Sarah Jenkins",
        userXp = 12450,
        userLevel = 12,
        streakDays = 12,
        profileStreakDays = 14,
        fajrCompleted = true,
        dhuhrCompleted = false,
        asrCompleted = false,
        maghribCompleted = false,
        ishaCompleted = false,
        waterIntakeMl = 1200f,
        quranPages = 5,
        studyHours = 2.5f,
        techProgress = 60,
        workoutActiveMin = 42,
        workoutCalories = 320,
        workoutHeartRate = 145,
        workoutDurationSelected = 30,
        workoutActivitySelected = "Running",
        currentMood = "Feeling Excellent",
        dietCalorieGoal = 2000,
        dietCaloriesConsumed = 1250,
        dietCarbsGoalG = 250,
        dietCarbsConsumedG = 145,
        dietProteinGoalG = 130,
        dietProteinConsumedG = 85,
        dietFatGoalG = 65,
        dietFatConsumedG = 38
      )
      dao.insertAppState(defaultState)

      dao.insertActivityLog(
        NurActivityLog(
          title = "System Reset",
          subtitle = "All progress reset to demo defaults",
          xpReward = 0,
          type = "STUDY"
        )
      )
    }
  }

  fun updateUserWeight(weight: Float) {
    viewModelScope.launch {
      val state = dao.getAppState() ?: return@launch
      val targets = calculateDietTargets(weight, state.fitnessGoal)
      dao.updateAppState(
        state.copy(
          userWeightKg = weight,
          dietCalorieGoal = targets.calories,
          dietCarbsGoalG = targets.carbs,
          dietProteinGoalG = targets.protein,
          dietFatGoalG = targets.fat
        )
      )
    }
  }

  fun updateWeatherTemp(temp: Float) {
    viewModelScope.launch {
      val state = dao.getAppState() ?: return@launch
      dao.updateAppState(state.copy(weatherTempC = temp))
    }
  }

  fun updateFitnessGoal(goal: String) {
    viewModelScope.launch {
      val state = dao.getAppState() ?: return@launch
      val targets = calculateDietTargets(state.userWeightKg, goal)
      dao.updateAppState(
        state.copy(
          fitnessGoal = goal,
          dietCalorieGoal = targets.calories,
          dietCarbsGoalG = targets.carbs,
          dietProteinGoalG = targets.protein,
          dietFatGoalG = targets.fat
        )
      )
    }
  }

  data class DietTargets(val calories: Int, val carbs: Int, val protein: Int, val fat: Int)

  private fun calculateDietTargets(weight: Float, goal: String): DietTargets {
    val multiplier = when (goal) {
      "Lose Weight" -> 24
      "Gain Muscle" -> 36
      else -> 30
    }
    val proteinMultiplier = when (goal) {
      "Lose Weight" -> 2.2f
      "Gain Muscle" -> 2.4f
      else -> 2.0f
    }
    val fatMultiplier = when (goal) {
      "Lose Weight" -> 0.8f
      "Gain Muscle" -> 1.0f
      else -> 0.9f
    }
    val calorieGoal = (weight * multiplier).toInt()
    val proteinG = (weight * proteinMultiplier).toInt()
    val fatG = (weight * fatMultiplier).toInt()
    val carbsG = maxOf(20, (calorieGoal - (proteinG * 4 + fatG * 9)) / 4)
    return DietTargets(calorieGoal, carbsG, proteinG, fatG)
  }
}

data class PakistanPrayerInfo(
  val name: String,
  val hour: Int,
  val minute: Int,
  val formattedTime: String
)

object PakistanPrayerCalculator {
  fun getPrayerTimes(city: String, dayOfYear: Int): List<PakistanPrayerInfo> {
    // Smooth seasonal variation using a sine wave
    // Spring equinox is day 80. Summer solstice is day 172. Winter solstice is day 355.
    val theta = 2.0 * Math.PI * (dayOfYear - 80) / 365.0
    val sinTheta = Math.sin(theta)

    return when (city) {
      "Karachi" -> {
        val fajrMin = (5 * 60 + 0) - (40 * sinTheta).toInt()
        val dhuhrMin = (12 * 60 + 35) - (5 * Math.sin(2 * theta)).toInt()
        val asrMin = (16 * 60 + 35) + (35 * sinTheta).toInt()
        val maghribMin = (18 * 60 + 35) + (50 * sinTheta).toInt()
        val ishaMin = (20 * 60 + 0) + (45 * sinTheta).toInt()

        listOf(
          createPrayerInfo("Fajr", fajrMin),
          createPrayerInfo("Dhuhr", dhuhrMin),
          createPrayerInfo("Asr", asrMin),
          createPrayerInfo("Maghrib", maghribMin),
          createPrayerInfo("Isha", ishaMin)
        )
      }
      "Lahore" -> {
        val fajrMin = (4 * 60 + 40) - (55 * sinTheta).toInt()
        val dhuhrMin = (12 * 60 + 15) - (5 * Math.sin(2 * theta)).toInt()
        val asrMin = (16 * 60 + 20) + (50 * sinTheta).toInt()
        val maghribMin = (18 * 60 + 10) + (65 * sinTheta).toInt()
        val ishaMin = (19 * 60 + 42) + (62 * sinTheta).toInt()

        listOf(
          createPrayerInfo("Fajr", fajrMin),
          createPrayerInfo("Dhuhr", dhuhrMin),
          createPrayerInfo("Asr", asrMin),
          createPrayerInfo("Maghrib", maghribMin),
          createPrayerInfo("Isha", ishaMin)
        )
      }
      "Islamabad" -> {
        val fajrMin = (4 * 60 + 37) - (62 * sinTheta).toInt()
        val dhuhrMin = (12 * 60 + 18) - (5 * Math.sin(2 * theta)).toInt()
        val asrMin = (16 * 60 + 22) + (52 * sinTheta).toInt()
        val maghribMin = (18 * 60 + 12) + (72 * sinTheta).toInt()
        val ishaMin = (19 * 60 + 47) + (67 * sinTheta).toInt()

        listOf(
          createPrayerInfo("Fajr", fajrMin),
          createPrayerInfo("Dhuhr", dhuhrMin),
          createPrayerInfo("Asr", asrMin),
          createPrayerInfo("Maghrib", maghribMin),
          createPrayerInfo("Isha", ishaMin)
        )
      }
      "Peshawar" -> {
        val fajrMin = (4 * 60 + 42) - (62 * sinTheta).toInt()
        val dhuhrMin = (12 * 60 + 23) - (5 * Math.sin(2 * theta)).toInt()
        val asrMin = (16 * 60 + 27) + (53 * sinTheta).toInt()
        val maghribMin = (18 * 60 + 17) + (72 * sinTheta).toInt()
        val ishaMin = (19 * 60 + 52) + (67 * sinTheta).toInt()

        listOf(
          createPrayerInfo("Fajr", fajrMin),
          createPrayerInfo("Dhuhr", dhuhrMin),
          createPrayerInfo("Asr", asrMin),
          createPrayerInfo("Maghrib", maghribMin),
          createPrayerInfo("Isha", ishaMin)
        )
      }
      "Quetta" -> {
        val fajrMin = (5 * 60 + 0) - (48 * sinTheta).toInt()
        val dhuhrMin = (12 * 60 + 42) - (5 * Math.sin(2 * theta)).toInt()
        val asrMin = (16 * 60 + 45) + (42 * sinTheta).toInt()
        val maghribMin = (18 * 60 + 38) + (58 * sinTheta).toInt()
        val ishaMin = (20 * 60 + 5) + (52 * sinTheta).toInt()

        listOf(
          createPrayerInfo("Fajr", fajrMin),
          createPrayerInfo("Dhuhr", dhuhrMin),
          createPrayerInfo("Asr", asrMin),
          createPrayerInfo("Maghrib", maghribMin),
          createPrayerInfo("Isha", ishaMin)
        )
      }
      else -> {
        val fajrMin = (5 * 60 + 0) - (40 * sinTheta).toInt()
        val dhuhrMin = (12 * 60 + 35) - (5 * Math.sin(2 * theta)).toInt()
        val asrMin = (16 * 60 + 35) + (35 * sinTheta).toInt()
        val maghribMin = (18 * 60 + 35) + (50 * sinTheta).toInt()
        val ishaMin = (20 * 60 + 0) + (45 * sinTheta).toInt()

        listOf(
          createPrayerInfo("Fajr", fajrMin),
          createPrayerInfo("Dhuhr", dhuhrMin),
          createPrayerInfo("Asr", asrMin),
          createPrayerInfo("Maghrib", maghribMin),
          createPrayerInfo("Isha", ishaMin)
        )
      }
    }
  }

  private fun createPrayerInfo(name: String, totalMinutes: Int): PakistanPrayerInfo {
    val normalizedMinutes = (totalMinutes + 1440) % 1440
    val hr24 = normalizedMinutes / 60
    val min = normalizedMinutes % 60
    val amPm = if (hr24 >= 12) "PM" else "AM"
    val hr12 = when {
      hr24 == 0 -> 12
      hr24 > 12 -> hr24 - 12
      else -> hr24
    }
    val formattedStr = String.format("%02d:%02d %s", hr12, min, amPm)
    return PakistanPrayerInfo(name, hr24, min, formattedStr)
  }
}

enum class TrendPeriod {
  WEEKLY, MONTHLY
}

data class TrendDataPoint(
  val label: String,
  val caloriesBurned: Float,
  val hydrationMl: Float
)

