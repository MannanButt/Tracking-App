package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.viewmodel.NurViewModel
import java.io.File

@Composable
fun LandingScreen(
  viewModel: NurViewModel,
  modifier: Modifier = Modifier
) {
  val appState by viewModel.appState.collectAsState()
  val state = appState ?: return

  // Entry animation states
  val alphaAnim = remember { Animatable(0f) }
  val offsetAnim = remember { Animatable(50f) }

  LaunchedEffect(Unit) {
    alphaAnim.animateTo(1f, animationSpec = tween(durationMillis = 800))
  }
  LaunchedEffect(Unit) {
    offsetAnim.animateTo(0f, animationSpec = tween(durationMillis = 800))
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Decorative glowing background elements
    Box(
      modifier = Modifier
        .size(320.dp)
        .align(Alignment.TopEnd)
        .offset(x = 120.dp, y = (-80).dp)
        .blur(90.dp)
        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), CircleShape)
    )
    Box(
      modifier = Modifier
        .size(280.dp)
        .align(Alignment.BottomStart)
        .offset(x = (-100).dp, y = 100.dp)
        .blur(90.dp)
        .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f), CircleShape)
    )

    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 24.dp, vertical = 40.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Spacer(modifier = Modifier.height(16.dp))

      // Top Welcome section
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.graphicsLayer {
          alpha = alphaAnim.value
          translationY = offsetAnim.value
        }
      ) {
        // App logo or user avatar
        val profilePicFile = state.profilePicUri?.let { File(it) }
        val hasProfilePic = profilePicFile != null && profilePicFile.exists()

        Box(
          modifier = Modifier
            .size(100.dp)
            .background(
              brush = Brush.sweepGradient(
                colors = listOf(
                  MaterialTheme.colorScheme.primary,
                  MaterialTheme.colorScheme.secondary,
                  MaterialTheme.colorScheme.primary
                )
              ),
              shape = CircleShape
            )
            .padding(3.dp),
          contentAlignment = Alignment.Center
        ) {
          if (hasProfilePic) {
            AsyncImage(
              model = ImageRequest.Builder(LocalContext.current)
                .data(profilePicFile)
                .crossfade(true)
                .build(),
              contentDescription = "User Avatar",
              modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface),
              contentScale = ContentScale.Crop
            )
          } else {
            val initials = state.userName.split(" ")
              .mapNotNull { it.firstOrNull()?.toString() }
              .take(2)
              .joinToString("")
              .uppercase()

            Box(
              modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(
                  brush = Brush.linearGradient(
                    colors = listOf(
                      MaterialTheme.colorScheme.primaryContainer,
                      MaterialTheme.colorScheme.secondaryContainer
                    )
                  )
                ),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = initials.ifEmpty { "?" },
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
          text = "Assalamu Alaikum,\n${state.userName}",
          fontSize = 28.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface,
          textAlign = TextAlign.Center,
          lineHeight = 36.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center,
          modifier = Modifier
            .background(
              MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
              RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Icon(
            imageVector = Icons.Default.LocalFireDepartment,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "${state.streakDays} Day Streak",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
          )
        }
      }

      // Middle Snapshot card
      Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 24.dp)
          .graphicsLayer {
            alpha = alphaAnim.value
            translationY = offsetAnim.value * 0.7f
          }
      ) {
        Column(
          modifier = Modifier.padding(20.dp),
          horizontalAlignment = Alignment.Start
        ) {
          Text(
            text = "Today's Focus Summary",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(16.dp))

          // Calculated prayer completion count
          val prayerCount = listOf(
            state.fajrCompleted,
            state.dhuhrCompleted,
            state.asrCompleted,
            state.maghribCompleted,
            state.ishaCompleted
          ).count { it }

          Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            LandingStatItem(
              icon = Icons.Default.CalendarToday,
              label = "Prayers Ticked",
              value = "$prayerCount / 5 Completed"
            )
            LandingStatItem(
              icon = Icons.Default.Opacity,
              label = "Water Intake",
              value = "${state.waterIntakeMl.toInt()} ml / 3000 ml"
            )
            LandingStatItem(
              icon = Icons.Default.QueryStats,
              label = "Workout Active",
              value = "${state.workoutActiveMin} min completed"
            )
          }
        }
      }

      // Bottom Call-to-action button
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .graphicsLayer {
            alpha = alphaAnim.value
            translationY = offsetAnim.value * 0.4f
          },
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "Every step you take brings you closer to consistency.",
          fontSize = 13.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          onClick = { viewModel.navigateTo("dashboard") },
          modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
          shape = RoundedCornerShape(28.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
          )
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Text(
              text = "Let's Start",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
              imageVector = Icons.Default.PlayArrow,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onPrimary,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
fun LandingStatItem(
  icon: ImageVector,
  label: String,
  value: String
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(36.dp)
        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f), CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(18.dp)
      )
    }
    Spacer(modifier = Modifier.width(12.dp))
    Column {
      Text(
        text = label,
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
        fontWeight = FontWeight.Medium
      )
      Text(
        text = value,
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.Bold
      )
    }
  }
}

// Extension to simulate graphicsLayer transformations
private fun Modifier.graphicsLayer(
  block: androidx.compose.ui.graphics.GraphicsLayerScope.() -> Unit
): Modifier = this.then(
  androidx.compose.ui.graphics.graphicsLayer(block)
)
