package com.ivzb.urgat.ui.components.format

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.theme.expenseColor
import com.ivzb.urgat.util.annotateFormattedString
import com.ivzb.urgat.util.format
import java.math.BigDecimal

@Composable
fun formatExpensesChart(name: String, value: BigDecimal): AnnotatedString {
    val valueFormatted = (value * -1.toBigDecimal()).format()
    val textFormatted =
        stringResource(R.string.chart_text_format, name, valueFormatted).trim()

    return annotateFormattedString(
        formattedString = textFormatted,
        part = valueFormatted,
        spanStyle = SpanStyle(
            color = expenseColor(value),
            fontWeight = FontWeight.Bold
        ),
    )
}