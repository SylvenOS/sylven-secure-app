package com.sylvenos.tools.ui.splash

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sylvenos.tools.R
import com.sylvenos.tools.ui.theme.SylvenSecureTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

/**
Splash Screen component, displays animated logo, typography, and progress indicator.
Automatically picks to Light/Dark system mode with smooth transitions.
@param onSplashFinished Callback invoked when the splash animation completes.
 */
@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
) {
    val scale = remember { Animatable(0.6f) }
    val alpha = remember { Animatable(0f) }
    val pulseScale = remember { Animatable(1f) }

    val animatedBgColor by animateColorAsState(
        targetValue = MaterialTheme.colorScheme.background,
        animationSpec = tween(durationMillis = 400),
        label = "SplashBgColor",
    )
    val animatedTextColor by animateColorAsState(
        targetValue = MaterialTheme.colorScheme.onBackground,
        animationSpec = tween(durationMillis = 400),
        label = "SplashTextColor",
    )
    val animatedPrimaryColor by animateColorAsState(
        targetValue = MaterialTheme.colorScheme.primary,
        animationSpec = tween(durationMillis = 400),
        label = "SplashPrimaryColor",
    )

    LaunchedEffect(Unit) {
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 800,
                easing = FastOutSlowInEasing,
            ),
        )
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 800,
                easing = FastOutSlowInEasing,
            ),
        )

        pulseScale.animateTo(
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
        )
    }

    LaunchedEffect(Unit) {
        delay(1500.milliseconds)
        onSplashFinished()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = animatedBgColor,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .scale(scale.value * pulseScale.value)
                    .alpha(alpha.value),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_splash_logo),
                    contentDescription = "SylvenOS Logo",
                    modifier = Modifier.size(160.dp),
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Sylven Secure",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
                    color = animatedTextColor,
                    letterSpacing = 2.sp,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Developed by SylvenOS",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = animatedPrimaryColor,
                    letterSpacing = 1.5.sp,
                )

                Spacer(modifier = Modifier.height(48.dp))

                CircularProgressIndicator(
                    color = animatedPrimaryColor,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SylvenSecureTheme {
        SplashScreen(onSplashFinished = {})
    }
}
