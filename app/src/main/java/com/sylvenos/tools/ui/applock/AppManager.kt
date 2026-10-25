package com.sylvenos.tools.ui.applock

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap

// Data class representing an installed app on the user device
data class InstalledAppItem(
    val packageName: String,
    val appName: String,
    val icon: Drawable? = null,
)

// Helper function to fetch all launchable installed apps on the device
fun getInstalledApps(context: Context): List<InstalledAppItem> {
    val packageManager = context.packageManager
    val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
        addCategory(Intent.CATEGORY_LAUNCHER)
    }

    // Query all launchable activities
    val resolveInfoList = packageManager.queryIntentActivities(mainIntent, 0)
    val appList = mutableListOf<InstalledAppItem>()
    val seenPackages = mutableSetOf<String>()

    for (resolveInfo in resolveInfoList) {
        val packageName = resolveInfo.activityInfo.packageName
        if ((packageName != context.packageName) && seenPackages.add(packageName)) {
            val appName = resolveInfo.loadLabel(packageManager).toString()
            val icon = resolveInfo.loadIcon(packageManager)
            appList.add(InstalledAppItem(packageName, appName, icon))
        }
    }

    // Sort apps alphabetically by app name
    return appList.sortedBy { it.appName.lowercase() }
}

// Convert Drawable icon to Compose ImageBitmap safely
fun Drawable.toImageBitmap(): ImageBitmap? {
    return try {
        this.toBitmap().asImageBitmap()
    } catch (_: Exception) {
        null
    }
}
