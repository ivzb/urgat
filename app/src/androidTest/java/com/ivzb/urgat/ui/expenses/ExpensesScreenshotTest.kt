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
    fun screenshotTest_ExpensesScreen_sampleData() = screenshotTest(
        uiState = ExpensesUiState(
            items = SampleData.SAMPLE_EXPENSES_GROUPED,
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