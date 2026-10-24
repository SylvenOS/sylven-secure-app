package com.sylvenos.secure.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * Thin wrapper around [SharedPreferences] for storing app settings,
 * lock configs, and hidden-app sets persistently on-device.
 *
 * All keys are namespaced under [PREFS_NAME] to avoid collisions.
 */
class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // ── Lock configs ──────────────────────────────────────────────────────────

    /** Stores which apps are locked (serialised as a Set<String> of packageNames). */
    fun getLockedApps(): Set<String> =
        prefs.getStringSet(KEY_LOCKED_APPS, emptySet()) ?: emptySet()

    fun addLockedApp(packageName: String) {
        val current = getLockedApps().toMutableSet()
        current.add(packageName)
        prefs.edit { putStringSet(KEY_LOCKED_APPS, current) }
    }

    fun removeLockedApp(packageName: String) {
        val current = getLockedApps().toMutableSet()
        current.remove(packageName)
        prefs.edit { putStringSet(KEY_LOCKED_APPS, current) }
    }

    fun isLocked(packageName: String): Boolean = packageName in getLockedApps()

    // ── Lock type per app ─────────────────────────────────────────────────────

    fun getLockType(packageName: String): String =
        prefs.getString(lockTypeKey(packageName), "NONE") ?: "NONE"

    fun setLockType(packageName: String, lockType: String) {
        prefs.edit { putString(lockTypeKey(packageName), lockType) }
    }

    // ── Hidden apps ───────────────────────────────────────────────────────────

    fun getHiddenApps(): Set<String> =
        prefs.getStringSet(KEY_HIDDEN_APPS, emptySet()) ?: emptySet()

    fun setHidden(packageName: String, hide: Boolean) {
        val current = getHiddenApps().toMutableSet()
        if (hide) current.add(packageName) else current.remove(packageName)
        prefs.edit { putStringSet(KEY_HIDDEN_APPS, current) }
    }

    fun isHidden(packageName: String): Boolean = packageName in getHiddenApps()

    fun clearHiddenApps() {
        prefs.edit { remove(KEY_HIDDEN_APPS) }
    }

    // ── PIN ───────────────────────────────────────────────────────────────────

    /** Stores a hashed PIN. Never store the raw PIN. */
    fun savePin(hashedPin: String) {
        prefs.edit { putString(KEY_PIN_HASH, hashedPin) }
    }

    fun getPin(): String? = prefs.getString(KEY_PIN_HASH, null)

    fun hasPin(): Boolean = getPin() != null

    fun clearPin() {
        prefs.edit { remove(KEY_PIN_HASH) }
    }

    // ── General settings ──────────────────────────────────────────────────────

    var isFirstLaunch: Boolean
        get()      = prefs.getBoolean(KEY_FIRST_LAUNCH, true)
        set(value) = prefs.edit { putBoolean(KEY_FIRST_LAUNCH, value) }

    var isDarkTheme: Boolean
        get()      = prefs.getBoolean(KEY_DARK_THEME, true)
        set(value) = prefs.edit { putBoolean(KEY_DARK_THEME, value) }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun lockTypeKey(packageName: String) = "lock_type_$packageName"

    companion object {
        private const val PREFS_NAME       = "sylven_secure_prefs"
        private const val KEY_LOCKED_APPS  = "locked_apps"
        private const val KEY_HIDDEN_APPS  = "hidden_apps"
        private const val KEY_PIN_HASH     = "pin_hash"
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_DARK_THEME   = "dark_theme"
    }
}
