package com.ivzb.urgat.ui.finance

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivzb.urgat.R
import com.ivzb.urgat.data.budget.Budget
import com.ivzb.urgat.data.budget.GroupedBudget
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.fire.FiRe
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.saving.Saving
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.navigation.FakeNavigator
import com.ivzb.urgat.navigation.Navigator
import com.ivzb.urgat.ui.budgets.BudgetsFilteringUiInfo
import com.ivzb.urgat.ui.budgets.BudgetsUiState
import com.ivzb.urgat.ui.budgets.CategoriesPage
import com.ivzb.urgat.ui.components.AllDevicesPreview
import com.ivzb.urgat.ui.components.EmptyStateCard
import com.ivzb.urgat.ui.components.FilterMenu
import com.ivzb.urgat.ui.components.ItemHeaderWithMore
import com.ivzb.urgat.ui.components.MoreMenu
import com.ivzb.urgat.ui.components.ScreenTitleWithInfo
import com.ivzb.urgat.ui.components.SortMenu
import com.ivzb.urgat.ui.components.screens.NavigationPagerScreen
import com.ivzb.urgat.ui.components.uiState.PagerUiState
import com.ivzb.urgat.ui.components.uiState.UiState
import com.ivzb.urgat.ui.expenses.ExpensesUiState
import com.ivzb.urgat.ui.investments.InvestmentsUiState
import com.ivzb.urgat.ui.savings.SavingsUiState
import com.ivzb.urgat.ui.theme.UrgatTheme
import com.ivzb.urgat.util.format

private const val EMPTY_FIRE_CALCULATION = "∞"

@Composable
fun FinanceScreen(
    viewModel: FinanceViewModel,

    onAddExpense: () -> Unit,
    onMoreExpenses: () -> Unit,
    onExpenseClick: (Expense) -> Unit,

    onAddInvestment: () -> Unit,
    onMoreInvestments: () -> Unit,
    onInvestmentClick: (Investment) -> Unit,

    onAddSaving: () -> Unit,
    onMoreSavings: () -> Unit,
    onSavingClick: (Saving) -> Unit,

    onAddBudget: () -> Unit,
    onMoreBudgets: () -> Unit,
    onBudgetClick: (Budget) -> Unit,

    navigator: Navigator,
    modifier: Modifier = Modifier,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FinanceScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,

        onMoreExpenses = onMoreExpenses,
        onExpenseClick = onExpenseClick,

        onMoreInvestments = onMoreInvestments,
        onInvestmentClick = onInvestmentClick,

        onMoreSavings = onMoreSavings,
        onSavingClick = onSavingClick,

        onMoreBudgets = onMoreBudgets,
        onBudgetClick = onBudgetClick,

        navigator = navigator,
        modifier = modifier
    )

    LaunchedEffect(uiState.uiEvent) {
        uiState.uiEvent?.let { event ->
            when (event) {
                is FinanceUiEvent.AddExpense -> onAddExpense()
                is FinanceUiEvent.AddInvestment -> onAddInvestment()
                is FinanceUiEvent.AddSaving -> onAddSaving()
                is FinanceUiEvent.AddBudget -> onAddBudget()
            }

            viewModel.onIntent(FinanceIntent.EventConsumed)
        }
    }
}

