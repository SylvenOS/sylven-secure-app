package com.sylvenos.secure.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sylvenos.secure.ui.apphider.AppHiderScreen
import com.sylvenos.secure.ui.applock.AppLockScreen
import com.sylvenos.secure.ui.home.HomeScreen
import com.sylvenos.secure.ui.settings.SettingsScreen
import com.sylvenos.secure.ui.splash.SplashScreen

/**
 * Root navigation graph for SylvenSecure.
 *
 * Splash is the entry point; it pops itself before pushing Home so the
 * back-stack never returns to the splash screen.
 */
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController    = navController,
        startDestination = Screen.Splash.route
    ) {

        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToAppLock  = { navController.navigate(Screen.AppLock.route) },
                onNavigateToAppHider = { navController.navigate(Screen.AppHider.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(Screen.AppLock.route) {
            AppLockScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.AppHider.route) {
            AppHiderScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Settings.route) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
