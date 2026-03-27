package com.ivzb.urgat.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import java.math.BigDecimal

private val POSITIVE_AMOUNT_COLOR = Color(0xFF4CAF50)
private val NEGATIVE_AMOUNT_COLOR = Color(0xFFF44336)

fun financeColor(amount: BigDecimal?): Color =
    when {
        amount == null -> Color.Unspecified
        amount >= BigDecimal.ZERO -> POSITIVE_AMOUNT_COLOR
        amount < BigDecimal.ZERO -> NEGATIVE_AMOUNT_COLOR
        else -> Color.Unspecified
    }

fun annotateFinance(text: String, amount: BigDecimal?, prefix: String = "", suffix: String = ""): AnnotatedString =
    annotateFormattedString(
        formattedString = prefix + text + suffix,
        part = text,
        spanStyle = SpanStyle(fontWeight = FontWeight.Bold, color = financeColor(amount)),
    )

fun annotateFinancePnL(text: String, pnl: BigDecimal?): AnnotatedString {
    if (pnl == null || pnl == BigDecimal.ZERO) {
        return buildAnnotatedString {
            append(text)
        }
    }

    val formattedPnL = pnl.formatPnL()

    return annotateFormattedString(
        formattedString = "$text ($formattedPnL)",
        part = formattedPnL,
        spanStyle = SpanStyle(fontWeight = FontWeight.Bold, color = financeColor(pnl)),
    )
}

fun annotateExpenses(amount: BigDecimal, prefix: String = ""): AnnotatedString =
    annotateFinance((-amount).format(), -amount, prefix = prefix)

fun annotateExpensesChart(name: String, value: BigDecimal): AnnotatedString =
    annotateExpenses(value, "$name\n")

fun annotateInvestmentsChart(name: String, total: AnnotatedString?): AnnotatedString =
    buildAnnotatedString {
        append(name)

        if (total != null) {
            appendLine()

            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append(total)
            }
        }
    }

fun annotateFinance(amount: BigDecimal, prefix: String = ""): AnnotatedString =
    annotateFinance(amount.format(), amount, prefix = prefix)

fun annotateBudgetsChart(name: String, total: BigDecimal): AnnotatedString {
    val totalFormatted = total.format()
    return annotateFormattedString(
        formattedString = "$name\n$totalFormatted",
        part = totalFormatted,
        spanStyle = SpanStyle(fontWeight = FontWeight.Bold),
    )
}

fun annotateSavingsChart(name: String, total: BigDecimal): AnnotatedString =
    annotateBudgetsChart(name, total)

fun annotateBold(text: String): AnnotatedString =
    buildAnnotatedString {
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
            append(text)
        }
    }

private fun annotateFormattedString(
    formattedString: String,
    part: String,
    spanStyle: SpanStyle,
): AnnotatedString {
    return buildAnnotatedString {
        append(formattedString)

        val partStart = formattedString.indexOf(part)

        if (partStart >= 0) {
            addStyle(
                spanStyle,
                partStart,
                partStart + part.length
            )
        }
    }
}