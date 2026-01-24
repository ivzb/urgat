package com.ivzb.urgat.ui.finance

import android.content.res.Configuration
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivzb.urgat.R
import com.ivzb.urgat.data.budget.Budget
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.saving.Saving
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.navigation.FakeNavigator
import com.ivzb.urgat.navigation.Navigator
import com.ivzb.urgat.ui.budgets.BudgetItem
import com.ivzb.urgat.ui.budgets.BudgetsFilteringUiInfo
import com.ivzb.urgat.ui.components.AllDevicesPreview
import com.ivzb.urgat.ui.components.EmptyStateCard
import com.ivzb.urgat.ui.components.ItemHeaderWithSeeAll
import com.ivzb.urgat.ui.components.screens.NavigationScreen
import com.ivzb.urgat.ui.expenses.ExpenseItem
import com.ivzb.urgat.ui.expenses.ExpensesFilteringUiInfo
import com.ivzb.urgat.ui.investments.InvestmentItem
import com.ivzb.urgat.ui.investments.InvestmentsFilteringUiInfo
import com.ivzb.urgat.ui.savings.SavingItem
import com.ivzb.urgat.ui.savings.SavingsFilteringUiInfo
import com.ivzb.urgat.ui.theme.UrgatTheme

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
            FinanceContent(
                expenses = uiState.expenses,
                expensesFilteringUiInfo = uiState.expensesFilteringUiInfo,
                onAddExpense = { onIntent(FinanceIntent.FabActionClick(FinanceFabActionId.Expense)) },
                onSeeAllExpenses = onSeeAllExpenses,
                onExpenseClick = onExpenseClick,

                investments = uiState.investments,
                investmentsFilteringUiInfo = uiState.investmentsFilteringUiInfo,
                onAddInvestment = { onIntent(FinanceIntent.FabActionClick(FinanceFabActionId.Investment)) },
                onSeeAllInvestments = onSeeAllInvestments,
                onInvestmentClick = onInvestmentClick,

                savings = uiState.savings,
                savingsFilteringUiInfo = uiState.savingsFilteringUiInfo,
                onAddSaving = { onIntent(FinanceIntent.FabActionClick(FinanceFabActionId.Saving)) },
                onSeeAllSavings = onSeeAllSavings,
                onSavingClick = onSavingClick,

                budgets = uiState.budgets,
                budgetsFilteringUiInfo = uiState.budgetsFilteringUiInfo,
                onAddBudget = { onIntent(FinanceIntent.FabActionClick(FinanceFabActionId.Budget)) },
                onSeeAllBudgets = onSeeAllBudgets,
                onBudgetClick = onBudgetClick,
            )
        },

        modifier = modifier
    )
}

