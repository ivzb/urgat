package com.ivzb.urgat.ui.components.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.theme.UrgatTheme
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class LineChartItem(val value: Float, val timestamp: Long)

@Composable
fun LineChart(
    items: List<LineChartItem>,
    modifier: Modifier = Modifier,
    dateFormatter: DateFormat = remember { SimpleDateFormat("dd/MM", Locale.getDefault()) },
    lineColor: Color = MaterialTheme.colorScheme.primary,
    circleColor: Color = MaterialTheme.colorScheme.primary,
    horizontalPadding: Dp = dimensionResource(R.dimen.chart_horizontal_padding),
    verticalPadding: Dp = dimensionResource(R.dimen.chart_vertical_padding),
    pointRadius: Dp = dimensionResource(R.dimen.chart_point_radius),
    lineStrokeWidth: Dp = dimensionResource(R.dimen.chart_line_stroke_width),
) {
    if (items.isEmpty()) {
        return
    }

    val minValue = items.minOf { it.value }
    val maxValue = items.maxOf { it.value }
    val minTimestamp = items.minOf { it.timestamp }
    val maxTimestamp = items.maxOf { it.timestamp }

    Canvas(
        modifier = modifier
            .padding(dimensionResource(R.dimen.content_padding))
    ) {

        val chartWidth = size.width - (horizontalPadding.toPx() * 2)
        val chartHeight = size.height - (verticalPadding.toPx() * 2)
        val points = items.map { entry ->
            val x = horizontalPadding.toPx() + ((entry.timestamp - minTimestamp).toFloat() / (maxTimestamp - minTimestamp)) * chartWidth
            val y = verticalPadding.toPx() + chartHeight - ((entry.value - minValue) / (maxValue - minValue)) * chartHeight
            Offset(x, y)
        }

        if (points.size == 1) {
            drawCircle(
                color = circleColor,
                radius = pointRadius.toPx(),
                center = Offset(
                    x = horizontalPadding.toPx() + chartWidth / 2,
                    y = verticalPadding.toPx() + chartHeight / 2
                )
            )
        } else {
            val path = catmullRom(points)

            drawPath(path, color = lineColor, style = Stroke(lineStrokeWidth.toPx()))

            points.forEach { point ->
                drawCircle(
                    color = circleColor,
                    radius = 6f,
                    center = point
                )
            }
        }

        drawContext.canvas.nativeCanvas.apply {
            val paint = android.graphics.Paint().apply {
                color = android.graphics.Color.BLACK
                textSize = 30f
                isFakeBoldText = true
            }

            drawText(maxValue.toString(), 0f, verticalPadding.toPx() + 10f, paint)
            drawText(minValue.toString(), 0f, verticalPadding.toPx() + chartHeight, paint)
            drawText(dateFormatter.format(Date(minTimestamp)), horizontalPadding.toPx(), size.height, paint)
            drawText(dateFormatter.format(Date(maxTimestamp)), horizontalPadding.toPx() + chartWidth - 50f, size.height, paint)
        }
    }
}

fun catmullRom(points: List<Offset>): Path {
    val path = Path()

    if (points.size < 2) {
        return path
    }

    path.moveTo(points[0].x, points[0].y)

    for (i in 0 until points.size - 1) {
        val p0 = points.getOrNull(i - 1) ?: points[i]
        val p1 = points[i]
        val p2 = points[i + 1]
        val p3 = points.getOrNull(i + 2) ?: points[i + 1]

        val control1X = p1.x + (p2.x - p0.x) / 6f
        val control1Y = p1.y + (p2.y - p0.y) / 6f
        val control2X = p2.x - (p3.x - p1.x) / 6f
        val control2Y = p2.y - (p3.y - p1.y) / 6f

        path.cubicTo(control1X, control1Y, control2X, control2Y, p2.x, p2.y)
    }

    return path
}

@Preview
@Composable
fun WeightChartPreview() {
    UrgatTheme {
        LineChart(
            items = listOf(
                LineChartItem(70f, 1696204800000),
                LineChartItem(71f, 1696291200000),
                LineChartItem(69.5f, 1696377600000),
                LineChartItem(70.2f, 1696464000000),
                LineChartItem(71.3f, 1696550400000)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )
    }
}

@Preview
@Composable
fun WeightChartSingleItemPreview() {
    UrgatTheme {
        LineChart(
            items = listOf(
                LineChartItem(70f, 1696204800000),
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )
    }
}

@Preview
@Composable
fun WeightChartSingleEmptyPreview() {
    UrgatTheme {
        LineChart(
            items = listOf(),
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )
    }
}
