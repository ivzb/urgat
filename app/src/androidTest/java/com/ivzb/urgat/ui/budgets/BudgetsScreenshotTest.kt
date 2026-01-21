package com.ivzb.urgat.ui.budgets

import androidx.compose.ui.Modifier
import com.ivzb.urgat.annotations.Screenshot
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.ui.base.ScreenshotTest
import com.ivzb.urgat.ui.theme.UrgatTheme
import org.junit.Test

@Screenshot
class BudgetsScreenshotTest: ScreenshotTest() {

    @Test
    fun screenshotTest_BudgetsScreen_sampleData_transactions() =
        screenshotTest_BudgetsScreen_sampleData(initialPage = 0)

    @Test
    fun screenshotTest_BudgetsScreen_sampleData_monthly() =
        screenshotTest_BudgetsScreen_sampleData(initialPage = 1)

    @Test
    fun screenshotTest_BudgetsScreen_sampleData_yearly() =
        screenshotTest_BudgetsScreen_sampleData(initialPage = 2)

    private fun screenshotTest_BudgetsScreen_sampleData(initialPage: Int) = screenshotTest(
        uiState = BudgetsUiState(
            budgets = SampleData.SAMPLE_BUDGETS,

            isLoading = false,
            hasError = false,

            initialPage = initialPage,
        ),
    )

    @Test
    fun screenshotTest_BudgetsScreen_emptyState() = screenshotTest(
        uiState = BudgetsUiState(
            budgets = listOf(),
            isLoading = false,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_BudgetsScreen_loadingState() = screenshotTest(
        uiState = BudgetsUiState(
            isLoading = true,
        ),
    )

    @Test
    fun screenshotTest_BudgetsScreen_errorState() = screenshotTest(
        uiState = BudgetsUiState(
            hasError = true,
        ),
    )

    private fun screenshotTest(uiState: BudgetsUiState) {
        composeTestRule.setContent {
            UrgatTheme {
                BudgetsScreen(
                    uiState = uiState,
                    onIntent = {},
                    onAddBudget = {},
                    onBudgetClick = {},
                    onBack = {},
                    modifier = Modifier
                )
            }
        }

        screenshot()
    }

}