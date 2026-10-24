package com.sylvenos.secure.data.repository

import com.sylvenos.secure.data.local.PreferencesManager
import com.sylvenos.secure.domain.repository.AppHiderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Concrete implementation of [AppHiderRepository] backed by [PreferencesManager].
 *
 * Note: actual launcher-level hiding requires device-owner / DPC privileges or
 * a custom launcher.  This repository manages the *intent* (which apps to hide),
 * and the enforcement layer will be wired in a later task.
 */
class AppHiderRepositoryImpl(
    private val prefs: PreferencesManager
) : AppHiderRepository {

    private val _hiddenApps = MutableStateFlow(prefs.getHiddenApps())

    override fun getHiddenApps(): Flow<Set<String>> = _hiddenApps.asStateFlow()

    override suspend fun setHidden(packageName: String, hide: Boolean) {
        prefs.setHidden(packageName, hide)
        _hiddenApps.value = prefs.getHiddenApps()
    }

    override suspend fun isHidden(packageName: String): Boolean =
        prefs.isHidden(packageName)

    override suspend fun clearAll() {
        prefs.clearHiddenApps()
        _hiddenApps.value = emptySet()
    }
}
