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
import com.ivzb.urgat.ui.investments.InvestmentsFilteringUiInfo
import com.ivzb.urgat.ui.savings.SavingsFilteringUiInfo
import com.ivzb.urgat.ui.theme.UrgatTheme
import org.junit.Test
import org.junit.experimental.categories.Category

@Screenshot
class FinanceScreenshotTest: ScreenshotTest() {

    @Test
    @Category(PlayStoreScreenshotTest::class)
    fun screenshotTest_FinanceScreen_sampleData_expenses() =
        screenshotTest_FinanceScreen_sampleData(initialPage = 0)

    @Test
    fun screenshotTest_FinanceScreen_sampleData_investments() =
        screenshotTest_FinanceScreen_sampleData(initialPage = 1)

    @Test
    fun screenshotTest_FinanceScreen_sampleData_savings() =
        screenshotTest_FinanceScreen_sampleData(initialPage = 2)

    @Test
    fun screenshotTest_FinanceScreen_sampleData_budgets() =
        screenshotTest_FinanceScreen_sampleData(initialPage = 3)

    private fun screenshotTest_FinanceScreen_sampleData(initialPage: Int) = screenshotTest(
        uiState = FinanceUiState(
            expenses = SampleData.SAMPLE_EXPENSES.take(itemsLimit),
            expensesFilteringUiInfo = ExpensesFilteringUiInfo(
                currentFilteringLabel = R.string.recent_expenses,
            ),
            investments = SampleData.SAMPLE_INVESTMENTS.take(itemsLimit),
            investmentsFilteringUiInfo = InvestmentsFilteringUiInfo(
                currentFilteringLabel = R.string.recent_investments,
            ),
            savings = SampleData.SAMPLE_SAVINGS.take(itemsLimit),
            savingsFilteringUiInfo = SavingsFilteringUiInfo(
                currentFilteringLabel = R.string.recent_savings,
            ),
            budgets = SampleData.SAMPLE_BUDGETS.take(itemsLimit),
            budgetsFilteringUiInfo = BudgetsFilteringUiInfo(
                currentFilteringLabel = R.string.recent_budgets,
            ),
            isLoading = false,
            initialPage = initialPage,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_FinanceScreen_emptyState() = screenshotTest(
        uiState = FinanceUiState(
            expenses = listOf(),
            investments = listOf(),
            isLoading = false,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_FinanceScreen_loadingState() = screenshotTest(
        uiState = FinanceUiState(
            expenses = listOf(),
            investments = listOf(),
            isLoading = true,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_FinanceScreen_errorState() = screenshotTest(
        uiState = FinanceUiState(
            expenses = listOf(),
            investments = listOf(),
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

                    onSeeAllExpenses = {},
                    onExpenseClick = {},

                    onSeeAllInvestments = {},
                    onInvestmentClick = {},

                    onSeeAllSavings = {},
                    onSavingClick = {},

                    onSeeAllBudgets = {},
                    onBudgetClick = {},

                    navigator = FakeNavigator(),

                    modifier = Modifier,
                )
            }
        }

        screenshot()
    }

}