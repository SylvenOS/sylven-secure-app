package com.sylvenos.tools.ui.applock

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sylvenos.tools.ui.theme.BluePrimary
import com.sylvenos.tools.ui.theme.SylvenSecureTheme

enum class LockTypeOption {
    PATTERN,
    PIN,
    PASSWORD,
}

enum class LockSetupMode {
    MENU,
    PIN_SETUP,
    PATTERN_SETUP,
    PASSWORD_SETUP,
}

// LockType Feature Container managing lock type selection and configuration setups (PIN, Pattern, Password)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LockType(
    onBackClick: () -> Unit,
) {
    val context = LocalContext.current
    var activeMode by remember { mutableStateOf(LockSetupMode.MENU) }
    var selectedOption by remember { mutableStateOf(LockSecurityManager.getLockType(context)) }
    var isFaceEnabled by remember { mutableStateOf(value = false) }
    var isFingerprintEnabled by remember { mutableStateOf(value = false) }

    // Intercept system back button / side swipe gesture when inside a sub setup screen
    BackHandler(enabled = (activeMode != LockSetupMode.MENU)) {
        activeMode = LockSetupMode.MENU
    }

    Crossfade(
        targetState = activeMode,
        animationSpec = tween(
            durationMillis = 250,
            easing = FastOutSlowInEasing,
        ),
        label = "LockTypeCrossfade",
    ) { mode ->
        when (mode) {
            LockSetupMode.MENU -> {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "App lock type",
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
                    },
                    containerColor = MaterialTheme.colorScheme.background,
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp, vertical = 20.dp),
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                // Pattern
                                LockTypeRow(
                                    title = "Pattern",
                                    subtitle = if (selectedOption == LockTypeOption.PATTERN) "Active - Medium security" else "Medium security",
                                    isSelected = selectedOption == LockTypeOption.PATTERN,
                                ) {
                                    selectedOption = LockTypeOption.PATTERN
                                    LockSecurityManager.setLockType(context, LockTypeOption.PATTERN)
                                    activeMode = LockSetupMode.PATTERN_SETUP
                                }

                                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                                // PIN
                                LockTypeRow(
                                    title = "PIN",
                                    subtitle = if (selectedOption == LockTypeOption.PIN) "Active - Medium to high security" else "Medium to high security",
                                    isSelected = selectedOption == LockTypeOption.PIN,
                                ) {
                                    selectedOption = LockTypeOption.PIN
                                    LockSecurityManager.setLockType(context, LockTypeOption.PIN)
                                    activeMode = LockSetupMode.PIN_SETUP
                                }

                                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                                // Password
                                LockTypeRow(
                                    title = "Password",
                                    subtitle = if (selectedOption == LockTypeOption.PASSWORD) "Active - High security" else "High security",
                                    isSelected = selectedOption == LockTypeOption.PASSWORD,
                                ) {
                                    selectedOption = LockTypeOption.PASSWORD
                                    LockSecurityManager.setLockType(context, LockTypeOption.PASSWORD)
                                    activeMode = LockSetupMode.PASSWORD_SETUP
                                }

                                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                                // Face Biometric Toggle Row
                                LockTypeSwitchRow(
                                    title = "Face",
                                    isChecked = isFaceEnabled,
                                ) { isFaceEnabled = it }

                                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                                // Fingerprints Biometric Toggle Row
                                LockTypeSwitchRow(
                                    title = "Fingerprints",
                                    isChecked = isFingerprintEnabled,
                                ) { isFingerprintEnabled = it }
                            }
                        }
                    }
                }
            }
            LockSetupMode.PIN_SETUP -> {
                PinSetupScreen(
                    onBackClick = { activeMode = LockSetupMode.MENU },
                ) {
                    activeMode = LockSetupMode.MENU
                }
            }
            LockSetupMode.PATTERN_SETUP -> {
                PatternSetupScreen(
                    onBackClick = { activeMode = LockSetupMode.MENU },
                ) {
                    activeMode = LockSetupMode.MENU
                }
            }
            LockSetupMode.PASSWORD_SETUP -> {
                PasswordSetupScreen(
                    onBackClick = { activeMode = LockSetupMode.MENU },
                ) {
                    activeMode = LockSetupMode.MENU
                }
            }
        }
    }
}

// Individual Selectable Lock Type Row
@Composable
fun LockTypeRow(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isSelected) BluePrimary.copy(alpha = 0.12f)
                else Color.Transparent,
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = title,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) BluePrimary else MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = subtitle,
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            color = if (isSelected) BluePrimary else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// Individual Biometric Switch Row
@Composable
fun LockTypeSwitchRow(
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = BluePrimary,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LockTypePreview() {
    SylvenSecureTheme {
        LockType {}
    }
}