@Composable
private fun FinanceContent(
    expenses: List<Expense>,
    expensesFilteringUiInfo: ExpensesFilteringUiInfo,
    onAddExpense: () -> Unit,
    onSeeAllExpenses: () -> Unit,
    onExpenseClick: (Expense) -> Unit,

    investments: List<Investment>,
    investmentsFilteringUiInfo: InvestmentsFilteringUiInfo,
    onAddInvestment: () -> Unit,
    onSeeAllInvestments: () -> Unit,
    onInvestmentClick: (Investment) -> Unit,

    savings: List<Saving>,
    savingsFilteringUiInfo: SavingsFilteringUiInfo,
    onAddSaving: () -> Unit,
    onSeeAllSavings: () -> Unit,
    onSavingClick: (Saving) -> Unit,

    budgets: List<Budget>,
    budgetsFilteringUiInfo: BudgetsFilteringUiInfo,
    onAddBudget: () -> Unit,
    onSeeAllBudgets: () -> Unit,
    onBudgetClick: (Budget) -> Unit,

    modifier: Modifier = Modifier
) {

    val configuration = LocalConfiguration.current

    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        FinanceContentLandscape(
            expenses = expenses,
            expensesFilteringUiInfo = expensesFilteringUiInfo,
            onAddExpense = onAddExpense,
            onSeeAllExpenses = onSeeAllExpenses,
            onExpenseClick = onExpenseClick,

            investments = investments,
            investmentsFilteringUiInfo = investmentsFilteringUiInfo,
            onAddInvestment = onAddInvestment,
            onSeeAllInvestments = onSeeAllInvestments,
            onInvestmentClick = onInvestmentClick,

            savings = savings,
            savingsFilteringUiInfo = savingsFilteringUiInfo,
            onAddSaving = onAddSaving,
            onSeeAllSavings = onSeeAllSavings,
            onSavingClick = onSavingClick,

            budgets = budgets,
            budgetsFilteringUiInfo = budgetsFilteringUiInfo,
            onAddBudget = onAddBudget,
            onSeeAllBudgets = onSeeAllBudgets,
            onBudgetClick = onBudgetClick,

            modifier = modifier,
        )
    } else {
        FinanceContentPortrait(
            expenses = expenses,
            expensesFilteringUiInfo = expensesFilteringUiInfo,
            onAddExpense = onAddExpense,
            onSeeAllExpenses = onSeeAllExpenses,
            onExpenseClick = onExpenseClick,

            investments = investments,
            investmentsFilteringUiInfo = investmentsFilteringUiInfo,
            onAddInvestment = onAddInvestment,
            onSeeAllInvestments = onSeeAllInvestments,
            onInvestmentClick = onInvestmentClick,

            savings = savings,
            savingsFilteringUiInfo = savingsFilteringUiInfo,
            onAddSaving = onAddSaving,
            onSeeAllSavings = onSeeAllSavings,
            onSavingClick = onSavingClick,

            budgets = budgets,
            budgetsFilteringUiInfo = budgetsFilteringUiInfo,
            onAddBudget = onAddBudget,
            onSeeAllBudgets = onSeeAllBudgets,
            onBudgetClick = onBudgetClick,

            modifier = modifier,
        )
    }
}

@Composable
private fun FinanceContentPortrait(
    expenses: List<Expense>,
    expensesFilteringUiInfo: ExpensesFilteringUiInfo,
    onAddExpense: () -> Unit,
    onSeeAllExpenses: () -> Unit,
    onExpenseClick: (Expense) -> Unit,

    investments: List<Investment>,
    investmentsFilteringUiInfo: InvestmentsFilteringUiInfo,
    onAddInvestment: () -> Unit,
    onSeeAllInvestments: () -> Unit,
    onInvestmentClick: (Investment) -> Unit,

    savings: List<Saving>,
    savingsFilteringUiInfo: SavingsFilteringUiInfo,
    onAddSaving: () -> Unit,
    onSeeAllSavings: () -> Unit,
    onSavingClick: (Saving) -> Unit,

    budgets: List<Budget>,
    budgetsFilteringUiInfo: BudgetsFilteringUiInfo,
    onAddBudget: () -> Unit,
    onSeeAllBudgets: () -> Unit,
    onBudgetClick: (Budget) -> Unit,

    modifier: Modifier = Modifier
) {
    val screenName = stringResource(R.string.finance)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .semantics { contentDescription = screenName },
        contentPadding = PaddingValues(
            horizontal = dimensionResource(R.dimen.content_padding_horizontal),
            vertical = dimensionResource(R.dimen.content_padding_vertical),
        ),
    ) {

        itemExpenses(
            expenses = expenses,
            expensesFilteringUiInfo = expensesFilteringUiInfo,
            onAddExpense = onAddExpense,
            onSeeAllExpenses = onSeeAllExpenses,
            onExpenseClick = onExpenseClick,
        )

        item {
            Spacer(
                modifier = Modifier
                    .height(dimensionResource(R.dimen.vertical_margin))
            )
        }

        itemInvestments(
            investments = investments,
            investmentsFilteringUiInfo = investmentsFilteringUiInfo,
            onAddInvestment = onAddInvestment,
            onSeeAllInvestments = onSeeAllInvestments,
            onInvestmentClick = onInvestmentClick,
        )

        item {
            Spacer(
                modifier = Modifier
                    .height(dimensionResource(R.dimen.vertical_margin))
            )
        }

        itemSavings(
            savings = savings,
            savingsFilteringUiInfo = savingsFilteringUiInfo,
            onAddSaving = onAddSaving,
            onSeeAllSavings = onSeeAllSavings,
            onSavingClick = onSavingClick,
        )

        item {
            Spacer(
                modifier = Modifier
                    .height(dimensionResource(R.dimen.vertical_margin))
            )
        }

        itemBudgets(
            budgets = budgets,
            budgetsFilteringUiInfo = budgetsFilteringUiInfo,
            onAddBudget = onAddBudget,
            onSeeAllBudgets = onSeeAllBudgets,
            onBudgetClick = onBudgetClick,
        )

        item {
            Spacer(
                modifier = Modifier
                    .height(dimensionResource(R.dimen.vertical_margin))
            )
        }
    }
}

