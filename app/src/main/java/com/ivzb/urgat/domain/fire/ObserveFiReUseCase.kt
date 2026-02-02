package com.ivzb.urgat.domain.fire

import com.ivzb.urgat.data.fire.FiRe
import com.ivzb.urgat.domain.budget.ObserveGroupedBudgetCategoriesMonthlyUseCase
import com.ivzb.urgat.domain.expense.ObserveGroupedExpensesMonthlyUseCase
import com.ivzb.urgat.domain.investment.ObserveGroupedInvestmentsAllTimeUseCase
import com.ivzb.urgat.domain.saving.ObserveGroupedSavingsAllTimeUseCase
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.Tracker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject
import kotlin.math.pow

interface ObserveFiReUseCase {

    operator fun invoke(
        withdrawalRateFlow: Flow<BigDecimal?>,
        returnOfInvestmentsFlow: Flow<BigDecimal?>,
        taxRateFlow: Flow<BigDecimal?>,

        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>? = null,
        filterTypeFlow: Flow<Unit>? = null,
        sortTypeFlow: Flow<Unit>?,
        limitFlow: Flow<Int>? = null,
    ): Flow<Async<FiRe?>>

}

class DefaultObserveFiReUseCase @Inject constructor(
    private val observeGroupedBudgetCategoriesMonthlyUseCase: ObserveGroupedBudgetCategoriesMonthlyUseCase,
    private val observeGroupedExpensesMonthlyUseCase: ObserveGroupedExpensesMonthlyUseCase,
    private val observeGroupedInvestmentsAllTimeUseCase: ObserveGroupedInvestmentsAllTimeUseCase,
    private val observeGroupedSavingsAllTimeUseCase: ObserveGroupedSavingsAllTimeUseCase,
): ObserveFiReUseCase {

    override fun invoke(
        withdrawalRateFlow: Flow<BigDecimal?>,
        returnOfInvestmentsFlow: Flow<BigDecimal?>,
        taxRateFlow: Flow<BigDecimal?>,

        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<Unit>?,
        sortTypeFlow: Flow<Unit>?,
        limitFlow: Flow<Int>?
    ): Flow<Async<FiRe?>> {

        return com.ivzb.urgat.util.combine(
            withdrawalRateFlow,
            returnOfInvestmentsFlow,
            taxRateFlow,

            observeGroupedBudgetCategoriesMonthlyUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = dateRangeFlow, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
            observeGroupedExpensesMonthlyUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = dateRangeFlow, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
            observeGroupedInvestmentsAllTimeUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = null, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
            observeGroupedSavingsAllTimeUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = null, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
        ) { withdrawalRate, returnOfInvestments, taxRate, budgetCategoriesMonthlyAsync, expensesMonthlyAsync, investmentsAllTimeAsync, savingsAllTimeAsync ->

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

                val monthlyIncome = budget.total
                val monthlyExpenses = expenses.total
                val monthlyInvestments = monthlyIncome - monthlyExpenses

                val annualIncome = monthlyIncome * 12.toBigDecimal()
                val annualExpenses = monthlyExpenses * 12.toBigDecimal()
                val annualInvestments = monthlyInvestments * 12.toBigDecimal()

                val allTimeInvestments = investmentsAllTimeAsync.data.items.sumOf { it.marketValue }
                val allTimeSavings = savingsAllTimeAsync.data.total
                val accumulatedStash = allTimeInvestments + allTimeSavings

                val isIncomplete = annualIncome == 0.toBigDecimal() || annualExpenses == 0.toBigDecimal()

                if (isIncomplete) {
                    return@combine Async.Success(null)
                }

                val isReady = withdrawalRate != null && returnOfInvestments != null && taxRate != null

                if (!isReady) {
                    return@combine Async.Success(
                        FiRe(
                            selection = selectionGroup,

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
                            returnOfInvestments = returnOfInvestments,
                            taxRate = taxRate,

                            yearsUntilFiRe = null,
                        )
                    )
                }

                val withdrawalRatePercentage = withdrawalRate.coerceAtLeast(1.toBigDecimal()).toPercentage()
                val returnOfInvestmentsPercentage = 1.toBigDecimal() + returnOfInvestments.toPercentage()
                val taxRatePercentage = 1.toBigDecimal() + taxRate.toPercentage()

                val savingsRate = (1.toBigDecimal() - (annualExpenses / annualIncome)) * 100.toBigDecimal()
                val target = (annualExpenses * taxRatePercentage / withdrawalRatePercentage).round()

                val yearsUntilFiRe = calculateFiReYears(
                    annualIncome = annualIncome,
                    annualInvestments = annualIncome - annualExpenses,
                    accumulatedStash = accumulatedStash,
                    target = target,
                    returnOfInvestments = returnOfInvestmentsPercentage.toDouble(),
                )

                val fire = FiRe(
                    selection = selectionGroup,

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
                    returnOfInvestments = returnOfInvestments,
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
        returnOfInvestments: Double,
        maxYears: Int = 50,
    ): BigDecimal {

        if (annualIncome >= target) {
            return 0.toBigDecimal()
        }

        val stash = DoubleArray(maxYears + 1)
        stash[0] = accumulatedStash.toDouble()

        var fireYear = maxYears

        for (year in 1..maxYears) {
            stash[year] = (stash[year - 1] * returnOfInvestments) + (annualInvestments.toDouble() * returnOfInvestments.pow(0.5))

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

    private fun BigDecimal.round(): BigDecimal {
        return this.setScale(2, RoundingMode.HALF_UP)
    }

    private fun BigDecimal.toPercentage(): BigDecimal {
        return this / 100.toBigDecimal()
    }

    private operator fun BigDecimal.div(other: BigDecimal): BigDecimal {
        return this.divide(
            other,
            maxOf(this.scale(), other.scale(), 2),
            RoundingMode.HALF_UP
        )
    }

    companion object {

        private val INVALID_YEAR = 99.99.toBigDecimal()

    }

}