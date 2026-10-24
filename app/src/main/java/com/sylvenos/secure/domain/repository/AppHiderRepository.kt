package com.sylvenos.secure.domain.repository

import com.sylvenos.secure.domain.model.AppInfo
import kotlinx.coroutines.flow.Flow

/**
 * Contract for hiding / un-hiding apps from the device launcher.
 *
 * Implementation lives in [data.repository.AppHiderRepositoryImpl].
 */
interface AppHiderRepository {

    /** Emits the set of currently hidden package names. */
    fun getHiddenApps(): Flow<Set<String>>

    /** Hides or un-hides [packageName] based on [hide]. */
    suspend fun setHidden(packageName: String, hide: Boolean)

    /** Returns true if [packageName] is currently hidden. */
    suspend fun isHidden(packageName: String): Boolean

    /** Removes all hidden-app entries (full restore). */
    suspend fun clearAll()
}
