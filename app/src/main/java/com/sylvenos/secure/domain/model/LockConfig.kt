package com.sylvenos.secure.domain.model

/**
 * Describes the lock configuration for a single app.
 *
 * @param packageName  App being configured
 * @param lockType     The kind of authentication required
 * @param isEnabled    Whether the lock is currently active
 */
data class LockConfig(
    val packageName : String,
    val lockType    : LockType  = LockType.NONE,
    val isEnabled   : Boolean   = false
)

/** Supported authentication methods. */
enum class LockType {
    NONE,
    PIN,
    PATTERN,
    BIOMETRIC,
    PIN_AND_BIOMETRIC
}
