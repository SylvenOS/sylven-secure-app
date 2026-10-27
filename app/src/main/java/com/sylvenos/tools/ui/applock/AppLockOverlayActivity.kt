package com.sylvenos.tools.ui.applock

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.sylvenos.tools.ui.theme.SylvenSecureTheme

// System Overlay Lock Window asking for active lock before granting access to protected app
class AppLockOverlayActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        overridePendingTransition(0, 0)
        enableEdgeToEdge()

        val targetPackage = intent?.getStringExtra("TARGET_PACKAGE_NAME") ?: ""

        setContent {
            SylvenSecureTheme {
                // Intercept back button to navigate to Android Home Screen so app remains protected
                BackHandler {
                    val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                        addCategory(Intent.CATEGORY_HOME)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    startActivity(homeIntent)
                    finish()
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    LockVerification(
                        showTopBar = false,
                        onBackClick = {
                            val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                                addCategory(Intent.CATEGORY_HOME)
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            startActivity(homeIntent)
                            finish()
                        },
                    ) {
                        if (targetPackage.isNotBlank()) {
                            AppLockService.unlockedSessionPackages.add(targetPackage)
                        }
                        finish()
                    }
                }
            }
        }
    }

    override fun finish() {
        super.finish()
        @Suppress("DEPRECATION")
        overridePendingTransition(0, 0)
    }
}
