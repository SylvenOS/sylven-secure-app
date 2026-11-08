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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sylvenos.tools.R
import com.sylvenos.tools.ui.theme.BluePrimary
import com.sylvenos.tools.ui.theme.SylvenSecureTheme

// 2-Step 4-Digit PIN Creation & Verification Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinSetup(
    onBackClick: () -> Unit,
    onPinCreated: () -> Unit,
) {
    val context = LocalContext.current
    var firstStepPin by remember { mutableStateOf("") }
    var confirmStepPin by remember { mutableStateOf("") }
    var isConfirmStep by remember { mutableStateOf(value = false) }
    var errorMessage by remember { mutableStateOf("") }

    val currentEnteredPin = if (isConfirmStep) confirmStepPin else firstStepPin

    // Handle keypad digit input
    fun onKeyPress(digit: String) {
        errorMessage = ""
        if (currentEnteredPin.length < 4) {
            val newPin = currentEnteredPin + digit
            if (isConfirmStep) {
                confirmStepPin = newPin
                if (newPin.length == 4) {
                    if (newPin == firstStepPin) {
                        // PIN matched -> Save PIN and navigate
                        LockSecurityManager.savePin(context, newPin)
                        onPinCreated()
                    } else {
                        // PIN mismatch -> Show error and reset
                        errorMessage = "PINs do not match. Try again."
                        firstStepPin = ""
                        confirmStepPin = ""
                        isConfirmStep = false
                    }
                }
            } else {
                firstStepPin = newPin
                if (newPin.length == 4) {
                    // Move to Step 2 (Confirm PIN)
                    isConfirmStep = true
                }
            }
        }
    }

    // Handle backspace key press
    fun onBackspace() {
        errorMessage = ""
        if (isConfirmStep) {
            if (confirmStepPin.isNotEmpty()) {
                confirmStepPin = confirmStepPin.dropLast(1)
            }
        } else {
            if (firstStepPin.isNotEmpty()) {
                firstStepPin = firstStepPin.dropLast(1)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isConfirmStep) "Confirm PIN" else "Set PIN",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (isConfirmStep) {
                                // Go back to Step 1
                                isConfirmStep = false
                                firstStepPin = ""
                                confirmStepPin = ""
                                errorMessage = ""
                            } else {
                                onBackClick()
                            }
                        },
                    ) {
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
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            // Top Section (Logo, Title, Indicators)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 16.dp),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_splash_logo),
                    contentDescription = "Shield Logo",
                    modifier = Modifier.size(90.dp),
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = if (isConfirmStep) "Confirm 4-Digit PIN" else "Set 4-Digit PIN",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isConfirmStep) "Re-enter your 4-digit PIN to confirm" else "Enter a 4-digit PIN to protect your apps",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 4 PIN Dot Indicators
                Row(
                    horizontalArrangement = Arrangement.spacedBy(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    for (i in 0 until 4) {
                        val isFilled = i < currentEnteredPin.length
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isFilled) BluePrimary
                                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                ),
                        )
                    }
                }

                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = errorMessage,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }

            // Custom Keypad Grid with doubled 32dp vertical space above
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val keypadRows = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("", "0", "DEL"),
                )

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
                                        onBackspace()
                                    }
                                }
                                "" -> {
                                    Spacer(modifier = Modifier.size(68.dp))
                                }
                                else -> {
                                    KeypadButton(digit = key) {
                                        onKeyPress(key)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Individual Numeric Keypad Button
@Composable
fun KeypadButton(
    digit: String,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .size(68.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            Text(
                text = digit,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PinSetupPreview() {
    SylvenSecureTheme {
        PinSetup(onBackClick = {}) {}
    }
}
