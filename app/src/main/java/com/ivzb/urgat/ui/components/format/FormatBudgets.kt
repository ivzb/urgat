package com.ivzb.urgat.ui.components.format

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import com.ivzb.urgat.R
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.util.annotateFormattedString
import com.ivzb.urgat.util.format
import java.math.BigDecimal

@Composable
fun formatBudgetsChart(name: String, total: BigDecimal): AnnotatedString {
    val totalFormatted = total.format()
    val textFormatted = stringResource(R.string.chart_text_format, name, totalFormatted).trim()

    return annotateFormattedString(
        formattedString = textFormatted,
        part = totalFormatted,
        spanStyle = SpanStyle(
            fontWeight = FontWeight.Bold
        ),
    )
}