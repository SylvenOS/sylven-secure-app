package com.sylvenos.secure.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sylvenos.secure.ui.theme.SylvenAccent
import com.sylvenos.secure.ui.theme.SylvenNavy
import com.sylvenos.secure.ui.theme.SylvenNavyLight
import com.sylvenos.secure.ui.theme.SylvenSecureTheme
import com.sylvenos.secure.ui.theme.SylvenWhite
import com.sylvenos.secure.ui.theme.SylvenWhite70

@Composable
fun HomeScreen(
    onNavigateToAppLock  : () -> Unit = {},
    onNavigateToAppHider : () -> Unit = {},
    onNavigateToSettings : () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SylvenNavy)
    ) {
        Column(
            modifier            = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Top bar ───────────────────────────────────────────────────────
            Row(
                modifier            = Modifier
                    .fillMaxWidth()
                    .padding(top = 52.dp, bottom = 8.dp),
                verticalAlignment   = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text       = "SylvenOS",
                        color      = SylvenWhite,
                        fontSize   = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text     = "Your privacy dashboard",
                        color    = SylvenWhite70,
                        fontSize = 13.sp
                    )
                }
                IconButton(onClick = onNavigateToSettings) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint   = SylvenWhite70,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // ── Feature cards ─────────────────────────────────────────────────
            HomeFeatureCard(
                icon        = Icons.Default.Lock,
                title       = "App Lock",
                description = "Protect apps with PIN, pattern or biometrics",
                onClick     = onNavigateToAppLock
            )

            Spacer(Modifier.height(16.dp))

            HomeFeatureCard(
                icon        = Icons.Default.VisibilityOff,
                title       = "App Hider",
                description = "Hide apps from the launcher and recent screen",
                onClick     = onNavigateToAppHider
            )
        }
    }
}

@Composable
private fun HomeFeatureCard(
    icon        : androidx.compose.ui.graphics.vector.ImageVector,
    title       : String,
    description : String,
    onClick     : () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SylvenNavyLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier          = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier         = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SylvenAccent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = icon,
                    contentDescription = title,
                    tint               = SylvenAccent,
                    modifier           = Modifier.size(24.dp)
                )
            }
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            ) {
                Text(
                    text       = title,
                    color      = SylvenWhite,
                    fontSize   = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text     = description,
                    color    = SylvenWhite70,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    SylvenSecureTheme { HomeScreen() }
}
