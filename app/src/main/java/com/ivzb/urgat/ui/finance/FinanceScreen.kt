package com.ivzb.urgat.ui.finance

import android.content.res.Configuration
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ivzb.urgat.R
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.navigation.FinanceDestination
import com.ivzb.urgat.ui.components.EmptyStateCard
import com.ivzb.urgat.ui.components.EmptyStateScreen
import com.ivzb.urgat.ui.components.Header
import com.ivzb.urgat.ui.components.ItemHeaderWithSeeAll
import com.ivzb.urgat.ui.components.LandscapePreview
import com.ivzb.urgat.ui.components.LoadingState
import com.ivzb.urgat.ui.components.MenuScaffold
import com.ivzb.urgat.ui.expenses.ExpenseItem
import com.ivzb.urgat.ui.expenses.ExpensesFilteringUiInfo
import com.ivzb.urgat.ui.investments.InvestmentItem
import com.ivzb.urgat.ui.investments.InvestmentsFilteringUiInfo
import com.ivzb.urgat.ui.theme.UrgatTheme

@Composable
fun FinanceScreen(
    onAddExpense: () -> Unit,
    onSeeAllExpenses: () -> Unit,
    onExpenseClick: (Expense) -> Unit,

    onAddInvestment: () -> Unit,
    onSeeAllInvestments: () -> Unit,
    onInvestmentClick: (Investment) -> Unit,

    navController: NavHostController,

    modifier: Modifier = Modifier,
    viewModel: FinanceViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FinanceScreenScaffold(
        uiState = uiState,
        onIntent = viewModel::onIntent,

        onSeeAllExpenses = onSeeAllExpenses,
        onExpenseClick = onExpenseClick,

        onSeeAllInvestments = onSeeAllInvestments,
        onInvestmentClick = onInvestmentClick,

        navController = navController,
        modifier = modifier
    )

    LaunchedEffect(uiState.uiEvent) {
        uiState.uiEvent?.let { event ->
            when (event) {
                is FinanceUiEvent.AddExpense -> onAddExpense()
                is FinanceUiEvent.AddInvestment -> onAddInvestment()
            }

            viewModel.onIntent(FinanceIntent.EventConsumed)
        }
    }
}

@Composable
private fun FinanceScreenScaffold(
    uiState: FinanceUiState,
    onIntent: (FinanceIntent) -> Unit,

    onSeeAllExpenses: () -> Unit,
    onExpenseClick: (Expense) -> Unit,

    onSeeAllInvestments: () -> Unit,
    onInvestmentClick: (Investment) -> Unit,

    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    MenuScaffold(
        selectedDestination = FinanceDestination,
        onSearchQueryChanged = { query -> onIntent(FinanceIntent.SearchQueryChanged(query)) },
        navController = navController,
        withTopAppBarActions = uiState.expenses.isNotEmpty() || uiState.investments.isNotEmpty(),
        modifier = modifier
    ) { listState ->

        when {
            uiState.isLoading -> {
                LoadingState(
                    uiState.filteringUiInfo.loadingLabel
                )
            }

            uiState.hasError -> {
                EmptyStateScreen(
                    label = uiState.filteringUiInfo.errorLabel,
                    iconRes = uiState.filteringUiInfo.errorIconRes,
                )
            }

            else -> {
                FinanceContent(
                    headerItems = uiState.headerItems,
                    onHeaderItemClick = { id -> onIntent(FinanceIntent.HeaderItemClick(id)) },

                    expenses = uiState.expenses,
                    expensesFilteringUiInfo = uiState.expensesFilteringUiInfo,
                    onSeeAllExpenses = onSeeAllExpenses,
                    onExpenseClick = onExpenseClick,

                    investments = uiState.investments,
                    investmentsFilteringUiInfo = uiState.investmentsFilteringUiInfo,
                    onSeeAllInvestments = onSeeAllInvestments,
                    onInvestmentClick = onInvestmentClick,

                    listState = listState,
                )
            }
        }

    }
}

