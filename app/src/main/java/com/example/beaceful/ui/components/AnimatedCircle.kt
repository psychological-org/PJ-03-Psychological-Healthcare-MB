package com.example.beaceful.ui.components

import android.util.Log
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.beaceful.ui.screens.diary.PieChartData
import kotlin.math.*

private const val DividerLengthInDegrees = 1.8f

@Composable
fun AnimatedCircle(
    proportions: List<PieChartData>,
    modifier: Modifier = Modifier
) {
    val currentState = remember {
        MutableTransitionState(AnimatedCircleProgress.START)
            .apply { targetState = AnimatedCircleProgress.END }
    }
    val stroke = with(LocalDensity.current) { Stroke(50.dp.toPx()) }
    val transition = rememberTransition(currentState)
    val angleOffset by transition.animateFloat(
        transitionSpec = {
            tween(
                delayMillis = 500,
                durationMillis = 900,
                easing = LinearOutSlowInEasing
            )
        }
    ) { progress ->
        if (progress == AnimatedCircleProgress.START) {
            0f
        } else {
            360f
        }
    }
    val shift by transition.animateFloat(
        transitionSpec = {
            tween(
                delayMillis = 500,
                durationMillis = 900,
                easing = CubicBezierEasing(0f, 0.75f, 0.35f, 0.85f)
            )
        }
    ) { progress ->
        if (progress == AnimatedCircleProgress.START) {
            0f
        } else {
            30f
        }
    }

    Box(Modifier.padding(36.dp)){
        Canvas(
            modifier = Modifier
                .height(300.dp)
                .align(Alignment.Center)
                .fillMaxWidth()
        ) {
            val innerRadius = (size.minDimension - stroke.width) / 2
            val halfSize = size / 2.0f
            val topLeft = Offset(
                halfSize.width - innerRadius,
                halfSize.height - innerRadius
            )
            val size = Size(innerRadius * 2, innerRadius * 2)
            var startAngle = shift
            proportions.forEach { proportion ->
                val sweep = proportion.score * angleOffset
                drawArc(
                    color = proportion.color,
                    startAngle = startAngle + DividerLengthInDegrees / 2,
                    sweepAngle = sweep - DividerLengthInDegrees,
                    topLeft = topLeft,
                    size = size,
                    useCenter = false,
                    style = stroke
                )
                val midAngle = startAngle + sweep / 2
                val angleInRad = Math.toRadians(midAngle.toDouble())

                val lineStart = Offset(
                    x = (halfSize.width + cos(angleInRad) * innerRadius).toFloat(),
                    y = (halfSize.height + sin(angleInRad) * innerRadius).toFloat()
                )

                val lineEnd = Offset(
                    x = (halfSize.width + cos(angleInRad) * (innerRadius + 120)).toFloat(),
                    y = (halfSize.height + sin(angleInRad) * (innerRadius + 120)).toFloat()
                )

                val labelPos = Offset(
                    x = (halfSize.width + cos(angleInRad) * (innerRadius + 160)).toFloat(),
                    y = (halfSize.height + sin(angleInRad) * (innerRadius + 160)).toFloat()
                )

// Draw guide line
                drawLine(
                    color = Color.Black,
                    start = lineStart,
                    end = lineEnd,
                    strokeWidth = 2f
                )
// Draw label
                drawIntoCanvas {
                    val text = proportion.label

                    // Viền trắng
                    val strokePaint = android.graphics.Paint().apply {
                        style = android.graphics.Paint.Style.STROKE
                        strokeWidth = 6f
                        color = android.graphics.Color.WHITE
                        textSize = 36f
                        isAntiAlias = true
                    }

                    // Chữ đen bên trong
                    val fillPaint = android.graphics.Paint().apply {
                        style = android.graphics.Paint.Style.FILL
                        color = android.graphics.Color.BLACK
                        textSize = 36f
                        isAntiAlias = true
                    }

                    it.nativeCanvas.drawText(text, labelPos.x, labelPos.y, strokePaint) // viền
                    it.nativeCanvas.drawText(text, labelPos.x, labelPos.y, fillPaint)   // nội dung
                }
                startAngle += sweep
            }
        }
    }
}
private enum class AnimatedCircleProgress { START, END }

fun preprocessProportions(
    raw: List<PieChartData>,
    minAngleThreshold: Float = 5f,
    totalAngle: Float = 360f - DividerLengthInDegrees * raw.size
): List<PieChartData> {
    val filtered = mutableListOf<PieChartData>()
    val grouped = mutableListOf<PieChartData>()
    for (item in raw) {
        val angle = item.score * totalAngle
        if (angle >= minAngleThreshold) {
            filtered.add(item)
        } else {
            grouped.add(item)
        }
    }

    var otherScore = 0f
    for (item in grouped) {
        otherScore += item.score
    }

    if (otherScore > 0f) {
        filtered.add(
            PieChartData(
                label = "other",
                score = otherScore,
                color = Color.LightGray
            )
        )
    }

    return filtered
}
