package com.ivzb.urgat.domain.finance

import com.ivzb.urgat.R
import com.ivzb.urgat.data.budget.Budget
import com.ivzb.urgat.data.budget.BudgetCategory
import com.ivzb.urgat.data.budget.GroupedBudget
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.expense.ExpenseTotal
import com.ivzb.urgat.data.fire.FiRe
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentTotal
import com.ivzb.urgat.data.saving.Saving
import com.ivzb.urgat.data.saving.SavingTotal
import com.ivzb.urgat.domain.budget.FakeObserveBudgetsFilteringUseCase
import com.ivzb.urgat.domain.budget.FakeObserveBudgetsSortingUseCase
import com.ivzb.urgat.domain.budget.FakeObserveBudgetsUseCase
import com.ivzb.urgat.domain.budget.FakeObserveReportBudgetUseCase
import com.ivzb.urgat.domain.budget.GroupedBudgetCategories
import com.ivzb.urgat.domain.expense.FakeObserveExpensesFilteringUseCase
import com.ivzb.urgat.domain.expense.FakeObserveExpensesSortingUseCase
import com.ivzb.urgat.domain.expense.FakeObserveGroupedExpensesMonthlyUseCase
import com.ivzb.urgat.domain.fire.FakeObserveFiReUseCase
import com.ivzb.urgat.domain.investment.FakeObserveGroupedInvestmentsMonthlyUseCase
import com.ivzb.urgat.domain.investment.FakeObserveInvestmentsFilteringUseCase
import com.ivzb.urgat.domain.investment.FakeObserveInvestmentsSortingUseCase
import com.ivzb.urgat.domain.saving.FakeObserveGroupedSavingsMonthlyUseCase
import com.ivzb.urgat.domain.saving.FakeObserveSavingsSortingUseCase
import com.ivzb.urgat.domain.trades.FakeObserveGroupedTradesMonthlyUseCase
import com.ivzb.urgat.domain.trades.FakeObserveTradesFilteringUseCase
import com.ivzb.urgat.domain.trades.FakeObserveTradesSortingUseCase
import com.ivzb.urgat.domain.watchlist.FakeObserveWatchlistAssetsSortingUseCase
import com.ivzb.urgat.domain.watchlist.FakeObserveWatchlistAssetsUseCase
import com.ivzb.urgat.ui.budgets.BudgetsFilterType
import com.ivzb.urgat.ui.budgets.BudgetsSortType
import com.ivzb.urgat.ui.components.FilterMenuItem
import com.ivzb.urgat.ui.components.GroupedItems
import com.ivzb.urgat.ui.components.SortMenuItem
import com.ivzb.urgat.ui.expenses.ExpensesFilterType
import com.ivzb.urgat.ui.expenses.ExpensesSortType
import com.ivzb.urgat.ui.fire.FireDateRangeType
import com.ivzb.urgat.ui.investments.INVESTMENTS_FILTER_TYPE_ALL
import com.ivzb.urgat.ui.investments.InvestmentsSortType
import com.ivzb.urgat.ui.savings.SavingsSortType
import com.ivzb.urgat.ui.trades.TRADES_FILTER_TYPE_ALL
import com.ivzb.urgat.ui.trades.TradesSortType
import com.ivzb.urgat.ui.watchlist.WatchlistAssetsSortType
import com.ivzb.urgat.util.Async
import java.math.BigDecimal
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import org.junit.Before

@OptIn(ExperimentalCoroutinesApi::class)
abstract class ObserveFinanceUiStateUseCaseTestBase {

