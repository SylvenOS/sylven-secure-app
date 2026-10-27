package com.sylvenos.tools

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.runtime.LaunchedEffect
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
import com.sylvenos.tools.ui.applock.AppLockHome
import com.sylvenos.tools.ui.applock.AppLockService
import com.sylvenos.tools.ui.applock.LockSecurityManager
import com.sylvenos.tools.ui.applock.LockVerification
import com.sylvenos.tools.ui.applock.PinSetup
import com.sylvenos.tools.ui.permission.PermissionScreen
import com.sylvenos.tools.ui.permission.hasAllRequiredPermissions
import com.sylvenos.tools.ui.splash.SplashScreen
import com.sylvenos.tools.ui.theme.BluePrimary
import com.sylvenos.tools.ui.theme.SylvenSecureTheme

enum class MainAppDestination {
    SPLASH,
    HOME,
    PERMISSION,
    FIRST_TIME_PIN_SETUP,
    LOCK_VERIFICATION,
    APP_LOCK_HOME,
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

    // Ensure background service is running when locked packages exist
    LaunchedEffect(Unit) {
        if (hasAllRequiredPermissions(context)) {
            if (LockSecurityManager.getLockedPackages(context).isNotEmpty()) {
                AppLockService.startService(context)
            }
        }
    }

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
                        // Clicking "App Lock" -> Check required permissions
                        if (!hasAllRequiredPermissions(context)) {
                            navigateTo(MainAppDestination.PERMISSION)
                        } else if (!LockSecurityManager.isLockSet(context)) {
                            navigateTo(MainAppDestination.FIRST_TIME_PIN_SETUP)
                        } else {
                            navigateTo(MainAppDestination.LOCK_VERIFICATION)
                        }
                    }
                }
                MainAppDestination.PERMISSION -> {
                    PermissionScreen {
                        // Once permissions allowed -> Check if lock is set
                        if (backStack.isNotEmpty()) {
                            backStack.removeAt(backStack.lastIndex)
                        }
                        if (!LockSecurityManager.isLockSet(context)) {
                            navigateTo(MainAppDestination.FIRST_TIME_PIN_SETUP)
                        } else {
                            navigateTo(MainAppDestination.LOCK_VERIFICATION)
                        }
                    }
                }
                MainAppDestination.FIRST_TIME_PIN_SETUP -> {
                    PinSetup(
                        onBackClick = { navigateBack() },
                    ) {
                        if (backStack.isNotEmpty()) {
                            backStack.removeAt(backStack.lastIndex)
                        }
                        navigateTo(MainAppDestination.APP_LOCK_HOME)
                    }
                }
                MainAppDestination.LOCK_VERIFICATION -> {
                    LockVerification(
                        onBackClick = { navigateBack() },
                    ) {
                        if (backStack.isNotEmpty()) {
                            backStack.removeAt(backStack.lastIndex)
                        }
                        navigateTo(MainAppDestination.APP_LOCK_HOME)
                    }
                }
                MainAppDestination.APP_LOCK_HOME -> {
                    AppLockHome { navigateBack() }
                }
            }
        }
    }
}

// Main Home Screen Dashboard for Sylven Secure
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAppLockClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                    ) {
                        Text(
                            text = "Sylven Secure",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.alignByBaseline(),
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Open Source",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = BluePrimary,
                            modifier = Modifier.alignByBaseline(),
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
            // App Lock
            HomeFeatureCard(
                title = "App Lock",
                description = "",
                icon = Icons.Default.Lock,
                accentColor = BluePrimary,
                onClick = onAppLockClick,
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

// Feature Card Item with ImageVector and clipped rounded corners for touch feedback
@Composable
fun HomeFeatureCard(
    title: String,
    description: String = "",
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

// Feature Card Item with Painter and clipped rounded corners for touch feedback
@Composable
fun HomeFeatureCardWithPainter(
    title: String,
    description: String = "",
    painter: Painter,
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
