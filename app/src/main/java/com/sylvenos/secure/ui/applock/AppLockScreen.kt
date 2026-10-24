package com.sylvenos.secure.ui.applock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.sylvenos.secure.ui.theme.SylvenNavy
import com.sylvenos.secure.ui.theme.SylvenSecureTheme
import com.sylvenos.secure.ui.theme.SylvenWhite
import com.sylvenos.secure.ui.theme.SylvenWhite70

/**
 * App Lock screen — lets users select which installed apps to lock
 * with PIN / pattern / biometrics.
 *
 * TODO: wire [AppLockViewModel] and display installed app list.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppLockScreen(onBack: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SylvenNavy)
    ) {
        TopAppBar(
            title = {
                Text(
                    text       = "App Lock",
                    color      = SylvenWhite,
                    fontWeight = FontWeight.SemiBold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint               = SylvenWhite
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = SylvenNavy
            )
        )

        // Placeholder — will be replaced with app list + lock toggles
        Box(
            modifier         = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text       = "App Lock — Coming Soon",
                color      = SylvenWhite70,
                fontSize   = 16.sp
            )
        }
    }
}

@Preview
@Composable
fun AppLockScreenPreview() {
    SylvenSecureTheme { AppLockScreen() }
}
