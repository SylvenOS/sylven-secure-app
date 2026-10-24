package com.sylvenos.secure.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sylvenos.secure.ui.theme.SylvenAccent
import com.sylvenos.secure.ui.theme.SylvenNavy
import com.sylvenos.secure.ui.theme.SylvenWhite
import com.sylvenos.secure.ui.theme.SylvenWhite40
import com.sylvenos.secure.ui.theme.SylvenWhite70
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(onSplashComplete: () -> Unit) {

    // ── Animatables ───────────────────────────────────────────────────────────
    val logoAlpha    = remember { Animatable(0f) }
    val logoScale    = remember { Animatable(0.55f) }
    val taglineAlpha = remember { Animatable(0f) }
    val accentWidth  = remember { Animatable(0f) }
    val exitAlpha    = remember { Animatable(1f) }

    // Glow pulse behind logo
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue  = 0.15f,
        targetValue   = 0.45f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    // Staggered bouncing dots
    val dot1Y by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -10f,
        animationSpec = infiniteRepeatable(
            tween(500, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ), label = "dot1"
    )
    val dot2Y by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -10f,
        animationSpec = infiniteRepeatable(
            tween(500, delayMillis = 150, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ), label = "dot2"
    )
    val dot3Y by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -10f,
        animationSpec = infiniteRepeatable(
            tween(500, delayMillis = 300, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ), label = "dot3"
    )

    // ── Animation orchestration ───────────────────────────────────────────────
    LaunchedEffect(Unit) {
        launch { logoAlpha.animateTo(1f,  tween(900, easing = FastOutSlowInEasing)) }
        launch { logoScale.animateTo(1f,  tween(900, easing = FastOutSlowInEasing)) }
        delay(400)
        launch { accentWidth.animateTo(1f, tween(700, easing = FastOutSlowInEasing)) }
        delay(700)
        launch { taglineAlpha.animateTo(1f, tween(600, easing = FastOutSlowInEasing)) }
        delay(2400)
        exitAlpha.animateTo(0f, tween(500, easing = FastOutSlowInEasing))
        onSplashComplete()
    }

    // ── UI ────────────────────────────────────────────────────────────────────
    Box(
        modifier         = Modifier
            .fillMaxSize()
            .background(SylvenNavy)
            .alpha(exitAlpha.value),
        contentAlignment = Alignment.Center
    ) {

        // Radial glow halo
        Canvas(modifier = Modifier.size(320.dp)) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(SylvenAccent.copy(alpha = glowAlpha), Color.Transparent),
                    center = center,
                    radius = size.minDimension / 1.4f
                ),
                radius = size.minDimension / 1.4f
            )
        }

        // Center content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier            = Modifier.padding(horizontal = 32.dp)
        ) {
            // Logo
            Text(
                text          = "SylvenOS",
                color         = SylvenWhite,
                fontSize      = 58.sp,
                fontWeight    = FontWeight.ExtraBold,
                textAlign     = TextAlign.Center,
                letterSpacing = 6.sp,
                modifier      = Modifier
                    .alpha(logoAlpha.value)
                    .scale(logoScale.value)
            )

            Spacer(Modifier.height(10.dp))

            // Animated accent line
            Canvas(
                modifier = Modifier
                    .width(160.dp)
                    .height(3.dp)
                    .alpha(logoAlpha.value)
            ) {
                val w = size.width * accentWidth.value
                drawLine(
                    brush = Brush.horizontalGradient(
                        listOf(Color.Transparent, SylvenAccent, Color.Transparent)
                    ),
                    start       = Offset((size.width - w) / 2f, size.height / 2f),
                    end         = Offset((size.width + w) / 2f, size.height / 2f),
                    strokeWidth = size.height,
                    cap         = StrokeCap.Round
                )
            }

            Spacer(Modifier.height(18.dp))

            // Tagline
            Text(
                text          = "Secure  ·  Smart  ·  Simple",
                color         = SylvenWhite70,
                fontSize      = 13.sp,
                fontWeight    = FontWeight.Light,
                textAlign     = TextAlign.Center,
                letterSpacing = 2.sp,
                modifier      = Modifier.alpha(taglineAlpha.value)
            )
        }

        // Bouncing loader at bottom
        Column(
            modifier            = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 56.dp)
                .alpha(taglineAlpha.value),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text          = "LOADING",
                color         = SylvenWhite40,
                fontSize      = 10.sp,
                letterSpacing = 3.sp,
                fontWeight    = FontWeight.Light
            )
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BouncingDot(dot1Y)
                BouncingDot(dot2Y)
                BouncingDot(dot3Y)
            }
        }
    }
}

@Composable
private fun BouncingDot(offsetY: Float) {
    Canvas(modifier = Modifier.size(7.dp)) {
        drawCircle(
            color  = SylvenAccent,
            radius = size.minDimension / 2f,
            center = Offset(center.x, center.y + offsetY)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF12253F)
@Composable
fun SplashScreenPreview() {
    SplashScreen(onSplashComplete = {})
}