    private lateinit var fakeObserveGroupedExpensesMonthlyUseCase: FakeObserveGroupedExpensesMonthlyUseCase
    private lateinit var fakeObserveExpensesFilteringUseCase: FakeObserveExpensesFilteringUseCase
    private lateinit var fakeObserveExpensesSortingUseCase: FakeObserveExpensesSortingUseCase
    protected lateinit var fakeObserveGroupedInvestmentsMonthlyUseCase: FakeObserveGroupedInvestmentsMonthlyUseCase
    private lateinit var fakeObserveInvestmentsFilteringUseCase: FakeObserveInvestmentsFilteringUseCase
    private lateinit var fakeObserveInvestmentsSortingUseCase: FakeObserveInvestmentsSortingUseCase
    protected lateinit var fakeObserveGroupedTradesMonthlyUseCase: FakeObserveGroupedTradesMonthlyUseCase
    private lateinit var fakeObserveTradesFilteringUseCase: FakeObserveTradesFilteringUseCase
    private lateinit var fakeObserveTradesSortingUseCase: FakeObserveTradesSortingUseCase
    private lateinit var fakeObserveGroupedSavingsMonthlyUseCase: FakeObserveGroupedSavingsMonthlyUseCase
    private lateinit var fakeObserveSavingsSortingUseCase: FakeObserveSavingsSortingUseCase
    protected lateinit var fakeObserveWatchlistAssetsUseCase: FakeObserveWatchlistAssetsUseCase
    private lateinit var fakeObserveWatchlistAssetsSortingUseCase: FakeObserveWatchlistAssetsSortingUseCase
    private lateinit var fakeObserveBudgetsUseCase: FakeObserveBudgetsUseCase
    private lateinit var fakeObserveBudgetsFilteringUseCase: FakeObserveBudgetsFilteringUseCase
    private lateinit var fakeObserveBudgetsSortingUseCase: FakeObserveBudgetsSortingUseCase
    private lateinit var fakeObserveReportBudgetUseCase: FakeObserveReportBudgetUseCase
    private lateinit var fakeObserveFiReUseCase: FakeObserveFiReUseCase

    private lateinit var useCase: ObserveFinanceUiStateUseCase

    @Before
    fun setUp() {
        fakeObserveGroupedExpensesMonthlyUseCase = FakeObserveGroupedExpensesMonthlyUseCase()
        fakeObserveExpensesFilteringUseCase = FakeObserveExpensesFilteringUseCase()
        fakeObserveExpensesSortingUseCase = FakeObserveExpensesSortingUseCase()
        fakeObserveGroupedInvestmentsMonthlyUseCase = FakeObserveGroupedInvestmentsMonthlyUseCase()
        fakeObserveInvestmentsFilteringUseCase = FakeObserveInvestmentsFilteringUseCase()
        fakeObserveInvestmentsSortingUseCase = FakeObserveInvestmentsSortingUseCase()
        fakeObserveGroupedTradesMonthlyUseCase = FakeObserveGroupedTradesMonthlyUseCase()
        fakeObserveTradesFilteringUseCase = FakeObserveTradesFilteringUseCase()
        fakeObserveTradesSortingUseCase = FakeObserveTradesSortingUseCase()
        fakeObserveGroupedSavingsMonthlyUseCase = FakeObserveGroupedSavingsMonthlyUseCase()
        fakeObserveSavingsSortingUseCase = FakeObserveSavingsSortingUseCase()
        fakeObserveWatchlistAssetsUseCase = FakeObserveWatchlistAssetsUseCase()
        fakeObserveWatchlistAssetsSortingUseCase = FakeObserveWatchlistAssetsSortingUseCase()
        fakeObserveBudgetsUseCase = FakeObserveBudgetsUseCase()
        fakeObserveBudgetsFilteringUseCase = FakeObserveBudgetsFilteringUseCase()
        fakeObserveBudgetsSortingUseCase = FakeObserveBudgetsSortingUseCase()
        fakeObserveReportBudgetUseCase = FakeObserveReportBudgetUseCase()
        fakeObserveFiReUseCase = FakeObserveFiReUseCase()

        useCase = DefaultObserveFinanceUiStateUseCase(
            fakeObserveGroupedExpensesMonthlyUseCase,
            fakeObserveExpensesFilteringUseCase,
            fakeObserveExpensesSortingUseCase,
            fakeObserveGroupedInvestmentsMonthlyUseCase,
            fakeObserveInvestmentsFilteringUseCase,
            fakeObserveInvestmentsSortingUseCase,
            fakeObserveGroupedTradesMonthlyUseCase,
            fakeObserveTradesFilteringUseCase,
            fakeObserveTradesSortingUseCase,
            fakeObserveGroupedSavingsMonthlyUseCase,
            fakeObserveSavingsSortingUseCase,
            fakeObserveWatchlistAssetsUseCase,
            fakeObserveWatchlistAssetsSortingUseCase,
            fakeObserveBudgetsUseCase,
            fakeObserveBudgetsFilteringUseCase,
            fakeObserveBudgetsSortingUseCase,
            fakeObserveReportBudgetUseCase,
            fakeObserveFiReUseCase,
        )
    }

