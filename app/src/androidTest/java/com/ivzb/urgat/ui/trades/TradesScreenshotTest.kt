package com.ivzb.urgat.ui.trades

import androidx.compose.ui.Modifier
import com.ivzb.urgat.annotations.Screenshot
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.ui.base.ScreenshotTest
import com.ivzb.urgat.ui.theme.UrgatTheme
import org.junit.Test

@Screenshot
class TradesScreenshotTest: ScreenshotTest() {

    @Test
    fun screenshotTest_TradesScreen_sampleData_monthly() =
        screenshotTest_TradesScreen_sampleData(initialPage = 0)

    @Test
    fun screenshotTest_TradesScreen_sampleData_yearly() =
        screenshotTest_TradesScreen_sampleData(initialPage = 1)

    @Test
    fun screenshotTest_TradesScreen_sampleData_allTime() =
        screenshotTest_TradesScreen_sampleData(initialPage = 2)

    fun screenshotTest_TradesScreen_sampleData(initialPage: Int) = screenshotTest(
        uiState = TradesUiState(
            trades = SampleData.SAMPLE_TRADES_GROUPED,
            categoriesMonthly = SampleData.SAMPLE_TRADES_CATEGORIES_MONTHLY,
            categoriesYearly = SampleData.SAMPLE_TRADES_CATEGORIES_YEARLY,
            categoriesAllTime = SampleData.SAMPLE_TRADES_CATEGORIES_ALL_TIME,
            isLoading = false,
            hasError = false,
            initialPage = initialPage,
        ),
    )

    @Test
    fun screenshotTest_TradesScreen_emptyState() = screenshotTest(
        uiState = TradesUiState(
            trades = listOf(),
            categoriesMonthly = listOf(),
            categoriesYearly = listOf(),
            categoriesAllTime = listOf(),
            isLoading = false,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_TradesScreen_loadingState() = screenshotTest(
        uiState = TradesUiState(
            trades = listOf(),
            categoriesMonthly = listOf(),
            categoriesYearly = listOf(),
            categoriesAllTime = listOf(),
            isLoading = true,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_TradesScreen_errorState() = screenshotTest(
        uiState = TradesUiState(
            trades = listOf(),
            categoriesMonthly = listOf(),
            categoriesYearly = listOf(),
            categoriesAllTime = listOf(),
            isLoading = false,
            hasError = true,
        ),
    )

    private fun screenshotTest(uiState: TradesUiState) {
        composeTestRule.setContent {
            UrgatTheme {
                TradesScreen(
                    uiState = uiState,
                    onIntent = {},
                    onTradeClick = {},
                    onBack = {},
                    modifier = Modifier
                )
            }
        }

        screenshot()
    }

}