@VisibleForTesting
@Composable
fun FinanceScreen(
    uiState: FinanceUiState,
    onIntent: (FinanceIntent) -> Unit,

    onMoreExpenses: () -> Unit,
    onExpenseClick: (Expense) -> Unit,

    onMoreInvestments: () -> Unit,
    onInvestmentClick: (Investment) -> Unit,

    onMoreSavings: () -> Unit,
    onSavingClick: (Saving) -> Unit,

    onMoreBudgets: () -> Unit,
    onBudgetClick: (Budget) -> Unit,

    navigator: Navigator,
    modifier: Modifier = Modifier
) {

    NavigationPagerScreen(
        uiState = uiState,

        route = FinanceRoute,
        searchQuery = uiState.filteringUiInfo.searchQuery,
        onSearchQueryChanged = { query -> onIntent(FinanceIntent.SearchQueryChanged(query)) },
        onFabActionClick = { id -> onIntent(FinanceIntent.FabActionClick(id)) },
        navigator = navigator,

        withSearch = { page -> uiState.withTopbarActions(page) },
        topAppBarActionsContent = { page ->
            if (uiState.withTopbarActions(page)) {
                when (page) {
                    FinancePage.Expenses -> {
                        FilterMenu(
                            items = uiState.expensesUiState.filteringUiInfo.filterMenuItems,
                            showBadge = uiState.expensesUiState.filteringUiInfo.showFilterBadge,
                            onFilter = { onIntent(FinanceIntent.SetExpensesFiltering(it)) },
                        )

                        SortMenu(
                            items = uiState.expensesUiState.filteringUiInfo.sortMenuItems,
                            showBadge = uiState.expensesUiState.filteringUiInfo.showSortBadge,
                            onFilter = { onIntent(FinanceIntent.SetExpensesSorting(it)) },
                        )
                    }

                    FinancePage.Investments -> {
                        FilterMenu(
                            items = uiState.investmentsUiState.filteringUiInfo.filterMenuItems,
                            showBadge = uiState.investmentsUiState.filteringUiInfo.showFilterBadge,
                            onFilter = { onIntent(FinanceIntent.SetInvestmentsFiltering(it)) },
                        )

                        SortMenu(
                            items = uiState.investmentsUiState.filteringUiInfo.sortMenuItems,
                            showBadge = uiState.investmentsUiState.filteringUiInfo.showSortBadge,
                            onFilter = { onIntent(FinanceIntent.SetInvestmentsSorting(it)) },
                        )
                    }

                    FinancePage.Savings -> {
                        SortMenu(
                            items = uiState.savingsUiState.filteringUiInfo.sortMenuItems,
                            showBadge = uiState.savingsUiState.filteringUiInfo.showSortBadge,
                            onFilter = { onIntent(FinanceIntent.SetSavingsSorting(it)) },
                        )
                    }

                    FinancePage.Budgets -> {
                        FilterMenu(
                            items = uiState.budgetsUiState.filteringUiInfo.filterMenuItems,
                            showBadge = uiState.budgetsUiState.filteringUiInfo.showFilterBadge,
                            onFilter = { onIntent(FinanceIntent.SetBudgetsFiltering(it)) },
                        )

                        SortMenu(
                            items = uiState.budgetsUiState.filteringUiInfo.sortMenuItems,
                            showBadge = uiState.budgetsUiState.filteringUiInfo.showSortBadge,
                            onFilter = { onIntent(FinanceIntent.SetBudgetsSorting(it)) },
                        )
                    }
                }

                MoreMenu(
                    listOfNotNull(
                        (R.string.reset_filters to { onIntent(FinanceIntent.ResetFilters) }).takeIf { uiState.hasAppliedFilters },
                    )
                )
            }
        },

        content = { page, listState, onListStateChanged ->
            when (page) {
                FinancePage.Expenses -> ExpensesPage(
                    uiState = uiState.expensesUiState,

                    onMoreExpenses = onMoreExpenses,
                    onAddExpense = { onIntent(FinanceIntent.FabActionClick(FinanceFabActionId.Expense)) },
                    onExpenseClick = onExpenseClick,

                    listState = listState,
                    onListStateChanged = onListStateChanged,
                )

                FinancePage.Investments -> InvestmentsPage(
                    uiState = uiState.investmentsUiState,

                    onMoreInvestments = onMoreInvestments,
                    onAddInvestment = { onIntent(FinanceIntent.FabActionClick(FinanceFabActionId.Investment)) },
                    onInvestmentClick = onInvestmentClick,

                    listState = listState,
                    onListStateChanged = onListStateChanged,
                )

                FinancePage.Savings -> SavingsPage(
                    uiState = uiState.savingsUiState,

                    onMoreSavings = onMoreSavings,
                    onAddSaving = { onIntent(FinanceIntent.FabActionClick(FinanceFabActionId.Saving)) },
                    onSavingClick = onSavingClick,

                    listState = listState,
                    onListStateChanged = onListStateChanged,
                )

                FinancePage.Budgets -> BudgetsPage(
                    uiState = uiState.budgetsUiState,

                    onMoreBudgets = onMoreBudgets,
                    onAddBudget = { onIntent(FinanceIntent.FabActionClick(FinanceFabActionId.Budget)) },
                    onBudgetClick = onBudgetClick,

                    listState = listState,
                    onListStateChanged = onListStateChanged,
                )

                FinancePage.Report -> ReportPage(
                    reportBudget = uiState.reportBudget,

                    listState = listState,
                    onListStateChanged = onListStateChanged,
                )

                FinancePage.FIRE -> FiRePage(
                    fire = uiState.fire,
                    onWithdrawalRateChange = { onIntent(FinanceIntent.UpdateFireWithdrawalRate(it)) },
                    onReturnOfInvestmentChange = { onIntent(FinanceIntent.UpdateFireReturnOfInvestment(it)) },
                    onTaxRateChange = { onIntent(FinanceIntent.UpdateFireTaxRate(it)) },

                    listState = listState,
                    onListStateChanged = onListStateChanged,
                )
            }
        },

        modifier = modifier
    )
}

