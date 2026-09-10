package com.ivzb.urgat.ui.investments

import androidx.compose.ui.Modifier
import com.ivzb.urgat.annotations.Screenshot
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.suites.PlayStoreScreenshotTest
import com.ivzb.urgat.ui.base.ScreenshotTest
import com.ivzb.urgat.ui.theme.UrgatTheme
import org.junit.Test
import org.junit.experimental.categories.Category

@Screenshot
class InvestmentsScreenshotTest: ScreenshotTest() {

    @Test
    fun screenshotTest_InvestmentsScreen_sampleData_yearly() =
        screenshotTest_InvestmentsScreen_sampleData(initialPage = 0)

    @Test
    @Category(PlayStoreScreenshotTest::class)
    fun screenshotTest_InvestmentsScreen_sampleData_allTime() =
        screenshotTest_InvestmentsScreen_sampleData(initialPage = 1)

    @Test
    fun screenshotTest_InvestmentsScreen_sampleData_assets() =
        screenshotTest_InvestmentsScreen_sampleData(initialPage = 2)

    private fun screenshotTest_InvestmentsScreen_sampleData(initialPage: Int) = screenshotTest(
        uiState = InvestmentsUiState(
            investments = SampleData.SAMPLE_INVESTMENTS_GROUPED,
            categoriesYearly = SampleData.SAMPLE_INVESTMENTS_CATEGORIES_YEARLY,
            assets = SampleData.SAMPLE_INVESTMENTS_ASSETS,
            categories = SampleData.SAMPLE_INVESTMENTS_CATEGORIES,
            netWorth = SampleData.SAMPLE_INVESTMENTS_NET_WORTH,
            averagesYearly = SampleData.SAMPLE_INVESTMENTS_AVERAGES_YEARLY,
            averageAllTime = SampleData.SAMPLE_INVESTMENTS_AVERAGE_ALL_TIME,

            isLoading = false,
            hasError = false,

            initialPage = initialPage,
        ),
    )

    @Test
    fun screenshotTest_InvestmentsScreen_emptyState() = screenshotTest(
        uiState = InvestmentsUiState(
            investments = listOf(),
            isLoading = false,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_InvestmentsScreen_loadingState() = screenshotTest(
        uiState = InvestmentsUiState(
            isLoading = true,
        ),
    )

    @Test
    fun screenshotTest_InvestmentsScreen_errorState() = screenshotTest(
        uiState = InvestmentsUiState(
            hasError = true,
        ),
    )

    private fun screenshotTest(uiState: InvestmentsUiState) {
        composeTestRule.setContent {
            UrgatTheme {
                InvestmentsScreen(
                    uiState = uiState,
                    onIntent = {},
                    onInvestmentClick = {},
                    onBack = {},
                    modifier = Modifier
                )
            }
        }

        screenshot()
    }

}