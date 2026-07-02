package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.viewmodel.NurViewModel

@Composable
fun PrayerTrackerScreen(
  viewModel: NurViewModel,
  modifier: Modifier = Modifier
) {
  val appState by viewModel.appState.collectAsState()
  val state = appState ?: return

  val selectedCity by viewModel.selectedPrayerCity.collectAsState()
  val selectedDayOffset by viewModel.selectedPrayerDayOffset.collectAsState()
  val prayerErrorMessage by viewModel.prayerErrorMessage.collectAsState()

  val context = LocalContext.current

  // Show dynamic toast warning on timing violation
  androidx.compose.runtime.LaunchedEffect(prayerErrorMessage) {
    prayerErrorMessage?.let {
      android.widget.Toast.makeText(context, it, android.widget.Toast.LENGTH_LONG).show()
    }
  }

  // Calculate dynamic overall prayer completion percentage
  val prayers = listOf(
    state.fajrCompleted,
    state.dhuhrCompleted,
    state.asrCompleted,
    state.maghribCompleted,
    state.ishaCompleted
  )
  val completedCount = prayers.count { it }
  val prayerPercentage = (completedCount / 5f * 100).toInt()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background),
    contentPadding = PaddingValues(bottom = 24.dp)
  ) {
    // 1. Header / Hero section with beautiful mosque hotlink
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(260.dp)
          .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
      ) {
        AsyncImage(
          model = ImageRequest.Builder(LocalContext.current)
            .data("https://lh3.googleusercontent.com/aida-public/AB6AXuDxLk-0mrL8u7IJoCR-iwO0a9qhCJGCDBq6Eh7giH6FI2_sIusY59RONkT01ORcc658rSqA0EB97B_ifOc1ArQS03JjKnGZrC8R6YT5dVks2vYfyDckfmo8zbrJ3kPrP0AB3_0HLx0py5QuZlTjCHvutGRhwaTynwRJ4_JSrhqA0FlJkm-BpXnIjo-itMB8SfUgRvQ96ia9fWt1yyVPhOtVU1x4cYM4Jykt-p1PXXqrQOwib388Kh3Kkg")
            .crossfade(true)
            .build(),
          contentDescription = "Mosque Courtyard",
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop
        )

        // Gradient overlay for daylight transitions and readability
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              brush = Brush.verticalGradient(
                colors = listOf(
                  Color.Transparent,
                  MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                  MaterialTheme.colorScheme.background
                )
              )
            )
        )

        // Top Header Actions
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .align(Alignment.TopCenter),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = { viewModel.navigateTo("dashboard") },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f), CircleShape)
          ) {
            Icon(
              imageVector = Icons.Default.ChevronLeft,
              contentDescription = "Back",
              tint = MaterialTheme.colorScheme.primary
            )
          }

          Text(
            text = "Daily Prayers",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )

          IconButton(
            onClick = { /* Demo only */ },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f), CircleShape)
          ) {
            Icon(
              imageVector = Icons.Default.Notifications,
              contentDescription = "Notifications",
              tint = MaterialTheme.colorScheme.primary
            )
          }
        }

        // Today info and Circular Progress alignment
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 20.dp)
            .align(Alignment.BottomCenter),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Bottom
        ) {
          Column {
            Text(
              text = "TODAY",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.secondary,
              letterSpacing = 1.5.sp
            )
            Text(
              text = "Daily Prayers",
              fontSize = 28.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
          }

          // Small Circular progress with text inside
          Box(
            modifier = Modifier
              .size(60.dp)
              .background(MaterialTheme.colorScheme.surface, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "$prayerPercentage%",
              color = MaterialTheme.colorScheme.tertiary,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
          }
        }
      }
    }

    // 1a. Prayer Time-Based Warning Banner
    if (prayerErrorMessage != null) {
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.95f)
          ),
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
          Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Warning,
              contentDescription = "Warning",
              tint = MaterialTheme.colorScheme.onErrorContainer,
              modifier = Modifier.size(24.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Prayer Time Validation",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onErrorContainer
              )
              Text(
                text = prayerErrorMessage ?: "",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.9f),
                lineHeight = 16.sp
              )
            }
            IconButton(
              onClick = { viewModel.clearPrayerErrorMessage() },
              modifier = Modifier.size(24.dp)
            ) {
              Text(
                text = "×",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onErrorContainer
              )
            }
          }
        }
      }
    }

    // 1b. Pakistan Region Selection Row (Select PK City)
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 8.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Pakistan Region",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )
          Text(
            text = "Pakistan (PK) Timezone",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
        Spacer(modifier = Modifier.height(8.dp))

        androidx.compose.foundation.lazy.LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          val cities = listOf("Karachi", "Lahore", "Islamabad", "Peshawar", "Quetta")
          items(cities) { city ->
            val isSelected = (city == selectedCity)
            Box(
              modifier = Modifier
                .background(
                  color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                  shape = RoundedCornerShape(12.dp)
                )
                .clickable { viewModel.setPrayerCity(city) }
                .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
              Text(
                text = "$city, PK",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }
    }

    // 1c. Dynamic Date Navigation Card
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
        ),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 8.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = { viewModel.setPrayerDayOffset(selectedDayOffset - 1) }
          ) {
            Icon(
              imageVector = Icons.Default.ChevronLeft,
              contentDescription = "Previous Day",
              tint = MaterialTheme.colorScheme.primary
            )
          }

          // Format selected date
          val targetCalendar = java.util.Calendar.getInstance()
          targetCalendar.add(java.util.Calendar.DAY_OF_YEAR, selectedDayOffset)
          val formatter = java.text.SimpleDateFormat("EEEE, MMMM d, yyyy", java.util.Locale.getDefault())
          val formattedDate = formatter.format(targetCalendar.time)

          Text(
            text = if (selectedDayOffset == 0) "Today ($formattedDate)" else formattedDate,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )

          IconButton(
            onClick = { viewModel.setPrayerDayOffset(selectedDayOffset + 1) }
          ) {
            Icon(
              imageVector = Icons.Default.ChevronRight,
              contentDescription = "Next Day",
              tint = MaterialTheme.colorScheme.primary
            )
          }
        }
      }
    }

    // 2. Current Schedule Title Section
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Dynamic Prayer Times",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )

        Box(
          modifier = Modifier
            .background(
              MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
              RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
          Text(
            text = "$selectedCity, PK",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    // 3. Dynamic Automated Prayer Cards
    item {
      // Dynamic list of prayer times for selected city/day
      val calculatedPrayers = viewModel.getCalculatedPrayerTimes()

      // Get current hour & minute for marking "isNow"
      val currentCal = java.util.Calendar.getInstance()
      val currentHour = currentCal.get(java.util.Calendar.HOUR_OF_DAY)
      val currentMin = currentCal.get(java.util.Calendar.MINUTE)
      val currentTotalMinutes = currentHour * 60 + currentMin

      // Find which prayer is currently "Now" (active)
      // The currently active prayer is the latest one that has already started today
      var activePrayerIndex = -1
      if (selectedDayOffset == 0) {
        val prayerMinutes = calculatedPrayers.map { it.hour * 60 + it.minute }
        for (i in prayerMinutes.indices) {
          if (currentTotalMinutes >= prayerMinutes[i]) {
            activePrayerIndex = i
          }
        }
      }

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        calculatedPrayers.forEachIndexed { index, prayer ->
          val isCompleted = when (prayer.name.lowercase()) {
            "fajr" -> state.fajrCompleted
            "dhuhr" -> state.dhuhrCompleted
            "asr" -> state.asrCompleted
            "maghrib" -> state.maghribCompleted
            "isha" -> state.ishaCompleted
            else -> false
          }
          val isNowActive = (index == activePrayerIndex)

          PrayerCard(
            name = prayer.name,
            time = prayer.formattedTime,
            completed = isCompleted,
            isNow = isNowActive,
            onToggle = { viewModel.togglePrayer(prayer.name) }
          )
        }
      }
    }

    // 4. Weekly Consistency Bar Chart Card
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 24.dp)
      ) {
        Text(
          text = "Weekly Consistency",
          fontSize = 20.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface,
          modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
          ),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(20.dp)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.Bottom
            ) {
              // Custom Bar Chart Column Elements
              WeeklyBar(day = "M", progress = 1.0f, active = false)
              WeeklyBar(day = "T", progress = 0.8f, active = false)
              WeeklyBar(day = "W", progress = 1.0f, active = false)
              WeeklyBar(day = "T", progress = 1.0f, active = false)
              WeeklyBar(day = "F", progress = 0.2f, active = true) // Highlights Friday
              WeeklyBar(day = "S", progress = 0.0f, active = false)
              WeeklyBar(day = "S", progress = 0.0f, active = false)
            }
          }
        }
      }
    }
  }
}

