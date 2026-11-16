package com.ivzb.urgat.ui.widget

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import androidx.core.graphics.createBitmap

/**
 * Where [prices] land in a [width] by [height] box, oldest on the left.
 *
 * The [baseline] is part of the range, so the line always reads against it - a day spent
 * entirely above the previous close stays visibly above it rather than filling the whole box.
 * Both ends are inset by [inset], so a thick stroke at the very top or bottom is not clipped.
 *
 * @return `null` when there is no line to draw, which takes at least two prices
 */
internal fun sparklinePoints(
    prices: List<Double>,
    baseline: Double?,
    width: Float,
    height: Float,
    inset: Float = 0f,
): SparklinePoints? {
    if (prices.size < 2) {
        return null
    }

    val values = if (baseline != null) prices + baseline else prices
    val min = values.min()
    val max = values.max()
    val range = max - min

    val drawableHeight = height - 2 * inset
    val step = width / (prices.size - 1)

    // a perfectly flat day has no range to scale by, so it goes through the middle instead
    fun y(price: Double): Float =
        if (range == 0.0) {
            height / 2
        } else {
            inset + ((max - price) / range * drawableHeight).toFloat()
        }

    return SparklinePoints(
        xs = List(prices.size) { index -> index * step },
        ys = prices.map(::y),
        baselineY = baseline?.let(::y),
    )
}

internal data class SparklinePoints(
    val xs: List<Float>,
    val ys: List<Float>,
    val baselineY: Float?,
)

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

/**
 * Averages [prices] down to at most [maxPoints], keeping the very first and last price exact.
 *
 * A symbol trading around the clock carries several times the points of one trading only during
 * market hours. Squeezed into the same width, those extra points zig-zag closer together than the
 * line is thick and smear into a visibly bolder line than their neighbours'.
 */
internal fun downsample(prices: List<Double>, maxPoints: Int): List<Double> {
    if (prices.size <= maxPoints || maxPoints < 2) {
        return prices
    }

    val averaged = List(maxPoints) { bucket ->
        val start = bucket * prices.size / maxPoints
        val end = (bucket + 1) * prices.size / maxPoints

        prices.subList(start, end).average()
    }

    // the line has to start and end where the day actually did, whatever the averaging says
    return listOf(prices.first()) + averaged.subList(1, maxPoints - 1) + prices.last()
}
