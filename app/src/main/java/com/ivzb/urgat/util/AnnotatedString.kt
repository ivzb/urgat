package com.ivzb.urgat.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import com.ivzb.urgat.data.investment.Investment
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

fun expenseColor(amount: BigDecimal): Color =
    financeColor(-amount)

fun annotateExpense(amount: BigDecimal, prefix: String = ""): AnnotatedString =
    annotateFinance((-amount).format(), -amount, prefix = prefix)

fun annotateExpenseChart(name: String, value: BigDecimal): AnnotatedString =
    annotateExpense(value, "$name\n")

fun annotateInvestmentChart(name: String, total: String): AnnotatedString {
    val totalFormatted = total.split(Investment.SEPARATOR).joinToString("\n")
    return annotateFormattedString(
        formattedString = "$name\n$totalFormatted",
        part = totalFormatted,
        spanStyle = SpanStyle(fontWeight = FontWeight.Bold),
    )
}

fun annotateBudgetChart(name: String, total: BigDecimal): AnnotatedString {
    val totalFormatted = total.format()
    return annotateFormattedString(
        formattedString = "$name\n$totalFormatted",
        part = totalFormatted,
        spanStyle = SpanStyle(fontWeight = FontWeight.Bold),
    )
}

fun annotateSavingsChart(name: String, total: BigDecimal): AnnotatedString =
    annotateBudgetChart(name, total)

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