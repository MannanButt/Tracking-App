package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.layout.widthIn
import kotlin.math.roundToInt
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.NurViewModel
import kotlin.math.sin

@Composable
fun DashboardScreen(
  viewModel: NurViewModel,
  modifier: Modifier = Modifier
) {
  val appState by viewModel.appState.collectAsState()
  val state = appState ?: return

  // Calculate dynamic overall completion progress
  val waterProg = (state.waterIntakeMl / 3000f).coerceIn(0f, 1f)
  val studyProg = (state.studyHours / 10f).coerceIn(0f, 1f)
  val techProg = (state.techProgress / 100f).coerceIn(0f, 1f)
  val quranProg = (state.quranPages / 20f).coerceIn(0f, 1f)
  val prayerCount = listOf(
    state.fajrCompleted,
    state.dhuhrCompleted,
    state.asrCompleted,
    state.maghribCompleted,
    state.ishaCompleted
  ).count { it }
  val prayerProg = prayerCount / 5f

  val overallProg = ((waterProg + studyProg + techProg + quranProg + prayerProg) / 5f * 100).toInt()

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Shared Custom Header Component
    DashboardHeader(
      isDarkMode = state.isDarkMode,
      onToggleDarkMode = { viewModel.toggleDarkMode() }
    )

    LazyVerticalGrid(
      columns = GridCells.Fixed(2),
      modifier = Modifier
        .fillMaxSize()
        .weight(1f),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
      horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Overall Progress Ring Card
      item(span = { GridItemSpan(2) }) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Box(
            modifier = Modifier.size(160.dp),
            contentAlignment = Alignment.Center
          ) {
            val primaryColor = MaterialTheme.colorScheme.primary
            val outlineColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
            Canvas(modifier = Modifier.fillMaxSize()) {
              drawCircle(
                color = outlineColor,
                radius = size.minDimension / 2 - 8.dp.toPx(),
                style = Stroke(width = 12.dp.toPx())
              )
              drawArc(
                color = primaryColor,
                startAngle = -90f,
                sweepAngle = 360f * (overallProg / 100f),
                useCenter = false,
                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
              )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "$overallProg%",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Overall",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          Text(
            text = "\"Small steps, every day.\"",
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
          )
        }
      }

      // 2. Streak Counter Card
      item(span = { GridItemSpan(2) }) {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
          ),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "🔥",
                fontSize = 24.sp
              )
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "${state.streakDays} Day Streak",
                  fontWeight = FontWeight.Bold,
                  fontSize = 16.sp,
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = "Keep it up!",
                  fontSize = 12.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            // Streak Visualizer Bars (Matching the mockup exact count)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              Box(modifier = Modifier.size(width = 8.dp, height = 32.dp).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp)))
              Box(modifier = Modifier.size(width = 8.dp, height = 32.dp).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp)))
              Box(modifier = Modifier.size(width = 8.dp, height = 32.dp).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp)))
              Box(modifier = Modifier.size(width = 8.dp, height = 32.dp).background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), RoundedCornerShape(4.dp)))
              Box(modifier = Modifier.size(width = 8.dp, height = 32.dp).background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), RoundedCornerShape(4.dp)))
            }
          }
        }
      }

      // 2b. Combined Core Goals (Prayers & Workouts) Concentric Circular Progress
      item(span = { GridItemSpan(2) }) {
        CombinedGoalsCard(
          state = state,
          viewModel = viewModel
        )
      }

      // 3. Upcoming Prayer Card (Grid: Double Width on Mobile, or single depending on viewport. Let's match layout grid!)
      item {
        val selectedCity by viewModel.selectedPrayerCity.collectAsState()
        val prayerTimes = viewModel.getCalculatedPrayerTimes()

        val currentCal = java.util.Calendar.getInstance()
        val currentHour = currentCal.get(java.util.Calendar.HOUR_OF_DAY)
        val currentMin = currentCal.get(java.util.Calendar.MINUTE)

        val upcoming = prayerTimes.firstOrNull {
          it.hour > currentHour || (it.hour == currentHour && it.minute > currentMin)
        }
        val upcomingName = upcoming?.name ?: "Fajr"
        val upcomingTime = upcoming?.formattedTime ?: "05:12 AM"

        val countdownText = if (upcoming != null) {
          val diffMin = (upcoming.hour * 60 + upcoming.minute) - (currentHour * 60 + currentMin)
          val hours = diffMin / 60
          val mins = diffMin % 60
          String.format("-%02d:%02d:00", hours, mins)
        } else {
          val tomorrowFajrMin = 5 * 60 + 12
          val minutesLeft = (1440 - (currentHour * 60 + currentMin)) + tomorrowFajrMin
          val hours = minutesLeft / 60
          val mins = minutesLeft % 60
          String.format("-%02d:%02d:00", hours, mins)
        }

        DashboardBentoCard(
          title = "Upcoming ($selectedCity)",
          subtitle = "$upcomingName at $upcomingTime",
          icon = Icons.Default.Mosque,
          iconColor = MaterialTheme.colorScheme.primary,
          onClick = { viewModel.navigateTo("prayer_tracker") }
        ) {
          Box(
            modifier = Modifier
              .background(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                RoundedCornerShape(12.dp)
              )
              .padding(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Text(
              text = countdownText,
              color = MaterialTheme.colorScheme.primary,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              fontFamily = FontFamily.Monospace
            )
          }
        }
      }

      // 4. Workout Tracker Card
      item {
        DashboardBentoCard(
          title = "Workout",
          subtitle = "${state.workoutDurationSelected} min planned",
          icon = Icons.Default.FitnessCenter,
          iconColor = MaterialTheme.colorScheme.secondary,
          onClick = { viewModel.navigateTo("workout_tracker") }
        ) {
          Box(modifier = Modifier.size(40.dp)) {
            val secondaryColor = MaterialTheme.colorScheme.secondary
            val outlineColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
            Canvas(modifier = Modifier.fillMaxSize()) {
              drawCircle(
                color = outlineColor,
                radius = size.minDimension / 2 - 2.dp.toPx(),
                style = Stroke(width = 4.dp.toPx())
              )
              drawArc(
                color = secondaryColor,
                startAngle = -90f,
                sweepAngle = 270f, // Demo progress ring
                useCenter = false,
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
              )
            }
          }
        }
      }

      // 5. Water Card with Wave Animation Clickable
      item {
        DashboardBentoCard(
          title = "Water",
          subtitle = "${String.format("%.1f", state.waterIntakeMl / 1000f)}L / 3L",
          icon = Icons.Default.WaterDrop,
          iconColor = MaterialTheme.colorScheme.secondary,
          onClick = { viewModel.incrementWater() }
        ) {
          // Water wave simulation card background/icon
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .background(
                MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f),
                RoundedCornerShape(8.dp)
              ),
            contentAlignment = Alignment.Center
          ) {
            WaveAnimation(waterRatio = waterProg)
            Text(
              text = "+ Tap to Drink",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.secondary,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      // 6. Mood Card
      item {
        DashboardBentoCard(
          title = "Mood",
          subtitle = state.currentMood,
          icon = Icons.Default.Mood,
          iconColor = MaterialTheme.colorScheme.tertiary,
          onClick = { viewModel.cycleMood() }
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .background(
                  MaterialTheme.colorScheme.surfaceVariant,
                  CircleShape
                ),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = when (state.currentMood) {
                  "Feeling Excellent" -> "😁"
                  "Feeling Peaceful" -> "😇"
                  "Feeling Normal" -> "🙂"
                  "Feeling Tired" -> "🥱"
                  "Feeling Radiant" -> "🌟"
                  else -> "😁"
                },
                fontSize = 20.sp
              )
            }
          }
        }
      }

      // 7. Study Tracker Card
      item {
        DashboardBentoCard(
          title = "Study",
          subtitle = "${state.studyHours}h tracked",
          icon = Icons.Default.School,
          iconColor = MaterialTheme.colorScheme.primary,
          onClick = { viewModel.addStudyHours(0.5f) }
        ) {
          Box(modifier = Modifier.size(40.dp)) {
            val primaryColor = MaterialTheme.colorScheme.primary
            val outlineColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
            Canvas(modifier = Modifier.fillMaxSize()) {
              drawCircle(
                color = outlineColor,
                radius = size.minDimension / 2 - 2.dp.toPx(),
                style = Stroke(width = 4.dp.toPx())
              )
              drawArc(
                color = primaryColor,
                startAngle = -90f,
                sweepAngle = 360f * studyProg,
                useCenter = false,
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
              )
            }
          }
        }
      }

      // 8. Tech Learning Python Card
      item {
        DashboardBentoCard(
          title = "Tech Learning",
          subtitle = "Python progress",
          icon = Icons.Default.Terminal,
          iconColor = MaterialTheme.colorScheme.secondary,
          onClick = { viewModel.increaseTechProgress() }
        ) {
          Column(modifier = Modifier.fillMaxWidth()) {
            LinearProgressIndicator(
              progress = { techProg },
              modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
              color = MaterialTheme.colorScheme.secondary,
              trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
              strokeCap = StrokeCap.Round
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "${state.techProgress}% completed",
              fontSize = 10.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      // 9. Wellness Trends Chart Card
      item(span = { GridItemSpan(2) }) {
        WellnessTrendChart(
          state = state,
          viewModel = viewModel
        )
      }

      // 10. Quran Pages Card
      item(span = { GridItemSpan(2) }) {
        DashboardBentoCard(
          title = "Quran",
          subtitle = "${state.quranPages} pages read",
          icon = Icons.Default.AutoStories,
          iconColor = MaterialTheme.colorScheme.tertiary,
          onClick = { viewModel.incrementQuranPages() }
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "+ Tap to read page",
              color = MaterialTheme.colorScheme.tertiary,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
            Icon(
              imageVector = Icons.Default.Book,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.6f),
              modifier = Modifier.size(24.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
fun DashboardHeader(
  isDarkMode: Boolean,
  onToggleDarkMode: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .height(64.dp)
      .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
      .padding(horizontal = 16.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Image(
        painter = painterResource(id = com.example.R.drawable.img_app_logo),
        contentDescription = "MB Tracking Logo",
        modifier = Modifier
          .size(36.dp)
          .clip(RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Crop
      )
      Spacer(modifier = Modifier.width(10.dp))
      Text(
        text = "MB Tracking",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        fontFamily = FontFamily.SansSerif,
        letterSpacing = (-0.5).sp
      )
    }

    // Centered greeting exactly as designed
    Text(
      text = "Assalamu Alaikum, Amina",
      fontSize = 14.sp,
      fontWeight = FontWeight.SemiBold,
      color = MaterialTheme.colorScheme.onSurface,
      modifier = Modifier.weight(1f),
      textAlign = TextAlign.Center
    )

    Row(verticalAlignment = Alignment.CenterVertically) {
      // Dark Mode Toggle directly in Top App Bar
      IconButton(onClick = onToggleDarkMode) {
        Icon(
          imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
          contentDescription = "Toggle Dark Mode",
          tint = MaterialTheme.colorScheme.primary
        )
      }

      Icon(
        imageVector = Icons.Default.Notifications,
        contentDescription = "Notifications",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(24.dp)
      )
    }
  }
}

@Composable
fun DashboardBentoCard(
  title: String,
  subtitle: String,
  icon: ImageVector,
  iconColor: Color,
  onClick: () -> Unit,
  content: @Composable () -> Unit
) {
  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ),
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = icon,
              contentDescription = null,
              tint = iconColor,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = title,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = subtitle,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
      ) {
        content()
      }
    }
  }
}

@Composable
fun WaveAnimation(waterRatio: Float) {
  val infiniteTransition = rememberInfiniteTransition(label = "wave")
  val wavePhase by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 2f * Math.PI.toFloat(),
    animationSpec = infiniteRepeatable(
      animation = tween(2000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "wavePhase"
  )

  val waveColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)

  Canvas(modifier = Modifier.fillMaxSize()) {
    val path = Path()
    val width = size.width
    val height = size.height
    val waveHeight = 4.dp.toPx()
    val waterHeight = height * (1f - waterRatio)

    path.moveTo(0f, height)
    path.lineTo(0f, waterHeight)

    for (x in 0..width.toInt() step 5) {
      val y = waterHeight + sin(x.toFloat() / width * 2 * Math.PI.toFloat() + wavePhase) * waveHeight
      path.lineTo(x.toFloat(), y)
    }

    path.lineTo(width, height)
    path.close()

    drawPath(path = path, color = waveColor)
  }
}

@Composable
fun CombinedGoalsCard(
  state: com.example.data.NurAppState,
  viewModel: NurViewModel
) {
  val prayerCount = listOf(
    state.fajrCompleted,
    state.dhuhrCompleted,
    state.asrCompleted,
    state.maghribCompleted,
    state.ishaCompleted
  ).count { it }
  val prayerProg = prayerCount / 5f
  val workoutProg = (state.workoutActiveMin / 60f).coerceIn(0f, 1f)
  val overallCombined = ((prayerProg + workoutProg) / 2f * 100).toInt()

  Card(
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { viewModel.navigateTo("prayer_tracker") }
      .testTag("combined_goals_card")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Core Daily Synergy",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Spiritual & Physical Harmony",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
        
        Box(
          modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
          Text(
            text = "Daily",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp)
      ) {
        // Left side: Dual concentric rings
        Box(
          modifier = Modifier.size(120.dp),
          contentAlignment = Alignment.Center
        ) {
          val prayerColor = MaterialTheme.colorScheme.primary
          val workoutColor = MaterialTheme.colorScheme.secondary
          val outlineColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)

          Canvas(modifier = Modifier.fillMaxSize()) {
            // Outer Ring (Prayers)
            drawCircle(
              color = outlineColor,
              radius = size.minDimension / 2 - 8.dp.toPx(),
              style = Stroke(width = 8.dp.toPx())
            )
            drawArc(
              color = prayerColor,
              startAngle = -90f,
              sweepAngle = 360f * prayerProg,
              useCenter = false,
              style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
            )

            // Inner Ring (Workouts)
            drawCircle(
              color = outlineColor,
              radius = size.minDimension / 2 - 20.dp.toPx(),
              style = Stroke(width = 6.dp.toPx())
            )
            drawArc(
              color = workoutColor,
              startAngle = -90f,
              sweepAngle = 360f * workoutProg,
              useCenter = false,
              style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
            )
          }

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "$overallCombined%",
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Goal",
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              letterSpacing = 0.5.sp
            )
          }
        }

        // Right side: Progress list
        Column(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // Prayer progress row
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(8.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Prayers completed",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = MaterialTheme.colorScheme.onSurface
                )
              }
              Text(
                text = "$prayerCount / 5",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
            // Minimalist dot list for individual prayers
            Row(
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              val prayers = listOf("F", "D", "A", "M", "I")
              val completedList = listOf(
                state.fajrCompleted,
                state.dhuhrCompleted,
                state.asrCompleted,
                state.maghribCompleted,
                state.ishaCompleted
              )
              prayers.forEachIndexed { index, name ->
                val comp = completedList[index]
                Box(
                  modifier = Modifier
                    .size(18.dp)
                    .background(
                      if (comp) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                      CircleShape
                    ),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = name,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (comp) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            }
          }

          // Workout progress row
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(8.dp)
                    .background(MaterialTheme.colorScheme.secondary, CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Active Minutes",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = MaterialTheme.colorScheme.onSurface
                )
              }
              Text(
                text = "${state.workoutActiveMin} / 60m",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
            // Horizontal progress bar for workout active minutes
            LinearProgressIndicator(
              progress = { workoutProg },
              modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
              color = MaterialTheme.colorScheme.secondary,
              trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
            )
          }
        }
      }
    }
  }
}

