package com.sylvenos.secure.domain.usecase

import android.content.Context
import android.content.pm.PackageManager
import com.sylvenos.secure.domain.model.AppInfo

/**
 * Retrieves the list of user-installed (and optionally system) apps
 * from the device's [PackageManager].
 *
 * Usage:
 * ```kotlin
 * val apps = GetInstalledAppsUseCase(context).invoke(includeSystemApps = false)
 * ```
 */
class GetInstalledAppsUseCase(private val context: Context) {

    /**
     * @param includeSystemApps  When true, system packages are included.
     * @return Sorted list of [AppInfo] objects.
     */
    suspend operator fun invoke(includeSystemApps: Boolean = false): List<AppInfo> {
        val pm    = context.packageManager
        val flags = PackageManager.GET_META_DATA

        @Suppress("DEPRECATION")
        val packages = pm.getInstalledApplications(flags)

        return packages
            .filter { info ->
                val isSystem = (info.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0
                includeSystemApps || !isSystem
            }
            .map { info ->
                val isSystem = (info.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0
                AppInfo(
                    packageName = info.packageName,
                    appName     = pm.getApplicationLabel(info).toString(),
                    icon        = pm.getApplicationIcon(info),
                    isSystemApp = isSystem
                )
            }
            .sortedBy { it.appName }
    }
}