    protected fun invokeUseCase(
        searchQuery: String = "",
        expensesFilterType: ExpensesFilterType = ExpensesFilterType.ALL_EXPENSES,
        expensesSortType: ExpensesSortType = ExpensesSortType.BY_DATE,
        investmentsFilterType: String = INVESTMENTS_FILTER_TYPE_ALL,
        investmentsSortType: InvestmentsSortType = InvestmentsSortType.BY_DATE,
        tradesFilterType: String = TRADES_FILTER_TYPE_ALL,
        tradesSortType: TradesSortType = TradesSortType.BY_DATE,
        savingsSortType: SavingsSortType = SavingsSortType.BY_DATE,
        budgetsFilterType: BudgetsFilterType = BudgetsFilterType.ALL_BUDGETS,
        budgetsSortType: BudgetsSortType = BudgetsSortType.BY_DATE,
    ) = useCase(
        searchQueryFlow = flowOf(searchQuery),
        expensesFilterTypeFlow = flowOf(expensesFilterType),
        expensesSortTypeFlow = flowOf(expensesSortType),
        investmentsFilterTypeFlow = flowOf(investmentsFilterType),
        investmentsSortTypeFlow = flowOf(investmentsSortType),
        tradesFilterTypeFlow = flowOf(tradesFilterType),
        tradesSortTypeFlow = flowOf(tradesSortType),
        savingsSortTypeFlow = flowOf(savingsSortType),
        budgetsFilterTypeFlow = flowOf(budgetsFilterType),
        budgetsSortTypeFlow = flowOf(budgetsSortType),
        budgetDateRangeFlow = flowOf(0L..Long.MAX_VALUE),
        watchlistAssetsSortTypeFlow = flowOf(WatchlistAssetsSortType.BY_DATE),
        withdrawalRateFlow = flowOf(null),
        returnOfInvestmentFlow = flowOf(null),
        taxRateFlow = flowOf(null),
        fireDateRangeFlow = flowOf(0L..Long.MAX_VALUE),
        fireDateRangeTypeFlow = flowOf(FireDateRangeType.LAST_MONTH),
        fireCustomStartFlow = flowOf(0L),
        fireCustomEndFlow = flowOf(0L),
    )

    protected fun emitAllLoading() {
        fakeObserveGroupedExpensesMonthlyUseCase.emit(Async.Loading)
        fakeObserveExpensesFilteringUseCase.emit(Async.Loading)
        fakeObserveExpensesSortingUseCase.emit(Async.Loading)
        fakeObserveGroupedInvestmentsMonthlyUseCase.emit(Async.Loading)
        fakeObserveInvestmentsFilteringUseCase.emit(Async.Loading)
        fakeObserveInvestmentsSortingUseCase.emit(Async.Loading)
        fakeObserveGroupedTradesMonthlyUseCase.emit(Async.Loading)
        fakeObserveTradesFilteringUseCase.emit(Async.Loading)
        fakeObserveTradesSortingUseCase.emit(Async.Loading)
        fakeObserveGroupedSavingsMonthlyUseCase.emit(Async.Loading)
        fakeObserveSavingsSortingUseCase.emit(Async.Loading)
        fakeObserveWatchlistAssetsUseCase.emit(Async.Loading)
        fakeObserveWatchlistAssetsSortingUseCase.emit(Async.Loading)
        fakeObserveBudgetsUseCase.emit(Async.Loading)
        fakeObserveBudgetsFilteringUseCase.emit(Async.Loading)
        fakeObserveBudgetsSortingUseCase.emit(Async.Loading)
        fakeObserveReportBudgetUseCase.emit(Async.Loading)
        fakeObserveFiReUseCase.emit(Async.Loading)
    }

