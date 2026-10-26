package com.sylvenos.tools

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.sylvenos.tools.ui.applock.AppLockHideHome
import com.sylvenos.tools.ui.applock.LockSecurityManager
<<<<<<< HEAD
=======
import com.sylvenos.tools.ui.applock.LockType
import com.sylvenos.tools.ui.applock.LockTypeOption
import com.sylvenos.tools.ui.applock.PasswordSetupScreen
import com.sylvenos.tools.ui.applock.PatternSetupScreen
>>>>>>> origin/main
import com.sylvenos.tools.ui.applock.PinSetupScreen
import com.sylvenos.tools.ui.permission.PermissionScreen
import com.sylvenos.tools.ui.permission.hasAllRequiredPermissions
import com.sylvenos.tools.ui.splash.SplashScreen
import com.sylvenos.tools.ui.theme.BluePrimary
import com.sylvenos.tools.ui.theme.SylvenSecureTheme

enum class MainAppDestination {
    SPLASH,
    HOME,
    PERMISSION,
<<<<<<< HEAD
    FIRST_TIME_PIN_SETUP,
=======
    PIN_SETUP,
    PATTERN_SETUP,
    PASSWORD_SETUP,
>>>>>>> origin/main
    APP_LOCK_HIDE_HOME,
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SylvenSecureTheme {
                SylvenOSApp()
            }
        }
    }
}

@Composable
fun SylvenOSApp() {
    val context = LocalContext.current

    // Root Navigation Stack tracking main app flow
    val backStack = remember { mutableStateListOf(MainAppDestination.SPLASH) }
    val currentDestination = backStack.lastOrNull() ?: MainAppDestination.HOME

    // Push new destination to navigation stack
    fun navigateTo(destination: MainAppDestination) {
        if (backStack.lastOrNull() != destination) {
            backStack.add(destination)
        }
    }

    // Pop top destination from navigation stack
    fun navigateBack(): Boolean {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
            return true
        }
        return false
    }

    // Intercept Android System Softkey Back Button & Side Swipe Gesture
    BackHandler(enabled = ((backStack.size > 1) && (currentDestination != MainAppDestination.SPLASH))) {
        navigateBack()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Crossfade(
            targetState = currentDestination,
            animationSpec = tween(
                durationMillis = 250,
                easing = FastOutSlowInEasing,
            ),
            modifier = Modifier.fillMaxSize(),
            label = "AppScreenTransition",
        ) { destination ->
            when (destination) {
                MainAppDestination.SPLASH -> {
                    SplashScreen {
                        // Splash finishes -> Clear stack and set HOME as root
                        backStack.clear()
                        backStack.add(MainAppDestination.HOME)
                    }
                }
                MainAppDestination.HOME -> {
                    HomeScreen {
                        // Clicking "App Lock and Hide" -> Check 3 required permissions
                        if (!hasAllRequiredPermissions(context)) {
                            navigateTo(MainAppDestination.PERMISSION)
                        } else if (!LockSecurityManager.isLockSet(context)) {
<<<<<<< HEAD
                            navigateTo(MainAppDestination.FIRST_TIME_PIN_SETUP)
=======
                            when (LockSecurityManager.getLockType(context)) {
                                LockTypeOption.PATTERN -> navigateTo(AppDestination.PATTERN_SETUP)
                                LockTypeOption.PASSWORD -> navigateTo(AppDestination.PASSWORD_SETUP)
                                else -> navigateTo(AppDestination.PIN_SETUP)
                            }
>>>>>>> origin/main
                        } else {
                            navigateTo(MainAppDestination.APP_LOCK_HIDE_HOME)
                        }
                    }
                }
                MainAppDestination.PERMISSION -> {
                    PermissionScreen {
                        // Once all 3 permissions allowed -> Check if lock is set
                        if (backStack.isNotEmpty()) {
                            backStack.removeAt(backStack.lastIndex)
                        }
                        if (!LockSecurityManager.isLockSet(context)) {
<<<<<<< HEAD
                            navigateTo(MainAppDestination.FIRST_TIME_PIN_SETUP)
=======
                            when (LockSecurityManager.getLockType(context)) {
                                LockTypeOption.PATTERN -> navigateTo(AppDestination.PATTERN_SETUP)
                                LockTypeOption.PASSWORD -> navigateTo(AppDestination.PASSWORD_SETUP)
                                else -> navigateTo(AppDestination.PIN_SETUP)
                            }
>>>>>>> origin/main
                        } else {
                            navigateTo(MainAppDestination.APP_LOCK_HIDE_HOME)
                        }
                    }
                }
                MainAppDestination.FIRST_TIME_PIN_SETUP -> {
                    PinSetupScreen(
                        onBackClick = { navigateBack() },
                    ) {
                        if (backStack.isNotEmpty()) {
                            backStack.removeAt(backStack.lastIndex)
                        }
                        navigateTo(MainAppDestination.APP_LOCK_HIDE_HOME)
                    }
                }
<<<<<<< HEAD
                MainAppDestination.APP_LOCK_HIDE_HOME -> {
                    AppLockHideHome { navigateBack() }
=======
                AppDestination.PATTERN_SETUP -> {
                    PatternSetupScreen(
                        onBackClick = { navigateBack() },
                    ) {
                        if (backStack.isNotEmpty()) {
                            backStack.removeAt(backStack.lastIndex)
                        }
                        navigateTo(AppDestination.APP_LOCK_HIDE_HOME)
                    }
                }
                AppDestination.PASSWORD_SETUP -> {
                    PasswordSetupScreen(
                        onBackClick = { navigateBack() },
                    ) {
                        if (backStack.isNotEmpty()) {
                            backStack.removeAt(backStack.lastIndex)
                        }
                        navigateTo(AppDestination.APP_LOCK_HIDE_HOME)
                    }
                }
                AppDestination.APP_LOCK_HIDE_HOME -> {
                    AppLockHideHome(
                        onBackClick = { navigateBack() },
                        onLockTypeClick = { navigateTo(AppDestination.LOCK_TYPE) },
                        onAppLockClick = { navigateTo(AppDestination.APP_LOCK) },
                    ) {
                        navigateTo(AppDestination.HIDE_APP)
                    }
                }
                AppDestination.LOCK_TYPE -> {
                    LockType(
                        onBackClick = { navigateBack() },
                    ) { lockOption ->
                        when (lockOption) {
                            LockTypeOption.PATTERN -> {
                                navigateTo(AppDestination.PATTERN_SETUP)
                            }
                            LockTypeOption.PIN -> {
                                navigateTo(AppDestination.PIN_SETUP)
                            }
                            LockTypeOption.PASSWORD -> {
                                navigateTo(AppDestination.PASSWORD_SETUP)
                            }
                        }
                    }
                }
                AppDestination.APP_LOCK -> {
                    AppLock { navigateBack() }
                }
                AppDestination.HIDE_APP -> {
                    HideApp { navigateBack() }
>>>>>>> origin/main
                }
            }
        }
    }
}

