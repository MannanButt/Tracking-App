package com.example.ui.screens

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.viewmodel.NurViewModel

@Composable
fun LeaderboardScreen(
  viewModel: NurViewModel,
  modifier: Modifier = Modifier
) {
  val appState by viewModel.appState.collectAsState()
  val state = appState ?: return

  var selectedTab by remember { mutableStateOf("friends") } // friends, global

  val totalXp = state.userXp
  val selfXpText = "${String.format("%.1fk", totalXp / 1000f)}"

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background),
    contentPadding = PaddingValues(bottom = 24.dp)
  ) {
    // 1. Shared App Header
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp),
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
          text = "Leaderboard",
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

    // 2. Tab layout (capsule slide pill design)
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp, vertical = 8.dp)
          .background(
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            CircleShape
          )
          .padding(4.dp)
      ) {
        Row(modifier = Modifier.fillMaxWidth()) {
          // Friends Tab
          Box(
            modifier = Modifier
              .weight(1f)
              .height(36.dp)
              .background(
                color = if (selectedTab == "friends") MaterialTheme.colorScheme.surface else Color.Transparent,
                shape = CircleShape
              )
              .clickable { selectedTab = "friends" },
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "Friends",
              color = if (selectedTab == "friends") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              letterSpacing = 0.5.sp
            )
          }

          // Global Tab
          Box(
            modifier = Modifier
              .weight(1f)
              .height(36.dp)
              .background(
                color = if (selectedTab == "global") MaterialTheme.colorScheme.surface else Color.Transparent,
                shape = CircleShape
              )
              .clickable { selectedTab = "global" },
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "Global",
              color = if (selectedTab == "global") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              letterSpacing = 0.5.sp
            )
          }
        }
      }
    }

    // 3. Top 3 Podium Layout (Gold, Silver, Bronze style)
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(240.dp)
          .padding(horizontal = 24.dp)
          .padding(top = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
      ) {
        // Rank 2: Master Seeker (Left Podium)
        PodiumCol(
          rank = "2",
          name = "Master Seeker",
          xp = selfXpText,
          avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAPiYG-vUy-wQgzUbZanxZdcyulVmt9QBvplz7elPvt-fGUZidzUMo5hr8tByWsD3EazD5nfDAV8GXkXeCaJak5XTWYJ-bD0WRD92JMs1vAuYDhcAMDOitHgLZA_25wM46VXI3mp-PQnxaIrZNZrBnxcToEANpHF3MvSU_CmmzvN6zo5aHtJs5kkWfg3z3o_SdGwId-VwOr47LlipLNzKLhVyJAt8UVMNpiOqlwBeRzE6UNDny4oMTdnQ",
          podiumHeightRatio = 0.65f,
          podiumColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
          labelColor = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Rank 1: Lumina (Center Podium - Tallest!)
        PodiumCol(
          rank = "1",
          name = "Lumina",
          xp = "15.2k",
          avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuByT3-PWp5bGzASYaHSQwgMSzeDVZNTe8ax19z_ZbgaGeZmfjiZjr6tpn92ovNJJ6NsKEZwj_nU1LzdBU0RvSh38KkD7zB3G7tYotEGrYRfs_XPPYx2JNA0A-6fLUqA9wers5bIpPJ5GdwdojwNtjHzlcCD-VgXXpihEDZGmm3qET1wGaFDQ_ybcirdXmcSf2jlbdEN-IGrG8Dfeeln4NnJm5bNUnwi6S-eAwgNEJAWUxR34iMY0R0VhA",
          podiumHeightRatio = 0.9f,
          podiumColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),
          labelColor = MaterialTheme.colorScheme.onSurface,
          showTrophy = true
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Rank 3: Zenith (Right Podium - Lowest)
        PodiumCol(
          rank = "3",
          name = "Zenith",
          xp = "11.8k",
          avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAjNQZ8_7ILqXz952yfiNK_n8zofCBHFSt5VnI0eoSrN9HTzVtE8hrjoFadDtttjyd1VJkNM4-Dj932h9fpIlNAabJlFTYFygZRxvHQ5jiJouvfE11Ur2PaEt_oudTBpxBz5eB1JccdOzVxi2OsifzGTQAqg3Uop4XnlKRmgw9MeuAT1sfA6sEsf2T5aIWJ3LjiMIIOWStYqEMdVf44mwKsXxXN4uFEy9smzZuCwiD0jZQWtnF9Tm4l7g",
          podiumHeightRatio = 0.5f,
          podiumColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
          labelColor = MaterialTheme.colorScheme.onSurface
        )
      }
    }

    // 4. Scrollable Lists for Ranks 4+
    item {
      Spacer(modifier = Modifier.height(16.dp))

      Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
      ) {
        Column(
          modifier = Modifier.padding(8.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          // List Item 4: Atlas
          RankRowItem(
            rank = "4",
            name = "Atlas",
            xp = "10.5k",
            avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCnAcyZLqFlw-EyXoom7QCVCE0ym5bAA8ewNaURnIESjOs7x29yq1cbb2YkOpqUXflhzfFYKyCjXIxkXcTdFy9ea_0jGieOSgBjSyJv0mnHrH4LEDAhZg8Itw0qcN3jBPlrEc9ak6OInItM-5UfU3wDTwPlH6m3883es4vpK65528F4Im93iqWafH6AbryriYNxzVv1nEi3UMTT1PdhUPS-V6TjWpUtS-ohFxsVc2FXEO6k0Q_02_QwVQ"
          )

          // List Item 5: Orion
          RankRowItem(
            rank = "5",
            name = "Orion",
            xp = "9.8k",
            avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAZnzCIMPuOBwKF6CEhfiUQM_6q6h__hxuBcCmzY2kwIwxuEeMpppDKCmtHMG7laQw2cxCLJ7CiNQwhQikHtfFki7WpvGkRQ3qTHMrVEy93okXtxbSU2JubDDGF-trVQ9s3zVTZyrGjEhJFpuMmCsi4Aig6fzmCBmwSZEQKJHM3HnpReKLRPYUYd7Y8kwJ3D3U7ok1ZOiSrevlQfVSgt6q_oz8MSBNDFOEv42MlkqWLg8BqrTET3X07uw"
          )

          // List Item 6: Lyra
          RankRowItem(
            rank = "6",
            name = "Lyra",
            xp = "9.2k",
            avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuC-veU6ATpkeI-oa8OXA0MFp78YUSrU-VkpOOoQU7vR1WoEd6jmSZuaCRgThUWlckXb6WcT5zNRuoECJ_vvAWlpHDf7HsuxpDhmBF8DaYt4EETPLRUQ0EZc0dvqDJeG3_sDVLCP8vttAAgzc4cg9iWfAqcYZkW5HxQRnVOa-l6I4p1lgYakl8ZSyFTsdiqcgNFNskOyD7GqChgi8w9YPe1o2PO6kom4oIaprEW0XL4ivQ3CYeukMsixHA"
          )

          // List Item 7: Nova (Linear precision default typography avatar)
          RankRowItem(
            rank = "7",
            name = "Nova",
            xp = "8.9k",
            avatarUrl = null // Triggers letter badge 'N'
          )
        }
      }
    }
  }
}

