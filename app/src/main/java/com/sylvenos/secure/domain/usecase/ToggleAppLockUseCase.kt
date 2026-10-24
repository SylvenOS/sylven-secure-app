package com.sylvenos.secure.domain.usecase

import com.sylvenos.secure.domain.model.LockConfig
import com.sylvenos.secure.domain.repository.AppLockRepository

/**
 * Toggles the lock state of a given app.
 *
 * If the app has no [LockConfig] yet (or it's disabled), the supplied
 * [config] is saved and enabled.  If it's already enabled, it is disabled.
 *
 * Usage:
 * ```kotlin
 * toggleAppLock(config)
 * ```
 */
class ToggleAppLockUseCase(private val repository: AppLockRepository) {

    suspend operator fun invoke(config: LockConfig) {
        val current = repository.getLockConfig(config.packageName)
        if (current == null || !current.isEnabled) {
            repository.setLockConfig(config.copy(isEnabled = true))
        } else {
            repository.setLockConfig(current.copy(isEnabled = false))
        }
    }
}