@Composable
private fun FinanceContent(
    headerItems: List<FinanceHeaderItem>,
    onHeaderItemClick: (FinanceHeaderItemId) -> Unit,

    expenses: List<Expense>,
    expensesFilteringUiInfo: ExpensesFilteringUiInfo,
    onSeeAllExpenses: () -> Unit,
    onExpenseClick: (Expense) -> Unit,

    investments: List<Investment>,
    investmentsFilteringUiInfo: InvestmentsFilteringUiInfo,
    onSeeAllInvestments: () -> Unit,
    onInvestmentClick: (Investment) -> Unit,

    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    val screenName = stringResource(R.string.finance)
    val configuration = LocalConfiguration.current
    val orientation = configuration.orientation

    Row (
        modifier = Modifier.fillMaxSize()
    ) {

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Header(
                items = headerItems,
                onItemClick = { onHeaderItemClick(it.id) },
                orientation
            )
        }

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .semantics { contentDescription = screenName },
            contentPadding = PaddingValues(
                horizontal = dimensionResource(R.dimen.horizontal_margin),
                vertical = dimensionResource(R.dimen.vertical_margin)
            ),
            state = listState
        ) {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                item {
                    Header(
                        items = headerItems,
                        onItemClick = { onHeaderItemClick(it.id) },
                        orientation
                    )
                }
            }

            item {
                Spacer(
                    modifier = Modifier
                        .height(dimensionResource(R.dimen.vertical_margin))
                )
            }

            itemExpenses(
                expenses = expenses,
                expensesFilteringUiInfo = expensesFilteringUiInfo,
                onAddExpense = { onHeaderItemClick(FinanceHeaderItemId.Expense) },
                onSeeAllExpenses = onSeeAllExpenses,
                onExpenseClick = onExpenseClick
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
                onAddInvestment = { onHeaderItemClick(FinanceHeaderItemId.Investment) },
                onSeeAllInvestments = onSeeAllInvestments,
                onInvestmentClick = onInvestmentClick
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

            items(expenses) { expense ->
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

            items(investments) { investment ->
                InvestmentItem(
                    investment = investment,
                    onInvestmentClick = onInvestmentClick
                )
            }
        }
    }
}

@Preview
@LandscapePreview
@Composable
fun FinanceScreenScaffoldPreview() {
    UrgatTheme {
        FinanceScreenScaffold(
            uiState = FinanceUiState(
                headerItems = listOf(
                    FinanceHeaderItem(
                        FinanceHeaderItemId.Expense,
                        Icons.Filled.ShoppingCart,
                        R.string.add_expense
                    ),
                    FinanceHeaderItem(
                        FinanceHeaderItemId.Investment,
                        Icons.Filled.AttachMoney,
                        R.string.add_investment
                    ),
                ),
                expenses = SampleData.SAMPLE_EXPENSES.take(3),
                expensesFilteringUiInfo = ExpensesFilteringUiInfo(),
                investments = SampleData.SAMPLE_INVESTMENTS.take(3),
                investmentsFilteringUiInfo = InvestmentsFilteringUiInfo(),
                isLoading = false,
                hasError = false,
                filteringUiInfo = FinanceFilteringUiInfo()
            ),
            onIntent = {},
            onSeeAllExpenses = {},
            onExpenseClick = {},
            onSeeAllInvestments = {},
            onInvestmentClick = {},
            navController = rememberNavController(),
            modifier = Modifier
        )
    }
}

@Preview
@LandscapePreview
@Composable
fun FinanceScreenScaffoldEmptyPreview() {
    UrgatTheme {
        FinanceScreenScaffold(
            uiState = FinanceUiState(
                headerItems = listOf(
                    FinanceHeaderItem(
                        FinanceHeaderItemId.Expense,
                        Icons.Filled.ShoppingCart,
                        R.string.add_expense
                    ),
                    FinanceHeaderItem(
                        FinanceHeaderItemId.Investment,
                        Icons.Filled.AttachMoney,
                        R.string.add_investment
                    ),
                ),
                expenses = listOf(),
                expensesFilteringUiInfo = ExpensesFilteringUiInfo(),
                investments = listOf(),
                investmentsFilteringUiInfo = InvestmentsFilteringUiInfo(),
                isLoading = false,
                hasError = false,
                filteringUiInfo = FinanceFilteringUiInfo()
            ),
            onIntent = {},
            onSeeAllExpenses = {},
            onExpenseClick = {},
            onSeeAllInvestments = {},
            onInvestmentClick = {},
            navController = rememberNavController(),
            modifier = Modifier
        )
    }
}

