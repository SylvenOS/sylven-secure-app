package com.sylvenos.tools.ui.applock

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sylvenos.tools.ui.theme.BluePrimary
import com.sylvenos.tools.ui.theme.SylvenSecureTheme

enum class AppLockSubDestination {
    MENU_HOME,
    LOCK_TYPE,
    APP_LOCK,
}

// AppLockHome Feature Container managing sub-screens (LOCK_TYPE, APP_LOCK)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppLockHome(
    onBackClick: () -> Unit,
) {
    val context = LocalContext.current
    var subDestination by remember { mutableStateOf(AppLockSubDestination.MENU_HOME) }

    // Intercept system back button / side swipe gesture when inside a sub-screen
    BackHandler(enabled = (subDestination != AppLockSubDestination.MENU_HOME)) {
        subDestination = AppLockSubDestination.MENU_HOME
    }

    val activeLockName = when (LockSecurityManager.getLockType(context)) {
        LockTypeOption.PATTERN -> "Pattern"
        LockTypeOption.PIN -> "PIN"
        LockTypeOption.PASSWORD -> "Password"
    }

    Crossfade(
        targetState = subDestination,
        animationSpec = tween(
            durationMillis = 250,
            easing = FastOutSlowInEasing,
        ),
        label = "AppLockSubCrossfade",
    ) { destination ->
        when (destination) {
            AppLockSubDestination.MENU_HOME -> {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "App Lock",
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
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        // Option 1: Lock Type (Displays currently active lock)
                        OptionCard(
                            title = "Lock Type",
                            description = "Change lock types - Active $activeLockName",
                            icon = Icons.Outlined.Lock,
                            accentColor = BluePrimary,
                        ) {
                            subDestination = AppLockSubDestination.LOCK_TYPE
                        }

                        // Option 2: App Lock
                        OptionCard(
                            title = "App Lock",
                            description = "Lock/Unlock Apps",
                            icon = Icons.Default.Lock,
                            accentColor = Color(0xFF10B981),
                        ) {
                            subDestination = AppLockSubDestination.APP_LOCK
                        }
                    }
                }
            }
            AppLockSubDestination.LOCK_TYPE -> {
                LockType {
                    subDestination = AppLockSubDestination.MENU_HOME
                }
            }
            AppLockSubDestination.APP_LOCK -> {
                AppLock {
                    subDestination = AppLockSubDestination.MENU_HOME
                }
            }
        }
    }
}

// Individual Option Card Item with ImageVector and clipped rounded corners for touch feedback
@Composable
fun OptionCard(
    title: String,
    description: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = accentColor.copy(alpha = 0.12f),
                    modifier = Modifier.size(48.dp),
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = accentColor,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = description,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp,
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Arrow Right",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppLockHomePreview() {
    SylvenSecureTheme {
        AppLockHome {}
    }
}
