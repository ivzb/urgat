package com.ivzb.urgat.data.investment

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import com.ivzb.urgat.util.annotateFinance
import com.ivzb.urgat.util.annotateFinancePnL
import com.ivzb.urgat.util.format
import com.ivzb.urgat.util.formatCurrency
import java.math.BigDecimal

data class InvestmentTotal(
    val currency: String,
    val costPrice: BigDecimal,
    val marketPrice: BigDecimal,
    val pnl: BigDecimal?,
) {

    companion object {

        const val SEPARATOR = "\n"

        fun List<InvestmentTotal>.formatCostPrice(withPnL: Boolean = true): AnnotatedString = format(InvestmentTotal::costPrice, withPnL)

        fun List<InvestmentTotal>.formatMarketPrice(withPnL: Boolean = true): AnnotatedString = format(InvestmentTotal::marketPrice, withPnL)

        private fun List<InvestmentTotal>.format(total: (InvestmentTotal) -> BigDecimal, withPnL: Boolean): AnnotatedString =
            buildAnnotatedString {
                forEachIndexed { index, item ->
                    append(
                        annotateFinancePnL(
                            text = "${total(item).format()} ${item.currency.formatCurrency()}",
                            pnl = if (withPnL) item.pnl else null,
                        )
                    )

                    if (index < lastIndex) {
                        append(SEPARATOR)
                    }
                }
            }

    }

}