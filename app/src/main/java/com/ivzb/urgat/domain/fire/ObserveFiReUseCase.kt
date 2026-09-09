package com.ivzb.urgat.domain.fire

import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.fire.FiRe
import com.ivzb.urgat.data.fire.StashBreakdownItem
import com.ivzb.urgat.data.forex.ForexRepository
import com.ivzb.urgat.data.saving.Saving
import com.ivzb.urgat.data.trade.Trade
import com.ivzb.urgat.domain.budget.ObserveGroupedBudgetCategoriesMonthlyUseCase
import com.ivzb.urgat.domain.expense.ObserveGroupedExpensesMonthlyUseCase
import com.ivzb.urgat.domain.investment.ObserveGroupedInvestmentsAllTimeUseCase
import com.ivzb.urgat.domain.saving.ObserveGroupedSavingsAllTimeUseCase
import com.ivzb.urgat.domain.trades.ObserveGroupedTradesAllTimeUseCase
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.Tracker
import com.ivzb.urgat.util.div
import com.ivzb.urgat.util.round
import com.ivzb.urgat.util.toPercentage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.math.BigDecimal
import javax.inject.Inject
import kotlin.math.pow

interface ObserveFiReUseCase {

    operator fun invoke(
        withdrawalRateFlow: Flow<BigDecimal?>,
        returnOfInvestmentFlow: Flow<BigDecimal?>,
        taxRateFlow: Flow<BigDecimal?>,

        dateRangeFlow: Flow<LongRange>? = null,
    ): Flow<Async<FiRe?>>

}

