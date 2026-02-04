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
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
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
import com.ivzb.urgat.ui.budgets.budgetCategoryPage
import com.ivzb.urgat.ui.components.AllDevicesPreview
import com.ivzb.urgat.ui.components.EmptyStateCard
import com.ivzb.urgat.ui.components.ItemHeaderWithSeeAll
import com.ivzb.urgat.ui.components.screens.NavigationScreen
import com.ivzb.urgat.ui.components.screens.PagerScreen
import com.ivzb.urgat.ui.expenses.ExpenseItem
import com.ivzb.urgat.ui.expenses.ExpensesFilteringUiInfo
import com.ivzb.urgat.ui.investments.InvestmentItem
import com.ivzb.urgat.ui.investments.InvestmentsFilteringUiInfo
import com.ivzb.urgat.ui.savings.SavingItem
import com.ivzb.urgat.ui.savings.SavingsFilteringUiInfo
import com.ivzb.urgat.ui.theme.UrgatTheme
import com.ivzb.urgat.util.format

@Composable
fun FinanceScreen(
    viewModel: FinanceViewModel,

    onAddExpense: () -> Unit,
    onSeeAllExpenses: () -> Unit,
    onExpenseClick: (Expense) -> Unit,

    onAddInvestment: () -> Unit,
    onSeeAllInvestments: () -> Unit,
    onInvestmentClick: (Investment) -> Unit,

    onAddSaving: () -> Unit,
    onSeeAllSavings: () -> Unit,
    onSavingClick: (Saving) -> Unit,

    onAddBudget: () -> Unit,
    onSeeAllBudgets: () -> Unit,
    onBudgetClick: (Budget) -> Unit,

    navigator: Navigator,
    modifier: Modifier = Modifier,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FinanceScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,

        onSeeAllExpenses = onSeeAllExpenses,
        onExpenseClick = onExpenseClick,

        onSeeAllInvestments = onSeeAllInvestments,
        onInvestmentClick = onInvestmentClick,

        onSeeAllSavings = onSeeAllSavings,
        onSavingClick = onSavingClick,

        onSeeAllBudgets = onSeeAllBudgets,
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

    val resources = LocalResources.current
    val itemsLimit = resources.getInteger(R.integer.items_limit)

    LaunchedEffect(itemsLimit) {
        viewModel.onIntent(FinanceIntent.ItemsLimitChanged(itemsLimit))
    }
}

