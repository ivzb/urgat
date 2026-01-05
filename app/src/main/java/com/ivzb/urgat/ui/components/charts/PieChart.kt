package com.ivzb.urgat.ui.components.charts

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.DarkModePreview
import com.ivzb.urgat.ui.theme.UrgatTheme
import com.ivzb.urgat.ui.theme.dataColor
import java.lang.Math.toDegrees
import kotlin.math.absoluteValue
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.min

@Composable
fun PieChart(
    data: List<PieChartItem>,

    defaultText: AnnotatedString,

    textFontWeight: FontWeight? = null,
    textStyle: TextStyle,

    modifier: Modifier = Modifier,
) {
    val total = data.map { it.value.absoluteValue }.sum()
    val strokeWidthDp = dimensionResource(R.dimen.pie_chart_stroke_width)
    val strokeWidth = with(LocalDensity.current) { strokeWidthDp.toPx() }

    val sliceAngles = remember(data) { mutableListOf<PieChartSliceAngle>() }
    val selectedIndex = remember { mutableStateOf<Int?>(null) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f),
    ) {

        val offsets = data.mapIndexed { index, _ ->
            val isSelected = selectedIndex.value == index && data.size > 1
            animateFloatAsState(targetValue = if (isSelected) 50f else 0f).value
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { tapOffset ->
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val dx = tapOffset.x - center.x
                        val dy = tapOffset.y - center.y

                        val distance = hypot(dx, dy)
                        val outerRadius = min(size.width, size.height) / 2f
                        val innerRadius = outerRadius - strokeWidth

                        if (distance !in innerRadius..outerRadius) {
                            return@detectTapGestures
                        }

                        var angle = toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()

                        if (angle < 0f) {
                            angle += 360f
                        }

                        val adjustedAngle = (angle + 90f) % 360f

                        val tappedSlice = sliceAngles.firstOrNull {
                            adjustedAngle in it.start..(it.start + it.sweep)
                        }

                        tappedSlice?.let {
                            selectedIndex.value = if (selectedIndex.value != it.index) it.index else null
                        }
                    }
                }
        ) {

            sliceAngles.clear()

            var startAngle = -90f

            data.forEachIndexed { index, item ->
                val sweepAngle = (item.value.absoluteValue / total) * 360f
                sliceAngles += PieChartSliceAngle(
                    index = index,
                    start = startAngle + 90f,
                    sweep = sweepAngle
                )

                val halfStroke = strokeWidth / 2
                val diameter = size.minDimension - strokeWidth
                val offset = offsets[index]

                val rad = Math.toRadians((startAngle + sweepAngle / 2).toDouble())
                val dx = (offset * kotlin.math.cos(rad)).toFloat()
                val dy = (offset * kotlin.math.sin(rad)).toFloat()

                drawArc(
                    color = dataColor(index),
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(halfStroke, halfStroke) + Offset(dx, dy),
                    size = Size(diameter, diameter),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                )

                startAngle += sweepAngle
            }
        }

        val centerText = remember(selectedIndex.value) {
            val selectedIndex = selectedIndex.value

            if (selectedIndex == null) {
                defaultText
            } else {
                data[selectedIndex].selectedText
            }
        }

        Text(
            text = centerText,
            style = textStyle,
            fontWeight = textFontWeight,
            textAlign = TextAlign.Center,
        )
    }
}

data class PieChartItem(
    val value: Float,
    val selectedText: AnnotatedString,
)

private data class PieChartSliceAngle(
    val index: Int,
    val start: Float,
    val sweep: Float
)

@Preview
@DarkModePreview
@Composable
private fun PieChartPreview() {
    UrgatTheme {
        PieChart(
            data = listOf(
                PieChartItem(45f, AnnotatedString("45f")),
                PieChartItem(25f, AnnotatedString("25f")),
                PieChartItem(20f, AnnotatedString("20f")),
                PieChartItem(10f, AnnotatedString("10f")),
            ),
            defaultText = buildAnnotatedString {
                append("Expenses:\n")

                withStyle(SpanStyle(color = Color.Red)) {
                    append("222")
                }
            },
            textStyle = TextStyle.Default,
            modifier = Modifier,
        )
    }
}