@Composable
private fun FinancePage(
    uiState: PagerUiState,
    onEmptyStateButtonClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    when {
        uiState.isEmpty -> {
            EmptyState(uiState, onEmptyStateButtonClick)
        }

        else -> {
            content()
        }
    }
}

@Composable
private fun ExpensesPage(
    uiState: ExpensesUiState,

    onMoreExpenses: () -> Unit,
    onAddExpense: () -> Unit,
    onExpenseClick: (Expense) -> Unit,

    listState: LazyListState,
    onListStateChanged: (LazyListState) -> Unit,
) {

    FinancePage(
        uiState = uiState,
        onEmptyStateButtonClick = onAddExpense,

        content = {
            com.ivzb.urgat.ui.expenses.TransactionsPage(
                expenses = uiState.expenses,
                onExpenseClick = onExpenseClick,

                header = {
                    ItemHeaderWithMore(
                        title = stringResource(uiState.filteringUiInfo.currentFilteringLabel),
                        onMoreClick = onMoreExpenses,
                    )
                },

                listState = listState,
                onListStateChanged = onListStateChanged,
            )
        }
    )
}

@Composable
private fun InvestmentsPage(
    uiState: InvestmentsUiState,

    onMoreInvestments: () -> Unit,
    onAddInvestment: () -> Unit,
    onInvestmentClick: (Investment) -> Unit,

    listState: LazyListState,
    onListStateChanged: (LazyListState) -> Unit,
) {

    FinancePage(
        uiState = uiState,
        onEmptyStateButtonClick = onAddInvestment,
        content = {
            com.ivzb.urgat.ui.investments.TransactionsPage(
                investments = uiState.investments,
                onInvestmentClick = onInvestmentClick,

                header = {
                    ItemHeaderWithMore(
                        title = stringResource(uiState.filteringUiInfo.currentFilteringLabel),
                        onMoreClick = onMoreInvestments,
                    )
                },

                listState = listState,
                onListStateChanged = onListStateChanged,
            )
        }
    )
}

@Composable
private fun SavingsPage(
    uiState: SavingsUiState,

    onMoreSavings: () -> Unit,
    onAddSaving: () -> Unit,
    onSavingClick: (Saving) -> Unit,

    listState: LazyListState,
    onListStateChanged: (LazyListState) -> Unit,
) {

    FinancePage(
        uiState = uiState,
        onEmptyStateButtonClick = onAddSaving,
        content = {
            com.ivzb.urgat.ui.savings.TransactionsPage(
                savings = uiState.savings,
                onSavingClick = onSavingClick,

                header = {
                    ItemHeaderWithMore(
                        title = stringResource(uiState.filteringUiInfo.currentFilteringLabel),
                        onMoreClick = onMoreSavings,
                    )
                },

                listState = listState,
                onListStateChanged = onListStateChanged,
            )
        }
    )
}

@Composable
private fun BudgetsPage(
    uiState: BudgetsUiState,

    onMoreBudgets: () -> Unit,
    onAddBudget: () -> Unit,
    onBudgetClick: (Budget) -> Unit,

    listState: LazyListState,
    onListStateChanged: (LazyListState) -> Unit,
) {

    FinancePage(
        uiState = uiState,
        onEmptyStateButtonClick = onAddBudget,
        content = {
            com.ivzb.urgat.ui.budgets.TransactionsPage(
                budgets = uiState.budgets,
                onBudgetClick = onBudgetClick,

                header = {
                    ItemHeaderWithMore(
                        title = stringResource(uiState.filteringUiInfo.currentFilteringLabel),
                        onMoreClick = onMoreBudgets,
                    )
                },

                listState = listState,
                onListStateChanged = onListStateChanged,
            )
        }
    )
}

@Composable
private fun ReportPage(
    reportBudget: GroupedBudget?,

    listState: LazyListState,
    onListStateChanged: (LazyListState) -> Unit,
) {

    if (reportBudget == null) {
        return
    }

    CategoriesPage(
        budget = reportBudget,
        group = null,
        listState = listState,
        onListStateChanged = onListStateChanged,
    )
}

