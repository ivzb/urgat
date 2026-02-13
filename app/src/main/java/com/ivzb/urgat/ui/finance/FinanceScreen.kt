package com.ivzb.urgat.ui.finance

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
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
import com.ivzb.urgat.ui.budgets.BudgetItem
import com.ivzb.urgat.ui.budgets.BudgetsFilteringUiInfo
import com.ivzb.urgat.ui.budgets.CategoriesPage
import com.ivzb.urgat.ui.budgets.budgetCategoryPage
import com.ivzb.urgat.ui.components.AllDevicesPreview
import com.ivzb.urgat.ui.components.EmptyStateCard
import com.ivzb.urgat.ui.components.FilterMenu
import com.ivzb.urgat.ui.components.ItemHeaderWithSeeAll
import com.ivzb.urgat.ui.components.ScreenTitleWithInfo
import com.ivzb.urgat.ui.components.SortMenu
import com.ivzb.urgat.ui.components.screens.NavigationPagerScreen
import com.ivzb.urgat.ui.expenses.ExpensesScreen
import com.ivzb.urgat.ui.expenses.ExpensesUiState
import com.ivzb.urgat.ui.investments.InvestmentItem
import com.ivzb.urgat.ui.investments.InvestmentsFilteringUiInfo
import com.ivzb.urgat.ui.savings.SavingItem
import com.ivzb.urgat.ui.savings.SavingsFilteringUiInfo
import com.ivzb.urgat.ui.theme.UrgatTheme
import com.ivzb.urgat.util.format

private const val EMPTY_FIRE_CALCULATION = "∞"

@Composable
fun FinanceScreen(
    viewModel: FinanceViewModel,

    onAddExpense: () -> Unit,
    onExpenseClick: (Expense) -> Unit,

    onAddInvestment: () -> Unit,
    onInvestmentClick: (Investment) -> Unit,

    onAddSaving: () -> Unit,
    onSavingClick: (Saving) -> Unit,

    onAddBudget: () -> Unit,
    onBudgetClick: (Budget) -> Unit,

    navigator: Navigator,
    modifier: Modifier = Modifier,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FinanceScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,

        onExpenseClick = onExpenseClick,
        onInvestmentClick = onInvestmentClick,
        onSavingClick = onSavingClick,
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

    onExpenseClick: (Expense) -> Unit,
    onInvestmentClick: (Investment) -> Unit,
    onSavingClick: (Saving) -> Unit,
    onBudgetClick: (Budget) -> Unit,

    navigator: Navigator,
    modifier: Modifier = Modifier
) {

    NavigationPagerScreen(
        uiState = uiState,

        route = FinanceRoute,
        onSearchQueryChanged = { query -> onIntent(FinanceIntent.SearchQueryChanged(query)) },
        onFabActionClick = { id -> onIntent(FinanceIntent.FabActionClick(id)) },
        navigator = navigator,

        withTopAppBarActions = uiState.expensesUiState.expenses.isNotEmpty() || uiState.investments.isNotEmpty(),
        topAppBarActionsContent = { page ->
            // todo: make it when statement

            if (page == FinancePage.Expenses) {
                FilterMenu(
                    items = uiState.expensesUiState.filteringUiInfo.filterMenuItems,
                    onFilter = { onIntent(FinanceIntent.SetExpensesFiltering(it)) },
                )

                SortMenu(
                    items = uiState.expensesUiState.filteringUiInfo.sortMenuItems,
                    onFilter = { onIntent(FinanceIntent.SetExpensesSorting(it)) },
                )
            }

            if (page == FinancePage.Investments) {
                // todo
            }

            if (page == FinancePage.Savings) {
                // todo
            }

            if (page == FinancePage.Budgets) {
                // todo
            }
        },

        content = { page, listState, onListStateChanged ->

            // todo: consider extracting listState/onListStateChanged code here

//            // todo: move to each screen
//            LaunchedEffect(Unit) {
//                onListStateChanged(listState)
//            }
//
//            val screenName = stringResource(R.string.finance)

            when (page) {
                FinancePage.Dashboard -> DashboardPage(
                    dashboardBudget = uiState.dashboardBudget,

                    listState = listState,
                    onListStateChanged = onListStateChanged,
                )

                FinancePage.Expenses -> ExpensesPage(
                    uiState = uiState.expensesUiState,
                    onAddExpense = {
                        onIntent(
                            FinanceIntent.FabActionClick(
                                FinanceFabActionId.Expense
                            )
                        )
                    },
                    onExpenseClick = onExpenseClick,

                    listState = listState,
                    onListStateChanged = onListStateChanged,
                )
//
//                FinancePage.Investments -> itemInvestments(
//                    investments = uiState.investments,
//                    investmentsFilteringUiInfo = uiState.investmentsFilteringUiInfo,
//                    onAddInvestment = {
//                        onIntent(
//                            FinanceIntent.FabActionClick(
//                                FinanceFabActionId.Investment
//                            )
//                        )
//                    },
//                    onSeeAllInvestments = onSeeAllInvestments,
//                    onInvestmentClick = onInvestmentClick,
//                )
//
//                FinancePage.Savings -> itemSavings(
//                    savings = uiState.savings,
//                    savingsFilteringUiInfo = uiState.savingsFilteringUiInfo,
//                    onAddSaving = {
//                        onIntent(
//                            FinanceIntent.FabActionClick(
//                                FinanceFabActionId.Saving
//                            )
//                        )
//                    },
//                    onSeeAllSavings = onSeeAllSavings,
//                    onSavingClick = onSavingClick,
//                )
//
//                FinancePage.Budgets -> itemBudgets(
//                    budgets = uiState.budgets,
//                    budgetsFilteringUiInfo = uiState.budgetsFilteringUiInfo,
//                    onAddBudget = {
//                        onIntent(
//                            FinanceIntent.FabActionClick(
//                                FinanceFabActionId.Budget
//                            )
//                        )
//                    },
//                    onSeeAllBudgets = onSeeAllBudgets,
//                    onBudgetClick = onBudgetClick,
//                )
//
//                FinancePage.FIRE -> itemFiRe(
//                    fire = uiState.fire,
//                    onWithdrawalRateChange = { onIntent(FinanceIntent.UpdateFireWithdrawalRate(it)) },
//                    onReturnOfInvestmentChange = { onIntent(FinanceIntent.UpdateFireReturnOfInvestment(it)) },
//                    onTaxRateChange = { onIntent(FinanceIntent.UpdateFireTaxRate(it)) },
//                )
            }
        },

        modifier = modifier
    )
}

