package com.ivzb.urgat.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ivzb.urgat.ui.theme.UrgatTheme

/**
 * A tiny line chart of [prices], with [baseline] as a dashed line behind it - the in-app
 * counterpart of the watchlist widget's sparkline.
 *
 * Whatever has fewer than two prices still takes up the space, so rows stay lined up.
 */
@Composable
fun Sparkline(
    prices: List<Double>,
    baseline: Double?,
    lineColor: Color,
    modifier: Modifier = Modifier,
    baselineColor: Color = MaterialTheme.colorScheme.outline,
    strokeWidth: Dp = 1.5.dp,
) {
    Canvas(modifier = modifier) {
        val strokeWidthPx = strokeWidth.toPx()

        val points = sparklinePoints(
            prices = downsample(prices, maxPoints = (size.width / strokeWidthPx).toInt()),
            baseline = baseline,
            width = size.width,
            height = size.height,
            inset = strokeWidthPx,
        ) ?: return@Canvas

        points.baselineY?.let { y ->
            drawLine(
                color = baselineColor,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = strokeWidthPx / 2,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(strokeWidthPx * 2, strokeWidthPx * 2)),
            )
        }

        val path = Path().apply {
            moveTo(points.xs.first(), points.ys.first())

            for (index in 1 until points.xs.size) {
                lineTo(points.xs[index], points.ys[index])
            }
        }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(
                width = strokeWidthPx,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round,
            ),
        )
    }
}

@Preview
@DarkModePreview
@Composable
private fun SparklinePositivePreview() {
    UrgatTheme {
        Surface {
            Sparkline(
                prices = listOf(100.0, 101.5, 100.8, 102.3, 103.1, 102.6, 104.0),
                baseline = 101.0,
                lineColor = Color(0xFF4CAF50),
                modifier = Modifier.size(48.dp, 24.dp),
            )
        }
    }
}

@Preview
@DarkModePreview
@Composable
private fun SparklineNegativePreview() {
    UrgatTheme {
        Surface {
            Sparkline(
                prices = listOf(104.0, 103.2, 103.6, 102.1, 101.4, 101.9, 100.5),
                baseline = 103.0,
                lineColor = Color(0xFFF44336),
                modifier = Modifier.size(48.dp, 24.dp),
            )
        }
    }
}