@Preview
@LandscapePreview
@Composable
fun FinanceScreenScaffoldLoadingPreview() {
    UrgatTheme {
        FinanceScreenScaffold(
            uiState = FinanceUiState(
                headerItems = listOf(
                    FinanceHeaderItem(
                        FinanceHeaderItemId.Expense,
                        Icons.Filled.ShoppingCart,
                        R.string.add_expense
                    ),
                    FinanceHeaderItem(
                        FinanceHeaderItemId.Investment,
                        Icons.Filled.AttachMoney,
                        R.string.add_investment
                    ),
                ),
                expenses = listOf(),
                expensesFilteringUiInfo = ExpensesFilteringUiInfo(),
                investments = listOf(),
                investmentsFilteringUiInfo = InvestmentsFilteringUiInfo(),
                isLoading = true,
                hasError = false,
                filteringUiInfo = FinanceFilteringUiInfo()
            ),
            onIntent = {},
            onSeeAllExpenses = {},
            onExpenseClick = {},
            onSeeAllInvestments = {},
            onInvestmentClick = {},
            navController = rememberNavController(),
            modifier = Modifier
        )
    }
}

@Preview
@LandscapePreview
@Composable
fun FinanceScreenScaffoldErrorPreview() {
    UrgatTheme {
        FinanceScreenScaffold(
            uiState = FinanceUiState(
                headerItems = listOf(
                    FinanceHeaderItem(
                        FinanceHeaderItemId.Expense,
                        Icons.Filled.ShoppingCart,
                        R.string.add_expense
                    ),
                    FinanceHeaderItem(
                        FinanceHeaderItemId.Investment,
                        Icons.Filled.AttachMoney,
                        R.string.add_investment
                    ),
                ),
                expenses = listOf(),
                expensesFilteringUiInfo = ExpensesFilteringUiInfo(),
                investments = listOf(),
                investmentsFilteringUiInfo = InvestmentsFilteringUiInfo(),
                isLoading = false,
                hasError = true,
                filteringUiInfo = FinanceFilteringUiInfo()
            ),
            onIntent = {},
            onSeeAllExpenses = {},
            onExpenseClick = {},
            onSeeAllInvestments = {},
            onInvestmentClick = {},
            navController = rememberNavController(),
            modifier = Modifier
        )
    }
}


@Preview
@Composable
fun FinanceContentPreview() {
    UrgatTheme {
        FinanceContent(
            headerItems = listOf(
                FinanceHeaderItem(
                    FinanceHeaderItemId.Expense,
                    Icons.Filled.ShoppingCart,
                    R.string.add_expense
                ),
                FinanceHeaderItem(
                    FinanceHeaderItemId.Investment,
                    Icons.Filled.AttachMoney,
                    R.string.add_investment
                ),
            ),
            onHeaderItemClick = {},
            expenses = SampleData.SAMPLE_EXPENSES.take(3),
            expensesFilteringUiInfo = ExpensesFilteringUiInfo(),
            onSeeAllExpenses = {},
            onExpenseClick = {},
            investments = SampleData.SAMPLE_INVESTMENTS.take(3),
            investmentsFilteringUiInfo = InvestmentsFilteringUiInfo(),
            onSeeAllInvestments = {},
            onInvestmentClick = {},
            listState = rememberLazyListState()
        )
    }
}

@Preview
@Composable
fun ExpensesCardPreview() {
    UrgatTheme {
        LazyColumn {
            itemExpenses(
                expenses = SampleData.SAMPLE_EXPENSES.take(3),
                expensesFilteringUiInfo = ExpensesFilteringUiInfo(),
                onAddExpense = {},
                onSeeAllExpenses = {},
                onExpenseClick = {}
            )
        }
    }
}

@Preview
@Composable
fun InvestmentsCardPreview() {
    UrgatTheme {
        LazyColumn {
            itemInvestments(
                investments = SampleData.SAMPLE_INVESTMENTS.take(3),
                investmentsFilteringUiInfo = InvestmentsFilteringUiInfo(),
                onAddInvestment = {},
                onSeeAllInvestments = {},
                onInvestmentClick = {}
            )
        }
    }
}