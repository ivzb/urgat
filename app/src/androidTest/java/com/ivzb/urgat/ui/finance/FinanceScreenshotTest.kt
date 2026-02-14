package com.ivzb.urgat.ui.finance

import androidx.compose.ui.Modifier
import com.ivzb.urgat.R
import com.ivzb.urgat.annotations.Screenshot
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.navigation.FakeNavigator
import com.ivzb.urgat.suites.PlayStoreScreenshotTest
import com.ivzb.urgat.ui.base.ScreenshotTest
import com.ivzb.urgat.ui.budgets.BudgetsFilteringUiInfo
import com.ivzb.urgat.ui.expenses.ExpensesFilteringUiInfo
import com.ivzb.urgat.ui.expenses.ExpensesUiState
import com.ivzb.urgat.ui.investments.InvestmentsUiState
import com.ivzb.urgat.ui.savings.SavingsFilteringUiInfo
import com.ivzb.urgat.ui.theme.UrgatTheme
import org.junit.Test
import org.junit.experimental.categories.Category

@Screenshot
class FinanceScreenshotTest: ScreenshotTest() {

    @Test
    @Category(PlayStoreScreenshotTest::class)
    fun screenshotTest_FinanceScreen_sampleData_expenses_transactions() =
        screenshotTest_FinanceScreen_sampleData(initialPage = 0, expensesInitialPage = 0)

    @Test
    @Category(PlayStoreScreenshotTest::class)
    fun screenshotTest_FinanceScreen_sampleData_expenses_monthly() =
        screenshotTest_FinanceScreen_sampleData(initialPage = 0, expensesInitialPage = 1)

    @Test
    fun screenshotTest_FinanceScreen_sampleData_expenses_yearly() =
        screenshotTest_FinanceScreen_sampleData(initialPage = 0, expensesInitialPage = 2)

    @Test
    fun screenshotTest_FinanceScreen_sampleData_expenses_all_time() =
        screenshotTest_FinanceScreen_sampleData(initialPage = 0, expensesInitialPage = 3)

    @Test
    @Category(PlayStoreScreenshotTest::class)
    fun screenshotTest_FinanceScreen_sampleData_investments_transactions() =
        screenshotTest_FinanceScreen_sampleData(initialPage = 1, investmentsInitialPage = 0)

    @Test
    fun screenshotTest_FinanceScreen_sampleData_investments_yearly() =
        screenshotTest_FinanceScreen_sampleData(initialPage = 1, investmentsInitialPage = 1)

    @Test
    @Category(PlayStoreScreenshotTest::class)
    fun screenshotTest_FinanceScreen_sampleData_investments_all_time() =
        screenshotTest_FinanceScreen_sampleData(initialPage = 1, investmentsInitialPage = 2)

    @Test
    fun screenshotTest_FinanceScreen_sampleData_investments_assets() =
        screenshotTest_FinanceScreen_sampleData(initialPage = 1, investmentsInitialPage = 3)

    @Test
    fun screenshotTest_FinanceScreen_sampleData_savings() =
        screenshotTest_FinanceScreen_sampleData(initialPage = 2)

    @Test
    fun screenshotTest_FinanceScreen_sampleData_budgets() =
        screenshotTest_FinanceScreen_sampleData(initialPage = 3)

    @Test
    fun screenshotTest_FinanceScreen_sampleData_report() =
        screenshotTest_FinanceScreen_sampleData(initialPage = 4)

    @Test
    fun screenshotTest_FinanceScreen_sampleData_fire() =
        screenshotTest_FinanceScreen_sampleData(initialPage = 5)

    private fun screenshotTest_FinanceScreen_sampleData(
        initialPage: Int,
        expensesInitialPage: Int = 0,
        investmentsInitialPage: Int = 0,
    ) = screenshotTest(
        uiState = FinanceUiState(
            expensesUiState = ExpensesUiState(
                expenses = SampleData.SAMPLE_EXPENSES_GROUPED,
                categoriesMonthly = SampleData.SAMPLE_EXPENSES_CATEGORIES_MONTHLY,
                categoriesYearly = SampleData.SAMPLE_EXPENSES_CATEGORIES_YEARLY,
                categoriesAllTime = SampleData.SAMPLE_EXPENSES_CATEGORIES_ALL_TIME,
                filteringUiInfo = ExpensesFilteringUiInfo(
                    currentFilteringLabel = R.string.recent_expenses,
                ),
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
            savings = SampleData.SAMPLE_SAVINGS.take(itemsLimit),
            savingsFilteringUiInfo = SavingsFilteringUiInfo(
                currentFilteringLabel = R.string.recent_savings,
            ),
            budgets = SampleData.SAMPLE_BUDGETS.take(itemsLimit),
            budgetsFilteringUiInfo = BudgetsFilteringUiInfo(
                currentFilteringLabel = R.string.recent_budgets,
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
                    onInvestmentClick = {},
                    onSavingClick = {},
                    onBudgetClick = {},

                    navigator = FakeNavigator(),

                    modifier = Modifier,
                )
            }
        }

        screenshot()
    }

}