@VisibleForTesting
@Composable
fun FinanceScreen(
    uiState: FinanceUiState,
    onIntent: (FinanceIntent) -> Unit,

    onSeeAllExpenses: () -> Unit,
    onExpenseClick: (Expense) -> Unit,

    onSeeAllInvestments: () -> Unit,
    onInvestmentClick: (Investment) -> Unit,

    onSeeAllSavings: () -> Unit,
    onSavingClick: (Saving) -> Unit,

    onSeeAllBudgets: () -> Unit,
    onBudgetClick: (Budget) -> Unit,

    navigator: Navigator,
    modifier: Modifier = Modifier
) {

    NavigationScreen(
        uiState = uiState,

        route = FinanceRoute,
        onSearchQueryChanged = { query -> onIntent(FinanceIntent.SearchQueryChanged(query)) },
        onFabActionClick = { id -> onIntent(FinanceIntent.FabActionClick(id)) },
        navigator = navigator,
        withTopAppBarActions = uiState.expenses.isNotEmpty() || uiState.investments.isNotEmpty(),

        content = {
            PagerScreen(
                uiState = uiState,
                content = { page, listState, onListStateChanged ->

                    LaunchedEffect(Unit) {
                        onListStateChanged(listState)
                    }

                    val screenName = stringResource(R.string.finance)

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .semantics { contentDescription = screenName },
                        contentPadding = PaddingValues(
                            horizontal = dimensionResource(R.dimen.content_padding_horizontal),
                            vertical = dimensionResource(R.dimen.content_padding_vertical),
                        ),
                        state = listState,
                    ) {

                        when (page) {
                            FinancePage.Dashboard -> itemDashboard(
                                dashboardBudget = uiState.dashboardBudget,
                            )

                            FinancePage.Expenses -> itemExpenses(
                                expenses = uiState.expenses,
                                expensesFilteringUiInfo = uiState.expensesFilteringUiInfo,
                                onAddExpense = {
                                    onIntent(
                                        FinanceIntent.FabActionClick(
                                            FinanceFabActionId.Expense
                                        )
                                    )
                                },
                                onSeeAllExpenses = onSeeAllExpenses,
                                onExpenseClick = onExpenseClick,
                            )

                            FinancePage.Investments -> itemInvestments(
                                investments = uiState.investments,
                                investmentsFilteringUiInfo = uiState.investmentsFilteringUiInfo,
                                onAddInvestment = {
                                    onIntent(
                                        FinanceIntent.FabActionClick(
                                            FinanceFabActionId.Investment
                                        )
                                    )
                                },
                                onSeeAllInvestments = onSeeAllInvestments,
                                onInvestmentClick = onInvestmentClick,
                            )

                            FinancePage.Savings -> itemSavings(
                                savings = uiState.savings,
                                savingsFilteringUiInfo = uiState.savingsFilteringUiInfo,
                                onAddSaving = {
                                    onIntent(
                                        FinanceIntent.FabActionClick(
                                            FinanceFabActionId.Saving
                                        )
                                    )
                                },
                                onSeeAllSavings = onSeeAllSavings,
                                onSavingClick = onSavingClick,
                            )

                            FinancePage.Budgets -> itemBudgets(
                                budgets = uiState.budgets,
                                budgetsFilteringUiInfo = uiState.budgetsFilteringUiInfo,
                                onAddBudget = {
                                    onIntent(
                                        FinanceIntent.FabActionClick(
                                            FinanceFabActionId.Budget
                                        )
                                    )
                                },
                                onSeeAllBudgets = onSeeAllBudgets,
                                onBudgetClick = onBudgetClick,
                            )

                            FinancePage.FIRE -> itemFiRe(
                                fire = uiState.fire,
                                onWithdrawalRateChange = { onIntent(FinanceIntent.UpdateFireWithdrawalRate(it)) },
                                onReturnOfInvestmentChange = { onIntent(FinanceIntent.UpdateFireReturnOfInvestment(it)) },
                                onTaxRateChange = { onIntent(FinanceIntent.UpdateFireTaxRate(it)) },
                            )
                        }
                    }
                }
            )
        },

        modifier = modifier
    )
}

private fun LazyListScope.itemDashboard(
    dashboardBudget: GroupedBudget?,
) {

    if (dashboardBudget == null) {
        return
    }

    budgetCategoryPage(
        budget = dashboardBudget,
    )
}

private fun LazyListScope.itemExpenses(
    expenses: List<Expense>,
    expensesFilteringUiInfo: ExpensesFilteringUiInfo,
    onAddExpense: () -> Unit,
    onSeeAllExpenses: () -> Unit,
    onExpenseClick: (Expense) -> Unit,
) {
    when {
        expenses.isEmpty() -> {
            item {
                EmptyStateCard(
                    iconRes = expensesFilteringUiInfo.emptyIconRes,
                    label = expensesFilteringUiInfo.emptyLabel,
                    message = expensesFilteringUiInfo.emptyMessage,
                    buttonLabel = expensesFilteringUiInfo.emptyButtonLabel,
                    onButtonClick = onAddExpense,
                )
            }
        }

        else -> {
            item {
                ItemHeaderWithSeeAll(
                    title = stringResource(expensesFilteringUiInfo.currentFilteringLabel),
                    onSeeAllClick = onSeeAllExpenses,
                )
            }

            items(
                items = expenses,
                key = { expense -> expense.id },
            ) { expense ->

                ExpenseItem(
                    expense = expense,
                    onExpenseClick = onExpenseClick
                )
            }
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
        Text(stringResource(R.string.fire_title, fire.selection))

        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.fire_margin)))

        Text(stringResource(R.string.fire_info))

        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.fire_margin)))

        Text(stringResource(R.string.fire_accumulated_stash, fire.accumulatedStash.format()))
        Text(stringResource(R.string.fire_annual_income, fire.annualIncome.format()))
        Text(stringResource(R.string.fire_annual_expenses, fire.annualExpenses.format()))
        Text(stringResource(R.string.fire_annual_investments, fire.annualInvestments.format()))

        fire.savingsRate?.let {
            Text(stringResource(R.string.fire_savings_rate, fire.savingsRate.format()))
        }

        fire.target?.let {
            Text(stringResource(R.string.fire_target, fire.target.format()))
        }

        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.fire_margin)))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.fire_withdrawal_rate),
            )

            Spacer(modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.fire_margin_text_fields)))

            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.fire_return_of_investment),
            )

            Spacer(modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.fire_margin_text_fields)))

            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.fire_tax_rate),
            )
        }

        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.fire_margin_text_fields)))

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
                maxLines = 1,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
            )

            Spacer(modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.fire_margin_text_fields)))

            TextField(
                modifier = Modifier.weight(1f),
                value = returnOfInvestmentValue,
                onValueChange = { onReturnOfInvestmentChange(it.text) },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
            )

            Spacer(modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.fire_margin_text_fields)))

            TextField(
                modifier = Modifier.weight(1f),
                value = taxRateValue,
                onValueChange = { onTaxRateChange(it.text) },
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

        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.fire_margin)))

        fire.yearsUntilFiRe?.let {
            Text(stringResource(R.string.fire_years_until_fire, fire.yearsUntilFiRe.format()))
        }
    }
}

