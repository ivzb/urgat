package com.ivzb.urgat.ui.finance

import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.suites.PlayStoreScreenshotTest
import com.ivzb.urgat.ui.base.ScreenshotTest
import com.ivzb.urgat.ui.theme.UrgatTheme
import org.junit.Test
import org.junit.experimental.categories.Category

class FinanceScreenshotTest: ScreenshotTest() {

    @Test
    @Category(PlayStoreScreenshotTest::class)
    fun screenshotTest_FinanceScreen_sampleData() = screenshotTest(
        uiState = FinanceUiState(
            expenses = SampleData.SAMPLE_EXPENSES.take(3),
            investments = SampleData.SAMPLE_INVESTMENTS.take(3),
            isLoading = false,
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
                    navController = rememberNavController(),
                    modifier = Modifier,
                )
            }
        }

        screenshot()
    }

}