@Composable
private fun DashboardPage(
    dashboardBudget: GroupedBudget?,

    listState: LazyListState,
    onListStateChanged: (LazyListState) -> Unit,
) {

    if (dashboardBudget == null) {
        return
    }

    CategoriesPage(
        budget = dashboardBudget,
        group = null,
        listState = listState,
        onListStateChanged = onListStateChanged,
    )
}

@Composable
private fun ExpensesPage(
    uiState: ExpensesUiState,
    onAddExpense: () -> Unit,
    onExpenseClick: (Expense) -> Unit,

    listState: LazyListState,
    onListStateChanged: (LazyListState) -> Unit,
) {

    LaunchedEffect(Unit) {
        onListStateChanged(listState)
    }

    when {
        uiState.expenses.isEmpty() -> {
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
                    onButtonClick = onAddExpense,
                )
            }
        }

        else -> {
            ExpensesScreen(
                uiState = uiState,
                onExpenseClick = onExpenseClick,
            )
        }
    }
}

private fun LazyListScope.itemInvestments(
    investments: List<Investment>,
    investmentsFilteringUiInfo: InvestmentsFilteringUiInfo,
    onAddInvestment: () -> Unit,
    onSeeAllInvestments: () -> Unit,
    onInvestmentClick: (Investment) -> Unit,
) {
    when {
        investments.isEmpty() -> {
            item {
                EmptyStateCard(
                    iconRes = investmentsFilteringUiInfo.emptyIconRes,
                    label = investmentsFilteringUiInfo.emptyLabel,
                    message = investmentsFilteringUiInfo.emptyMessage,
                    buttonLabel = investmentsFilteringUiInfo.emptyButtonLabel,
                    onButtonClick = onAddInvestment,
                )
            }
        }

        else -> {
            item {
                ItemHeaderWithSeeAll(
                    title = stringResource(investmentsFilteringUiInfo.currentFilteringLabel),
                    onSeeAllClick = onSeeAllInvestments,
                )
            }

            items(
                items = investments,
                key = { investment -> investment.id },
            ) { investment ->
                InvestmentItem(
                    investment = investment,
                    onInvestmentClick = onInvestmentClick
                )
            }
        }
    }
}