@AllDevicesPreview
@Composable
private fun FinanceScreenPreview(initialPage: Int = 0) {
    UrgatTheme {
        FinanceScreen(
            uiState = FinanceUiState(
                expenses = SampleData.SAMPLE_EXPENSES.take(5),
                expensesFilteringUiInfo = ExpensesFilteringUiInfo(
                    currentFilteringLabel = R.string.recent_expenses,
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
            onSeeAllExpenses = {},
            onExpenseClick = {},
            onSeeAllInvestments = {},
            onInvestmentClick = {},
            onSeeAllSavings = {},
            onSavingClick = {},
            onSeeAllBudgets = {},
            onBudgetClick = {},
            navigator = FakeNavigator(),
            modifier = Modifier
        )
    }
}

@AllDevicesPreview
@Composable
private fun FinanceScreenExpensesPreview() {
    FinanceScreenPreview(initialPage = 1)
}

@AllDevicesPreview
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
                expenses = listOf(),
                expensesFilteringUiInfo = ExpensesFilteringUiInfo(),
                investments = listOf(),
                investmentsFilteringUiInfo = InvestmentsFilteringUiInfo(),
                savings = listOf(),
                savingsFilteringUiInfo = SavingsFilteringUiInfo(),
                isLoading = false,
                hasError = false,
                filteringUiInfo = FinanceFilteringUiInfo()
            ),
            onIntent = {},
            onSeeAllExpenses = {},
            onExpenseClick = {},
            onSeeAllInvestments = {},
            onInvestmentClick = {},
            onSeeAllSavings = {},
            onSavingClick = {},
            onSeeAllBudgets = {},
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
                expenses = listOf(),
                expensesFilteringUiInfo = ExpensesFilteringUiInfo(),
                investments = listOf(),
                investmentsFilteringUiInfo = InvestmentsFilteringUiInfo(),
                savings = listOf(),
                savingsFilteringUiInfo = SavingsFilteringUiInfo(),
                isLoading = true,
                hasError = false,
                filteringUiInfo = FinanceFilteringUiInfo()
            ),
            onIntent = {},
            onSeeAllExpenses = {},
            onExpenseClick = {},
            onSeeAllInvestments = {},
            onInvestmentClick = {},
            onSeeAllSavings = {},
            onSavingClick = {},
            onSeeAllBudgets = {},
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
                expenses = listOf(),
                expensesFilteringUiInfo = ExpensesFilteringUiInfo(),
                investments = listOf(),
                investmentsFilteringUiInfo = InvestmentsFilteringUiInfo(),
                savings = listOf(),
                savingsFilteringUiInfo = SavingsFilteringUiInfo(),
                isLoading = false,
                hasError = true,
                filteringUiInfo = FinanceFilteringUiInfo()
            ),
            onIntent = {},
            onSeeAllExpenses = {},
            onExpenseClick = {},
            onSeeAllInvestments = {},
            onInvestmentClick = {},
            onSeeAllSavings = {},
            onSavingClick = {},
            onSeeAllBudgets = {},
            onBudgetClick = {},
            navigator = FakeNavigator(),
            modifier = Modifier
        )
    }
}