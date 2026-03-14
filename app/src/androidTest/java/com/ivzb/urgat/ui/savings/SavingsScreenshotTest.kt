package com.ivzb.urgat.ui.savings

import androidx.compose.ui.Modifier
import com.ivzb.urgat.annotations.Screenshot
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.suites.PlayStoreScreenshotTest
import com.ivzb.urgat.ui.base.ScreenshotTest
import com.ivzb.urgat.ui.theme.UrgatTheme
import org.junit.Test
import org.junit.experimental.categories.Category

@Screenshot
class SavingsScreenshotTest: ScreenshotTest() {

    @Test
    fun screenshotTest_SavingsScreen_sampleData_transactions() =
        screenshotTest_SavingsScreen_sampleData(initialPage = 0)

    @Test
    @Category(PlayStoreScreenshotTest::class)
    fun screenshotTest_SavingsScreen_sampleData_monthly() =
        screenshotTest_SavingsScreen_sampleData(initialPage = 1)

    @Test
    fun screenshotTest_SavingsScreen_sampleData_yearly() =
        screenshotTest_SavingsScreen_sampleData(initialPage = 2)

    @Test
    fun screenshotTest_SavingsScreen_sampleData_allTime() =
        screenshotTest_SavingsScreen_sampleData(initialPage = 3)

    fun screenshotTest_SavingsScreen_sampleData(initialPage: Int) = screenshotTest(
        uiState = SavingsUiState(
            savings = SampleData.SAMPLE_SAVINGS_GROUPED,
            categoriesMonthly = SampleData.SAMPLE_SAVINGS_CATEGORIES_MONTHLY,
            categoriesYearly = SampleData.SAMPLE_SAVINGS_CATEGORIES_YEARLY,
            categoriesAllTime = SampleData.SAMPLE_SAVINGS_CATEGORIES_ALL_TIME,
            isLoading = false,
            hasError = false,
            initialPage = initialPage,
        ),
    )

    @Test
    fun screenshotTest_SavingsScreen_emptyState() = screenshotTest(
        uiState = SavingsUiState(
            savings = listOf(),
            categoriesMonthly = listOf(),
            categoriesYearly = listOf(),
            categoriesAllTime = listOf(),
            isLoading = false,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_SavingsScreen_loadingState() = screenshotTest(
        uiState = SavingsUiState(
            savings = listOf(),
            categoriesMonthly = listOf(),
            categoriesYearly = listOf(),
            categoriesAllTime = listOf(),
            isLoading = true,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_SavingsScreen_errorState() = screenshotTest(
        uiState = SavingsUiState(
            savings = listOf(),
            categoriesMonthly = listOf(),
            categoriesYearly = listOf(),
            categoriesAllTime = listOf(),
            isLoading = false,
            hasError = true,
        ),
    )

    private fun screenshotTest(uiState: SavingsUiState) {
        composeTestRule.setContent {
            UrgatTheme {
                SavingsScreen(
                    uiState = uiState,
                    onIntent = {},
                    onSavingClick = {},
                    onBack = {},
                    modifier = Modifier
                )
            }
        }

        screenshot()
    }

}