package com.sylvenos.tools.ui.applock

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sylvenos.tools.R
import com.sylvenos.tools.ui.theme.BluePrimary
import com.sylvenos.tools.ui.theme.SylvenSecureTheme
import kotlin.math.hypot

// 2-Step Pattern Lock Creation & Verification Screen with refined thin vector lines and simplified nodes
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatternSetup(
    onBackClick: () -> Unit,
    onPatternCreated: () -> Unit,
) {
    val context = LocalContext.current
    var firstStepPattern by remember { mutableStateOf<List<Int>>(emptyList()) }
    var isConfirmStep by remember { mutableStateOf(value = false) }
    var errorMessage by remember { mutableStateOf("") }
    var resetTrigger by remember { mutableIntStateOf(0) }

    // Callback when user completes a pattern gesture
    fun onPatternCompleted(pattern: List<Int>) {
        if (pattern.size < 4) {
            errorMessage = "Connect at least 4 dots. Try again."
            resetTrigger++
            return
        }

        errorMessage = ""
        if (!isConfirmStep) {
            firstStepPattern = pattern
            isConfirmStep = true
            resetTrigger++
        } else {
            if (pattern == firstStepPattern) {
                // Pattern matched -> Save pattern and navigate
                LockSecurityManager.savePattern(context, pattern)
                onPatternCreated()
            } else {
                // Pattern mismatch -> Show error and reset to Step 1
                errorMessage = "Patterns do not match. Try again."
                firstStepPattern = emptyList()
                isConfirmStep = false
                resetTrigger++
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isConfirmStep) "Confirm Pattern" else "Set Pattern",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (isConfirmStep) {
                                isConfirmStep = false
                                firstStepPattern = emptyList()
                                errorMessage = ""
                                resetTrigger++
                            } else {
                                onBackClick()
                            }
                        },
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            // Header Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 12.dp),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_splash_logo),
                    contentDescription = "Shield Logo",
                    modifier = Modifier.size(80.dp),
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = if (isConfirmStep) "Confirm Pattern Lock" else "Set Pattern Lock",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isConfirmStep) "Draw pattern again to confirm" else "Connect at least 4 dots to set pattern",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )

                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = errorMessage,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }

            // Interactive 3x3 Pattern Canvas Area
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .padding(12.dp),
                contentAlignment = Alignment.Center,
            ) {
                PatternGridCanvas(
                    resetTrigger = resetTrigger,
                ) { pattern -> onPatternCompleted(pattern) }
            }

            // Clear Button resets back to Step 1 immediately
            Button(
                onClick = {
                    firstStepPattern = emptyList()
                    isConfirmStep = false
                    errorMessage = ""
                    resetTrigger++
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
            ) {
                Text(text = "Clear Pattern", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// 3x3 Interactive Pattern Canvas with simplified dot nodes and thin vector line
@Composable
fun PatternGridCanvas(
    resetTrigger: Int,
    onPatternComplete: (List<Int>) -> Unit,
) {
    val selectedNodes = remember(resetTrigger) { mutableStateListOf<Int>() }
    var currentTouchPoint by remember(resetTrigger) { mutableStateOf<Offset?>(null) }

    val activeLineColor = BluePrimary
    val dotColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    val activeDotColor = BluePrimary

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(resetTrigger) {
                detectDragGestures(
                    onDragStart = { offset ->
                        selectedNodes.clear()
                        currentTouchPoint = offset
                        val hitNode = findHitNode(offset, size.width.toFloat(), size.height.toFloat())
                        if (hitNode != -1) {
                            selectedNodes.add(hitNode)
                        }
                    },
                    onDrag = { change, _ ->
                        currentTouchPoint = change.position
                        val hitNode = findHitNode(change.position, size.width.toFloat(), size.height.toFloat())
                        if ((hitNode != -1) && !selectedNodes.contains(hitNode)) {
                            selectedNodes.add(hitNode)
                        }
                    },
                    onDragEnd = {
                        currentTouchPoint = null
                        if (selectedNodes.isNotEmpty()) {
                            onPatternComplete(selectedNodes.toList())
                        }
                    },
                    onDragCancel = {
                        currentTouchPoint = null
                        selectedNodes.clear()
                    },
                )
            },
    ) {
        val width = size.width
        val height = size.height
        val colStep = width / 3f
        val rowStep = height / 3f

        // Calculate 9 node center offsets
        val nodeCenters = List(9) { index ->
            val col = index % 3
            val row = index / 3
            Offset(
                x = (colStep * col) + (colStep / 2f),
                y = (rowStep * row) + (rowStep / 2f),
            )
        }

        // Refined thin stroke width (3.5dp) matching reference snapshot
        val lineStrokePx = 3.5.dp.toPx()

        // Draw connecting lines between selected nodes
        if (selectedNodes.isNotEmpty()) {
            val path = Path().apply {
                val startPos = nodeCenters[selectedNodes[0]]
                moveTo(startPos.x, startPos.y)
                for (i in 1 until selectedNodes.size) {
                    val nextPos = nodeCenters[selectedNodes[i]]
                    lineTo(nextPos.x, nextPos.y)
                }
            }

            drawPath(
                path = path,
                color = activeLineColor,
                style = Stroke(width = lineStrokePx, cap = StrokeCap.Round, join = StrokeJoin.Round),
            )

            // Draw line to current touch drag point
            currentTouchPoint?.let { touchPos ->
                val lastNodePos = nodeCenters[selectedNodes.last()]
                drawLine(
                    color = activeLineColor,
                    start = lastNodePos,
                    end = touchPos,
                    strokeWidth = lineStrokePx,
                    cap = StrokeCap.Round,
                )
            }
        }

        // Draw 9 simplified grid dot nodes
        nodeCenters.forEachIndexed { index, center ->
            val isSelected = selectedNodes.contains(index)
            drawCircle(
                color = if (isSelected) activeDotColor else dotColor,
                radius = if (isSelected) 5.dp.toPx() else 4.dp.toPx(),
                center = center,
            )
        }
    }
}

// Helper function to check if touch hit is inside node radius
private fun findHitNode(touch: Offset, width: Float, height: Float): Int {
    val colStep = width / 3f
    val rowStep = height / 3f
    val hitRadius = colStep * 0.4f

    for (index in 0 until 9) {
        val col = index % 3
        val row = index / 3
        val centerX = (colStep * col) + (colStep / 2f)
        val centerY = (rowStep * row) + (rowStep / 2f)

        val dist = hypot((touch.x - centerX).toDouble(), (touch.y - centerY).toDouble()).toFloat()
        if (dist <= hitRadius) {
            return index
        }
    }
    return -1
}

@Preview(showBackground = true)
@Composable
fun PatternSetupPreview() {
    SylvenSecureTheme {
        PatternSetup(onBackClick = {}) {}
    }
}
