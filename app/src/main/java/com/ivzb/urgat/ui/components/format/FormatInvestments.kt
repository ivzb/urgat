package com.ivzb.urgat.ui.components.format

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import com.ivzb.urgat.R
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.util.annotateFormattedString

@Composable
fun formatInvestmentsChart(name: String, total: String): AnnotatedString {
    val totalFormatted = total.split(Investment.SEPARATOR).joinToString("\n")
    val textFormatted = stringResource(R.string.chart_text_format, name, totalFormatted).trim()

    return annotateFormattedString(
        formattedString = textFormatted,
        part = totalFormatted,
        spanStyle = SpanStyle(
            fontWeight = FontWeight.Bold
        ),
    )
}