package com.ivzb.urgat.domain.common

import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentAsset
import com.ivzb.urgat.data.investment.InvestmentAverage
import com.ivzb.urgat.data.investment.InvestmentCategory
import com.ivzb.urgat.data.investment.InvestmentNetWorth
import com.ivzb.urgat.data.investment.InvestmentTotal
import com.ivzb.urgat.util.calculatePnL
import com.ivzb.urgat.util.timestamp
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Test

class GroupInvestmentsTest {

    @Test
    fun groupInvestmentAssets() = runBlocking {
        val investments = assetInvestments()

        val expected = listOf(
            aaplUsdAsset(investments),
            brkbUsdAsset(investments),
            googlUsdAsset(investments),
            googlEurAsset(investments),
            goldUsdAsset(investments),
            goldEurAsset(investments),
            nvdaUsdAsset(investments),
            snp500UsdAsset(investments),
            snp500EurAsset(investments),
        )

        val actual = GroupInvestments.groupInvestmentAssets(investments, randomTestUUID)

        assertEquals(expected, actual)
    }

    private fun assetInvestments() = listOf(
        Investment("1", "GOOGL", BigDecimal("2.1"), BigDecimal("307.16"), BigDecimal("307.16"), "stocks", "USD", timestamp(2025, 12, 20), false, timestamp(2025, 12, 20)),
        Investment("2", "GOOGL", BigDecimal("2.1"), BigDecimal("307.16"), BigDecimal("307.16"), "stocks", "USD", timestamp(2025, 12, 20), false, timestamp(2025, 12, 20)),
        Investment("3", "AAPL", BigDecimal("2.3"), BigDecimal("273.67"), BigDecimal("273.67"), "stocks", "USD", timestamp(2025, 12, 19), false, timestamp(2025, 12, 19)),
        Investment("4", "AAPL", BigDecimal("2.3"), BigDecimal("273.67"), BigDecimal("273.67"), "stocks", "USD", timestamp(2025, 12, 19), false, timestamp(2025, 12, 19)),
        Investment("5", "NVDA", BigDecimal("2.5"), BigDecimal("180.99"), BigDecimal("180.99"), "stocks", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
        Investment("6", "NVDA", BigDecimal("2.5"), BigDecimal("180.99"), BigDecimal("180.99"), "stocks", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
        Investment("7", "SNP 500", BigDecimal("0.3"), BigDecimal("6834.5"), BigDecimal("6834.5"), "etf", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
        Investment("8", "SNP 500", BigDecimal("0.3"), BigDecimal("6834.5"), BigDecimal("6834.5"), "etf", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
        Investment("9", "BRK.B", BigDecimal("1.7"), BigDecimal("494.53"), BigDecimal("494.53"), "etf", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
        Investment("10", "BRK.B", BigDecimal("1.7"), BigDecimal("494.53"), BigDecimal("494.53"), "etf", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
        Investment("11", "Gold", BigDecimal("1"), BigDecimal("4338.55"), BigDecimal("4338.55"), "gold", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
        Investment("12", "Gold", BigDecimal("1"), BigDecimal("4338.55"), BigDecimal("4338.55"), "gold", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
        Investment("13", "GOOGL", BigDecimal("2.1"), BigDecimal("262.25"), BigDecimal("262.25"), "stocks", "EUR", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
        Investment("14", "GOOGL", BigDecimal("2.1"), BigDecimal("262.25"), BigDecimal("262.25"), "stocks", "EUR", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
        Investment("15", "SNP 500", BigDecimal("0.3"), BigDecimal("5835.3"), BigDecimal("5835.3"), "etf", "EUR", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
        Investment("16", "SNP 500", BigDecimal("0.3"), BigDecimal("5835.3"), BigDecimal("5835.3"), "etf", "EUR", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
        Investment("17", "Gold", BigDecimal("1"), BigDecimal("3704.25"), BigDecimal("3704.25"), "gold", "EUR", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
        Investment("18", "Gold", BigDecimal("1"), BigDecimal("3704.25"), BigDecimal("3704.25"), "gold", "EUR", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
    )

    private fun aaplUsdAsset(investments: List<Investment>) =
        InvestmentAsset(
            id = TEST_UUID,
            name = "AAPL",
            category = "stocks",
            shares = BigDecimal("4.6"),
            costPrice = BigDecimal("1258.882"),
            marketPrice = BigDecimal("1258.882"),
            currency = "USD",
            totals = listOf(
                InvestmentTotal("USD", BigDecimal("1258.882"), BigDecimal("1258.882"), calculatePnL(BigDecimal("1258.882"), BigDecimal("1258.882"))),
            ),
            investments = listOf(investments[2], investments[3]),
        )

    private fun brkbUsdAsset(investments: List<Investment>) =
        InvestmentAsset(
            id = TEST_UUID,
            name = "BRK.B",
            category = "etf",
            shares = BigDecimal("3.4"),
            costPrice = BigDecimal("1681.402"),
            marketPrice = BigDecimal("1681.402"),
            currency = "USD",
            totals = listOf(
                InvestmentTotal("USD", BigDecimal("1681.402"), BigDecimal("1681.402"), calculatePnL(BigDecimal("1681.402"), BigDecimal("1681.402"))),
            ),
            investments = listOf(investments[8], investments[9]),
        )

    private fun googlUsdAsset(investments: List<Investment>) =
        InvestmentAsset(
            id = TEST_UUID,
            name = "GOOGL",
            category = "stocks",
            shares = BigDecimal("4.2"),
            costPrice = BigDecimal("1290.072"),
            marketPrice = BigDecimal("1290.072"),
            currency = "USD",
            totals = listOf(
                InvestmentTotal("USD", BigDecimal("1290.072"), BigDecimal("1290.072"), calculatePnL(BigDecimal("1290.072"), BigDecimal("1290.072"))),
            ),
            investments = listOf(investments[0], investments[1]),
        )

    private fun googlEurAsset(investments: List<Investment>) =
        InvestmentAsset(
            id = TEST_UUID,
            name = "GOOGL",
            category = "stocks",
            shares = BigDecimal("4.2"),
            costPrice = BigDecimal("1101.450"),
            marketPrice = BigDecimal("1101.450"),
            currency = "EUR",
            totals = listOf(
                InvestmentTotal("EUR", BigDecimal("1101.450"), BigDecimal("1101.450"), calculatePnL(BigDecimal("1101.450"), BigDecimal("1101.450"))),
            ),
            investments = listOf(investments[12], investments[13]),
        )

    private fun goldUsdAsset(investments: List<Investment>) =
        InvestmentAsset(
            id = TEST_UUID,
            name = "Gold",
            category = "gold",
            shares = BigDecimal("2"),
            costPrice = BigDecimal("8677.10"),
            marketPrice = BigDecimal("8677.10"),
            currency = "USD",
            totals = listOf(
                InvestmentTotal("USD", BigDecimal("8677.10"), BigDecimal("8677.10"), calculatePnL(BigDecimal("8677.10"), BigDecimal("8677.10"))),
            ),
            investments = listOf(investments[10], investments[11]),
        )

    private fun goldEurAsset(investments: List<Investment>) =
        InvestmentAsset(
            id = TEST_UUID,
            name = "Gold",
            category = "gold",
            shares = BigDecimal("2"),
            costPrice = BigDecimal("7408.50"),
            marketPrice = BigDecimal("7408.50"),
            currency = "EUR",
            totals = listOf(
                InvestmentTotal("EUR", BigDecimal("7408.50"), BigDecimal("7408.50"), calculatePnL(BigDecimal("7408.50"), BigDecimal("7408.50"))),
            ),
            investments = listOf(investments[16], investments[17]),
        )

    private fun nvdaUsdAsset(investments: List<Investment>) =
        InvestmentAsset(
            id = TEST_UUID,
            name = "NVDA",
            category = "stocks",
            shares = BigDecimal("5.0"),
            costPrice = BigDecimal("904.950"),
            marketPrice = BigDecimal("904.950"),
            currency = "USD",
            totals = listOf(
                InvestmentTotal("USD", BigDecimal("904.950"), BigDecimal("904.950"), calculatePnL(BigDecimal("904.950"), BigDecimal("904.950"))),
            ),
            investments = listOf(investments[4], investments[5]),
        )

    private fun snp500UsdAsset(investments: List<Investment>) =
        InvestmentAsset(
            id = TEST_UUID,
            name = "SNP 500",
            category = "etf",
            shares = BigDecimal("0.6"),
            costPrice = BigDecimal("4100.70"),
            marketPrice = BigDecimal("4100.70"),
            currency = "USD",
            totals = listOf(
                InvestmentTotal("USD", BigDecimal("4100.70"), BigDecimal("4100.70"), calculatePnL(BigDecimal("4100.70"), BigDecimal("4100.70"))),
            ),
            investments = listOf(investments[6], investments[7]),
        )

    private fun snp500EurAsset(investments: List<Investment>) =
        InvestmentAsset(
            id = TEST_UUID,
            name = "SNP 500",
            category = "etf",
            shares = BigDecimal("0.6"),
            costPrice = BigDecimal("3501.18"),
            marketPrice = BigDecimal("3501.18"),
            currency = "EUR",
            totals = listOf(
                InvestmentTotal("EUR", BigDecimal("3501.18"), BigDecimal("3501.18"), calculatePnL(BigDecimal("3501.18"), BigDecimal("3501.18"))),
            ),
            investments = listOf(investments[14], investments[15]),
        )

    @Test
    fun groupInvestmentCategories() {
        val investments = listOf(
            Investment("1", "GOOGL", BigDecimal("2.1"), BigDecimal("307.16"), BigDecimal("307.16"), "stocks", "USD", timestamp(2025, 12, 20), false, timestamp(2025, 12, 20)),
            Investment("2", "AAPL", BigDecimal("2.3"), BigDecimal("273.67"), BigDecimal("273.67"), "stocks", "USD", timestamp(2025, 12, 19), false, timestamp(2025, 12, 19)),
            Investment("3", "NVDA", BigDecimal("2.5"), BigDecimal("180.99"), BigDecimal("180.99"), "stocks", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("4", "SNP 500", BigDecimal("0.3"), BigDecimal("6834.5"), BigDecimal("6834.5"), "etf", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("5", "BRK.B", BigDecimal("1.7"), BigDecimal("494.53"), BigDecimal("494.53"), "etf", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("6", "Gold", BigDecimal("1"), BigDecimal("4338.55"), BigDecimal("4338.55"), "gold", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("7", "GOOGL", BigDecimal("2.1"), BigDecimal("262.25"), BigDecimal("262.25"), "stocks", "EUR", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("8", "SNP 500", BigDecimal("0.3"), BigDecimal("5835.3"), BigDecimal("5835.3"), "etf", "EUR", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("9", "Gold", BigDecimal("1"), BigDecimal("3704.25"), BigDecimal("3704.25"), "gold", "EUR", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
        )

        val expected = listOf(
            InvestmentCategory(
                id = TEST_UUID,
                name = "etf",
                totals = listOf(
                    InvestmentTotal("EUR", BigDecimal("1750.59"), BigDecimal("1750.59"), calculatePnL(BigDecimal("1750.59"), BigDecimal("1750.59"))),
                    InvestmentTotal("USD", BigDecimal("2891.051"), BigDecimal("2891.051"), calculatePnL(BigDecimal("2891.051"), BigDecimal("2891.051"))),
                ),
                investments = listOf(investments[3], investments[4], investments[7]),
            ),
            InvestmentCategory(
                id = TEST_UUID,
                name = "gold",
                totals = listOf(
                    InvestmentTotal("EUR", BigDecimal("3704.25"), BigDecimal("3704.25"), calculatePnL(BigDecimal("3704.25"), BigDecimal("3704.25"))),
                    InvestmentTotal("USD", BigDecimal("4338.55"), BigDecimal("4338.55"), calculatePnL(BigDecimal("4338.55"), BigDecimal("4338.55"))),
                ),
                investments = listOf(investments[5], investments[8]),
            ),
            InvestmentCategory(
                id = TEST_UUID,
                name = "stocks",
                totals = listOf(
                    InvestmentTotal("EUR", BigDecimal("550.725"), BigDecimal("550.725"), calculatePnL(BigDecimal("550.725"), BigDecimal("550.725"))),
                    InvestmentTotal("USD", BigDecimal("1726.952"), BigDecimal("1726.952"), calculatePnL(BigDecimal("1726.952"), BigDecimal("1726.952"))),
                ),
                investments = listOf(investments[0], investments[1], investments[2], investments[6]),
            ),
        )

        val actual = GroupInvestments.groupInvestmentCategories(investments, randomTestUUID)

        assertEquals(expected, actual)
    }

    @Test
    fun groupInvestmentNetWorth() {
        val investments = listOf(
            Investment("1", "GOOGL", BigDecimal("2.1"), BigDecimal("307.16"), BigDecimal("307.16"), "stocks", "USD", timestamp(2025, 12, 20), false, timestamp(2025, 12, 20)),
            Investment("2", "AAPL", BigDecimal("2.3"), BigDecimal("273.67"), BigDecimal("273.67"), "stocks", "USD", timestamp(2025, 12, 19), false, timestamp(2025, 12, 19)),
            Investment("3", "NVDA", BigDecimal("2.5"), BigDecimal("180.99"), BigDecimal("180.99"), "stocks", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("4", "SNP 500", BigDecimal("0.3"), BigDecimal("6834.5"), BigDecimal("6834.5"), "etf", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("5", "BRK.B", BigDecimal("1.7"), BigDecimal("494.53"), BigDecimal("494.53"), "etf", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("6", "Gold", BigDecimal("1"), BigDecimal("4338.55"), BigDecimal("4338.55"), "gold", "USD", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("7", "GOOGL", BigDecimal("2.1"), BigDecimal("262.25"), BigDecimal("262.25"), "stocks", "EUR", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("8", "SNP 500", BigDecimal("0.3"), BigDecimal("5835.3"), BigDecimal("5835.3"), "etf", "EUR", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
            Investment("9", "Gold", BigDecimal("1"), BigDecimal("3704.25"), BigDecimal("3704.25"), "gold", "EUR", timestamp(2025, 12, 18), false, timestamp(2025, 12, 18)),
        )

        val expected = InvestmentNetWorth(
            totals = listOf(
                InvestmentTotal("EUR", BigDecimal("6005.565"), BigDecimal("6005.565"), calculatePnL(BigDecimal("6005.565"), BigDecimal("6005.565"))),
                InvestmentTotal("USD", BigDecimal("8956.553"), BigDecimal("8956.553"), calculatePnL(BigDecimal("8956.553"), BigDecimal("8956.553"))),
            ),
        )
        val actual = GroupInvestments.groupInvestmentNetWorth(investments)

        assertEquals(expected, actual)
    }

    @Test
    fun groupInvestmentAveragesYearly() {
        val investments = listOf(
            Investment("1", "GOOGL", BigDecimal("1"), BigDecimal("100"), BigDecimal("110"), "stocks", "USD", timestamp(2025, 11, 25), false, timestamp(2025, 11, 25)),
            Investment("2", "AAPL", BigDecimal("1"), BigDecimal("200"), BigDecimal("220"), "stocks", "USD", timestamp(2025, 12, 9), false, timestamp(2025, 12, 9)),
            Investment("3", "SNP 500", BigDecimal("1"), BigDecimal("400"), BigDecimal("440"), "etf", "USD", timestamp(2025, 12, 17), false, timestamp(2025, 12, 17)),
            Investment("4", "GOOGL", BigDecimal("1"), BigDecimal("60"), BigDecimal("66"), "stocks", "USD", timestamp(2024, 12, 17), false, timestamp(2024, 12, 17)),
        )

        val expected = mapOf(
            // 700 over the 2 months of 2025
            "2025" to InvestmentAverage(
                totals = listOf(investmentTotal("350.00", "385.00")),
                categories = mapOf(
                    "stocks" to listOf(investmentTotal("150.00", "165.00")),
                    "etf" to listOf(investmentTotal("200.00", "220.00")),
                ),
                assets = mapOf(
                    "GOOGL" to listOf(investmentTotal("50.00", "55.00")),
                    "AAPL" to listOf(investmentTotal("100.00", "110.00")),
                    "SNP 500" to listOf(investmentTotal("200.00", "220.00")),
                ),
            ),
            // a single month has no meaningful average
            "2024" to InvestmentAverage.EMPTY,
        )

        val actual = GroupInvestments.groupInvestmentAveragesYearly(investments)

        assertEquals(expected, actual)
    }

    @Test
    fun groupInvestmentAverageAllTime() {
        val investments = listOf(
            Investment("1", "GOOGL", BigDecimal("1"), BigDecimal("100"), BigDecimal("110"), "stocks", "USD", timestamp(2025, 11, 25), false, timestamp(2025, 11, 25)),
            Investment("2", "AAPL", BigDecimal("1"), BigDecimal("200"), BigDecimal("220"), "stocks", "USD", timestamp(2025, 12, 9), false, timestamp(2025, 12, 9)),
            Investment("3", "SNP 500", BigDecimal("1"), BigDecimal("400"), BigDecimal("440"), "etf", "USD", timestamp(2025, 12, 17), false, timestamp(2025, 12, 17)),
            Investment("4", "GOOGL", BigDecimal("1"), BigDecimal("60"), BigDecimal("66"), "stocks", "USD", timestamp(2024, 12, 17), false, timestamp(2024, 12, 17)),
        )

        // 760 over 3 months
        val expected = InvestmentAverage(
            totals = listOf(investmentTotal("253.33", "278.67")),
            categories = mapOf(
                "stocks" to listOf(investmentTotal("120.00", "132.00")),
                "etf" to listOf(investmentTotal("133.33", "146.67")),
            ),
            assets = mapOf(
                "GOOGL" to listOf(investmentTotal("53.33", "58.67")),
                "AAPL" to listOf(investmentTotal("66.67", "73.33")),
                "SNP 500" to listOf(investmentTotal("133.33", "146.67")),
            ),
        )

        val actual = GroupInvestments.groupInvestmentAverageAllTime(investments)

        assertEquals(expected, actual)
    }

    @Test
    fun `groupInvestmentAverageAllTime should be empty when investments span a single month`() {
        val investments = listOf(
            Investment("1", "GOOGL", BigDecimal("1"), BigDecimal("100"), BigDecimal("110"), "stocks", "USD", timestamp(2025, 12, 9), false, timestamp(2025, 12, 9)),
            Investment("2", "AAPL", BigDecimal("1"), BigDecimal("200"), BigDecimal("220"), "stocks", "USD", timestamp(2025, 12, 17), false, timestamp(2025, 12, 17)),
        )

        assertEquals(InvestmentAverage.EMPTY, GroupInvestments.groupInvestmentAverageAllTime(investments))
    }

    private fun investmentTotal(costPrice: String, marketPrice: String, currency: String = "USD") =
        InvestmentTotal(
            currency = currency,
            costPrice = BigDecimal(costPrice),
            marketPrice = BigDecimal(marketPrice),
            pnl = calculatePnL(BigDecimal(costPrice), BigDecimal(marketPrice)),
        )

    companion object {

        private const val TEST_UUID = "1"

        private val randomTestUUID = { TEST_UUID }

    }

}
