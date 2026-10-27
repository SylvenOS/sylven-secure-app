package com.sylvenos.tools.ui.applock

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.sylvenos.tools.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

// Standalone Foreground Service monitoring ONLY locked apps at ~10ms interval
class AppLockService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())

    companion object {
        private const val CHANNEL_ID = "AppLockServiceChannel"
        private const val NOTIFICATION_ID = 1001

        // Session memory tracking temporarily unlocked apps
        val unlockedSessionPackages = mutableSetOf<String>()

        fun startService(context: Context) {
            val intent = Intent(context, AppLockService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, AppLockService::class.java)
            context.stopService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Sylven Lock Protection Active")
            .setContentText("App Lock is running in the background")
            .setSmallIcon(R.drawable.ic_splash_logo)
            .setOngoing(true)
            .build()
        startForeground(NOTIFICATION_ID, notification)

        startTargetedMonitoringLoop()
    }

    // High-performance targeted monitoring loop at ~10ms interval for instant detection
    private fun startTargetedMonitoringLoop() {
        serviceScope.launch {
            var lastForegroundPackage: String? = null

            while (isActive) {
                val lockedPackages = LockSecurityManager.getLockedPackages(applicationContext)

                if (lockedPackages.isNotEmpty()) {
                    val currentPackage = getForegroundAppPackage(applicationContext)

                    if ((currentPackage != null) && (currentPackage != applicationContext.packageName)) {
                        // When user leaves an app, remove it from unlockedSessionPackages immediately
                        if ((lastForegroundPackage != null) && (lastForegroundPackage != currentPackage)) {
                            unlockedSessionPackages.remove(lastForegroundPackage)
                        }
                        lastForegroundPackage = currentPackage

                        // Check if current app is in the locked package set
                        if (lockedPackages.contains(currentPackage)) {
                            // If not yet unlocked in current session -> Launch Lock Overlay
                            if (!unlockedSessionPackages.contains(currentPackage)) {
                                launchLockOverlay(currentPackage)
                            }
                        }
                    } else if ((lastForegroundPackage != null) && (currentPackage != lastForegroundPackage)) {
                        // User returned to home screen or system UI -> Reset session unlock
                        unlockedSessionPackages.remove(lastForegroundPackage)
                        lastForegroundPackage = null
                    }
                }

                delay(10.milliseconds)
            }
        }
    }

    // Query current foreground application using UsageStatsManager with reliable 3-second window
    private fun getForegroundAppPackage(context: Context): String? {
        val manager = context.getSystemService(USAGE_STATS_SERVICE) as? UsageStatsManager
        val usageStatsManager = manager ?: return null
        val time = System.currentTimeMillis()
        val events = usageStatsManager.queryEvents(time - 3000, time)
        var foregroundPackage: String? = null
        val event = UsageEvents.Event()

        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            if ((event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) || (event.eventType == 1)) {
                foregroundPackage = event.packageName
            }
        }
        return foregroundPackage
    }

    // Launch overlay lock activity over the targeted app as an isolated standalone task
    private fun launchLockOverlay(targetPackage: String) {
        val intent = Intent(applicationContext, AppLockOverlayActivity::class.java).apply {
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK or
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                        Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS or
                        Intent.FLAG_ACTIVITY_NO_ANIMATION,
            )
            putExtra("TARGET_PACKAGE_NAME", targetPackage)
        }
        startActivity(intent)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "App Lock Protection",
                NotificationManager.IMPORTANCE_LOW,
            )
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