    protected fun emitAllError() {
        fakeObserveGroupedExpensesMonthlyUseCase.emit(Async.Error)
        fakeObserveExpensesFilteringUseCase.emit(Async.Error)
        fakeObserveExpensesSortingUseCase.emit(Async.Error)
        fakeObserveGroupedInvestmentsMonthlyUseCase.emit(Async.Error)
        fakeObserveInvestmentsFilteringUseCase.emit(Async.Error)
        fakeObserveInvestmentsSortingUseCase.emit(Async.Error)
        fakeObserveGroupedTradesMonthlyUseCase.emit(Async.Error)
        fakeObserveTradesFilteringUseCase.emit(Async.Error)
        fakeObserveTradesSortingUseCase.emit(Async.Error)
        fakeObserveGroupedSavingsMonthlyUseCase.emit(Async.Error)
        fakeObserveSavingsSortingUseCase.emit(Async.Error)
        fakeObserveWatchlistAssetsUseCase.emit(Async.Error)
        fakeObserveWatchlistAssetsSortingUseCase.emit(Async.Error)
        fakeObserveBudgetsUseCase.emit(Async.Error)
        fakeObserveBudgetsFilteringUseCase.emit(Async.Error)
        fakeObserveBudgetsSortingUseCase.emit(Async.Error)
        fakeObserveReportBudgetUseCase.emit(Async.Error)
        fakeObserveFiReUseCase.emit(Async.Error)
    }

    protected fun emitAllSuccess(
        reportBudget: GroupedBudget? = fakeReportBudget,
        fire: FiRe? = fakeFiRe,
    ) {
        fakeObserveGroupedExpensesMonthlyUseCase.emit(Async.Success(fakeExpenses))
        fakeObserveExpensesFilteringUseCase.emit(Async.Success(fakeExpensesFiltering))
        fakeObserveExpensesSortingUseCase.emit(Async.Success(fakeExpensesSorting))
        fakeObserveGroupedInvestmentsMonthlyUseCase.emit(Async.Success(fakeInvestments))
        fakeObserveInvestmentsFilteringUseCase.emit(Async.Success(fakeInvestmentsFiltering))
        fakeObserveInvestmentsSortingUseCase.emit(Async.Success(fakeInvestmentsSorting))
        fakeObserveGroupedTradesMonthlyUseCase.emit(Async.Success(listOf()))
        fakeObserveTradesFilteringUseCase.emit(Async.Success(listOf()))
        fakeObserveTradesSortingUseCase.emit(Async.Success(listOf()))
        fakeObserveGroupedSavingsMonthlyUseCase.emit(Async.Success(fakeSavings))
        fakeObserveSavingsSortingUseCase.emit(Async.Success(fakeSavingsSorting))
        fakeObserveWatchlistAssetsUseCase.emit(Async.Success(listOf()))
        fakeObserveWatchlistAssetsSortingUseCase.emit(Async.Success(listOf()))
        fakeObserveBudgetsUseCase.emit(Async.Success(fakeBudgets))
        fakeObserveBudgetsFilteringUseCase.emit(Async.Success(fakeBudgetsFiltering))
        fakeObserveBudgetsSortingUseCase.emit(Async.Success(fakeBudgetsSorting))
        fakeObserveReportBudgetUseCase.emit(Async.Success(reportBudget))
        fakeObserveFiReUseCase.emit(Async.Success(fire))
    }

    private val expenses = listOf(
        Expense("1", "Lunch", BigDecimal.TEN, "EUR", "food", 1L, false, false, 1L)
    )

    protected val fakeExpenses = listOf(
        GroupedItems(group = "2025/12", total = listOf(ExpenseTotal(BigDecimal.TEN, "EUR")), items = expenses)
    )

    protected val fakeExpensesFiltering = listOf(
        FilterMenuItem(type = ExpensesFilterType.ALL_EXPENSES, labelResId = R.string.filter_all, size = 1),
        FilterMenuItem(type = ExpensesFilterType.DEBIT_EXPENSES, labelResId = R.string.filter_debit, size = 0),
        FilterMenuItem(type = ExpensesFilterType.COST_EXPENSES, labelResId = R.string.filter_cost, size = 1),
        FilterMenuItem(type = ExpensesFilterType.EXCLUDED_FROM_ANALYTICS, labelResId = R.string.filter_excluded_from_analytics, size = 0),
    )

    protected val fakeExpensesSorting = listOf(
        SortMenuItem(type = ExpensesSortType.BY_DATE, labelResId = R.string.sort_by_date),
        SortMenuItem(type = ExpensesSortType.BY_MIN, labelResId = R.string.sort_by_min),
        SortMenuItem(type = ExpensesSortType.BY_MAX, labelResId = R.string.sort_by_max),
    )