private fun LazyListScope.itemSavings(
    savings: List<Saving>,
    savingsFilteringUiInfo: SavingsFilteringUiInfo,
    onAddSaving: () -> Unit,
    onSeeAllSavings: () -> Unit,
    onSavingClick: (Saving) -> Unit,
) {
    when {
        savings.isEmpty() -> {
            item {
                EmptyStateCard(
                    iconRes = savingsFilteringUiInfo.emptyIconRes,
                    label = savingsFilteringUiInfo.emptyLabel,
                    message = savingsFilteringUiInfo.emptyMessage,
                    buttonLabel = savingsFilteringUiInfo.emptyButtonLabel,
                    onButtonClick = onAddSaving,
                )
            }
        }

        else -> {
            item {
                ItemHeaderWithSeeAll(
                    title = stringResource(savingsFilteringUiInfo.currentFilteringLabel),
                    onSeeAllClick = onSeeAllSavings,
                )
            }

            items(
                items = savings,
                key = { saving -> saving.id },
            ) { saving ->
                SavingItem(
                    saving = saving,
                    onSavingClick = onSavingClick
                )
            }
        }
    }
}

private fun LazyListScope.itemBudgets(
    budgets: List<Budget>,
    budgetsFilteringUiInfo: BudgetsFilteringUiInfo,
    onAddBudget: () -> Unit,
    onSeeAllBudgets: () -> Unit,
    onBudgetClick: (Budget) -> Unit,
) {
    when {
        budgets.isEmpty() -> {
            item {
                EmptyStateCard(
                    iconRes = budgetsFilteringUiInfo.emptyIconRes,
                    label = budgetsFilteringUiInfo.emptyLabel,
                    message = budgetsFilteringUiInfo.emptyMessage,
                    buttonLabel = budgetsFilteringUiInfo.emptyButtonLabel,
                    onButtonClick = onAddBudget,
                )
            }
        }

        else -> {
            item {
                ItemHeaderWithSeeAll(
                    title = stringResource(budgetsFilteringUiInfo.currentFilteringLabel),
                    onSeeAllClick = onSeeAllBudgets,
                )
            }

            items(
                items = budgets,
                key = { budget -> budget.id },
            ) { budget ->
                BudgetItem(
                    budget = budget,
                    onBudgetClick = onBudgetClick
                )
            }
        }
    }
}

