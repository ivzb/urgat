package com.ivzb.urgat.ui.investments

import androidx.compose.ui.Modifier
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.suites.PlayStoreScreenshotTest
import com.ivzb.urgat.ui.base.ScreenshotTest
import com.ivzb.urgat.ui.theme.UrgatTheme
import org.junit.Test
import org.junit.experimental.categories.Category

class InvestmentsScreenshotTest: ScreenshotTest() {

    @Test
    @Category(PlayStoreScreenshotTest::class)
    fun screenshotTest_InvestmentsScreen_sampleData() = screenshotTest(
        uiState = InvestmentsUiState(
            items = SampleData.SAMPLE_INVESTMENTS,
            isLoading = false,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_InvestmentsScreen_emptyState() = screenshotTest(
        uiState = InvestmentsUiState(
            items = listOf(),
            isLoading = false,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_InvestmentsScreen_loadingState() = screenshotTest(
        uiState = InvestmentsUiState(
            items = listOf(),
            isLoading = true,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_InvestmentsScreen_errorState() = screenshotTest(
        uiState = InvestmentsUiState(
            items = listOf(),
            isLoading = false,
            hasError = true,
        ),
    )

    private fun screenshotTest(uiState: InvestmentsUiState) {
        composeTestRule.setContent {
            UrgatTheme {
                InvestmentsScreen(
                    uiState = uiState,
                    onIntent = {},
                    onAddInvestment = {},
                    onInvestmentClick = {},
                    onBack = {},
                    modifier = Modifier
                )
            }
        }

        screenshot()
    }

}