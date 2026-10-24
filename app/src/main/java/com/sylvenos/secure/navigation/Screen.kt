package com.sylvenos.secure.navigation

/**
 * Sealed class representing every navigable screen in the app.
 * Each object's [route] is the unique NavHost destination string.
 */
sealed class Screen(val route: String) {
    data object Splash    : Screen("splash")
    data object Home      : Screen("home")
    data object AppLock   : Screen("app_lock")
    data object AppHider  : Screen("app_hider")
    data object Settings  : Screen("settings")
}
