package com.ivzb.urgat.domain.fire

import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.fire.FiRe
import com.ivzb.urgat.data.fire.StashBreakdownItem
import com.ivzb.urgat.data.forex.ForexRepository
import com.ivzb.urgat.data.saving.Saving
import com.ivzb.urgat.data.trade.Trade
import com.ivzb.urgat.di.DefaultDispatcher
import com.ivzb.urgat.domain.budget.GroupedBudgetCategories
import com.ivzb.urgat.domain.budget.ObserveGroupedBudgetCategoriesMonthlyUseCase
import com.ivzb.urgat.domain.expense.GroupedExpenses
import com.ivzb.urgat.domain.expense.ObserveGroupedExpensesMonthlyUseCase
import com.ivzb.urgat.domain.investment.GroupedInvestments
import com.ivzb.urgat.domain.investment.ObserveGroupedInvestmentsAllTimeUseCase
import com.ivzb.urgat.domain.saving.GroupedSavings
import com.ivzb.urgat.domain.saving.ObserveGroupedSavingsAllTimeUseCase
import com.ivzb.urgat.domain.trades.GroupedTrades
import com.ivzb.urgat.domain.trades.ObserveGroupedTradesAllTimeUseCase
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.Tracker
import com.ivzb.urgat.util.combineAsync
import com.ivzb.urgat.util.div
import com.ivzb.urgat.util.round
import com.ivzb.urgat.util.toPercentage
import java.math.BigDecimal
import javax.inject.Inject
import kotlin.math.pow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

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
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
): ObserveFiReUseCase {

    override fun invoke(
        withdrawalRateFlow: Flow<BigDecimal?>,
        returnOfInvestmentFlow: Flow<BigDecimal?>,
        taxRateFlow: Flow<BigDecimal?>,

        dateRangeFlow: Flow<LongRange>?,
    ): Flow<Async<FiRe?>> {

        return combineAsync(
            withdrawalRateFlow.map { Async.Success(it) },
            returnOfInvestmentFlow.map { Async.Success(it) },
            taxRateFlow.map { Async.Success(it) },

            observeGroupedBudgetCategoriesMonthlyUseCase(searchQueryFlow = null, dateRangeFlow = dateRangeFlow, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
            observeGroupedExpensesMonthlyUseCase(searchQueryFlow = null, dateRangeFlow = dateRangeFlow, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
            observeGroupedInvestmentsAllTimeUseCase(searchQueryFlow = null, dateRangeFlow = null, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
            observeGroupedTradesAllTimeUseCase(searchQueryFlow = null, dateRangeFlow = null, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
            observeGroupedSavingsAllTimeUseCase(searchQueryFlow = null, dateRangeFlow = null, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
        ) { withdrawalRate, returnOfInvestment, taxRate, budgetCategoriesMonthly, expensesMonthly, investmentsAllTime, tradesAllTime, savingsAllTime ->

            // a failed conversion is dropped from the sums below, which quietly makes them
            // too small - wrapping the converter is what makes that visible afterwards
            val forex = TallyingConverter(forexRepository::convert)

            val cashflow = calculateCashflow(budgetCategoriesMonthly, expensesMonthly, forex) ?: return@combineAsync null
            val stash = calculateStash(investmentsAllTime, tradesAllTime, savingsAllTime, cashflow.currency, forex)

            if (cashflow.isIncomplete) {
                return@combineAsync null
            }

            val projection = calculateProjection(withdrawalRate, returnOfInvestment, taxRate, cashflow, stash.amount)

            FiRe(
                selection = cashflow.selection,
                currency = cashflow.currency,

                monthlyIncome = cashflow.monthlyIncome,
                monthlyExpenses = cashflow.monthlyExpenses,
                monthlyInvestments = cashflow.monthlyInvestments,

                annualIncome = cashflow.annualIncome,
                annualExpenses = cashflow.annualExpenses,
                annualInvestments = cashflow.annualInvestments,

                accumulatedStash = stash.amount,
                accumulatedStashCost = stash.cost,
                stashBreakdown = stash.breakdown,
                savingsRate = projection?.savingsRate,
                target = projection?.target,

                withdrawalRate = withdrawalRate,
                returnOfInvestment = returnOfInvestment,
                taxRate = taxRate,

                yearsUntilFiRe = projection?.yearsUntilFiRe,

                unavailableAssets = stash.unavailableAssets,
                unavailableRates = forex.unresolved.sorted(),
            )
        }
            .catch { e ->
                Tracker.error(e)
                emit(Async.Error)
            }
            .flowOn(dispatcher)
    }

    /**
     * Averages the income and expenses over the months which have both, in the budget's currency.
     */
    private suspend fun calculateCashflow(
        budgetCategoriesMonthly: List<GroupedBudgetCategories>,
        expensesMonthly: List<GroupedExpenses>,
        forex: TallyingConverter,
    ): Cashflow? {

        val completeMonths = findCompleteMonths(budgetCategoriesMonthly, expensesMonthly)

        if (completeMonths.isEmpty()) {
            return null
        }

        val budgetCurrency = completeMonths.first().second.items.first().currency

        // Sum income and expenses across all complete months, then average
        var totalIncome = BigDecimal.ZERO
        var totalExpenses = BigDecimal.ZERO

        for ((_, budget, expenses) in completeMonths) {
            totalIncome += budget.total
            totalExpenses += Expense.total(expenses.items, budgetCurrency, forex::convert)
        }

        val isSingleMonth = completeMonths.size == 1

        return Cashflow(
            selection = if (isSingleMonth) {
                completeMonths.first().first
            } else {
                "${completeMonths.last().first} - ${completeMonths.first().first}"
            },
            currency = budgetCurrency,
            monthlyIncome = if (isSingleMonth) totalIncome else totalIncome / completeMonths.size.toBigDecimal(),
            monthlyExpenses = if (isSingleMonth) totalExpenses else totalExpenses / completeMonths.size.toBigDecimal(),
        )
    }

    /**
     * Finds all months where both budget and expenses exist.
     */
    private fun findCompleteMonths(
        budgetCategoriesMonthly: List<GroupedBudgetCategories>,
        expensesMonthly: List<GroupedExpenses>,
    ): List<Triple<String, GroupedBudgetCategories, GroupedExpenses>> =
        (budgetCategoriesMonthly.map { it.group } + expensesMonthly.map { it.group })
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

    private suspend fun calculateStash(
        investmentsAllTime: GroupedInvestments,
        tradesAllTime: GroupedTrades,
        savingsAllTime: GroupedSavings,
        currency: String,
        forex: TallyingConverter,
    ): Stash {

        val allTimeInvestments = investmentsAllTime.items.sumOf { it.totalMarketPrice }
        val allTimeTrades = Trade.total(tradesAllTime.items, currency, forex::convert)
        val allTimeSavings = Saving.total(savingsAllTime.items, currency, forex::convert)

        // a trade and a saving are worth exactly what went into them, so they contribute
        // the very same amount to both sides and only the investments carry a profit
        val allTimeInvestmentsCost = investmentsAllTime.items.sumOf { it.totalCostPrice }

        // Calculate stash breakdown per asset
        val investmentBreakdown = investmentsAllTime.items
            .groupBy { it.category }
            .map { (category, investments) ->
                StashBreakdownItem(
                    name = category,
                    amount = investments.sumOf { it.totalMarketPrice },
                    costPrice = investments.sumOf { it.totalCostPrice },
                )
            }

        val tradesBreakdown = calculateConvertedBreakdown(tradesAllTime.items, Trade::name) { trade ->
            forex.convert(trade.amount, trade.currency, currency)
        }

        val savingsBreakdown = calculateConvertedBreakdown(savingsAllTime.items, Saving::name) { saving ->
            forex.convert(saving.amount, saving.currency, currency)
        }

        return Stash(
            amount = allTimeInvestments + allTimeTrades + allTimeSavings,
            cost = allTimeInvestmentsCost + allTimeTrades + allTimeSavings,
            breakdown = (investmentBreakdown + tradesBreakdown + savingsBreakdown).sortedByDescending { it.amount },
            // an unfetched quote values a position at cost and an unresolved rate drops an
            // amount from the sums, so either way the stash is understated rather than right
            unavailableAssets = investmentsAllTime.items
                .filter { it.isMarketPriceStale }
                .map { it.name }
                .distinct()
                .sorted(),
        )
    }

    private suspend fun <T> calculateConvertedBreakdown(
        items: List<T>,
        nameOf: (T) -> String,
        convert: suspend (T) -> BigDecimal?,
    ): List<StashBreakdownItem> = items
        .groupBy(nameOf)
        .map { (name, group) ->
            var total = BigDecimal.ZERO

            for (item in group) {
                total += convert(item) ?: BigDecimal.ZERO
            }

            StashBreakdownItem(name, total)
        }

    /**
     * Projects the target and the years until FiRe, `null` until all of the rates are set.
     */
    private fun calculateProjection(
        withdrawalRate: BigDecimal?,
        returnOfInvestment: BigDecimal?,
        taxRate: BigDecimal?,
        cashflow: Cashflow,
        accumulatedStash: BigDecimal,
    ): Projection? {

        if (withdrawalRate == null || returnOfInvestment == null || taxRate == null) {
            return null
        }

        val withdrawalRatePercentage = withdrawalRate.coerceAtLeast(1.toBigDecimal()).toPercentage()
        val returnOfInvestmentPercentage = 1.toBigDecimal() + returnOfInvestment.toPercentage()
        val taxRatePercentage = 1.toBigDecimal() + taxRate.toPercentage()

        val target = (cashflow.annualExpenses * taxRatePercentage / withdrawalRatePercentage).round()

        return Projection(
            savingsRate = (1.toBigDecimal() - (cashflow.annualExpenses / cashflow.annualIncome)) * 100.toBigDecimal(),
            target = target,
            yearsUntilFiRe = calculateFiReYears(
                annualIncome = cashflow.annualIncome,
                annualInvestments = cashflow.annualIncome - cashflow.annualExpenses,
                accumulatedStash = accumulatedStash,
                target = target,
                returnOfInvestment = returnOfInvestmentPercentage.toDouble(),
            ),
        )
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
 * The income and expenses averaged over the months which have both, in the budget's currency.
 */
private class Cashflow(
    val selection: String,
    val currency: String,
    val monthlyIncome: BigDecimal,
    val monthlyExpenses: BigDecimal,
) {

    val monthlyInvestments: BigDecimal = monthlyIncome - monthlyExpenses

    val annualIncome: BigDecimal = monthlyIncome * 12.toBigDecimal()
    val annualExpenses: BigDecimal = monthlyExpenses * 12.toBigDecimal()
    val annualInvestments: BigDecimal = monthlyInvestments * 12.toBigDecimal()

    val isIncomplete: Boolean = annualIncome == 0.toBigDecimal() || annualExpenses == 0.toBigDecimal()

}

private class Stash(
    val amount: BigDecimal,
    val cost: BigDecimal,
    val breakdown: List<StashBreakdownItem>,
    val unavailableAssets: List<String>,
)

private class Projection(
    val savingsRate: BigDecimal,
    val target: BigDecimal,
    val yearsUntilFiRe: BigDecimal,
)

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
