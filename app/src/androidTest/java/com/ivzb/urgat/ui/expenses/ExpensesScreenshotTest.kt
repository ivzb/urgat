package com.ivzb.urgat.ui.expenses

import androidx.compose.ui.Modifier
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.ui.base.ScreenshotTest
import com.ivzb.urgat.ui.theme.UrgatTheme
import org.junit.Test

class ExpensesScreenshotTest: ScreenshotTest() {

    @Test
    fun screenshotTest_ExpensesScreen_sampleData() = screenshotTest(
        uiState = ExpensesUiState(
            items = SampleData.SAMPLE_EXPENSES,
            isLoading = false,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_ExpensesScreen_emptyState() = screenshotTest(
        uiState = ExpensesUiState(
            items = listOf(),
            isLoading = false,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_ExpensesScreen_loadingState() = screenshotTest(
        uiState = ExpensesUiState(
            items = listOf(),
            isLoading = true,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_ExpensesScreen_errorState() = screenshotTest(
        uiState = ExpensesUiState(
            items = listOf(),
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
                    onAddExpense = {},
                    onExpenseClick = {},
                    onBack = {},
                    modifier = Modifier
                )
            }
        }

        screenshot()
    }

}