class DefaultObserveFiReUseCase @Inject constructor(
    private val observeGroupedBudgetCategoriesMonthlyUseCase: ObserveGroupedBudgetCategoriesMonthlyUseCase,
    private val observeGroupedExpensesMonthlyUseCase: ObserveGroupedExpensesMonthlyUseCase,
    private val observeGroupedInvestmentsAllTimeUseCase: ObserveGroupedInvestmentsAllTimeUseCase,
    private val observeGroupedTradesAllTimeUseCase: ObserveGroupedTradesAllTimeUseCase,
    private val observeGroupedSavingsAllTimeUseCase: ObserveGroupedSavingsAllTimeUseCase,
    private val forexRepository: ForexRepository,
): ObserveFiReUseCase {

    override fun invoke(
        withdrawalRateFlow: Flow<BigDecimal?>,
        returnOfInvestmentFlow: Flow<BigDecimal?>,
        taxRateFlow: Flow<BigDecimal?>,

        dateRangeFlow: Flow<LongRange>?,
    ): Flow<Async<FiRe?>> {

        return com.ivzb.urgat.util.combine(
            withdrawalRateFlow,
            returnOfInvestmentFlow,
            taxRateFlow,

            observeGroupedBudgetCategoriesMonthlyUseCase(searchQueryFlow = null, dateRangeFlow = dateRangeFlow, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
            observeGroupedExpensesMonthlyUseCase(searchQueryFlow = null, dateRangeFlow = dateRangeFlow, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
            observeGroupedInvestmentsAllTimeUseCase(searchQueryFlow = null, dateRangeFlow = null, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
            observeGroupedTradesAllTimeUseCase(searchQueryFlow = null, dateRangeFlow = null, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
            observeGroupedSavingsAllTimeUseCase(searchQueryFlow = null, dateRangeFlow = null, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
        ) { withdrawalRate, returnOfInvestment, taxRate, budgetCategoriesMonthlyAsync, expensesMonthlyAsync, investmentsAllTimeAsync, tradesAllTimeAsync, savingsAllTimeAsync ->

            if (budgetCategoriesMonthlyAsync is Async.Success &&
                expensesMonthlyAsync is Async.Success &&
                investmentsAllTimeAsync is Async.Success &&
                tradesAllTimeAsync is Async.Success &&
                savingsAllTimeAsync is Async.Success) {

                val budgetCategoriesMonthly = budgetCategoriesMonthlyAsync.data
                val expensesMonthly = expensesMonthlyAsync.data

                // Find all months where both budget and expenses exist
                val completeMonths = (budgetCategoriesMonthly.map { it.group } + expensesMonthly.map { it.group })
                    .distinct()
                    .mapNotNull { group ->
                        val budget = budgetCategoriesMonthly.find { budget -> budget.group == group }
                        val expense = expensesMonthly.find { expense -> expense.group == group }

                        if (budget != null && expense != null) {
                            Triple(group, budget, expense)
                        } else {
                            null
                        }
                    }

                if (completeMonths.isEmpty()) {
                    return@combine Async.Success(null)
                }

                val firstMonth = completeMonths.first()
                val budgetCurrency = firstMonth.second.items.first().currency

                // a failed conversion is dropped from the sums below, which quietly makes them
                // too small - wrapping the converter is what makes that visible afterwards
                val forex = TallyingConverter(forexRepository::convert)

                // Sum income and expenses across all complete months, then average
                var totalIncome = BigDecimal.ZERO
                var totalExpenses = BigDecimal.ZERO

                for ((_, budget, expenses) in completeMonths) {
                    totalIncome += budget.total
                    totalExpenses += Expense.total(expenses.items, budgetCurrency, forex::convert)
                }

                val monthlyIncome = if (completeMonths.size == 1) totalIncome else totalIncome / completeMonths.size.toBigDecimal()
                val monthlyExpenses = if (completeMonths.size == 1) totalExpenses else totalExpenses / completeMonths.size.toBigDecimal()
                val monthlyInvestments = monthlyIncome - monthlyExpenses

                val selectionGroup = if (completeMonths.size == 1) {
                    completeMonths.first().first
                } else {
                    "${completeMonths.last().first} - ${completeMonths.first().first}"
                }

                val annualIncome = monthlyIncome * 12.toBigDecimal()
                val annualExpenses = monthlyExpenses * 12.toBigDecimal()
                val annualInvestments = monthlyInvestments * 12.toBigDecimal()

                val allTimeInvestments = investmentsAllTimeAsync.data.items.sumOf { it.totalMarketPrice }
                val allTimeTrades = Trade.total(tradesAllTimeAsync.data.items, budgetCurrency, forex::convert)
                val allTimeSavings = Saving.total(savingsAllTimeAsync.data.items, budgetCurrency, forex::convert)
                val accumulatedStash = allTimeInvestments + allTimeTrades + allTimeSavings

                // a trade and a saving are worth exactly what went into them, so they contribute
                // the very same amount to both sides and only the investments carry a profit
                val allTimeInvestmentsCost = investmentsAllTimeAsync.data.items.sumOf { it.totalCostPrice }
                val accumulatedStashCost = allTimeInvestmentsCost + allTimeTrades + allTimeSavings

                // Calculate stash breakdown per asset
                val investmentBreakdown = investmentsAllTimeAsync.data.items
                    .groupBy { it.category }
                    .map { (category, investments) ->
                        StashBreakdownItem(
                            name = category,
                            amount = investments.sumOf { it.totalMarketPrice },
                            costPrice = investments.sumOf { it.totalCostPrice },
                        )
                    }

                val tradesBreakdown = tradesAllTimeAsync.data.items
                    .groupBy { it.name }
                    .map { (name, trades) ->
                        var total = BigDecimal.ZERO

                        for (trade in trades) {
                            total += forex.convert(trade.amount, trade.currency, budgetCurrency) ?: BigDecimal.ZERO
                        }

                        StashBreakdownItem(name, total)
                    }

                val savingsBreakdown = savingsAllTimeAsync.data.items
                    .groupBy { it.name }
                    .map { (name, savings) ->
                        var total = BigDecimal.ZERO

                        for (saving in savings) {
                            total += forex.convert(saving.amount, saving.currency, budgetCurrency) ?: BigDecimal.ZERO
                        }

                        StashBreakdownItem(name, total)
                    }

                val stashBreakdown = (investmentBreakdown + tradesBreakdown + savingsBreakdown)
                    .sortedByDescending { it.amount }

                // an unfetched quote values a position at cost and an unresolved rate drops an
                // amount from the sums, so either way the stash is understated rather than right
                val unavailableAssets = investmentsAllTimeAsync.data.items
                    .filter { it.isMarketPriceStale }
                    .map { it.name }
                    .distinct()
                    .sorted()

                val unavailableRates = forex.unresolved.sorted()

                val isIncomplete = annualIncome == 0.toBigDecimal() || annualExpenses == 0.toBigDecimal()

                if (isIncomplete) {
                    return@combine Async.Success(null)
                }

                val isReady = withdrawalRate != null && returnOfInvestment != null && taxRate != null

                if (!isReady) {
                    return@combine Async.Success(
                        FiRe(
                            selection = selectionGroup,
                            currency = budgetCurrency,

                            monthlyIncome = monthlyIncome,
                            monthlyExpenses = monthlyExpenses,
                            monthlyInvestments = monthlyInvestments,

                            annualIncome = annualIncome,
                            annualExpenses = annualExpenses,
                            annualInvestments = annualInvestments,

                            accumulatedStash = accumulatedStash,
                            accumulatedStashCost = accumulatedStashCost,
                            stashBreakdown = stashBreakdown,
                            savingsRate = null,
                            target = null,

                            withdrawalRate = withdrawalRate,
                            returnOfInvestment = returnOfInvestment,
                            taxRate = taxRate,

                            yearsUntilFiRe = null,

                            unavailableAssets = unavailableAssets,
                            unavailableRates = unavailableRates,
                        )
                    )
                }

                val withdrawalRatePercentage = withdrawalRate.coerceAtLeast(1.toBigDecimal()).toPercentage()
                val returnOfInvestmentPercentage = 1.toBigDecimal() + returnOfInvestment.toPercentage()
                val taxRatePercentage = 1.toBigDecimal() + taxRate.toPercentage()

                val savingsRate = (1.toBigDecimal() - (annualExpenses / annualIncome)) * 100.toBigDecimal()
                val target = (annualExpenses * taxRatePercentage / withdrawalRatePercentage).round()

                val yearsUntilFiRe = calculateFiReYears(
                    annualIncome = annualIncome,
                    annualInvestments = annualIncome - annualExpenses,
                    accumulatedStash = accumulatedStash,
                    target = target,
                    returnOfInvestment = returnOfInvestmentPercentage.toDouble(),
                )

                val fire = FiRe(
                    selection = selectionGroup,
                    currency = budgetCurrency,

                    monthlyIncome = monthlyIncome,
                    monthlyExpenses = monthlyExpenses,
                    monthlyInvestments = monthlyInvestments,

                    annualIncome = annualIncome,
                    annualExpenses = annualExpenses,
                    annualInvestments = annualInvestments,

                    accumulatedStash = accumulatedStash,
                    accumulatedStashCost = accumulatedStashCost,
                    stashBreakdown = stashBreakdown,
                    savingsRate = savingsRate,
                    target = target,

                    withdrawalRate = withdrawalRate,
                    returnOfInvestment = returnOfInvestment,
                    taxRate = taxRate,

                    yearsUntilFiRe = yearsUntilFiRe,

                    unavailableAssets = unavailableAssets,
                    unavailableRates = unavailableRates,
                )

                Async.Success(fire)
            } else {
                Async.Error
            }
        }
        .catch { e ->
            Tracker.error(e)
            emit(Async.Error)
        }
    }

    private fun calculateFiReYears(
        annualIncome: BigDecimal,
        annualInvestments: BigDecimal,
        accumulatedStash: BigDecimal,
        target: BigDecimal,
        returnOfInvestment: Double,
        maxYears: Int = 50,
    ): BigDecimal {

        if (annualIncome >= target) {
            return 0.toBigDecimal()
        }

        val stash = DoubleArray(maxYears + 1)
        stash[0] = accumulatedStash.toDouble()

        var fireYear = maxYears

        for (year in 1..maxYears) {
            stash[year] = (stash[year - 1] * returnOfInvestment) + (annualInvestments.toDouble() * returnOfInvestment.pow(0.5))

            if (stash[year] >= target.toDouble()) {
                fireYear = year
                break
            }
        }

        // Linear interpolation (exact FIRE year)
        val y0 = fireYear - 1
        val y1 = fireYear
        val s0 = stash[y0]
        val s1 = stash[y1]

        val exactYear = y0 + (target.toDouble() - s0) / (s1 - s0)

        return if (exactYear.isFinite()) {
            exactYear.toBigDecimal().round()
        } else {
            INVALID_YEAR
        }
    }

    companion object {

        private val INVALID_YEAR = 99.99.toBigDecimal()

    }

}

/**
 * Remembers the conversions which could not be resolved.
 *
 * [Expense.total] and its siblings drop a failed conversion from the sum, so an unreachable
 * exchange rate makes a total silently too small. Wrapping the converter is what lets the caller
 * tell a complete number apart from an incomplete one, without changing what the sums return.
 *
 * The pair is kept rather than a tally, so the caller can name what is missing instead of only
 * admitting that something is.
 */
private class TallyingConverter(
    private val delegate: suspend (
        amount: BigDecimal,
        fromCurrency: String,
        toCurrency: String,
    ) -> BigDecimal?,
) {

    private val _unresolved = mutableSetOf<String>()

    /** The currency pairs which could not be converted, written as `USD -> EUR`. */
    val unresolved: Set<String>
        get() = _unresolved

    suspend fun convert(
        amount: BigDecimal,
        fromCurrency: String,
        toCurrency: String,
    ): BigDecimal? = delegate(amount, fromCurrency, toCurrency)
        .also { converted ->
            if (converted == null) {
                _unresolved += "$fromCurrency $RATE_ARROW $toCurrency"
            }
        }

    private companion object {

        const val RATE_ARROW = "→"

    }

}