@Composable
fun WellnessTrendChart(
  state: com.example.data.NurAppState,
  viewModel: NurViewModel,
  modifier: Modifier = Modifier
) {
  val selectedPeriod by viewModel.selectedTrendPeriod.collectAsState()
  val trendData = viewModel.getTrendData(
    selectedPeriod,
    state.workoutCalories.toFloat(),
    state.waterIntakeMl
  )

  // Default to highlight the latest item (Today)
  var selectedIndex by remember { mutableStateOf<Int?>(null) }
  val activeIndex = selectedIndex ?: (trendData.size - 1)
  val activePoint = if (activeIndex in trendData.indices) trendData[activeIndex] else null

  val primaryColor = MaterialTheme.colorScheme.primary // gold
  val secondaryColor = MaterialTheme.colorScheme.secondary // teal
  val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
  val outlineVariant = MaterialTheme.colorScheme.outlineVariant

  Card(
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
    ),
    modifier = modifier.fillMaxWidth()
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp)
    ) {
      // 1. Title and Segmented Toggles Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Wellness Trends",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "30d vs Weekly tracking",
            fontSize = 12.sp,
            color = onSurfaceVariant
          )
        }

        // Custom iOS-styled segmented toggles
        Row(
          modifier = Modifier
            .background(
              color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
              shape = RoundedCornerShape(12.dp)
            )
            .padding(4.dp),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          val periods = listOf(
            com.example.viewmodel.TrendPeriod.WEEKLY to "Weekly",
            com.example.viewmodel.TrendPeriod.MONTHLY to "30 Days"
          )
          periods.forEach { (p, label) ->
            val isSel = selectedPeriod == p
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(
                  if (isSel) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                )
                .clickable {
                  viewModel.selectTrendPeriod(p)
                  selectedIndex = null // reset hover selection
                }
                .padding(horizontal = 12.dp, vertical = 6.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSel) MaterialTheme.colorScheme.onPrimaryContainer else onSurfaceVariant
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 2. Interactive Info Banner
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (activePoint != null) {
            val isToday = activePoint.label == "Today"
            val displayDay = if (isToday) "Today" else if (selectedPeriod == com.example.viewmodel.TrendPeriod.WEEKLY) "${activePoint.label}" else "Day ${activePoint.label}"
            
            Column {
              Text(
                text = displayDay,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
              ) {
                // Calorie label
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(6.dp)
                      .background(primaryColor, CircleShape)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "${activePoint.caloriesBurned.toInt()} Kcal",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                }

                // Water label
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(6.dp)
                      .background(secondaryColor, CircleShape)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "${(activePoint.hydrationMl / 1000f).let { String.format("%.1f", it) }}L Water",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                }
              }
            }
          } else {
            Text(
              text = "Tap points to inspect details",
              fontSize = 12.sp,
              color = onSurfaceVariant
            )
          }

          // Averages or Legend Info
          Column(horizontalAlignment = Alignment.End) {
            val avgKcal = trendData.map { it.caloriesBurned }.average().toInt()
            val avgWaterL = (trendData.map { it.hydrationMl }.average() / 1000f)
            Text(
              text = "Period Average",
              fontSize = 10.sp,
              color = onSurfaceVariant,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "~$avgKcal kcal • ${String.format("%.1f", avgWaterL)}L",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 3. Canvas for Charting
      val maxCal = trendData.maxOfOrNull { it.caloriesBurned }?.coerceAtLeast(300f) ?: 300f
      val maxHydration = trendData.maxOfOrNull { it.hydrationMl }?.coerceAtLeast(1500f) ?: 1500f

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(150.dp)
      ) {
        Canvas(
          modifier = Modifier
            .fillMaxSize()
            .pointerInput(trendData) {
              detectTapGestures { offset ->
                val pointsCount = trendData.size
                if (pointsCount > 0) {
                  val stepWidth = size.width / (pointsCount - 1).coerceAtLeast(1)
                  val index = (offset.x / stepWidth).roundToInt().coerceIn(0, pointsCount - 1)
                  selectedIndex = index
                }
              }
            }
        ) {
          val width = size.width
          val height = size.height
          val paddingY = 16.dp.toPx()
          val usableHeight = height - paddingY * 2
          val pointsCount = trendData.size

          // A. Draw Horizontal Grid lines
          val gridLines = 4
          for (g in 0 until gridLines) {
            val y = paddingY + (usableHeight * (g.toFloat() / (gridLines - 1)))
            drawLine(
              color = outlineVariant.copy(alpha = 0.15f),
              start = Offset(0f, y),
              end = Offset(width, y),
              strokeWidth = 1.dp.toPx()
            )
          }

          if (pointsCount > 0) {
            val stepWidth = width / (pointsCount - 1).coerceAtLeast(1)

            // B. Draw Hydration Levels as Translucent Vertical pillars
            trendData.forEachIndexed { i, point ->
              val x = i * stepWidth
              val barHeight = (point.hydrationMl / maxHydration) * usableHeight
              val barY = height - paddingY - barHeight
              val barWidth = if (selectedPeriod == com.example.viewmodel.TrendPeriod.WEEKLY) 16.dp.toPx() else 4.dp.toPx()

              // Highlight matching clicked/active element
              val isHovered = i == activeIndex
              val barColor = if (isHovered) secondaryColor.copy(alpha = 0.65f) else secondaryColor.copy(alpha = 0.25f)

              drawRoundRect(
                color = barColor,
                topLeft = Offset(x - barWidth / 2f, barY),
                size = androidx.compose.ui.geometry.Size(barWidth, barHeight.coerceAtLeast(4.dp.toPx())),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx())
              )
            }

            // C. Draw Calorie Bezier Curve and Gradient Fill
            val path = Path()
            val fillPath = Path()

            val points = trendData.mapIndexed { i, point ->
              val x = i * stepWidth
              val y = height - paddingY - (point.caloriesBurned / maxCal) * usableHeight
              Offset(x, y)
            }

            if (points.isNotEmpty()) {
              path.moveTo(points[0].x, points[0].y)
              fillPath.moveTo(points[0].x, height - paddingY)
              fillPath.lineTo(points[0].x, points[0].y)

              for (i in 1 until points.size) {
                val pPrev = points[i - 1]
                val pCurr = points[i]

                val controlX1 = pPrev.x + (pCurr.x - pPrev.x) / 2f
                val controlY1 = pPrev.y
                val controlX2 = pPrev.x + (pCurr.x - pPrev.x) / 2f
                val controlY2 = pCurr.y

                path.cubicTo(controlX1, controlY1, controlX2, controlY2, pCurr.x, pCurr.y)
                fillPath.cubicTo(controlX1, controlY1, controlX2, controlY2, pCurr.x, pCurr.y)
              }

              fillPath.lineTo(points.last().x, height - paddingY)
              fillPath.close()

              // Draw Area Gradient Fill
              drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                  colors = listOf(
                    primaryColor.copy(alpha = 0.35f),
                    Color.Transparent
                  )
                )
              )

              // Draw Bezier Line
              drawPath(
                path = path,
                color = primaryColor,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
              )
            }

            // D. Draw Interactive Indicator for hovered element
            if (activeIndex in points.indices) {
              val activePointOffset = points[activeIndex]

              // Vertical helper line
              drawLine(
                color = primaryColor.copy(alpha = 0.4f),
                start = Offset(activePointOffset.x, paddingY),
                end = Offset(activePointOffset.x, height - paddingY),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
              )

              // Glowing outer ring
              drawCircle(
                color = primaryColor.copy(alpha = 0.3f),
                radius = 10.dp.toPx(),
                center = activePointOffset
              )

              // Solid inner dot
              drawCircle(
                color = primaryColor,
                radius = 5.dp.toPx(),
                center = activePointOffset
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // 4. Custom Responsive X-Axis Labels Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (selectedPeriod == com.example.viewmodel.TrendPeriod.WEEKLY) {
          // Show all 7 labels
          trendData.forEach { point ->
            val isSelected = point.label == activePoint?.label
            Text(
              text = point.label,
              fontSize = 10.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
              color = if (isSelected) MaterialTheme.colorScheme.primary else onSurfaceVariant,
              modifier = Modifier.widthIn(min = 24.dp),
              textAlign = TextAlign.Center
            )
          }
        } else {
          // Monthly: Show 6 key interval labels to avoid crowding
          val labelsToShow = listOf("01", "06", "11", "16", "21", "26", "Today")
          labelsToShow.forEach { label ->
            val isSelected = activePoint != null && (
              (label == "Today" && activePoint.label == "Today") ||
              (activePoint.label == label)
            )
            Text(
              text = label,
              fontSize = 10.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
              color = if (isSelected) MaterialTheme.colorScheme.primary else onSurfaceVariant,
              textAlign = TextAlign.Center
            )
          }
        }
      }
    }
  }
}