    private val investments = listOf(
        Investment("1", "SNP", BigDecimal.ONE, BigDecimal.TEN, BigDecimal.ZERO, "stocks", "USD", 1L, false, 1L)
    )

    protected val fakeInvestments = listOf(
        GroupedItems(group = "2025/12", total = listOf(InvestmentTotal("USD", costPrice = 10.toBigDecimal(), marketPrice = 10.toBigDecimal(), pnl = 0.toBigDecimal())), items = investments)
    )

    protected val fakeInvestmentsFiltering = listOf(
        FilterMenuItem(type = INVESTMENTS_FILTER_TYPE_ALL, labelResId = R.string.filter_all, size = 1),
        FilterMenuItem(type = "stocks", labelResId = R.string.filter_all, size = 1),
    )

    protected val fakeInvestmentsSorting = listOf(
        SortMenuItem(type = InvestmentsSortType.BY_DATE, labelResId = R.string.sort_by_date),
        SortMenuItem(type = InvestmentsSortType.BY_MARKET_VALUE, labelResId = R.string.sort_by_market_value),
    )

    private val savings = listOf(
        Saving("1", "Coffee fund", BigDecimal.TEN, "EUR", 1L, false, 1L)
    )

    protected val fakeSavings = listOf(
        GroupedItems(group = "2025/12", total = listOf(SavingTotal(BigDecimal.TEN, "EUR")), items = savings)
    )

    protected val fakeSavingsSorting = listOf(
        SortMenuItem(type = SavingsSortType.BY_DATE, labelResId = R.string.sort_by_date),
        SortMenuItem(type = SavingsSortType.BY_MIN, labelResId = R.string.sort_by_min),
        SortMenuItem(type = SavingsSortType.BY_MAX, labelResId = R.string.sort_by_max),
    )

    protected val fakeBudgets = listOf(
        Budget("1", 6500.toBigDecimal(), 8800.toBigDecimal(), 7200.toBigDecimal(), "EUR", 1L, false, 1L),
    )

    protected val fakeBudgetsFiltering = listOf(
        FilterMenuItem(type = BudgetsFilterType.ALL_BUDGETS, labelResId = R.string.filter_all, size = 1),
        FilterMenuItem(type = BudgetsFilterType.POSITIVE_BUDGETS, labelResId = R.string.filter_positive, size = 1),
        FilterMenuItem(type = BudgetsFilterType.NEGATIVE_BUDGETS, labelResId = R.string.filter_negative, size = 0),
    )

    protected val fakeBudgetsSorting = listOf(
        SortMenuItem(type = BudgetsSortType.BY_DATE, labelResId = R.string.sort_by_date),
        SortMenuItem(type = BudgetsSortType.BY_MIN, labelResId = R.string.sort_by_min),
        SortMenuItem(type = BudgetsSortType.BY_MAX, labelResId = R.string.sort_by_max),
    )

    protected val fakeReportBudget = GroupedBudget(
        category = GroupedBudgetCategories(
            group = "2025/12",
            total = 6_500.toBigDecimal(),
            items = listOf(BudgetCategory(id = "1", name = "1", currency = "EUR", value = 6_500.toBigDecimal())),
        ),
        currency = "EUR",
        totalIncome = 6_500.toBigDecimal(),
        totalExpenses = 2_000.toBigDecimal(),
        totalInvestments = 3_000.toBigDecimal(),
        totalTrades = 700.toBigDecimal(),
        totalSavings = 500.toBigDecimal(),
    )

    protected val fakeFiRe = FiRe(
        selection = "2025/12",
        currency = "EUR",
        monthlyIncome = 6_500.toBigDecimal(),
        monthlyExpenses = 2_000.toBigDecimal(),
        monthlyInvestments = 3_000.toBigDecimal(),
        annualIncome = 78_000.toBigDecimal(),
        annualExpenses = 24_000.toBigDecimal(),
        annualInvestments = 36_000.toBigDecimal(),
        accumulatedStash = 100_000.toBigDecimal(),
        savingsRate = 50.toBigDecimal(),
        target = 600_000.toBigDecimal(),
        withdrawalRate = 4.toBigDecimal(),
        returnOfInvestment = 7.toBigDecimal(),
        taxRate = 10.toBigDecimal(),
        yearsUntilFiRe = 10.toBigDecimal(),
    )

}