@Composable
private fun FinanceContentLandscape(
    expenses: List<Expense>,
    expensesFilteringUiInfo: ExpensesFilteringUiInfo,
    onAddExpense: () -> Unit,
    onSeeAllExpenses: () -> Unit,
    onExpenseClick: (Expense) -> Unit,

    investments: List<Investment>,
    investmentsFilteringUiInfo: InvestmentsFilteringUiInfo,
    onAddInvestment: () -> Unit,
    onSeeAllInvestments: () -> Unit,
    onInvestmentClick: (Investment) -> Unit,

    savings: List<Saving>,
    savingsFilteringUiInfo: SavingsFilteringUiInfo,
    onAddSaving: () -> Unit,
    onSeeAllSavings: () -> Unit,
    onSavingClick: (Saving) -> Unit,

    budgets: List<Budget>,
    budgetsFilteringUiInfo: BudgetsFilteringUiInfo,
    onAddBudget: () -> Unit,
    onSeeAllBudgets: () -> Unit,
    onBudgetClick: (Budget) -> Unit,

    modifier: Modifier = Modifier
) {
    val screenName = stringResource(R.string.finance)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(R.dimen.content_padding_horizontal)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {

        LazyColumn(
            modifier = modifier
                .weight(1f)
                .semantics { contentDescription = screenName },
            contentPadding = PaddingValues(
                horizontal = dimensionResource(R.dimen.content_padding_horizontal),
                vertical = dimensionResource(R.dimen.content_padding_vertical),
            ),
        ) {

            itemExpenses(
                expenses = expenses,
                expensesFilteringUiInfo = expensesFilteringUiInfo,
                onAddExpense = onAddExpense,
                onSeeAllExpenses = onSeeAllExpenses,
                onExpenseClick = onExpenseClick
            )

            item {
                Spacer(
                    modifier = Modifier
                        .height(dimensionResource(R.dimen.vertical_margin))
                )
            }

            itemSavings(
                savings = savings,
                savingsFilteringUiInfo = savingsFilteringUiInfo,
                onAddSaving = onAddSaving,
                onSeeAllSavings = onSeeAllSavings,
                onSavingClick = onSavingClick,
            )

            item {
                Spacer(
                    modifier = Modifier
                        .height(dimensionResource(R.dimen.vertical_margin))
                )
            }

        }

        LazyColumn(
            modifier = modifier
                .weight(1f)
                .semantics { contentDescription = screenName },
            contentPadding = PaddingValues(
                horizontal = dimensionResource(R.dimen.content_padding_horizontal),
                vertical = dimensionResource(R.dimen.content_padding_vertical),
            ),
        ) {

            itemInvestments(
                investments = investments,
                investmentsFilteringUiInfo = investmentsFilteringUiInfo,
                onAddInvestment = onAddInvestment,
                onSeeAllInvestments = onSeeAllInvestments,
                onInvestmentClick = onInvestmentClick
            )

            item {
                Spacer(
                    modifier = Modifier
                        .height(dimensionResource(R.dimen.vertical_margin))
                )
            }

            itemBudgets(
                budgets = budgets,
                budgetsFilteringUiInfo = budgetsFilteringUiInfo,
                onAddBudget = onAddBudget,
                onSeeAllBudgets = onSeeAllBudgets,
                onBudgetClick = onBudgetClick,
            )

            item {
                Spacer(
                    modifier = Modifier
                        .height(dimensionResource(R.dimen.vertical_margin))
                )
            }
        }
    }
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

@AllDevicesPreview
@Composable
fun FinanceScreenPreview() {
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