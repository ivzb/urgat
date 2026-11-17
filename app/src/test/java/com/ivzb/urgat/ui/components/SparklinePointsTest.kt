package com.ivzb.urgat.ui.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SparklinePointsTest {

    @Test
    fun `sparklinePoints needs at least two prices`() {
        assertNull(sparklinePoints(emptyList(), baseline = 1.0, width = 40f, height = 20f))
        assertNull(sparklinePoints(listOf(1.0), baseline = 1.0, width = 40f, height = 20f))
    }

    @Test
    fun `sparklinePoints spreads prices across the width and puts higher prices higher`() {
        val points = requireNotNull(
            sparklinePoints(listOf(10.0, 20.0, 15.0), baseline = null, width = 40f, height = 20f)
        )

        assertEquals(listOf(0f, 20f, 40f), points.xs)
        assertEquals(listOf(20f, 0f, 10f), points.ys)
        assertNull(points.baselineY)
    }

    @Test
    fun `sparklinePoints keeps a baseline outside the prices within the box`() {
        val points = requireNotNull(
            sparklinePoints(listOf(15.0, 20.0), baseline = 10.0, width = 40f, height = 20f)
        )

        assertEquals(listOf(10f, 0f), points.ys)
        assertEquals(20f, points.baselineY)
    }

    @Test
    fun `sparklinePoints insets the line from the edges`() {
        val points = requireNotNull(
            sparklinePoints(listOf(10.0, 20.0), baseline = null, width = 40f, height = 20f, inset = 2f)
        )

        assertEquals(listOf(18f, 2f), points.ys)
    }

    @Test
    fun `sparklinePoints draws a flat day through the middle`() {
        val points = requireNotNull(
            sparklinePoints(listOf(10.0, 10.0), baseline = 10.0, width = 40f, height = 20f)
        )

        assertEquals(listOf(10f, 10f), points.ys)
        assertEquals(10f, points.baselineY)
    }

    @Test
    fun `downsample leaves prices which already fit alone`() {
        val prices = listOf(1.0, 2.0, 3.0)

        assertEquals(prices, downsample(prices, maxPoints = 3))
    }

    @Test
    fun `downsample averages the rest down while keeping the first and last price exact`() {
        val prices = listOf(1.0, 3.0, 10.0, 20.0, 30.0, 50.0, 6.0, 8.0)

        assertEquals(listOf(1.0, 15.0, 40.0, 8.0), downsample(prices, maxPoints = 4))
    }
}
