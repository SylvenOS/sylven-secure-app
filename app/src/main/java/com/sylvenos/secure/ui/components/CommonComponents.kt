package com.sylvenos.secure.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sylvenos.secure.ui.theme.SylvenAccent
import com.sylvenos.secure.ui.theme.SylvenNavyLight
import com.sylvenos.secure.ui.theme.SylvenWhite
import com.sylvenos.secure.ui.theme.SylvenWhite40
import com.sylvenos.secure.ui.theme.SylvenWhite70

// ── Loading indicator ─────────────────────────────────────────────────────────

/** Centered [CircularProgressIndicator] using the brand accent colour. */
@Composable
fun SylvenLoader(modifier: Modifier = Modifier, size: Dp = 40.dp) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            modifier  = Modifier.size(size),
            color     = SylvenAccent,
            strokeWidth = 3.dp
        )
    }
}

// ── Section header ────────────────────────────────────────────────────────────

/** A labelled section divider with a subtle rule line. */
@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Text(
            text          = title.uppercase(),
            color         = SylvenWhite40,
            fontSize      = 11.sp,
            fontWeight    = FontWeight.SemiBold,
            letterSpacing = 2.sp
        )
    }
}

// ── Divider ───────────────────────────────────────────────────────────────────

/** A subtle 1-dp horizontal divider. */
@Composable
fun SylvenDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .padding(horizontal = 24.dp)
            .background(SylvenWhite40.copy(alpha = 0.12f))
    )
}

// ── Empty state ───────────────────────────────────────────────────────────────

/** Shown when a list is empty. */
@Composable
fun EmptyState(message: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Text(
            text      = message,
            color     = SylvenWhite70,
            fontSize  = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

// ── Card surface ──────────────────────────────────────────────────────────────

/** A rounded card with the app's surface colour. */
@Composable
fun SylvenCard(
    modifier : Modifier  = Modifier,
    radius   : Dp        = 16.dp,
    content  : @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(radius))
            .background(SylvenNavyLight)
    ) {
        content()
    }
}

// ── Screen title ──────────────────────────────────────────────────────────────

/** Large page heading in the brand white. */
@Composable
fun ScreenTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text       = text,
        color      = SylvenWhite,
        fontSize   = 22.sp,
        fontWeight = FontWeight.Bold,
        modifier   = modifier
    )
}