@Composable
private fun FiRePage(
    fire: FiRe?,
    onWithdrawalRateChange: (String) -> Unit,
    onReturnOfInvestmentChange: (String) -> Unit,
    onTaxRateChange: (String) -> Unit,

    listState: LazyListState,
    onListStateChanged: (LazyListState) -> Unit,
) {

    if (fire == null) {
        return
    }

    LaunchedEffect(Unit) {
        onListStateChanged(listState)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = dimensionResource(R.dimen.content_padding_horizontal),
                vertical = dimensionResource(R.dimen.content_padding_vertical),
            )
    ) {

        ScreenTitleWithInfo(
            title = stringResource(R.string.fire_title, fire.selection),
            dialogTitle = stringResource(R.string.how_does_it_work),
            dialogText = StringBuilder().apply {
                appendLine(stringResource(R.string.fire_info))
                appendLine()

                appendLine(
                    stringResource(
                        R.string.fire_accumulated_stash,
                        fire.accumulatedStash.format()
                    )
                )
                appendLine(stringResource(R.string.fire_annual_income, fire.annualIncome.format()))
                appendLine(
                    stringResource(
                        R.string.fire_annual_expenses,
                        fire.annualExpenses.format()
                    )
                )
                appendLine(
                    stringResource(
                        R.string.fire_annual_investments,
                        fire.annualInvestments.format()
                    )
                )

                appendLine()

                appendLine(
                    stringResource(
                        R.string.fire_savings_rate,
                        fire.savingsRate?.format() ?: EMPTY_FIRE_CALCULATION
                    )
                )
                appendLine(
                    stringResource(
                        R.string.fire_target,
                        fire.target?.format() ?: EMPTY_FIRE_CALCULATION
                    )
                )

                appendLine()

                appendLine(
                    stringResource(
                        R.string.fire_years_until_fire,
                        fire.yearsUntilFiRe?.format() ?: EMPTY_FIRE_CALCULATION
                    )
                )
            }.toString(),
        )

        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.fire_margin)))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.fire_withdrawal_rate),
            )

            Spacer(modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.fire_margin)))

            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.fire_return_of_investment),
            )

            Spacer(modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.fire_margin)))

            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.fire_tax_rate),
            )
        }

        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.fire_margin)))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            var withdrawalRateValue by remember {
                mutableStateOf(
                    TextFieldValue(
                        fire.withdrawalRate?.toPlainString() ?: ""
                    )
                )
            }
            var returnOfInvestmentValue by remember {
                mutableStateOf(
                    TextFieldValue(
                        fire.returnOfInvestment?.toPlainString() ?: ""
                    )
                )
            }
            var taxRateValue by remember {
                mutableStateOf(
                    TextFieldValue(
                        fire.taxRate?.toPlainString() ?: ""
                    )
                )
            }

            TextField(
                modifier = Modifier.weight(1f),
                value = withdrawalRateValue,
                onValueChange = { onWithdrawalRateChange(it.text) },
                placeholder = {
                    Text(
                        stringResource(
                            R.string.format_default,
                            fire.withdrawalRatePlaceholder.toPlainString()
                        )
                    )
                },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
            )

            Spacer(modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.fire_margin)))

            TextField(
                modifier = Modifier.weight(1f),
                value = returnOfInvestmentValue,
                onValueChange = { onReturnOfInvestmentChange(it.text) },
                placeholder = {
                    Text(
                        stringResource(
                            R.string.format_default,
                            fire.returnOfInvestmentPlaceholder.toPlainString()
                        )
                    )
                },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
            )

            Spacer(modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.fire_margin)))

            TextField(
                modifier = Modifier.weight(1f),
                value = taxRateValue,
                onValueChange = { onTaxRateChange(it.text) },
                placeholder = {
                    Text(
                        stringResource(
                            R.string.format_default,
                            fire.taxRatePlaceholder.toPlainString()
                        )
                    )
                },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
            )

            LaunchedEffect(fire) {
                val withdrawalRate = fire.withdrawalRate?.toPlainString() ?: ""
                val returnOfInvestment = fire.returnOfInvestment?.toPlainString() ?: ""
                val taxRate = fire.taxRate?.toPlainString() ?: ""

                withdrawalRateValue = TextFieldValue(
                    text = withdrawalRate,
                    selection = TextRange(withdrawalRate.length)
                )

                returnOfInvestmentValue = TextFieldValue(
                    text = returnOfInvestment,
                    selection = TextRange(returnOfInvestment.length)
                )

                taxRateValue = TextFieldValue(
                    text = taxRate,
                    selection = TextRange(taxRate.length)
                )
            }
        }

        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.vertical_margin)))

        Text(
            text = stringResource(
                R.string.fire_years_until_fire,
                fire.yearsUntilFiRe?.format() ?: EMPTY_FIRE_CALCULATION
            ),
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun EmptyState(
    uiState: UiState,
    onButtonClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = dimensionResource(R.dimen.content_padding),
                vertical = dimensionResource(R.dimen.content_padding),
            )
    ) {
        EmptyStateCard(
            iconRes = uiState.filteringUiInfo.emptyIconRes,
            label = uiState.filteringUiInfo.emptyLabel,
            message = uiState.filteringUiInfo.emptyMessage,
            buttonLabel = uiState.filteringUiInfo.emptyButtonLabel,
            onButtonClick = onButtonClick,
        )
    }
}

