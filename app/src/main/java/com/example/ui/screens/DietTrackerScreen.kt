package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.CheckCircle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.NurViewModel

data class PresetMeal(
  val name: String,
  val type: String, // Breakfast, Lunch, Dinner, Snack
  val calories: Int,
  val carbs: Int,
  val protein: Int,
  val fat: Int
)

@Composable
fun DietTrackerScreen(
  viewModel: NurViewModel,
  modifier: Modifier = Modifier
) {
  val appState by viewModel.appState.collectAsState()
  val state = appState ?: return

  val isAnalyzingImage by viewModel.isAnalyzingImage.collectAsState()
  val imageAnalysisError by viewModel.imageAnalysisError.collectAsState()
  val imageAnalysisSuccess by viewModel.imageAnalysisSuccess.collectAsState()

  val context = LocalContext.current

  // Camera launcher to capture food image
  val cameraLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.TakePicturePreview()
  ) { bitmap: android.graphics.Bitmap? ->
    if (bitmap != null) {
      viewModel.analyzeAndLogMeal(bitmap)
    }
  }

  // Permission launcher to request CAMERA permission
  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    if (isGranted) {
      cameraLauncher.launch(null)
    }
  }

  var showCustomDialog by remember { mutableStateOf(false) }
  var customMealName by remember { mutableStateOf("") }
  var customCalories by remember { mutableStateOf("") }
  var customCarbs by remember { mutableStateOf("") }
  var customProtein by remember { mutableStateOf("") }
  var customFat by remember { mutableStateOf("") }

  val presets = listOf(
    PresetMeal("Oatmeal with Berries", "Breakfast", 280, 45, 10, 5),
    PresetMeal("Avocado Toast", "Breakfast", 320, 38, 12, 14),
    PresetMeal("Grilled Chicken Salad", "Lunch", 420, 15, 42, 12),
    PresetMeal("Brown Rice & Salmon", "Dinner", 580, 42, 38, 18),
    PresetMeal("Greek Yogurt Bowl", "Snack", 190, 12, 18, 4),
    PresetMeal("Protein Shake", "Snack", 220, 8, 30, 3)
  )

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(24.dp)
  ) {
    // 1. Header Navigation Bar
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = { viewModel.navigateTo("dashboard") },
          modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), CircleShape)
        ) {
          Icon(
            imageVector = Icons.Default.ChevronLeft,
            contentDescription = "Back",
            tint = MaterialTheme.colorScheme.primary
          )
        }

        Text(
          text = "Diet Tracker",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )

        IconButton(
          onClick = { viewModel.resetDiet() },
          modifier = Modifier.background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f), CircleShape)
        ) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "Reset Diet",
            tint = MaterialTheme.colorScheme.error
          )
        }
      }
    }

    // 1b. Diet & Hydration Dynamic Configurator Card
    item {
      Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.12f)
        ),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "Personalized Diet & Hydration Plan",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )
          Text(
            text = "Configure your parameters to calculate dynamic goals",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
          )

          // 1. Fitness Goal Selector (chips)
          Text(
            text = "Fitness Goal",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 6.dp)
          )
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            listOf("Lose Weight", "Maintain Weight", "Gain Muscle").forEach { goal ->
              val isSel = state.fitnessGoal == goal
              Box(
                modifier = Modifier
                  .weight(1f)
                  .background(
                    color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp)
                  )
                  .clickable { viewModel.updateFitnessGoal(goal) }
                  .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = goal.replace(" Weight", ""),
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isSel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // 2. Weight and Weather Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            // Body Weight Control
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Body Weight",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 6.dp)
              )
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                  .padding(horizontal = 6.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                IconButton(
                  onClick = { viewModel.updateUserWeight(maxOf(30f, state.userWeightKg - 1f)) },
                  modifier = Modifier.size(28.dp)
                ) {
                  Text("-", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                }
                Text(
                  text = "${String.format("%.0f", state.userWeightKg)} kg",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(
                  onClick = { viewModel.updateUserWeight(minOf(200f, state.userWeightKg + 1f)) },
                  modifier = Modifier.size(28.dp)
                ) {
                  Text("+", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                }
              }
            }

            // Weather Temp Control
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Weather Temp",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 6.dp)
              )
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                  .padding(horizontal = 6.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                IconButton(
                  onClick = { viewModel.updateWeatherTemp(maxOf(0f, state.weatherTempC - 1f)) },
                  modifier = Modifier.size(28.dp)
                ) {
                  Text("-", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                }
                Text(
                  text = "${String.format("%.0f", state.weatherTempC)}°C",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(
                  onClick = { viewModel.updateWeatherTemp(minOf(50f, state.weatherTempC + 1f)) },
                  modifier = Modifier.size(28.dp)
                ) {
                  Text("+", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // 3. Calculated targets feedback box
          val minWater = state.userWeightKg * 30f + (if (state.weatherTempC > 30f) (state.weatherTempC - 30f) * 50f else 0f)
          val maxWater = state.userWeightKg * 45f + (if (state.weatherTempC > 30f) (state.weatherTempC - 30f) * 100f else 0f)

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(14.dp))
              .padding(12.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(
                text = "⚡ Plan Recommendations:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
              Text(
                text = "• Diet: Active goal is ${state.fitnessGoal} target of ${state.dietCalorieGoal} kcal.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = "• Hydration: At ${String.format("%.0f", state.weatherTempC)}°C, standard base intake scales up to a recommended range of ${String.format("%.0f", minWater)} - ${String.format("%.0f", maxWater)} ml/day.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }
    }

    // 2. Calorie Progress Radial Ring Display
    item {
      val calorieRatio = (state.dietCaloriesConsumed.toFloat() / state.dietCalorieGoal.toFloat()).coerceIn(0f, 1f)
      val remaining = maxOf(0, state.dietCalorieGoal - state.dietCaloriesConsumed)

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier.size(180.dp),
            contentAlignment = Alignment.Center
          ) {
            val primaryColor = MaterialTheme.colorScheme.primary
            val trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)

            Canvas(modifier = Modifier.fillMaxSize()) {
              drawCircle(
                color = trackColor,
                radius = size.minDimension / 2,
                style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
              )
              drawArc(
                brush = Brush.sweepGradient(
                  colors = listOf(primaryColor.copy(alpha = 0.6f), primaryColor)
                ),
                startAngle = -90f,
                sweepAngle = calorieRatio * 360f,
                useCenter = false,
                style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
              )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(
                imageVector = Icons.Default.LocalFireDepartment,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "${state.dietCaloriesConsumed}",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "of ${state.dietCalorieGoal} kcal",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "Remaining", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Text(text = "$remaining kcal", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            Box(
              modifier = Modifier
                .width(1.dp)
                .height(32.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "Status", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Text(
                text = if (calorieRatio >= 1.0f) "Goal Achieved!" else "Keep Going!",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (calorieRatio >= 1.0f) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }
      }
    }

    // 3. Macros Breakdown Sliders
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          Text(
            text = "Daily Macronutrients",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )

          // Protein Progress
          val proteinRatio = (state.dietProteinConsumedG.toFloat() / state.dietProteinGoalG.toFloat()).coerceIn(0f, 1f)
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Egg,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp),
                  tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Protein", fontSize = 14.sp, fontWeight = FontWeight.Medium)
              }
              Text(text = "${state.dietProteinConsumedG}g / ${state.dietProteinGoalG}g", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
              progress = { proteinRatio },
              modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
              color = MaterialTheme.colorScheme.primary,
              trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
          }

          // Carbs Progress
          val carbsRatio = (state.dietCarbsConsumedG.toFloat() / state.dietCarbsGoalG.toFloat()).coerceIn(0f, 1f)
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Fastfood,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp),
                  tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Carbs", fontSize = 14.sp, fontWeight = FontWeight.Medium)
              }
              Text(text = "${state.dietCarbsConsumedG}g / ${state.dietCarbsGoalG}g", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
              progress = { carbsRatio },
              modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
              color = MaterialTheme.colorScheme.secondary,
              trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
          }

          // Fat Progress
          val fatRatio = (state.dietFatConsumedG.toFloat() / state.dietFatGoalG.toFloat()).coerceIn(0f, 1f)
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Spa,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp),
                  tint = MaterialTheme.colorScheme.tertiary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Fat", fontSize = 14.sp, fontWeight = FontWeight.Medium)
              }
              Text(text = "${state.dietFatConsumedG}g / ${state.dietFatGoalG}g", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
              progress = { fatRatio },
              modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
              color = MaterialTheme.colorScheme.tertiary,
              trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
          }
        }
      }
    }

    // 3b. AI Meal Scanner Card
    item {
      Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.15f)
        ),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(24.dp)
              )
              Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  Text(
                    text = "AI Meal Scanner",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
                  )
                  Box(
                    modifier = Modifier
                      .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(6.dp))
                      .padding(horizontal = 6.dp, vertical = 2.dp)
                  ) {
                    Text(
                      text = "Gemini",
                      fontSize = 8.sp,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.onTertiary
                    )
                  }
                }
                Text(
                  text = "Track automatically from photo",
                  fontSize = 12.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = "Snap a photo of your food or meal. Gemini will analyze the image, identify the item, and automatically log its calories, protein, carbs, and fat. Non-food photos are detected and rejected to ensure clean logs.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 16.sp,
            modifier = Modifier.padding(bottom = 12.dp)
          )

          // Actions
          if (isAnalyzingImage) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                .padding(12.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.tertiary,
                strokeWidth = 2.5.dp
              )
              Text(
                text = "Analyzing image via Gemini AI...",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          } else {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Button(
                onClick = {
                  viewModel.clearAnalysisMessages()
                  val hasPermission = ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.CAMERA
                  ) == PackageManager.PERMISSION_GRANTED
                  if (hasPermission) {
                    cameraLauncher.launch(null)
                  } else {
                    permissionLauncher.launch(android.Manifest.permission.CAMERA)
                  }
                },
                modifier = Modifier.weight(1f).testTag("capture_meal_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
              ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Capture Meal", fontSize = 13.sp, color = MaterialTheme.colorScheme.onTertiary)
              }
            }
          }

          // Error Message Display
          imageAnalysisError?.let { err ->
            Spacer(modifier = Modifier.height(12.dp))
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .padding(12.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Error",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(20.dp)
              )
              Text(
                text = err,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.error,
                lineHeight = 16.sp,
                modifier = Modifier.weight(1f)
              )
              IconButton(
                onClick = { viewModel.clearAnalysisMessages() },
                modifier = Modifier.size(20.dp)
              ) {
                Text("×", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
              }
            }
          }

          // Success Message Display
          imageAnalysisSuccess?.let { msg ->
            Spacer(modifier = Modifier.height(12.dp))
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .padding(12.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Success",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
              )
              Text(
                text = msg,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary,
                lineHeight = 16.sp,
                modifier = Modifier.weight(1f)
              )
              IconButton(
                onClick = { viewModel.clearAnalysisMessages() },
                modifier = Modifier.size(20.dp)
              ) {
                Text("×", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
              }
            }
          }
        }
      }
    }

    // 4. Quick Logging Section
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Quick Log Premium Presets",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )

        Button(
          onClick = { showCustomDialog = true },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
          shape = RoundedCornerShape(12.dp),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
          modifier = Modifier.height(36.dp).testTag("custom_meal_button")
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = "Custom", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimary)
        }
      }
    }

    // Preset Meals Grid
    items(presets.chunked(2)) { pair ->
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        for (preset in pair) {
          Card(
            modifier = Modifier
              .weight(1f)
              .clickable {
                viewModel.logMeal(preset.name, preset.calories, preset.carbs, preset.protein, preset.fat)
              },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text(
                text = preset.type.uppercase(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = preset.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
              )
              Spacer(modifier = Modifier.height(8.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "${preset.calories} kcal",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(
                  modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(14.dp)
                  )
                }
              }
            }
          }
        }
      }
    }

    // 5. Hydration Side Card
    item {
      val calculatedMinWater = state.userWeightKg * 30f + (if (state.weatherTempC > 30f) (state.weatherTempC - 30f) * 50f else 0f)
      val calculatedMaxWater = state.userWeightKg * 45f + (if (state.weatherTempC > 30f) (state.weatherTempC - 30f) * 100f else 0f)
      val dynamicWaterTarget = (calculatedMinWater + calculatedMaxWater) / 2f
      val waterProg = (state.waterIntakeMl / dynamicWaterTarget).coerceIn(0f, 1f)
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.08f))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.WaterDrop,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.secondary,
              modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "Hydration Tracker",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "${String.format("%.1f", state.waterIntakeMl / 1000f)}L of ${String.format("%.1f", dynamicWaterTarget / 1000f)}L Target\nRange: ${String.format("%.0f", calculatedMinWater)}-${String.format("%.0f", calculatedMaxWater)} ml",
                fontSize = 11.sp,
                lineHeight = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Button(
            onClick = { viewModel.incrementWater() },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.height(36.dp)
          ) {
            Text(text = "+250ml", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSecondary)
          }
        }
      }
    }
  }

  // 6. Custom Meal Logging Dialog
  if (showCustomDialog) {
    AlertDialog(
      onDismissRequest = { showCustomDialog = false },
      title = { Text(text = "Log Custom Meal", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          OutlinedTextField(
            value = customMealName,
            onValueChange = { customMealName = it },
            label = { Text("Meal Name") },
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = customCalories,
            onValueChange = { customCalories = it },
            label = { Text("Calories (kcal)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
          )
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
              value = customProtein,
              onValueChange = { customProtein = it },
              label = { Text("Protein (g)") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
              value = customCarbs,
              onValueChange = { customCarbs = it },
              label = { Text("Carbs (g)") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
              value = customFat,
              onValueChange = { customFat = it },
              label = { Text("Fat (g)") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              modifier = Modifier.weight(1f)
            )
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val name = customMealName.ifEmpty { "Custom Meal" }
            val cals = customCalories.toIntOrNull() ?: 0
            val carbs = customCarbs.toIntOrNull() ?: 0
            val protein = customProtein.toIntOrNull() ?: 0
            val fat = customFat.toIntOrNull() ?: 0

            viewModel.logMeal(name, cals, carbs, protein, fat)

            // Reset local dialog inputs
            customMealName = ""
            customCalories = ""
            customCarbs = ""
            customProtein = ""
            customFat = ""
            showCustomDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
          Text("Log Meal", color = Color.White)
        }
      },
      dismissButton = {
        Button(
          onClick = { showCustomDialog = false },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
          Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      },
      shape = RoundedCornerShape(24.dp)
    )
  }
}
