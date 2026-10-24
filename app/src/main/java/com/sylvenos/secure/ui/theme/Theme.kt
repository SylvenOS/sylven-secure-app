package com.sylvenos.secure.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary        = SylvenAccent,
    secondary      = SylvenGold,
    tertiary       = SylvenWhite70,
    background     = SylvenNavy,
    surface        = SylvenNavyLight,
    onPrimary      = SylvenWhite,
    onBackground   = SylvenWhite,
    onSurface      = SylvenWhite
)

private val LightColorScheme = lightColorScheme(
    primary        = SylvenNavy,
    secondary      = SylvenAccent,
    tertiary       = SylvenGold,
    background     = SylvenWhite,
    surface        = SylvenWhite,
    onPrimary      = SylvenWhite,
    onBackground   = SylvenNavy,
    onSurface      = SylvenNavy
)

@Composable
fun SylvenSecureTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,          // disabled – we enforce brand colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else      -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}