@Composable
fun PrayerCard(
  name: String,
  time: String,
  completed: Boolean,
  isNow: Boolean,
  onToggle: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isNow) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.08f)
      } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
      }
    ),
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onToggle)
  ) {
    Box(modifier = Modifier.fillMaxWidth()) {
      // Highlight vertical left edge for active prayer
      if (isNow) {
        Box(
          modifier = Modifier
            .width(4.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.primary)
            .align(Alignment.CenterStart)
        )
      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
          .padding(start = if (isNow) 8.dp else 0.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          // Icon based on completion status
          Box(
            modifier = Modifier
              .size(48.dp)
              .background(
                color = when {
                  completed -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                  isNow -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                  else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                },
                shape = CircleShape
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = when {
                completed -> Icons.Default.CheckCircle
                isNow -> Icons.Default.Schedule
                else -> Icons.Default.Pending
              },
              contentDescription = null,
              tint = when {
                completed -> MaterialTheme.colorScheme.tertiary
                isNow -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.outline
              }
            )
          }

          Spacer(modifier = Modifier.width(16.dp))

          Column {
            Text(
              text = name,
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = time,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontFamily = FontFamily.Monospace
              )
              if (isNow) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "• Now",
                  fontSize = 13.sp,
                  color = MaterialTheme.colorScheme.primary,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }

        // Action Badge or Button
        Box {
          if (completed) {
            Box(
              modifier = Modifier
                .background(
                  MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f),
                  CircleShape
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Text(
                text = "Completed",
                color = MaterialTheme.colorScheme.tertiary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }
          } else {
            Box(
              modifier = Modifier
                .size(36.dp)
                .background(
                  color = Color.Transparent,
                  shape = CircleShape
                )
                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Complete",
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun WeeklyBar(
  day: String,
  progress: Float,
  active: Boolean
) {
  Column(
    modifier = Modifier
      .width(32.dp)
      .fillMaxHeight(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Bottom
  ) {
    // Bar
    Box(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
        .background(
          MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.15f),
          RoundedCornerShape(tCorner = 6.dp, bCorner = 6.dp)
        ),
      contentAlignment = Alignment.BottomCenter
    ) {
      if (progress > 0f) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(progress)
            .background(
              color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
              shape = RoundedCornerShape(tCorner = 6.dp, bCorner = 6.dp)
            )
        )
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Label
    Text(
      text = day,
      fontSize = 12.sp,
      fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
      color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}

// RoundedCornerShape helper
fun RoundedCornerShape(tCorner: androidx.compose.ui.unit.Dp, bCorner: androidx.compose.ui.unit.Dp) =
  RoundedCornerShape(
    topStart = tCorner,
    topEnd = tCorner,
    bottomStart = bCorner,
    bottomEnd = bCorner
  )
