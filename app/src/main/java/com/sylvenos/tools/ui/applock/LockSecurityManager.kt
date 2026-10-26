package com.sylvenos.tools.ui.applock

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

// Secure Manager for saving and retrieving PIN lock settings
object LockSecurityManager {
    private const val PREF_NAME = "sylven_security_prefs"
    private const val KEY_PIN = "key_app_lock_pin"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // Check if 4-digit PIN is set
    fun isPinSet(context: Context): Boolean {
        return getPreferences(context).getString(KEY_PIN, null)?.isNotBlank() == true
    }

    // Save 4-digit PIN
    fun savePin(context: Context, pin: String) {
        getPreferences(context).edit {
            putString(KEY_PIN, pin)
        }
    }

    // Retrieve saved PIN
    fun getPin(context: Context): String? {
        return getPreferences(context).getString(KEY_PIN, null)
    }

    // Verify if entered PIN matches saved PIN
    fun verifyPin(context: Context, enteredPin: String): Boolean {
        return getPin(context) == enteredPin
    }
}
