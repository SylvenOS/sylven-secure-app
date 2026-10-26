package com.sylvenos.tools

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.sylvenos.tools.ui.applock.AppLock
import com.sylvenos.tools.ui.applock.AppLockHideHome
import com.sylvenos.tools.ui.applock.HideApp
import com.sylvenos.tools.ui.applock.LockType
import com.sylvenos.tools.ui.home.HomeScreen
import com.sylvenos.tools.ui.permission.PermissionScreen
import com.sylvenos.tools.ui.permission.hasAllRequiredPermissions
import com.sylvenos.tools.ui.splash.SplashScreen
import com.sylvenos.tools.ui.theme.SylvenSecureTheme

enum class AppDestination {
    SPLASH,
    HOME,
    PERMISSION,
    APP_LOCK_HIDE_HOME,
    LOCK_TYPE,
    APP_LOCK,
    HIDE_APP,
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

    // Navigation backstack array list starting with SPLASH
    val backStack = remember { mutableStateListOf(AppDestination.SPLASH) }
    val currentDestination = backStack.lastOrNull() ?: AppDestination.HOME

    // Push new destination to navigation stack
    fun navigateTo(destination: AppDestination) {
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
    BackHandler(enabled = (backStack.size > 1) && (currentDestination != AppDestination.SPLASH)) {
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
                AppDestination.SPLASH -> {
                    SplashScreen {
                        // Splash finishes -> Clear stack and set HOME as root
                        backStack.clear()
                        backStack.add(AppDestination.HOME)
                    }
                }
                AppDestination.HOME -> {
                    HomeScreen {
                        // Clicking "App Lock and Hide" -> Check 3 required permissions
                        if (hasAllRequiredPermissions(context)) {
                            navigateTo(AppDestination.APP_LOCK_HIDE_HOME)
                        } else {
                            navigateTo(AppDestination.PERMISSION)
                        }
                    }
                }
                AppDestination.PERMISSION -> {
                    PermissionScreen {
                        // Once all 3 permissions allowed -> Replace PERMISSION with APP_LOCK_HIDE_HOME
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
                    LockType { navigateBack() }
                }
                AppDestination.APP_LOCK -> {
                    AppLock { navigateBack() }
                }
                AppDestination.HIDE_APP -> {
                    HideApp { navigateBack() }
                }
            }
        }
    }
}
