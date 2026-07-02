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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.NurViewModel

@Composable
fun WorkoutTrackerScreen(
  viewModel: NurViewModel,
  modifier: Modifier = Modifier
) {
  val appState by viewModel.appState.collectAsState()
  val state = appState ?: return

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
          text = "Workout Tracker",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )

        IconButton(onClick = { /* Demo only */ }) {
          Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = "Notifications",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }

    // 2. Title & Streak section
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
      ) {
        Column {
          Text(
            text = "Fitness Journey",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Ready to move?",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        // Fire streak indicator
        Column(horizontalAlignment = Alignment.End) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
          ) {
            Icon(
              imageVector = Icons.Default.LocalFireDepartment,
              contentDescription = "Fire",
              tint = MaterialTheme.colorScheme.tertiary,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "${state.streakDays}",
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.tertiary
            )
          }
          Text(
            text = "DAY STREAK",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.sp
          )
        }
      }
    }

    // 2b. Body Weight Controller Card
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
        ),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Your Body Weight",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
              Text(
                text = "Used for accurate calorie calculation",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Button(
                onClick = { viewModel.updateUserWeight(maxOf(30f, state.userWeightKg - 1f)) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.size(36.dp)
              ) {
                Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
              }
              
              Text(
                text = "${String.format("%.1f", state.userWeightKg)} kg",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              
              Button(
                onClick = { viewModel.updateUserWeight(minOf(200f, state.userWeightKg + 1f)) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.size(36.dp)
              ) {
                Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))
          
          val currentMet = when (state.workoutActivitySelected) {
            "Gym" -> 5.0f
            "Running" -> 9.8f
            "Yoga" -> 3.0f
            "Cycling" -> 7.5f
            else -> 6.0f
          }
          val calculatedKcal = (currentMet * state.userWeightKg * (state.workoutDurationSelected / 60f)).toInt()

          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.25f)
            ),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(56.dp)
                  .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.LocalFireDepartment,
                  contentDescription = "Calories",
                  tint = MaterialTheme.colorScheme.secondary,
                  modifier = Modifier.size(32.dp)
                )
              }

              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "~$calculatedKcal KCAL EST. BURN",
                  fontSize = 18.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = "Based on ${state.workoutActivitySelected} for ${state.workoutDurationSelected}m with ${String.format("%.1f", state.userWeightKg)}kg body weight (MET: ${currentMet})",
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  lineHeight = 15.sp
                )
              }
            }
          }
        }
      }
    }

    // 3. Active Minutes Concentric SVG Ring
    item {
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Box(
          modifier = Modifier.size(200.dp),
          contentAlignment = Alignment.Center
        ) {
          val secondaryColor = MaterialTheme.colorScheme.secondary
          val tertiaryColor = MaterialTheme.colorScheme.tertiary
          val outlineColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)

          Canvas(modifier = Modifier.fillMaxSize()) {
            // Track 1 (Outer Active Minutes)
            drawCircle(
              color = outlineColor,
              radius = size.minDimension / 2 - 12.dp.toPx(),
              style = Stroke(width = 8.dp.toPx())
            )
            drawArc(
              color = secondaryColor,
              startAngle = -90f,
              sweepAngle = 360f * (state.workoutActiveMin / 100f).coerceAtMost(1f),
              useCenter = false,
              style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
            )

            // Track 2 (Inner Ring decoration)
            drawCircle(
              color = outlineColor,
              radius = size.minDimension / 2 - 24.dp.toPx(),
              style = Stroke(width = 6.dp.toPx())
            )
            drawArc(
              color = tertiaryColor,
              startAngle = -90f,
              sweepAngle = 180f, // Demo inner ring progress
              useCenter = false,
              style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
            )
          }

          // Center Text
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "${state.workoutActiveMin}",
              fontSize = 44.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "ACTIVE MIN",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              letterSpacing = 1.sp
            )
          }
        }
      }
    }

    // 4. Quick Select Duration Card
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Duration",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 12.dp)
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            listOf(20, 30, 40, 60).forEach { mins ->
              val isSelected = state.workoutDurationSelected == mins
              Box(
                modifier = Modifier
                  .weight(1f)
                  .height(44.dp)
                  .background(
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                  )
                  .clickable { viewModel.selectWorkoutDuration(mins) },
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "${mins}m",
                  color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp,
                  fontFamily = FontFamily.Monospace
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Adjust Custom Duration",
              fontSize = 13.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = "${state.workoutDurationSelected} mins",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          }
          Slider(
            value = state.workoutDurationSelected.toFloat(),
            onValueChange = { viewModel.selectWorkoutDuration(it.toInt()) },
            valueRange = 5f..120f,
            steps = 22,
            colors = SliderDefaults.colors(
              thumbColor = MaterialTheme.colorScheme.primary,
              activeTrackColor = MaterialTheme.colorScheme.primary,
              inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.fillMaxWidth()
          )
        }
      }
    }

    // 5. Workout Activity chips
    item {
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = "Activity",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface,
          modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          WorkoutChip(
            label = "Gym",
            icon = Icons.Default.FitnessCenter,
            selected = state.workoutActivitySelected == "Gym",
            onClick = { viewModel.selectWorkoutActivity("Gym") }
          )
          WorkoutChip(
            label = "Running",
            icon = Icons.Default.DirectionsRun,
            selected = state.workoutActivitySelected == "Running",
            onClick = { viewModel.selectWorkoutActivity("Running") }
          )
          WorkoutChip(
            label = "Yoga",
            icon = Icons.Default.SelfImprovement,
            selected = state.workoutActivitySelected == "Yoga",
            onClick = { viewModel.selectWorkoutActivity("Yoga") }
          )
          WorkoutChip(
            label = "Cycling",
            icon = Icons.Default.DirectionsBike,
            selected = state.workoutActivitySelected == "Cycling",
            onClick = { viewModel.selectWorkoutActivity("Cycling") }
          )
        }
      }
    }

    // 6. Stats Row Bento Cards
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // KCAL card
        StatsBentoCard(
          modifier = Modifier.weight(1f),
          value = "${state.workoutCalories}",
          unit = "KCAL",
          icon = Icons.Default.LocalFireDepartment,
          iconColor = MaterialTheme.colorScheme.secondary
        )

        // MIN card
        StatsBentoCard(
          modifier = Modifier.weight(1f),
          value = "${state.workoutDurationSelected}:00",
          unit = "MIN",
          icon = Icons.Default.Timer,
          iconColor = MaterialTheme.colorScheme.tertiary
        )

        // BPM card
        StatsBentoCard(
          modifier = Modifier.weight(1f),
          value = "${state.workoutHeartRate}",
          unit = "BPM",
          icon = Icons.Default.MonitorHeart,
          iconColor = MaterialTheme.colorScheme.error
        )
      }
    }

    // 7. Start Workout Action Button
    item {
      Button(
        onClick = { viewModel.startWorkout() },
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Unspecified),
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(56.dp)
          .background(
            brush = Brush.horizontalGradient(
              colors = listOf(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.secondary
              )
            ),
            shape = RoundedCornerShape(24.dp)
          )
          .testTag("start_workout_button")
      ) {
        Text(
          text = "Start Workout",
          color = Color.White,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}

@Composable
fun WorkoutChip(
  label: String,
  icon: ImageVector,
  selected: Boolean,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .background(
        color = if (selected) MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(20.dp)
      )
      .clickable(onClick = onClick)
      .padding(horizontal = 14.dp, vertical = 8.dp),
    contentAlignment = Alignment.Center
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = if (selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.size(16.dp)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = label,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = if (selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}

@Composable
fun StatsBentoCard(
  modifier: Modifier = Modifier,
  value: String,
  unit: String,
  icon: ImageVector,
  iconColor: Color
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = iconColor,
        modifier = Modifier.size(24.dp)
      )
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = value,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        fontFamily = FontFamily.Monospace
      )
      Text(
        text = unit,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}
