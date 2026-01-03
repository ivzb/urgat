package com.ivzb.urgat.ui.components.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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
import kotlin.math.absoluteValue

@Composable
fun PieChart(
    data: List<Float>,
    text: AnnotatedString,
    textFontWeight: FontWeight? = null,
    textStyle: TextStyle,
    modifier: Modifier = Modifier,
) {
    val total = data.map { it.absoluteValue }.sum()
    val strokeWidthDp = dimensionResource(R.dimen.pie_chart_stroke_width)
    val strokeWidth = with(LocalDensity.current) { strokeWidthDp.toPx() }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f),
    ) {

        Canvas(modifier = Modifier.fillMaxSize()) {
            val halfStroke = strokeWidth / 2
            val diameter = size.minDimension - strokeWidth

            var startAngle = -90f

            data.forEachIndexed { index, value ->
                val sweepAngle = (value.absoluteValue / total) * 360f

                drawArc(
                    color = dataColor(index),
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(halfStroke, halfStroke),
                    size = Size(diameter, diameter),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                )

                startAngle += sweepAngle
            }
        }

        Text(
            text = text,
            style = textStyle,
            fontWeight = textFontWeight,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview
@DarkModePreview
@Composable
private fun PieChartPreview() {
    UrgatTheme {
        PieChart(
            data = listOf(
                45f,
                25f,
                20f,
                10f,
            ),
            text = buildAnnotatedString {
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