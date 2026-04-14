package com.ivzb.urgat.ui.expenses

import androidx.compose.ui.Modifier
import com.ivzb.urgat.annotations.Screenshot
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.suites.PlayStoreScreenshotTest
import com.ivzb.urgat.ui.base.ScreenshotTest
import com.ivzb.urgat.ui.theme.UrgatTheme
import org.junit.Test
import org.junit.experimental.categories.Category

@Screenshot
class ExpensesScreenshotTest: ScreenshotTest() {

    @Test
    @Category(PlayStoreScreenshotTest::class)
    fun screenshotTest_ExpensesScreen_sampleData_monthly() =
        screenshotTest_ExpensesScreen_sampleData(initialPage = 0)

    @Test
    fun screenshotTest_ExpensesScreen_sampleData_yearly() =
        screenshotTest_ExpensesScreen_sampleData(initialPage = 1)

    @Test
    fun screenshotTest_ExpensesScreen_sampleData_allTime() =
        screenshotTest_ExpensesScreen_sampleData(initialPage = 2)

    fun screenshotTest_ExpensesScreen_sampleData(initialPage: Int) = screenshotTest(
        uiState = ExpensesUiState(
            expenses = SampleData.SAMPLE_EXPENSES_GROUPED,
            categoriesMonthly = SampleData.SAMPLE_EXPENSES_CATEGORIES_MONTHLY,
            categoriesYearly = SampleData.SAMPLE_EXPENSES_CATEGORIES_YEARLY,
            categoriesAllTime = SampleData.SAMPLE_EXPENSES_CATEGORIES_ALL_TIME,
            isLoading = false,
            hasError = false,
            initialPage = initialPage,
        ),
    )

    @Test
    fun screenshotTest_ExpensesScreen_emptyState() = screenshotTest(
        uiState = ExpensesUiState(
            expenses = listOf(),
            categoriesMonthly = listOf(),
            categoriesYearly = listOf(),
            categoriesAllTime = listOf(),
            isLoading = false,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_ExpensesScreen_loadingState() = screenshotTest(
        uiState = ExpensesUiState(
            expenses = listOf(),
            categoriesMonthly = listOf(),
            categoriesYearly = listOf(),
            categoriesAllTime = listOf(),
            isLoading = true,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_ExpensesScreen_errorState() = screenshotTest(
        uiState = ExpensesUiState(
            expenses = listOf(),
            categoriesMonthly = listOf(),
            categoriesYearly = listOf(),
            categoriesAllTime = listOf(),
            isLoading = false,
            hasError = true,
        ),
    )

    private fun screenshotTest(uiState: ExpensesUiState) {
        composeTestRule.setContent {
            UrgatTheme {
                ExpensesScreen(
                    uiState = uiState,
                    onIntent = {},
                    onExpenseClick = {},
                    onBack = {},
                    modifier = Modifier
                )
            }
        }

        screenshot()
    }

}