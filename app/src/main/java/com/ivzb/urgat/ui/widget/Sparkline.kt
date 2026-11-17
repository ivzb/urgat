package com.ivzb.urgat.ui.widget

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import androidx.core.graphics.createBitmap
import com.ivzb.urgat.ui.components.downsample
import com.ivzb.urgat.ui.components.sparklinePoints

/**
 * A tiny line chart of [prices], with [baseline] as a dashed line behind it.
 *
 * @return `null` when there is nothing worth drawing
 */
internal fun sparklineBitmap(
    prices: List<Double>,
    baseline: Double?,
    widthPx: Int,
    heightPx: Int,
    strokeWidthPx: Float,
    lineColor: Int,
    baselineColor: Int,
): Bitmap? {
    if (widthPx <= 0 || heightPx <= 0) {
        return null
    }

    val points = sparklinePoints(
        prices = downsample(prices, maxPoints = (widthPx / strokeWidthPx).toInt()),
        baseline = baseline,
        width = widthPx.toFloat(),
        height = heightPx.toFloat(),
        inset = strokeWidthPx,
    ) ?: return null

    val bitmap = createBitmap(widthPx, heightPx)
    val canvas = Canvas(bitmap)

    points.baselineY?.let { y ->
        canvas.drawLine(
            0f,
            y,
            widthPx.toFloat(),
            y,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                color = baselineColor
                strokeWidth = strokeWidthPx / 2
                pathEffect = DashPathEffect(floatArrayOf(strokeWidthPx * 2, strokeWidthPx * 2), 0f)
            },
        )
    }

    val path = Path().apply {
        moveTo(points.xs.first(), points.ys.first())

        for (index in 1 until points.xs.size) {
            lineTo(points.xs[index], points.ys[index])
        }
    }

    canvas.drawPath(
        path,
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            color = lineColor
            strokeWidth = strokeWidthPx
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        },
    )

    return bitmap
}
