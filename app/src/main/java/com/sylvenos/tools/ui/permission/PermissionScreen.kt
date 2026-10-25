package com.sylvenos.tools.ui.permission

import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.os.Process
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.sylvenos.tools.R
import com.sylvenos.tools.ui.theme.BluePrimary
import com.sylvenos.tools.ui.theme.SylvenSecureTheme

/**
 * Checks if Usage Access (PACKAGE_USAGE_STATS) permission is granted.
 */
fun hasUsageAccessPermission(context: Context): Boolean {
    val appOps = (context.getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager) ?: return false
    val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        @Suppress("DEPRECATION")
        appOps.unsafeCheckOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName,
        )
    } else {
        @Suppress("DEPRECATION")
        appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName,
        )
    }
    return mode == AppOpsManager.MODE_ALLOWED
}

/**
 * Checks if Display Over Other Apps (SYSTEM_ALERT_WINDOW) permission is granted.
 */
fun hasOverlayPermission(context: Context): Boolean {
    return Settings.canDrawOverlays(context)
}

/**
 * Checks if Battery Optimization is disabled for this app to ensure smooth background operation.
 */
fun isBatteryOptimizationIgnored(context: Context): Boolean {
    val powerManager = (context.getSystemService(Context.POWER_SERVICE) as? PowerManager) ?: return true
    return powerManager.isIgnoringBatteryOptimizations(context.packageName)
}

/**
 * Requests system prompt to disable battery optimization.
 */
@SuppressLint("BatteryLife")
fun requestDisableBatteryOptimization(context: Context) {
    try {
        val intent = Intent(
            Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
            "package:${context.packageName}".toUri(),
        )
        context.startActivity(intent)
    } catch (_: Exception) {
        val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
        context.startActivity(intent)
    }
}

/**
 * Checks if all 3 required permissions for App Lock are granted.
 */
fun hasAllRequiredPermissions(context: Context): Boolean {
    return hasUsageAccessPermission(context) &&
            hasOverlayPermission(context) &&
            isBatteryOptimizationIgnored(context)
}

/**
 * Permission Required Screen matching the design and requirements.
 */
@Composable
fun PermissionScreen(
    onAllPermissionsGranted: () -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var isUsageGranted by remember { mutableStateOf(hasUsageAccessPermission(context)) }
    var isOverlayGranted by remember { mutableStateOf(hasOverlayPermission(context)) }
    var isBatteryIgnored by remember { mutableStateOf(isBatteryOptimizationIgnored(context)) }

    // Re-check permissions automatically when user returns to app from system settings
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isUsageGranted = hasUsageAccessPermission(context)
                isOverlayGranted = hasOverlayPermission(context)
                isBatteryIgnored = isBatteryOptimizationIgnored(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val areAllPermissionsGranted = isUsageGranted && isOverlayGranted && isBatteryIgnored

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(top = 12.dp),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_splash_logo),
                    contentDescription = "Shield Icon",
                    modifier = Modifier.size(90.dp),
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Permission Required",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "To protect your apps, S-Secure requires system permissions. Allow all to continue.",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(horizontal = 8.dp),
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 3 Permission Cards
                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    // Card 1: Usage Access
                    PermissionCard(
                        title = "Usage Access Permission",
                        description = "Detect when protected apps are launched.",
                        isGranted = isUsageGranted,
                    ) {
                        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                        context.startActivity(intent)
                    }

                    // Card 2: Display Over Other Apps
                    PermissionCard(
                        title = "Display Over Other Apps",
                        description = "Show lock overlay screen.",
                        isGranted = isOverlayGranted,
                    ) {
                        val intent = Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            "package:${context.packageName}".toUri(),
                        )
                        context.startActivity(intent)
                    }

                    // Card 3: Disable Battery Optimization
                    PermissionCard(
                        title = "Disable Battery Optimization",
                        description = "Ensure background app lock runs smoothly with zero lag.",
                        isGranted = isBatteryIgnored,
                    ) {
                        requestDisableBatteryOptimization(context)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Bottom Action Button: Continue to App (Dummy/Disabled until ALL 3 granted)
            Button(
                onClick = {
                    if (areAllPermissionsGranted) {
                        onAllPermissionsGranted()
                    }
                },
                enabled = areAllPermissionsGranted,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BluePrimary,
                    contentColor = Color.White,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                ),
            ) {
                Text(
                    text = "Continue to App",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

/**
 * Individual Permission Item Card.
 */
@Composable
fun PermissionCard(
    title: String,
    description: String,
    isGranted: Boolean,
    onAllowClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp,
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Button(
                onClick = onAllowClick,
                enabled = !isGranted,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BluePrimary,
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFFE2E8F0),
                    disabledContentColor = Color(0xFF258CF4),
                ),
                modifier = Modifier.height(38.dp),
            ) {
                Text(
                    text = if (isGranted) "Allowed ✓" else "Allow",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PermissionScreenPreview() {
    SylvenSecureTheme {
        PermissionScreen {}
    }
}
