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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.NurViewModel

@Composable
fun AchievementsScreen(
  viewModel: NurViewModel,
  modifier: Modifier = Modifier
) {
  val appState by viewModel.appState.collectAsState()
  val activityLogs by viewModel.activityLogs.collectAsState()
  val state = appState ?: return

  // Calculate level progress dynamically
  val totalXp = state.userXp
  val xpInLevel = totalXp % 1000
  val levelProgress = xpInLevel / 1000f

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(24.dp)
  ) {
    // 1. App Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Achievements",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )
        }

        IconButton(onClick = { /* Demo only */ }) {
          Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = "Notifications",
            tint = MaterialTheme.colorScheme.primary
          )
        }
      }
    }

    // 2. Level Progress card
    item {
      Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
          // Circular progress for level
          Box(
            modifier = Modifier.size(100.dp),
            contentAlignment = Alignment.Center
          ) {
            val primaryColor = MaterialTheme.colorScheme.primary
            val outlineColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)

            Canvas(modifier = Modifier.fillMaxSize()) {
              drawCircle(
                color = outlineColor,
                radius = size.minDimension / 2 - 4.dp.toPx(),
                style = Stroke(width = 6.dp.toPx())
              )
              drawArc(
                color = primaryColor,
                startAngle = -90f,
                sweepAngle = 360f * levelProgress,
                useCenter = false,
                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
              )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "LEVEL",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              )
              Text(
                text = "${state.userLevel}",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
            }
          }

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Master Seeker",
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
              Text(
                text = "$totalXp",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontFamily = FontFamily.Monospace
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "XP Total",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Linear Progress to next level
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "Progress to Lv ${state.userLevel + 1}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = "$xpInLevel / 1000 XP",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontFamily = FontFamily.Monospace
              )
            }

            Spacer(modifier = Modifier.height(4.dp))

            LinearProgressIndicator(
              progress = { levelProgress },
              modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
              color = MaterialTheme.colorScheme.primary,
              trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
              strokeCap = StrokeCap.Round
            )
          }
        }
      }
    }

    // 3. Daily Milestones Carousel (Horizontal scroll)
    item {
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = "Daily Milestones",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface,
          modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(16.dp),
          contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
          // Milestone 1
          item {
            MilestoneCard(
              title = "7 Day Streak",
              reward = "+500 XP bonus",
              icon = Icons.Default.LocalFireDepartment,
              iconColor = MaterialTheme.colorScheme.tertiary,
              claimed = true,
              progress = 1.0f,
              progressText = ""
            )
          }

          // Milestone 2
          item {
            MilestoneCard(
              title = "Perfect Day",
              reward = "All habits complete",
              icon = Icons.Default.Verified,
              iconColor = MaterialTheme.colorScheme.primary,
              claimed = false,
              progress = 0.8f,
              progressText = "4/5 Habits"
            )
          }

          // Milestone 3
          item {
            MilestoneCard(
              title = "Early Bird",
              reward = "Fajr on time",
              icon = Icons.Default.LightMode,
              iconColor = MaterialTheme.colorScheme.secondary,
              claimed = true,
              progress = 1.0f,
              progressText = ""
            )
          }
        }
      }
    }

    // 4. Locked Badges
    item {
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = "Locked Badges",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface,
          modifier = Modifier.padding(bottom = 12.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          LockedBadgeItem(
            title = "100 Day Streak",
            progress = 0.45f,
            progressText = "45/100",
            icon = Icons.Default.AutoAwesome
          )
          LockedBadgeItem(
            title = "Quran Master",
            progress = 0.5f,
            progressText = "15/30",
            icon = Icons.Default.MenuBook
          )
          LockedBadgeItem(
            title = "Fitness Guru",
            progress = 0.24f,
            progressText = "12/50",
            icon = Icons.Default.FitnessCenter
          )
        }
      }
    }



    // 6. Recent Activity List
    item {
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = "Recent Activity",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface,
          modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
          ),
          modifier = Modifier.fillMaxWidth()
        ) {
          if (activityLogs.isEmpty()) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "No activities logged yet.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
              )
            }
          } else {
            Column(modifier = Modifier.fillMaxWidth()) {
              activityLogs.forEachIndexed { index, log ->
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                      modifier = Modifier
                        .size(40.dp)
                        .background(
                          color = when (log.type) {
                            "PRAYER" -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                            "WORKOUT" -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                            "QURAN" -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                          },
                          shape = CircleShape
                        ),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(
                        imageVector = when (log.type) {
                          "PRAYER" -> Icons.Default.Mosque
                          "WORKOUT" -> Icons.Default.FitnessCenter
                          "QURAN" -> Icons.Default.AutoStories
                          else -> Icons.Default.CheckCircle
                        },
                        contentDescription = null,
                        tint = when (log.type) {
                          "PRAYER" -> MaterialTheme.colorScheme.tertiary
                          "WORKOUT" -> MaterialTheme.colorScheme.secondary
                          "QURAN" -> MaterialTheme.colorScheme.primary
                          else -> MaterialTheme.colorScheme.outline
                        },
                        modifier = Modifier.size(20.dp)
                      )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                      Text(
                        text = log.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                      )
                      Text(
                        text = log.subtitle,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    }
                  }

                  Text(
                    text = if (log.xpReward > 0) "+${log.xpReward} XP" else "",
                    color = when (log.type) {
                      "PRAYER" -> MaterialTheme.colorScheme.tertiary
                      "WORKOUT" -> MaterialTheme.colorScheme.secondary
                      else -> MaterialTheme.colorScheme.primary
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace
                  )
                }

                if (index < activityLogs.size - 1) {
                  HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.15f))
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun MilestoneCard(
  title: String,
  reward: String,
  icon: ImageVector,
  iconColor: Color,
  claimed: Boolean,
  progress: Float,
  progressText: String
) {
  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
    ),
    modifier = Modifier
      .width(180.dp)
      .padding(bottom = 4.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Box(
        modifier = Modifier
          .size(48.dp)
          .background(iconColor.copy(alpha = 0.12f), CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = iconColor,
          modifier = Modifier.size(24.dp)
        )
      }

      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = title,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
          color = MaterialTheme.colorScheme.onSurface,
          textAlign = TextAlign.Center
        )
        Text(
          text = reward,
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = TextAlign.Center
        )
      }

      if (claimed) {
        Box(
          modifier = Modifier
            .background(
              MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f),
              CircleShape
            )
            .padding(horizontal = 14.dp, vertical = 4.dp)
        ) {
          Text(
            text = "Claimed",
            color = MaterialTheme.colorScheme.tertiary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      } else {
        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
              .fillMaxWidth()
              .height(4.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
            strokeCap = StrokeCap.Round
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = progressText,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontFamily = FontFamily.Monospace
          )
        }
      }
    }
  }
}

@Composable
fun LockedBadgeItem(
  title: String,
  progress: Float,
  progressText: String,
  icon: ImageVector
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        RoundedCornerShape(20.dp)
      )
      .padding(12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Box(
      modifier = Modifier
        .size(56.dp)
        .background(
          MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
          CircleShape
        ),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.outline,
        modifier = Modifier.size(24.dp)
      )
      Box(
        modifier = Modifier
          .size(16.dp)
          .align(Alignment.BottomEnd)
          .background(MaterialTheme.colorScheme.surface, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Lock,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.outline,
          modifier = Modifier.size(10.dp)
        )
      }
    }

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onSurface
      )

      Spacer(modifier = Modifier.height(6.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Progress",
          fontSize = 10.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
          text = progressText,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontFamily = FontFamily.Monospace
        )
      }

      Spacer(modifier = Modifier.height(4.dp))

      LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier
          .fillMaxWidth()
          .height(4.dp),
        color = MaterialTheme.colorScheme.outline,
        trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
        strokeCap = StrokeCap.Round
      )
    }
  }
}