@Composable
fun PodiumCol(
  rank: String,
  name: String,
  xp: String,
  avatarUrl: String,
  podiumHeightRatio: Float,
  podiumColor: Color,
  labelColor: Color,
  showTrophy: Boolean = false
) {
  Column(
    modifier = Modifier
      .width(96.dp)
      .fillMaxHeight(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Bottom
  ) {
    if (showTrophy) {
      Icon(
        imageVector = Icons.Default.WorkspacePremium,
        contentDescription = "Leader Award",
        tint = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.size(28.dp)
      )
      Spacer(modifier = Modifier.height(4.dp))
    }

    // Avatar with Border
    Box(contentAlignment = Alignment.BottomCenter) {
      AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
          .data(avatarUrl)
          .crossfade(true)
          .build(),
        contentDescription = name,
        modifier = Modifier
          .size(if (showTrophy) 72.dp else 56.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.surfaceVariant),
        contentScale = ContentScale.Crop
      )

      // Small rank circle bubble overlapping
      Box(
        modifier = Modifier
          .size(20.dp)
          .offset(y = 8.dp)
          .background(if (showTrophy) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = rank,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = if (showTrophy) Color.White else MaterialTheme.colorScheme.onSurface
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Name & XP labels
    Text(
      text = name,
      fontWeight = FontWeight.Bold,
      fontSize = 12.sp,
      color = labelColor,
      textAlign = TextAlign.Center,
      maxLines = 1
    )
    Text(
      text = "$xp XP",
      fontSize = 10.sp,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      fontFamily = FontFamily.Monospace,
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Physical Podium Block
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(podiumHeightRatio * 0.4f)
        .background(
          brush = Brush.verticalGradient(
            colors = listOf(
              podiumColor,
              podiumColor.copy(alpha = 0.1f)
            )
          ),
          shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        )
    )
  }
}

@Composable
fun RankRowItem(
  rank: String,
  name: String,
  xp: String,
  avatarUrl: String?
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(8.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(
        text = rank,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.width(24.dp),
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.width(8.dp))

      if (avatarUrl != null) {
        AsyncImage(
          model = ImageRequest.Builder(LocalContext.current)
            .data(avatarUrl)
            .crossfade(true)
            .build(),
          contentDescription = name,
          modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
          contentScale = ContentScale.Crop
        )
      } else {
        // Nova 'N' avatar default style
        Box(
          modifier = Modifier
            .size(40.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = name.take(1),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.width(16.dp))

      Text(
        text = name,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onSurface
      )
    }

    Column(horizontalAlignment = Alignment.End) {
      Text(
        text = xp,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        fontFamily = FontFamily.Monospace
      )
      Text(
        text = "XP",
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}