private fun LazyListScope.itemFiRe(
    fire: FiRe?,
    onWithdrawalRateChange: (String) -> Unit,
    onReturnOfInvestmentChange: (String) -> Unit,
    onTaxRateChange: (String) -> Unit,
) {

    if (fire == null) {
        return
    }

    item {
        ScreenTitleWithInfo(
            title = stringResource(R.string.fire_title, fire.selection),
            dialogTitle = stringResource(R.string.how_does_it_work),
            dialogText = StringBuilder().apply {
                appendLine(stringResource(R.string.fire_info))
                appendLine()

                appendLine(stringResource(R.string.fire_accumulated_stash, fire.accumulatedStash.format()))
                appendLine(stringResource(R.string.fire_annual_income, fire.annualIncome.format()))
                appendLine(stringResource(R.string.fire_annual_expenses, fire.annualExpenses.format()))
                appendLine(stringResource(R.string.fire_annual_investments, fire.annualInvestments.format()))

                appendLine()

                appendLine(stringResource(R.string.fire_savings_rate, fire.savingsRate?.format() ?: EMPTY_FIRE_CALCULATION))
                appendLine(stringResource(R.string.fire_target, fire.target?.format() ?: EMPTY_FIRE_CALCULATION))

                appendLine()

                appendLine(stringResource(R.string.fire_years_until_fire, fire.yearsUntilFiRe?.format() ?: EMPTY_FIRE_CALCULATION))
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
            var withdrawalRateValue by remember { mutableStateOf(TextFieldValue(fire.withdrawalRate?.toPlainString() ?: "")) }
            var returnOfInvestmentValue by remember { mutableStateOf(TextFieldValue(fire.returnOfInvestment?.toPlainString() ?: "")) }
            var taxRateValue by remember { mutableStateOf(TextFieldValue(fire.taxRate?.toPlainString() ?: "")) }

            TextField(
                modifier = Modifier.weight(1f),
                value = withdrawalRateValue,
                onValueChange = { onWithdrawalRateChange(it.text) },
                placeholder = { Text(stringResource(R.string.format_default, fire.withdrawalRatePlaceholder.toPlainString())) },
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
                placeholder = { Text(stringResource(R.string.format_default, fire.returnOfInvestmentPlaceholder.toPlainString())) },
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
                placeholder = { Text(stringResource(R.string.format_default, fire.taxRatePlaceholder.toPlainString())) },
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
            text = stringResource(R.string.fire_years_until_fire, fire.yearsUntilFiRe?.format() ?: EMPTY_FIRE_CALCULATION),
            fontWeight = FontWeight.Bold,
        )
    }
}

@AllDevicesPreview
@Preview
@Composable
private fun FinanceScreenPreview(
    initialPage: Int = 0,
    expensesInitialPage: Int = 0,
) {
    UrgatTheme {
        FinanceScreen(
            uiState = FinanceUiState(
                dashboardBudget = SampleData.SAMPLE_DASHBOARD_BUDGET,
                expensesUiState = ExpensesUiState(
                    expenses = SampleData.SAMPLE_EXPENSES_GROUPED,
                    categoriesMonthly = SampleData.SAMPLE_EXPENSES_CATEGORIES_MONTHLY,
                    categoriesYearly = SampleData.SAMPLE_EXPENSES_CATEGORIES_YEARLY,
                    categoriesAllTime = SampleData.SAMPLE_EXPENSES_CATEGORIES_ALL_TIME,
                    initialPage = expensesInitialPage,
                ),
                investments = SampleData.SAMPLE_INVESTMENTS.take(5),
                investmentsFilteringUiInfo = InvestmentsFilteringUiInfo(
                    currentFilteringLabel = R.string.recent_investments,
                ),
                savings = SampleData.SAMPLE_SAVINGS.take(5),
                savingsFilteringUiInfo = SavingsFilteringUiInfo(
                    currentFilteringLabel = R.string.recent_savings,
                ),
                budgets = SampleData.SAMPLE_BUDGETS.take(5),
                budgetsFilteringUiInfo = BudgetsFilteringUiInfo(
                    currentFilteringLabel = R.string.recent_budgets,
                ),
                isLoading = false,
                hasError = false,
                initialPage = initialPage,
                filteringUiInfo = FinanceFilteringUiInfo()
            ),
            onIntent = {},

            onExpenseClick = {},
            onInvestmentClick = {},
            onSavingClick = {},
            onBudgetClick = {},

            navigator = FakeNavigator(),
            modifier = Modifier
        )
    }
}

@AllDevicesPreview
@Preview
@Composable
private fun FinanceScreenExpensesTransactionsPreview() {
    FinanceScreenPreview(initialPage = 1, expensesInitialPage = 0)
}

@AllDevicesPreview
@Preview
@Composable
private fun FinanceScreenExpensesMonthlyPreview() {
    FinanceScreenPreview(initialPage = 1, expensesInitialPage = 1)
}

@AllDevicesPreview
@Preview
@Composable
private fun FinanceScreenExpensesYearlyPreview() {
    FinanceScreenPreview(initialPage = 1, expensesInitialPage = 2)
}

@AllDevicesPreview
@Preview
@Composable
private fun FinanceScreenExpensesAllTimePreview() {
    FinanceScreenPreview(initialPage = 1, expensesInitialPage = 3)
}

@AllDevicesPreview
@Preview
@Composable
private fun FinanceScreenInvestmentsPreview() {
    FinanceScreenPreview(initialPage = 2)
}

@AllDevicesPreview
@Composable
private fun FinanceScreenSavingsPreview() {
    FinanceScreenPreview(initialPage = 3)
}

@AllDevicesPreview
@Composable
private fun FinanceScreenBudgetsPreview() {
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
                investments = listOf(),
                investmentsFilteringUiInfo = InvestmentsFilteringUiInfo(),
                savings = listOf(),
                savingsFilteringUiInfo = SavingsFilteringUiInfo(),
                isLoading = false,
                hasError = false,
                filteringUiInfo = FinanceFilteringUiInfo()
            ),
            onIntent = {},

            onExpenseClick = {},
            onInvestmentClick = {},
            onSavingClick = {},
            onBudgetClick = {},

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
                investments = listOf(),
                investmentsFilteringUiInfo = InvestmentsFilteringUiInfo(),
                savings = listOf(),
                savingsFilteringUiInfo = SavingsFilteringUiInfo(),
                isLoading = true,
                hasError = false,
                filteringUiInfo = FinanceFilteringUiInfo()
            ),
            onIntent = {},

            onExpenseClick = {},
            onInvestmentClick = {},
            onSavingClick = {},
            onBudgetClick = {},

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
                investments = listOf(),
                investmentsFilteringUiInfo = InvestmentsFilteringUiInfo(),
                savings = listOf(),
                savingsFilteringUiInfo = SavingsFilteringUiInfo(),
                isLoading = false,
                hasError = true,
                filteringUiInfo = FinanceFilteringUiInfo()
            ),
            onIntent = {},

            onExpenseClick = {},
            onInvestmentClick = {},
            onSavingClick = {},
            onBudgetClick = {},

            navigator = FakeNavigator(),
            modifier = Modifier
        )
    }
}