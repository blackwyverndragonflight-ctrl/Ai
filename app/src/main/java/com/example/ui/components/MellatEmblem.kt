package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.MellatGold
import com.example.ui.theme.MellatRedPrimary
import kotlin.math.cos
import kotlin.math.sin

/**
 * Official Bank Mellat Logo Emblem based on the uploaded brand asset.
 * Features the signature 3-blade crimson hexagon knot, interlocking white pathways,
 * and circular gold core.
 */
@Composable
fun MellatEmblem(
    modifier: Modifier = Modifier,
    size: Dp = 38.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val cx = w / 2f
        val cy = h / 2f
        val r = w * 0.46f

        // Draw the 3 interlocking crimson lobes of Bank Mellat logo
        for (blade in 0 until 3) {
            rotate(degrees = blade * 120f, pivot = Offset(cx, cy)) {
                val lobePath = Path().apply {
                    // Start near center
                    moveTo(cx, cy - r * 0.38f)
                    // Outer top vertex
                    lineTo(cx + r * 0.35f, cy - r * 0.95f)
                    // Top-right corner
                    lineTo(cx + r * 0.92f, cy - r * 0.62f)
                    // Right wing
                    lineTo(cx + r * 0.78f, cy - r * 0.05f)
                    // Inward loop
                    lineTo(cx + r * 0.38f, cy - r * 0.05f)
                    // Back toward center
                    lineTo(cx + r * 0.18f, cy - r * 0.32f)
                    close()
                }
                drawPath(
                    path = lobePath,
                    color = MellatRedPrimary
                )
            }
        }

        // Draw the white separating spiral channels
        for (blade in 0 until 3) {
            rotate(degrees = blade * 120f, pivot = Offset(cx, cy)) {
                val channelPath = Path().apply {
                    moveTo(cx, cy - r * 0.36f)
                    lineTo(cx + r * 0.35f, cy - r * 0.96f)
                    lineTo(cx + r * 0.92f, cy - r * 0.62f)
                }
                drawPath(
                    path = channelPath,
                    color = Color.White,
                    style = Stroke(
                        width = w * 0.065f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }

        // Central white disc separator
        drawCircle(
            color = Color.White,
            radius = r * 0.38f,
            center = Offset(cx, cy)
        )

        // Central vibrant gold core
        drawCircle(
            color = MellatGold,
            radius = r * 0.32f,
            center = Offset(cx, cy)
        )
    }
}
