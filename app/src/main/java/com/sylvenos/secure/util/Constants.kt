package com.sylvenos.secure.util

/**
 * App-wide constants used across features.
 * Group related constants in nested objects for clarity.
 */
object Constants {

    // ── App metadata ──────────────────────────────────────────────────────────
    const val APP_NAME    = "SylvenOS"
    const val APP_VERSION = "1.0"

    // ── SharedPreferences ─────────────────────────────────────────────────────
    object Prefs {
        const val PREFS_FILE     = "sylven_secure_prefs"
        const val KEY_FIRST_RUN  = "first_run"
        const val KEY_PIN_HASH   = "pin_hash"
        const val KEY_LOCKED_APPS  = "locked_apps"
        const val KEY_HIDDEN_APPS  = "hidden_apps"
        const val KEY_DARK_THEME   = "dark_theme"
        const val KEY_LOCK_TYPE    = "lock_type_"      // prefix; append packageName
    }

    // ── Splash ────────────────────────────────────────────────────────────────
    object Splash {
        const val DURATION_MS     = 3500L
        const val FADE_IN_MS      = 900
        const val ACCENT_DELAY_MS = 400L
        const val TAGLINE_DELAY_MS = 700L
        const val EXIT_FADE_MS    = 500
    }

    // ── PIN ───────────────────────────────────────────────────────────────────
    object Pin {
        const val MIN_LENGTH = 4
        const val MAX_LENGTH = 6
    }

    // ── Intent actions ────────────────────────────────────────────────────────
    object Actions {
        const val LOCK_SERVICE_START = "com.sylvenos.secure.action.START_LOCK_SERVICE"
        const val LOCK_SERVICE_STOP  = "com.sylvenos.secure.action.STOP_LOCK_SERVICE"
    }

    // ── Notification ─────────────────────────────────────────────────────────
    object Notification {
        const val CHANNEL_ID   = "sylven_lock_channel"
        const val CHANNEL_NAME = "App Lock Service"
        const val NOTIF_ID     = 1001
    }
}
