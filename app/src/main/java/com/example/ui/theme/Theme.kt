package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
  primary = NurPrimaryDark,
  onPrimary = NurOnPrimaryDark,
  primaryContainer = NurPrimaryContainerDark,
  onPrimaryContainer = NurOnPrimaryContainerDark,
  secondary = NurSecondaryDark,
  onSecondary = NurOnSecondaryDark,
  secondaryContainer = NurSecondaryContainerDark,
  onSecondaryContainer = NurOnSecondaryContainerDark,
  tertiary = NurTertiaryDark,
  onTertiary = NurOnTertiaryDark,
  tertiaryContainer = NurTertiaryContainerDark,
  onTertiaryContainer = NurOnTertiaryContainerDark,
  background = NurBackgroundDark,
  onBackground = NurOnBackgroundDark,
  surface = NurSurfaceDark,
  onSurface = NurOnSurfaceDark,
  surfaceVariant = NurSurfaceVariantDark,
  onSurfaceVariant = NurOnSurfaceVariantDark,
  outline = NurOutline,
  outlineVariant = NurOutlineVariant,
  error = NurError,
  onError = NurOnError,
  errorContainer = NurErrorContainer,
  onErrorContainer = NurOnErrorContainer
)

private val LightColorScheme = lightColorScheme(
  primary = NurPrimary,
  onPrimary = NurOnPrimary,
  primaryContainer = NurPrimaryContainer,
  onPrimaryContainer = NurOnPrimaryContainer,
  secondary = NurSecondary,
  onSecondary = NurOnSecondary,
  secondaryContainer = NurSecondaryContainer,
  onSecondaryContainer = NurOnSecondaryContainer,
  tertiary = NurTertiary,
  onTertiary = NurOnTertiary,
  tertiaryContainer = NurTertiaryContainer,
  onTertiaryContainer = NurOnTertiaryContainer,
  background = NurBackground,
  onBackground = NurOnBackground,
  surface = NurSurface,
  onSurface = NurOnSurface,
  surfaceVariant = NurSurfaceVariant,
  onSurfaceVariant = NurOnSurfaceVariant,
  outline = NurOutline,
  outlineVariant = NurOutlineVariant,
  error = NurError,
  onError = NurOnError,
  errorContainer = NurErrorContainer,
  onErrorContainer = NurOnErrorContainer
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
