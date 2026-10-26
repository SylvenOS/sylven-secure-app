package com.sylvenos.tools.ui.applock

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sylvenos.tools.R
import com.sylvenos.tools.ui.theme.BluePrimary
import com.sylvenos.tools.ui.theme.SylvenSecureTheme

// Lock Verification Screen verifying active lock (PIN, Pattern, Password)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LockVerificationScreen(
    showTopBar: Boolean = true,
    onBackClick: () -> Unit,
    onUnlocked: () -> Unit,
) {
    val context = LocalContext.current
    val activeLockType = remember { LockSecurityManager.getLockType(context) }

    var enteredPin by remember { mutableStateOf("") }
    var enteredPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(value = false) }
    var errorMessage by remember { mutableStateOf("") }
    var patternResetTrigger by remember { mutableIntStateOf(0) }
    var failedAttempts by remember { mutableIntStateOf(0) }

    // Helper to handle incorrect attempts (Max 5 attempts allowed)
    fun registerFailedAttempt() {
        failedAttempts++
        if (failedAttempts >= 5) {
            errorMessage = "5 incorrect attempts. Access blocked."
            onBackClick()
        } else {
            val remaining = 5 - failedAttempts
            errorMessage = "Incorrect input ($remaining attempts left)."
        }
    }

    // PIN Keypad Handlers
    fun onPinKeyPress(digit: String) {
        if (enteredPin.length < 4) {
            val newPin = enteredPin + digit
            enteredPin = newPin
            if (newPin.length == 4) {
                if (LockSecurityManager.getPin(context) == newPin) {
                    onUnlocked()
                } else {
                    enteredPin = ""
                    registerFailedAttempt()
                }
            }
        }
    }

    fun onPinBackspace() {
        if (enteredPin.isNotEmpty()) {
            enteredPin = enteredPin.dropLast(1)
        }
    }

    // Pattern Handler
    fun onPatternCompleted(pattern: List<Int>) {
        if (LockSecurityManager.getPattern(context) == pattern) {
            onUnlocked()
        } else {
            patternResetTrigger++
            registerFailedAttempt()
        }
    }

    // Password Handler
    fun onPasswordSubmit() {
        if (LockSecurityManager.getPassword(context) == enteredPassword) {
            onUnlocked()
        } else {
            registerFailedAttempt()
        }
    }

    Scaffold(
        topBar = {
            if (showTopBar) {
                TopAppBar(
                    title = {
                        Text(
                            text = "Enter Lock",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            // Header Section
            Image(
                painter = painterResource(id = R.drawable.ic_splash_logo),
                contentDescription = "Shield Logo",
                modifier = Modifier.size(80.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = when (activeLockType) {
                    LockTypeOption.PIN -> "Enter PIN Lock"
                    LockTypeOption.PATTERN -> "Draw Pattern Lock"
                    LockTypeOption.PASSWORD -> "Enter Password Lock"
                },
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Verify your lock to access protected area",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = errorMessage,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(modifier = Modifier.height(60.dp))

            // Lock Input Area based on Active Lock Type
            when (activeLockType) {
                LockTypeOption.PIN -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        // 4 PIN Dot Indicators directly above keypad
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(18.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            for (i in 0 until 4) {
                                val isFilled = i < enteredPin.length
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isFilled) BluePrimary
                                            else MaterialTheme.colorScheme.surfaceVariant,
                                        ),
                                )
                            }
                        }

                        // Doubled vertical space between input dots and numbers grid
                        Spacer(modifier = Modifier.height(32.dp))

                        // Keypad Grid
                        val keypadRows = listOf(
                            listOf("1", "2", "3"),
                            listOf("4", "5", "6"),
                            listOf("7", "8", "9"),
                            listOf("", "0", "DEL"),
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            for (row in keypadRows) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    for (key in row) {
                                        when (key) {
                                            "DEL" -> {
                                                KeypadButton(digit = "⌫") {
                                                    onPinBackspace()
                                                }
                                            }
                                            "" -> {
                                                Spacer(modifier = Modifier.size(68.dp))
                                            }
                                            else -> {
                                                KeypadButton(digit = key) {
                                                    onPinKeyPress(key)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                LockTypeOption.PATTERN -> {
                    Box(
                        modifier = Modifier
                            .size(300.dp)
                            .padding(12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        PatternGridCanvas(
                            resetTrigger = patternResetTrigger,
                        ) { pattern -> onPatternCompleted(pattern) }
                    }
                }
                LockTypeOption.PASSWORD -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        OutlinedTextField(
                            value = enteredPassword,
                            onValueChange = { enteredPassword = it },
                            label = { Text("Enter Password") },
                            singleLine = true,
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                Text(
                                    text = if (isPasswordVisible) "Hide" else "Show",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BluePrimary,
                                    modifier = Modifier
                                        .clickable { isPasswordVisible = !isPasswordVisible }
                                        .padding(horizontal = 12.dp),
                                )
                            },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { onPasswordSubmit() }),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BluePrimary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                            ),
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { onPasswordSubmit() },
                            enabled = enteredPassword.length >= 4,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BluePrimary,
                                contentColor = Color.White,
                            ),
                        ) {
                            Text(text = "Unlock", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LockVerificationPreview() {
    SylvenSecureTheme {
        LockVerificationScreen(onBackClick = {}) {}
    }
}
