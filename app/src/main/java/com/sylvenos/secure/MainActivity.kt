package com.sylvenos.secure

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.sylvenos.secure.navigation.NavGraph
import com.sylvenos.secure.ui.theme.SylvenSecureTheme

/**
 * Single-Activity entry point for SylvenSecure.
 *
 * All navigation is handled by [NavGraph] (Compose Navigation).
 * The Activity itself stays lean — no business logic here.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SylvenSecureTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}
