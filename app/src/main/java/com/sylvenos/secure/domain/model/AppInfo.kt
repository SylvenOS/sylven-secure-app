package com.sylvenos.secure.domain.model

import android.graphics.drawable.Drawable

/**
 * Represents a single installed app on the device.
 *
 * @param packageName  Unique package identifier (e.g. "com.example.app")
 * @param appName      Human-readable display name
 * @param icon         App icon drawable (nullable for system apps without icons)
 * @param isLocked     Whether this app is currently protected by App Lock
 * @param isHidden     Whether this app is currently hidden from the launcher
 * @param isSystemApp  True if installed as part of the system image
 */
data class AppInfo(
    val packageName : String,
    val appName     : String,
    val icon        : Drawable?  = null,
    val isLocked    : Boolean    = false,
    val isHidden    : Boolean    = false,
    val isSystemApp : Boolean    = false
)
