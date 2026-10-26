package com.sylvenos.tools.ui.applock

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

// 2-Step Password Lock Creation & Verification Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordSetupScreen(
    onBackClick: () -> Unit,
    onPasswordCreated: () -> Unit,
) {
    val context = LocalContext.current
    var firstStepPassword by remember { mutableStateOf("") }
    var confirmStepPassword by remember { mutableStateOf("") }
    var isConfirmStep by remember { mutableStateOf(value = false) }
    var isPasswordVisible by remember { mutableStateOf(value = false) }
    var errorMessage by remember { mutableStateOf("") }

    val currentInputPassword = if (isConfirmStep) confirmStepPassword else firstStepPassword

    // Handle step submission
    fun onSubmit() {
        errorMessage = ""
        if (currentInputPassword.length < 4) {
            errorMessage = "Password must be at least 4 characters long."
            return
        }

        if (!isConfirmStep) {
            // Move to Step 2 (Confirm Password)
            isConfirmStep = true
        } else {
            if (confirmStepPassword == firstStepPassword) {
                // Password matched -> Save password and navigate
                LockSecurityManager.savePassword(context, confirmStepPassword)
                onPasswordCreated()
            } else {
                // Password mismatch -> Show error and reset
                errorMessage = "Passwords do not match. Try again."
                firstStepPassword = ""
                confirmStepPassword = ""
                isConfirmStep = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isConfirmStep) "Confirm Password" else "Set Password",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (isConfirmStep) {
                                isConfirmStep = false
                                firstStepPassword = ""
                                confirmStepPassword = ""
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            // Top Section (Logo, Title, TextField)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_splash_logo),
                    contentDescription = "Shield Logo",
                    modifier = Modifier.size(90.dp),
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = if (isConfirmStep) "Confirm Password" else "Set Password",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isConfirmStep) "Re-enter your password to confirm" else "Enter a password with at least 4 characters",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Password Input Field
                OutlinedTextField(
                    value = currentInputPassword,
                    onValueChange = { input ->
                        errorMessage = ""
                        if (isConfirmStep) {
                            confirmStepPassword = input
                        } else {
                            firstStepPassword = input
                        }
                    },
                    label = {
                        Text(if (isConfirmStep) "Confirm Password" else "Enter Password")
                    },
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
                    keyboardActions = KeyboardActions(onDone = { onSubmit() }),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BluePrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    ),
                )

                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = errorMessage,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Bottom Action Button
            Button(
                onClick = { onSubmit() },
                enabled = currentInputPassword.length >= 4,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BluePrimary,
                    contentColor = Color.White,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                ),
            ) {
                Text(
                    text = if (isConfirmStep) "Save Password" else "Continue",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PasswordSetupPreview() {
    SylvenSecureTheme {
        PasswordSetupScreen(onBackClick = {}) {}
    }
}
