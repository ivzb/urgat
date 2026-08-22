package com.ivzb.urgat.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.em
import com.ivzb.urgat.data.expense.ExpenseTotal
import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionTotal
import com.ivzb.urgat.data.investment.InvestmentTotal
import com.ivzb.urgat.data.saving.SavingTotal
import com.ivzb.urgat.data.trade.TradeTotal
import com.ivzb.urgat.data.watchlist.WatchlistAsset
import java.math.BigDecimal

private val POSITIVE_AMOUNT_COLOR = Color(0xFF4CAF50)
private val NEGATIVE_AMOUNT_COLOR = Color(0xFFF44336)

private val CHART_AVERAGE_EXPENSES_FONT_SIZE = 0.75.em

fun financeColor(amount: BigDecimal?): Color =
    when {
        amount == null || amount == BigDecimal.ZERO -> Color.Unspecified
        amount > BigDecimal.ZERO -> POSITIVE_AMOUNT_COLOR
        amount < BigDecimal.ZERO -> NEGATIVE_AMOUNT_COLOR
        else -> Color.Unspecified
    }

fun annotateFinance(text: String, amount: BigDecimal?, prefix: String = "", suffix: String = ""): AnnotatedString =
    annotateFormattedString(
        formattedString = prefix + text + suffix,
        part = text,
        spanStyle = SpanStyle(fontWeight = FontWeight.Bold, color = financeColor(amount)),
    )

fun annotateFinance(amount: BigDecimal, currency: String, prefix: String = "", pricePrecision: Int? = null): AnnotatedString =
    annotateFinance("${amount.format(digits = pricePrecision)} ${currency.formatCurrency()}", amount, prefix = prefix)

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

fun annotateWatchlistAsset(asset: WatchlistAsset): AnnotatedString {
    val text = asset.formatPriceChange()

    return annotateFormattedString(
        formattedString = text,
        part = text,
        spanStyle = SpanStyle(fontWeight = FontWeight.Bold, color = financeColor(asset.priceChange)),
    )
}

fun annotateExpenses(amount: BigDecimal, currency: String, prefix: String = ""): AnnotatedString =
    annotateFinance(-amount, currency, prefix = prefix)

fun annotateExpensesTotals(totals: List<ExpenseTotal>, prefix: String = ""): AnnotatedString =
    buildAnnotatedString {
        totals.forEachIndexed { index, total ->
            append(
                annotateExpenses(
                    amount = total.amount,
                    currency = total.currency,
                    prefix = prefix,
                )
            )

            if (index < totals.lastIndex) {
                appendLine()
            }
        }
    }

fun annotateCostPrice(totals: List<InvestmentTotal>, withPnL: Boolean = true): AnnotatedString = annotatePrice(totals, InvestmentTotal::costPrice, withPnL)

fun annotateMarketPrice(totals: List<InvestmentTotal>, withPnL: Boolean = true): AnnotatedString = annotatePrice(totals, InvestmentTotal::marketPrice, withPnL)

private fun annotatePrice(totals: List<InvestmentTotal>, price: (InvestmentTotal) -> BigDecimal, withPnL: Boolean): AnnotatedString =
    buildAnnotatedString {
        totals.forEachIndexed { index, total ->
            append(
                annotateFinancePnL(
                    text = "${price(total).format()} ${total.currency.formatCurrency()}",
                    pnl = if (withPnL) total.pnl else null,
                )
            )

            if (index < totals.lastIndex) {
                appendLine()
            }
        }
    }

fun annotateChart(name: String, total: AnnotatedString?, average: AnnotatedString? = null): AnnotatedString =
    buildAnnotatedString {
        append(name)

        if (total != null) {
            appendLine()

            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append(total)
            }
        }

        if (average != null && average.isNotEmpty()) {
            appendLine()

            withStyle(SpanStyle(fontSize = CHART_AVERAGE_EXPENSES_FONT_SIZE)) {
                append(average)
            }
        }
    }

fun annotateSavings(amount: BigDecimal, currency: String, prefix: String = ""): AnnotatedString =
    annotateFinance("${amount.format()} ${currency.formatCurrency()}", amount, prefix = prefix)

fun annotateSavingsTotals(totals: List<SavingTotal>): AnnotatedString =
    buildAnnotatedString {
        totals.forEachIndexed { index, total ->
            append(
                annotateSavings(
                    amount = total.amount,
                    currency = total.currency,
                )
            )

            if (index < totals.lastIndex) {
                appendLine()
            }
        }
    }

fun annotateTradesTotals(totals: List<TradeTotal>): AnnotatedString =
    buildAnnotatedString {
        totals.forEachIndexed { index, total ->
            append(
                annotateFinance(
                    amount = total.price,
                    currency = total.currency,
                )
            )

            if (index < totals.lastIndex) {
                appendLine()
            }
        }
    }

fun annotateFuelConsumptionTotals(totals: List<FuelConsumptionTotal>): AnnotatedString =
    buildAnnotatedString {
        totals.forEachIndexed { index, total ->
            append(AnnotatedString("${total.amount.format()}L ${total.type}"))

            if (index < totals.lastIndex) {
                appendLine()
            }
        }
    }


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