@AllDevicesPreview
@Preview
@Composable
private fun FinanceScreenPreview(
    initialPage: Int = 0,
    expensesInitialPage: Int = 0,
    investmentsInitialPage: Int = 0,
    savingsInitialPage: Int = 0,
    budgetsInitialPage: Int = 0,
) {
    UrgatTheme {
        FinanceScreen(
            uiState = FinanceUiState(
                expensesUiState = ExpensesUiState(
                    expenses = SampleData.SAMPLE_EXPENSES_GROUPED,
                    categoriesMonthly = SampleData.SAMPLE_EXPENSES_CATEGORIES_MONTHLY,
                    categoriesYearly = SampleData.SAMPLE_EXPENSES_CATEGORIES_YEARLY,
                    categoriesAllTime = SampleData.SAMPLE_EXPENSES_CATEGORIES_ALL_TIME,
                    initialPage = expensesInitialPage,
                ),
                investmentsUiState = InvestmentsUiState(
                    investments = SampleData.SAMPLE_INVESTMENTS_GROUPED,
                    categoriesYearly = SampleData.SAMPLE_INVESTMENTS_CATEGORIES_YEARLY,
                    assets = SampleData.SAMPLE_INVESTMENTS_ASSETS,
                    categories = SampleData.SAMPLE_INVESTMENTS_CATEGORIES,
                    netWorth = SampleData.SAMPLE_INVESTMENTS_NET_WORTH,
                    initialPage = investmentsInitialPage,
                ),
                savingsUiState = SavingsUiState(
                    savings = SampleData.SAMPLE_SAVINGS_GROUPED,
                    categoriesMonthly = SampleData.SAMPLE_SAVINGS_CATEGORIES_MONTHLY,
                    categoriesYearly = SampleData.SAMPLE_SAVINGS_CATEGORIES_YEARLY,
                    categoriesAllTime = SampleData.SAMPLE_SAVINGS_CATEGORIES_ALL_TIME,
                    initialPage = savingsInitialPage,
                ),
                budgetsUiState = BudgetsUiState(
                    budgets = SampleData.SAMPLE_BUDGETS,
                    filteringUiInfo = BudgetsFilteringUiInfo(),
                    initialPage = budgetsInitialPage,
                ),
                reportBudget = SampleData.SAMPLE_REPORT_BUDGET,
                fire = SampleData.SAMPLE_FIRE,
                isLoading = false,
                hasError = false,
                initialPage = initialPage,
                filteringUiInfo = FinanceFilteringUiInfo()
            ),
            onIntent = {},

            onExpenseClick = {},
            onMoreExpenses = {},

            onInvestmentClick = {},
            onMoreInvestments = {},

            onSavingClick = {},
            onMoreSavings = {},

            onBudgetClick = {},
            onMoreBudgets = {},

            navigator = FakeNavigator(),
            modifier = Modifier
        )
    }
}

@AllDevicesPreview
@Preview
@Composable
private fun FinanceScreenExpensesTransactionsPreview() {
    FinanceScreenPreview(initialPage = 0, expensesInitialPage = 0)
}

@AllDevicesPreview
@Preview
@Composable
private fun FinanceScreenExpensesMonthlyPreview() {
    FinanceScreenPreview(initialPage = 0, expensesInitialPage = 1)
}

@AllDevicesPreview
@Preview
@Composable
private fun FinanceScreenExpensesYearlyPreview() {
    FinanceScreenPreview(initialPage = 0, expensesInitialPage = 2)
}

@AllDevicesPreview
@Preview
@Composable
private fun FinanceScreenExpensesAllTimePreview() {
    FinanceScreenPreview(initialPage = 0, expensesInitialPage = 3)
}