// Main Home Screen Dashboard for Sylven Tools
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAppLockHideClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.Start,
                    ) {
                        Text(
                            text = "Sylven Tools",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Open Source",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = BluePrimary,
                            modifier = Modifier.padding(bottom = 2.dp),
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
            // App Lock and Hide
            HomeFeatureCard(
                title = "App Lock and Hide",
                description = "",
                icon = Icons.Default.Lock,
                accentColor = BluePrimary,
                onClick = onAppLockHideClick,
            )

            // WiFi Security - Planned
            HomeFeatureCardWithPainter(
                title = "WiFi Security",
                description = "Planned",
                painter = painterResource(id = R.drawable.ic_wifi_security),
                accentColor = Color(0xFF10B981),
            ) {}

            // Secure Vault - Planned
            HomeFeatureCard(
                title = "Secure Vault",
                description = "Planned",
                icon = Icons.Outlined.Lock,
                accentColor = Color(0xFF8B5CF6),
            ) {}
        }
    }
}

// Feature Card Item with ImageVector
@Composable
fun HomeFeatureCard(
    title: String,
    description: String = "",
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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

                    if (description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = description,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp,
                        )
                    }
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

// Feature Card Item with Painter
@Composable
fun HomeFeatureCardWithPainter(
    title: String,
    description: String = "",
    painter: Painter,
    accentColor: Color,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
                            painter = painter,
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

                    if (description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = description,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp,
                        )
                    }
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
