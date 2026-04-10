package com.ivzb.urgat.domain.fire

import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.fire.FiRe
import com.ivzb.urgat.data.forex.ForexRepository
import com.ivzb.urgat.data.saving.Saving
import com.ivzb.urgat.domain.budget.ObserveGroupedBudgetCategoriesMonthlyUseCase
import com.ivzb.urgat.domain.expense.ObserveGroupedExpensesMonthlyUseCase
import com.ivzb.urgat.domain.investment.ObserveGroupedInvestmentsAllTimeUseCase
import com.ivzb.urgat.domain.saving.ObserveGroupedSavingsAllTimeUseCase
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
            observeGroupedSavingsAllTimeUseCase(searchQueryFlow = null, dateRangeFlow = null, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
        ) { withdrawalRate, returnOfInvestment, taxRate, budgetCategoriesMonthlyAsync, expensesMonthlyAsync, investmentsAllTimeAsync, savingsAllTimeAsync ->

            if (budgetCategoriesMonthlyAsync is Async.Success &&
                expensesMonthlyAsync is Async.Success &&
                investmentsAllTimeAsync is Async.Success &&
                savingsAllTimeAsync is Async.Success) {

                val budgetCategoriesMonthly = budgetCategoriesMonthlyAsync.data
                val expensesMonthly = expensesMonthlyAsync.data

                val selection = (budgetCategoriesMonthly.map { it.group } + expensesMonthly.map { it.group })
                    .distinct()
                    .firstNotNullOfOrNull { group ->
                        val budget = budgetCategoriesMonthly.find { budget -> budget.group == group }
                        val expense = expensesMonthly.find { expense -> expense.group == group }

                        if (budget != null && expense != null) {
                            Triple(group, budget, expense)
                        } else {
                            null
                        }
                    }

                if (selection == null) {
                    return@combine Async.Success(null)
                }

                val (selectionGroup, budget, expenses) = selection

                val budgetCurrency = budget.items.first().currency

                val monthlyIncome = budget.total
                val monthlyExpenses = Expense.total(expenses.items, budgetCurrency, forexRepository::convert)
                val monthlyInvestments = monthlyIncome - monthlyExpenses

                val annualIncome = monthlyIncome * 12.toBigDecimal()
                val annualExpenses = monthlyExpenses * 12.toBigDecimal()
                val annualInvestments = monthlyInvestments * 12.toBigDecimal()

                val allTimeInvestments = investmentsAllTimeAsync.data.items.sumOf { it.totalMarketPrice }
                val allTimeSavings = Saving.total(savingsAllTimeAsync.data.items, budgetCurrency, forexRepository::convert)
                val accumulatedStash = allTimeInvestments + allTimeSavings

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
                            savingsRate = null,
                            target = null,

                            withdrawalRate = withdrawalRate,
                            returnOfInvestment = returnOfInvestment,
                            taxRate = taxRate,

                            yearsUntilFiRe = null,
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
                    savingsRate = savingsRate,
                    target = target,

                    withdrawalRate = withdrawalRate,
                    returnOfInvestment = returnOfInvestment,
                    taxRate = taxRate,

                    yearsUntilFiRe = yearsUntilFiRe,
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