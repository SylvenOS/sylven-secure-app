package com.sylvenos.secure.data.repository

import com.sylvenos.secure.data.local.PreferencesManager
import com.sylvenos.secure.domain.model.LockConfig
import com.sylvenos.secure.domain.model.LockType
import com.sylvenos.secure.domain.repository.AppLockRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

/**
 * Concrete implementation of [AppLockRepository] backed by [PreferencesManager].
 *
 * Emits reactive updates via a [MutableStateFlow] so the UI can observe
 * lock-state changes without polling.
 */
class AppLockRepositoryImpl(
    private val prefs: PreferencesManager
) : AppLockRepository {

    // In-memory cache of locked package names, kept in sync with prefs
    private val _lockedApps = MutableStateFlow(prefs.getLockedApps())

    override fun getLockConfigs(): Flow<List<LockConfig>> =
        _lockedApps.map { set ->
            set.map { pkg ->
                LockConfig(
                    packageName = pkg,
                    lockType    = LockType.valueOf(prefs.getLockType(pkg)),
                    isEnabled   = true
                )
            }
        }

    override suspend fun getLockConfig(packageName: String): LockConfig? {
        if (!prefs.isLocked(packageName)) return null
        return LockConfig(
            packageName = packageName,
            lockType    = LockType.valueOf(prefs.getLockType(packageName)),
            isEnabled   = true
        )
    }

    override suspend fun setLockConfig(config: LockConfig) {
        if (config.isEnabled) {
            prefs.addLockedApp(config.packageName)
            prefs.setLockType(config.packageName, config.lockType.name)
        } else {
            prefs.removeLockedApp(config.packageName)
        }
        _lockedApps.value = prefs.getLockedApps()
    }

    override suspend fun removeLockConfig(packageName: String) {
        prefs.removeLockedApp(packageName)
        _lockedApps.value = prefs.getLockedApps()
    }

    override suspend fun isLocked(packageName: String): Boolean =
        prefs.isLocked(packageName)
}