@AllDevicesPreview
@Preview
@Composable
private fun FinanceScreenInvestmentsTransactionsPreview() {
    FinanceScreenPreview(initialPage = 1, investmentsInitialPage = 0)
}

@AllDevicesPreview
@Preview
@Composable
private fun FinanceScreenInvestmentsYearlyPreview() {
    FinanceScreenPreview(initialPage = 1, investmentsInitialPage = 1)
}

@AllDevicesPreview
@Preview
@Composable
private fun FinanceScreenInvestmentsAllTimePreview() {
    FinanceScreenPreview(initialPage = 1, investmentsInitialPage = 2)
}

@AllDevicesPreview
@Preview
@Composable
private fun FinanceScreenInvestmentsAssetsPreview() {
    FinanceScreenPreview(initialPage = 1, investmentsInitialPage = 3)
}

@AllDevicesPreview
@Preview
@Composable
private fun FinanceScreenSavingsTransactionsPreview() {
    FinanceScreenPreview(initialPage = 2, expensesInitialPage = 0)
}

@AllDevicesPreview
@Preview
@Composable
private fun FinanceScreenSavingsMonthlyPreview() {
    FinanceScreenPreview(initialPage = 2, expensesInitialPage = 1)
}

@AllDevicesPreview
@Preview
@Composable
private fun FinanceScreenSavingsYearlyPreview() {
    FinanceScreenPreview(initialPage = 2, expensesInitialPage = 2)
}

@AllDevicesPreview
@Preview
@Composable
private fun FinanceScreenSavingsAllTimePreview() {
    FinanceScreenPreview(initialPage = 2, expensesInitialPage = 3)
}

@AllDevicesPreview
@Preview
@Composable
private fun FinanceScreenBudgetsTransactionsPreview() {
    FinanceScreenPreview(initialPage = 3, expensesInitialPage = 0)
}

@AllDevicesPreview
@Composable
private fun FinanceScreenReportPreview() {
    FinanceScreenPreview(initialPage = 4)
}

@AllDevicesPreview
@Composable
private fun FinanceScreenFiRePreview() {
    FinanceScreenPreview(initialPage = 5)
}

@AllDevicesPreview
@Composable
fun FinanceScreenEmptyPreview() {
    UrgatTheme {
        FinanceScreen(
            uiState = FinanceUiState(
                expensesUiState = ExpensesUiState(),
                investmentsUiState = InvestmentsUiState(),
                savingsUiState = SavingsUiState(),
                isLoading = false,
                hasError = false,
                filteringUiInfo = FinanceFilteringUiInfo()
            ),
            onIntent = {},

            onExpenseClick = {},
            onMoreExpenses = {},

            onInvestmentClick = {},
            onMoreInvestments = {},

            onSavingClick = {},
            onMoreSavings = {},

            onBudgetClick = {},
            onMoreBudgets = {},

            navigator = FakeNavigator(),
            modifier = Modifier
        )
    }
}

@AllDevicesPreview
@Composable
fun FinanceScreenLoadingPreview() {
    UrgatTheme {
        FinanceScreen(
            uiState = FinanceUiState(
                expensesUiState = ExpensesUiState(),
                investmentsUiState = InvestmentsUiState(),
                savingsUiState = SavingsUiState(),
                isLoading = true,
                hasError = false,
                filteringUiInfo = FinanceFilteringUiInfo()
            ),
            onIntent = {},

            onExpenseClick = {},
            onMoreExpenses = {},

            onInvestmentClick = {},
            onMoreInvestments = {},

            onSavingClick = {},
            onMoreSavings = {},

            onBudgetClick = {},
            onMoreBudgets = {},

            navigator = FakeNavigator(),
            modifier = Modifier
        )
    }
}

@AllDevicesPreview
@Composable
fun FinanceScreenErrorPreview() {
    UrgatTheme {
        FinanceScreen(
            uiState = FinanceUiState(
                expensesUiState = ExpensesUiState(),
                investmentsUiState = InvestmentsUiState(),
                savingsUiState = SavingsUiState(),
                isLoading = false,
                hasError = true,
                filteringUiInfo = FinanceFilteringUiInfo()
            ),
            onIntent = {},

            onExpenseClick = {},
            onMoreExpenses = {},

            onInvestmentClick = {},
            onMoreInvestments = {},

            onSavingClick = {},
            onMoreSavings = {},

            onBudgetClick = {},
            onMoreBudgets = {},

            navigator = FakeNavigator(),
            modifier = Modifier
        )
    }
}