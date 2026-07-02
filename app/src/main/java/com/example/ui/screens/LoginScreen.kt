package com.example.ui.screens

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.NurViewModel

@Composable
fun LoginScreen(
  viewModel: NurViewModel,
  modifier: Modifier = Modifier
) {
  var email by remember { mutableStateOf("sarah.j@nur.com") }
  var password by remember { mutableStateOf("SarahJ@2026!") }
  var passwordVisible by remember { mutableStateOf(false) }
  var emailError by remember { mutableStateOf<String?>(null) }
  var passwordError by remember { mutableStateOf<String?>(null) }

  fun validateInputs(): Boolean {
    var isValid = true
    emailError = null
    passwordError = null

    val trimmedEmail = email.trim()
    val trimmedPassword = password

    if (trimmedEmail.isEmpty()) {
      emailError = "Email or Username is required"
      isValid = false
    } else if (trimmedEmail.length < 3) {
      emailError = "Must be at least 3 characters"
      isValid = false
    } else if (trimmedEmail.length > 80) {
      emailError = "Email or Username is too long (max 80 chars)"
      isValid = false
    }

    if (trimmedPassword.isEmpty()) {
      passwordError = "Password is required"
      isValid = false
    } else if (trimmedPassword.length < 8) {
      passwordError = "Password must be at least 8 characters"
      isValid = false
    } else if (trimmedPassword.length > 64) {
      passwordError = "Password is too long (max 64 chars)"
      isValid = false
    } else {
      val hasUppercase = trimmedPassword.any { it.isUpperCase() }
      val hasLowercase = trimmedPassword.any { it.isLowerCase() }
      val hasSpecial = trimmedPassword.any { !it.isLetterOrDigit() }

      val errors = mutableListOf<String>()
      if (!hasUppercase) errors.add("one uppercase letter")
      if (!hasLowercase) errors.add("one lowercase letter")
      if (!hasSpecial) errors.add("one special character")

      if (errors.isNotEmpty()) {
        passwordError = "Require: ${errors.joinToString(", ")}"
        isValid = false
      } else {
        // Password will not be used as or contain the username
        val simplifiedEmail = trimmedEmail.substringBefore("@").lowercase()
        val lowerPass = trimmedPassword.lowercase()
        if (simplifiedEmail.isNotEmpty() && (lowerPass == simplifiedEmail || (simplifiedEmail.length >= 3 && lowerPass.contains(simplifiedEmail)))) {
          passwordError = "Password must not contain or be identical to username"
          isValid = false
        }
      }
    }

    return isValid
  }

  val appState by viewModel.appState.collectAsState()

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Decorative ambient background glow shapes
    Box(
      modifier = Modifier
        .size(300.dp)
        .align(Alignment.TopStart)
        .offset(x = (-100).dp, y = (-100).dp)
        .blur(80.dp)
        .background(
          MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
          CircleShape
        )
    )

    Box(
      modifier = Modifier
        .size(250.dp)
        .align(Alignment.BottomEnd)
        .offset(x = 100.dp, y = 100.dp)
        .blur(80.dp)
        .background(
          MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
          CircleShape
        )
    )

    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 24.dp, vertical = 40.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Spacer(modifier = Modifier.height(30.dp))

      // Logo
      Box(
        modifier = Modifier
          .size(80.dp)
          .background(
            brush = Brush.radialGradient(
              colors = listOf(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.primaryContainer
              )
            ),
            shape = CircleShape
          ),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "MB",
          color = MaterialTheme.colorScheme.onPrimary,
          fontSize = 32.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.SansSerif,
          letterSpacing = (-1).sp
        )
      }

      Spacer(modifier = Modifier.height(32.dp))

      // Heading
      Text(
        text = "Welcome Back",
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "Sign in to continue your journey of clarity.",
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(40.dp))

      // Form
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
      ) {
        Text(
          text = "Email or Username",
          fontSize = 12.sp,
          fontWeight = FontWeight.Medium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )

        OutlinedTextField(
          value = email,
          onValueChange = { 
            if (it.length <= 100) { 
              email = it 
              emailError = null 
            } 
          },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("email_input"),
          placeholder = { Text("Enter your email") },
          singleLine = true,
          isError = emailError != null,
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
            focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
          )
        )

        if (emailError != null) {
          Text(
            text = emailError ?: "",
            color = MaterialTheme.colorScheme.error,
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "Password",
          fontSize = 12.sp,
          fontWeight = FontWeight.Medium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )

        OutlinedTextField(
          value = password,
          onValueChange = { 
            if (it.length <= 80) { 
              password = it 
              passwordError = null 
            } 
          },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("password_input"),
          placeholder = { Text("Enter your password") },
          singleLine = true,
          isError = passwordError != null,
          visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
          trailingIcon = {
            val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
            val description = if (passwordVisible) "Hide password" else "Show password"

            IconButton(onClick = { passwordVisible = !passwordVisible }) {
              Icon(imageVector = image, contentDescription = description)
            }
          },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
            focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
          )
        )

        if (passwordError != null) {
          Text(
            text = passwordError ?: "",
            color = MaterialTheme.colorScheme.error,
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          TextButton(onClick = { /* Demo only */ }) {
            Text(
              text = "Forgot Password?",
              color = MaterialTheme.colorScheme.primary,
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sign In Button
        Button(
          onClick = { 
            if (validateInputs()) {
              val cleanName = if (email.contains("@")) {
                email.substringBefore("@").split('.').joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
              } else {
                email
              }
              viewModel.updateUserName(cleanName)
              viewModel.navigateTo("landing")
            }
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .testTag("login_button"),
          shape = RoundedCornerShape(24.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
          )
        ) {
          Text(
            text = "Sign In",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary
          )
        }
      }

      Spacer(modifier = Modifier.height(32.dp))

      // Divider
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        HorizontalDivider(
          modifier = Modifier.weight(1f),
          color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        )
        Text(
          text = "OR",
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.padding(horizontal = 16.dp),
          fontWeight = FontWeight.Medium
        )
        HorizontalDivider(
          modifier = Modifier.weight(1f),
          color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        )
      }

      Spacer(modifier = Modifier.height(32.dp))

      // Social Logins
      OutlinedButton(
        onClick = { 
          viewModel.updateUserName("Google User")
          viewModel.navigateTo("landing") 
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.outlinedButtonColors(
          contentColor = MaterialTheme.colorScheme.onSurface
        )
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = Icons.Default.Mail,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.width(12.dp))
          Text(
            text = "Continue with Google",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedButton(
        onClick = { 
          viewModel.updateUserName("Apple User")
          viewModel.navigateTo("landing") 
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.outlinedButtonColors(
          contentColor = MaterialTheme.colorScheme.onSurface
        )
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = Icons.Default.PhoneIphone,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.width(12.dp))
          Text(
            text = "Continue with Apple",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }

      Spacer(modifier = Modifier.height(48.dp))

      // Sign Up Footer
      Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Don't have an account? ",
          fontSize = 14.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        TextButton(
          onClick = { viewModel.navigateTo("dashboard") },
          modifier = Modifier.padding(0.dp)
        ) {
          Text(
            text = "Sign Up",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}
