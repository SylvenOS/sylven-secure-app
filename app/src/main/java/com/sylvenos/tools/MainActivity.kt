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
import com.sylvenos.tools.ui.applock.AppLockHideScreen
import com.sylvenos.tools.ui.home.HomeScreen
import com.sylvenos.tools.ui.permission.PermissionScreen
import com.sylvenos.tools.ui.permission.hasAllRequiredPermissions
import com.sylvenos.tools.ui.splash.SplashScreen
import com.sylvenos.tools.ui.theme.SylvenSecureTheme

enum class AppDestination {
    SPLASH,
    PERMISSION,
    HOME,
    APP_LOCK_HIDE,
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
                    currentDestination = if (hasAllRequiredPermissions(context)) {
                        AppDestination.HOME
                    } else {
                        AppDestination.PERMISSION
                    }
                }
            }
            AppDestination.PERMISSION -> {
                PermissionScreen {
                    currentDestination = AppDestination.HOME
                }
            }
            AppDestination.HOME -> {
                HomeScreen(
                    onAppLockHideClick = {
                        currentDestination = AppDestination.APP_LOCK_HIDE
                    },
                )
            }
            AppDestination.APP_LOCK_HIDE -> {
                AppLockHideScreen(
                    onBackClick = {
                        currentDestination = AppDestination.HOME
                    },
                )
            }
        }
    }
}
