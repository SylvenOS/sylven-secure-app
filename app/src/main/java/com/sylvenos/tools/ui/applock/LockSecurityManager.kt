package com.sylvenos.tools.ui.applock

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

// Standardized Security Manager for saving and verifying all lock types (PIN, Pattern, Password)
object LockSecurityManager {
    private const val PREF_NAME = "sylven_security_prefs"
    private const val KEY_LOCK_TYPE = "key_active_lock_type"
    private const val KEY_PIN = "key_app_lock_pin"
    private const val KEY_PATTERN = "key_app_lock_pattern"
    private const val KEY_PASSWORD = "key_app_lock_password"
    private const val KEY_LOCKED_PACKAGES = "key_locked_packages_set"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // Get active lock mode (PIN, PATTERN, PASSWORD)
    fun getLockType(context: Context): LockTypeOption {
        val typeStr = getPreferences(context).getString(KEY_LOCK_TYPE, LockTypeOption.PIN.name)
        return try {
            LockTypeOption.valueOf(typeStr ?: LockTypeOption.PIN.name)
        } catch (_: Exception) {
            LockTypeOption.PIN
        }
    }

    // Set active lock mode
    fun setLockType(context: Context, lockType: LockTypeOption) {
        getPreferences(context).edit {
            putString(KEY_LOCK_TYPE, lockType.name)
        }
    }

    // Check if any security lock (PIN, Pattern, or Password) is configured
    fun isLockSet(context: Context): Boolean {
        return when (getLockType(context)) {
            LockTypeOption.PIN -> isPinSet(context)
            LockTypeOption.PATTERN -> isPatternSet(context)
            LockTypeOption.PASSWORD -> isPasswordSet(context)
        }
    }

    // PIN Methods
    fun isPinSet(context: Context): Boolean {
        return getPreferences(context).getString(KEY_PIN, null)?.isNotBlank() == true
    }

    fun savePin(context: Context, pin: String) {
        setLockType(context, LockTypeOption.PIN)
        getPreferences(context).edit {
            putString(KEY_PIN, pin)
        }
    }

    fun getPin(context: Context): String? {
        return getPreferences(context).getString(KEY_PIN, null)
    }

    // Pattern Methods
    fun isPatternSet(context: Context): Boolean {
        return getPreferences(context).getString(KEY_PATTERN, null)?.isNotBlank() == true
    }

    fun savePattern(context: Context, patternNodes: List<Int>) {
        setLockType(context, LockTypeOption.PATTERN)
        val patternString = patternNodes.joinToString(separator = ",")
        getPreferences(context).edit {
            putString(KEY_PATTERN, patternString)
        }
    }

    fun getPattern(context: Context): List<Int> {
        val patternString = getPreferences(context).getString(KEY_PATTERN, "") ?: ""
        if (patternString.isBlank()) return emptyList()
        return try {
            patternString.split(",").map { it.toInt() }
        } catch (_: Exception) {
            emptyList()
        }
    }

    // Password Methods
    fun isPasswordSet(context: Context): Boolean {
        return getPreferences(context).getString(KEY_PASSWORD, null)?.isNotBlank() == true
    }

    fun savePassword(context: Context, password: String) {
        setLockType(context, LockTypeOption.PASSWORD)
        getPreferences(context).edit {
            putString(KEY_PASSWORD, password)
        }
    }

    fun getPassword(context: Context): String? {
        return getPreferences(context).getString(KEY_PASSWORD, null)
    }

    // Locked Package Methods
    fun getLockedPackages(context: Context): Set<String> {
        return getPreferences(context).getStringSet(KEY_LOCKED_PACKAGES, emptySet()) ?: emptySet()
    }

    fun addLockedPackage(context: Context, packageName: String) {
        val currentSet = getLockedPackages(context).toMutableSet()
        currentSet.add(packageName)
        getPreferences(context).edit {
            putStringSet(KEY_LOCKED_PACKAGES, currentSet)
        }
        AppLockService.startService(context)
    }

    fun removeLockedPackage(context: Context, packageName: String) {
        val currentSet = getLockedPackages(context).toMutableSet()
        currentSet.remove(packageName)
        getPreferences(context).edit {
            putStringSet(KEY_LOCKED_PACKAGES, currentSet)
        }
        if (currentSet.isEmpty()) {
            AppLockService.stopService(context)
        }
    }

    fun isPackageLocked(context: Context, packageName: String): Boolean {
        return getLockedPackages(context).contains(packageName)
    }
}
