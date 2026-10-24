package com.sylvenos.secure.util

import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import java.security.MessageDigest

// ── String extensions ─────────────────────────────────────────────────────────

/**
 * Returns the SHA-256 hex digest of this string.
 * Used for PIN hashing — never store raw PINs.
 */
fun String.sha256(): String {
    val bytes = MessageDigest.getInstance("SHA-256").digest(this.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}

/** Returns true if the string is a valid numeric PIN of the expected length. */
fun String.isValidPin(minLen: Int = 4, maxLen: Int = 6): Boolean =
    this.length in minLen..maxLen && this.all { it.isDigit() }

// ── Context extensions ────────────────────────────────────────────────────────

/** Shows a short [Toast] message. */
fun Context.showToast(message: String) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

/** Returns true if the given [packageName] is installed on this device. */
fun Context.isPackageInstalled(packageName: String): Boolean = try {
    packageManager.getPackageInfo(packageName, 0)
    true
} catch (e: PackageManager.NameNotFoundException) {
    false
}

// ── Collection extensions ─────────────────────────────────────────────────────

/** Returns the element at [index], or null if out of range. */
fun <T> List<T>.getOrNull(index: Int): T? =
    if (index in indices) this[index] else null

/** Toggles the presence of [item] in a [MutableSet]. */
fun <T> MutableSet<T>.toggle(item: T) {
    if (item in this) remove(item) else add(item)
}
