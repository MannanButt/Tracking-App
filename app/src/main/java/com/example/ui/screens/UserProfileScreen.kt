package com.example.ui.screens

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.viewmodel.NurViewModel

@Composable
fun UserProfileScreen(
  viewModel: NurViewModel,
  modifier: Modifier = Modifier
) {
  val appState by viewModel.appState.collectAsState()
  val state = appState ?: return

  var showResetDialog by remember { mutableStateOf(false) }

  if (showResetDialog) {
    AlertDialog(
      onDismissRequest = { showResetDialog = false },
      title = { Text("Reset App Data?") },
      text = { Text("This will clear all logged events and reset XP to baseline demo defaults.") },
      confirmButton = {
        TextButton(
          onClick = {
            viewModel.resetAllData()
            showResetDialog = false
          }
        ) {
          Text("Reset", color = MaterialTheme.colorScheme.error)
        }
      },
      dismissButton = {
        TextButton(onClick = { showResetDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(24.dp)
  ) {
    // 1. Shared Custom App Bar Header
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
            text = "Daily Tracker",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )
        }

        IconButton(onClick = { /* Demo only */ }) {
          Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = "Notifications",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }

    // 2. Profile Header (Avatar and Streaks)
    item {
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Box(contentAlignment = Alignment.BottomCenter) {
          // Glow profile ring
          Box(
            modifier = Modifier
              .size(136.dp)
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
              .padding(4.dp),
            contentAlignment = Alignment.Center
          ) {
            AsyncImage(
              model = ImageRequest.Builder(LocalContext.current)
                .data("https://lh3.googleusercontent.com/aida-public/AB6AXuCvLoqbpWpPsjW2E0-hdP7_xluf4r9o_cjM1LCNedFhCMn8aG1fMBDE9es6pOmgmlr2qQzrkxQOeOk1HjsTkYqKHXAHii4KzV8lE-Z2CGFi9hJDm1fPSCJtrisCtthdgMpgRc6N_XuV2WxWtSktZok8Wpr9JO81Qeb7GXOjcQszh8zKDXnVId9eS1wfwOYKOtsLHQNxxC4tLIu22i15D8_7FhsSOxPdgZsqsIxuKVUAUxn6MWYmi_g4OA")
                .crossfade(true)
                .build(),
              contentDescription = "User Avatar",
              modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface),
              contentScale = ContentScale.Crop
            )
          }

          // Streak Badge Overlap at bottom center
          Box(
            modifier = Modifier
              .offset(y = 12.dp)
              .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
              .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
              .padding(horizontal = 12.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.LocalFireDepartment,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "${state.profileStreakDays}d",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = FontFamily.Monospace
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
          text = state.userName,
          fontSize = 24.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )

        Text(
          text = "Joined Oct 2023",
          fontSize = 13.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    // 3. Stats Grid (Bento style)
    item {
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = "Your Stats",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface,
          modifier = Modifier.padding(bottom = 12.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          // Total XP Card
          Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "TOTAL INSIGHT",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                  Text(
                    text = "${state.userXp}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = FontFamily.Monospace
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "XP",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }

              Box(
                modifier = Modifier
                  .size(44.dp)
                  .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.AutoAwesome,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary
                )
              }
            }
          }

          // Two Column Stats Card
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            // Milestones Card
            Card(
              shape = RoundedCornerShape(20.dp),
              colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
              ),
              modifier = Modifier.weight(1f)
            ) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(16.dp)
                  )
                  Text(
                    text = "MILESTONES",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                  )
                }

                Column {
                  Text(
                    text = "24",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = "Unlocked",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            }

            // Consistency Card
            Card(
              shape = RoundedCornerShape(20.dp),
              colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
              ),
              modifier = Modifier.weight(1f)
            ) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(16.dp)
                  )
                  Text(
                    text = "CONSISTENCY",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                  )
                }

                Column {
                  Text(
                    text = "92%",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = FontFamily.Monospace
                  )
                  Text(
                    text = "This Month",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            }
          }
        }
      }
    }

    // 4. Recently Unlocked (Horizontal scroll)
    item {
      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Recent Badges",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          TextButton(onClick = { viewModel.navigateTo("leaderboard") }) {
            Text(
              text = "View All",
              fontSize = 14.sp,
              color = MaterialTheme.colorScheme.primary,
              fontWeight = FontWeight.Bold
            )
          }
        }

        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(16.dp),
          contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
          item {
            RecentBadgeCard(
              title = "Early Bird",
              description = "7 Days at 5AM",
              icon = Icons.Default.WorkspacePremium,
              iconColor = MaterialTheme.colorScheme.primary,
              locked = false
            )
          }

          item {
            RecentBadgeCard(
              title = "Zen Master",
              description = "30h Meditation",
              icon = Icons.Default.SelfImprovement,
              iconColor = MaterialTheme.colorScheme.secondary,
              locked = false
            )
          }

          item {
            RecentBadgeCard(
              title = "Next Milestone",
              description = "Locked",
              icon = Icons.Default.Lock,
              iconColor = MaterialTheme.colorScheme.outline,
              locked = true
            )
          }
        }
      }
    }

    // 5. Actions: Preferences/Reset & Logout
    item {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Preferences/Settings reset helper
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
          ),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { showResetDialog = true }
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
                imageVector = Icons.Default.Settings,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Spacer(modifier = Modifier.width(12.dp))
              Text(
                text = "Preferences (Reset Data)",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
              )
            }

            Icon(
              imageVector = Icons.Default.ChevronRight,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Sign Out action
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
          ),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { viewModel.navigateTo("login") }
            .testTag("sign_out_button")
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Logout,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Sign Out",
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp,
              color = MaterialTheme.colorScheme.error
            )
          }
        }
      }
    }
  }
}

@Composable
fun RecentBadgeCard(
  title: String,
  description: String,
  icon: ImageVector,
  iconColor: Color,
  locked: Boolean
) {
  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
    ),
    modifier = Modifier
      .width(128.dp)
      .background(
        color = Color.Transparent,
        shape = RoundedCornerShape(20.dp)
      )
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Box(
        modifier = Modifier
          .size(64.dp)
          .background(
            color = if (locked) MaterialTheme.colorScheme.surfaceVariant else iconColor.copy(alpha = 0.12f),
            shape = CircleShape
          ),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = if (locked) MaterialTheme.colorScheme.outline else iconColor,
          modifier = Modifier.size(32.dp)
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        color = if (locked) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center
      )
      Text(
        text = description,
        fontSize = 10.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
      )
    }
  }
}
