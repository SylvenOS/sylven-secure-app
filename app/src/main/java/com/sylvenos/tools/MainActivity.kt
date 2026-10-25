package com.sylvenos.tools

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    var currentDestination by remember { mutableStateOf(value = AppDestination.SPLASH) }

    Crossfade(
        targetState = currentDestination,
        animationSpec = tween(durationMillis = 400),
        modifier = Modifier.fillMaxSize(),
        label = "AppScreenTransition",
    ) { destination ->
        when (destination) {
            AppDestination.SPLASH -> {
                SplashScreen {
                    //Launch -> Splash Screen -> Lands on Home Screen
                    currentDestination = AppDestination.HOME
                }
            }
            AppDestination.HOME -> {
                HomeScreen {
                    // Step 2: Clicking "App Lock and Hide" -> Check 3 required permissions
                    currentDestination = if (hasAllRequiredPermissions(context)) {
                        AppDestination.APP_LOCK_HIDE_HOME
                    } else {
                        AppDestination.PERMISSION
                    }
                }
            }
            AppDestination.PERMISSION -> {
                PermissionScreen {
                    // Step 3: Once all 3 permissions allowed -> Enter AppLockHideHome
                    currentDestination = AppDestination.APP_LOCK_HIDE_HOME
                }
            }
            AppDestination.APP_LOCK_HIDE_HOME -> {
                // Step 4: AppLockHideHome showing 3 options: 1. Lock Type, 2. App Lock, 3. Hide App
                AppLockHideHome(
                    onBackClick = {
                        currentDestination = AppDestination.HOME
                    },
                    onLockTypeClick = {
                        currentDestination = AppDestination.LOCK_TYPE
                    },
                    onAppLockClick = {
                        currentDestination = AppDestination.APP_LOCK
                    },
                ) {
                    currentDestination = AppDestination.HIDE_APP
                }
            }
            AppDestination.LOCK_TYPE -> {
                LockType {
                    currentDestination = AppDestination.APP_LOCK_HIDE_HOME
                }
            }
            AppDestination.APP_LOCK -> {
                AppLock {
                    currentDestination = AppDestination.APP_LOCK_HIDE_HOME
                }
            }
            AppDestination.HIDE_APP -> {
                HideApp {
                    currentDestination = AppDestination.APP_LOCK_HIDE_HOME
                }
            }
        }
    }
}
