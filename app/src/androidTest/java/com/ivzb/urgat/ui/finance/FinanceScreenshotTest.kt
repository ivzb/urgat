package com.ivzb.urgat.ui.finance

import androidx.compose.ui.Modifier
import com.ivzb.urgat.annotations.Screenshot
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.navigation.FakeNavigator
import com.ivzb.urgat.suites.PlayStoreScreenshotTest
import com.ivzb.urgat.ui.base.ScreenshotTest
import com.ivzb.urgat.ui.budgets.BudgetsUiState
import com.ivzb.urgat.ui.expenses.ExpensesUiState
import com.ivzb.urgat.ui.investments.InvestmentsUiState
import com.ivzb.urgat.ui.savings.SavingsUiState
import com.ivzb.urgat.ui.theme.UrgatTheme
import com.ivzb.urgat.ui.trades.TradesUiState
import com.ivzb.urgat.ui.watchlist.WatchlistAssetsUiState
import org.junit.Test
import org.junit.experimental.categories.Category

@Screenshot
class FinanceScreenshotTest: ScreenshotTest() {

    @Test
    @Category(PlayStoreScreenshotTest::class)
    fun screenshotTest_FinanceScreen_sampleData_expenses() =
        screenshotTest_FinanceScreen_sampleData(initialPage = 0)

    @Test
    @Category(PlayStoreScreenshotTest::class)
    fun screenshotTest_FinanceScreen_sampleData_investments() =
        screenshotTest_FinanceScreen_sampleData(initialPage = 1)

    @Test
    fun screenshotTest_FinanceScreen_sampleData_trades() =
        screenshotTest_FinanceScreen_sampleData(initialPage = 2)

    @Test
    fun screenshotTest_FinanceScreen_sampleData_savings() =
        screenshotTest_FinanceScreen_sampleData(initialPage = 3)

    @Test
    fun screenshotTest_FinanceScreen_sampleData_budgets() =
        screenshotTest_FinanceScreen_sampleData(initialPage = 4)

    @Test
    fun screenshotTest_FinanceScreen_sampleData_report() =
        screenshotTest_FinanceScreen_sampleData(initialPage = 5)

    @Test
    fun screenshotTest_FinanceScreen_sampleData_watchlist_assets() =
        screenshotTest_FinanceScreen_sampleData(initialPage = 6)

    @Test
    fun screenshotTest_FinanceScreen_sampleData_fire() =
        screenshotTest_FinanceScreen_sampleData(initialPage = 7)

    private fun screenshotTest_FinanceScreen_sampleData(
        initialPage: Int,
    ) = screenshotTest(
        uiState = FinanceUiState(
            expensesUiState = ExpensesUiState(
                expenses = SampleData.SAMPLE_EXPENSES_GROUPED,
                categoriesMonthly = SampleData.SAMPLE_EXPENSES_CATEGORIES_MONTHLY,
                categoriesYearly = SampleData.SAMPLE_EXPENSES_CATEGORIES_YEARLY,
                categoriesAllTime = SampleData.SAMPLE_EXPENSES_CATEGORIES_ALL_TIME,
            ),
            investmentsUiState = InvestmentsUiState(
                investments = SampleData.SAMPLE_INVESTMENTS_GROUPED,
                categoriesYearly = SampleData.SAMPLE_INVESTMENTS_CATEGORIES_YEARLY,
                assets = SampleData.SAMPLE_INVESTMENTS_ASSETS,
                categories = SampleData.SAMPLE_INVESTMENTS_CATEGORIES,
                netWorth = SampleData.SAMPLE_INVESTMENTS_NET_WORTH,
            ),
            tradesUiState = TradesUiState(
                trades = SampleData.SAMPLE_TRADES_GROUPED,
            ),
            savingsUiState = SavingsUiState(
                savings = SampleData.SAMPLE_SAVINGS_GROUPED,
                categoriesMonthly = SampleData.SAMPLE_SAVINGS_CATEGORIES_MONTHLY,
                categoriesYearly = SampleData.SAMPLE_SAVINGS_CATEGORIES_YEARLY,
                categoriesAllTime = SampleData.SAMPLE_SAVINGS_CATEGORIES_ALL_TIME,
            ),
            budgetsUiState = BudgetsUiState(
                budgets = SampleData.SAMPLE_BUDGETS,
            ),
            watchlistAssetsUiState = WatchlistAssetsUiState(
                watchlistAssets = SampleData.SAMPLE_WATCHLIST_ASSETS,
            ),
            reportBudget = SampleData.SAMPLE_REPORT_BUDGET,
            fire = SampleData.SAMPLE_FIRE,
            isLoading = false,
            initialPage = initialPage,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_FinanceScreen_emptyState() = screenshotTest(
        uiState = FinanceUiState(
            isLoading = false,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_FinanceScreen_loadingState() = screenshotTest(
        uiState = FinanceUiState(
            isLoading = true,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_FinanceScreen_errorState() = screenshotTest(
        uiState = FinanceUiState(
            isLoading = false,
            hasError = true,
        ),
    )

    private fun screenshotTest(uiState: FinanceUiState) {
        composeTestRule.setContent {
            UrgatTheme {
                FinanceScreen(
                    uiState = uiState,
                    onIntent = {},

                    onExpenseClick = {},
                    onMoreExpenses = {},

                    onInvestmentClick = {},
                    onMoreInvestments = {},

                    onTradeClick = {},
                    onMoreTrades = {},

                    onSavingClick = {},
                    onMoreSavings = {},

                    onBudgetClick = {},
                    onMoreBudgets = {},

                    onMoreWatchlistAssets = {},
                    onWatchlistAssetClick = {},

                    navigator = FakeNavigator(),

                    modifier = Modifier,
                )
            }
        }

        screenshot()
    }

}