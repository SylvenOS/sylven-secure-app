package com.sylvenos.secure.domain.repository

import com.sylvenos.secure.domain.model.AppInfo
import com.sylvenos.secure.domain.model.LockConfig
import kotlinx.coroutines.flow.Flow

/**
 * Contract for persisting and querying app-lock configurations.
 *
 * Implementation lives in [data.repository.AppLockRepositoryImpl].
 */
interface AppLockRepository {

    /** Emits the current lock config for all configured apps. */
    fun getLockConfigs(): Flow<List<LockConfig>>

    /** Returns the lock config for a specific [packageName], or null if not set. */
    suspend fun getLockConfig(packageName: String): LockConfig?

    /** Saves or updates the lock config for one app. */
    suspend fun setLockConfig(config: LockConfig)

    /** Removes the lock config, effectively unlocking the app. */
    suspend fun removeLockConfig(packageName: String)

    /** Returns true if [packageName] is currently locked. */
    suspend fun isLocked(packageName: String): Boolean
}
