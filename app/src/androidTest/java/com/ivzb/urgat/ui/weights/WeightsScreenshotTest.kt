package com.ivzb.urgat.ui.weights

import androidx.compose.ui.Modifier
import com.ivzb.urgat.annotations.Screenshot
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.suites.PlayStoreScreenshotTest
import com.ivzb.urgat.ui.base.ScreenshotTest
import com.ivzb.urgat.ui.theme.UrgatTheme
import org.junit.Test
import org.junit.experimental.categories.Category

@Screenshot
class WeightsScreenshotTest: ScreenshotTest() {

    @Test
    @Category(PlayStoreScreenshotTest::class)
    fun screenshotTest_WeightsScreen_sampleData() = screenshotTest(
        uiState = WeightsUiState(
            weights = SampleData.SAMPLE_WEIGHTS,
            isLoading = false,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_WeightsScreen_emptyState() = screenshotTest(
        uiState = WeightsUiState(
            weights = listOf(),
            isLoading = false,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_WeightsScreen_loadingState() = screenshotTest(
        uiState = WeightsUiState(
            weights = listOf(),
            isLoading = true,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_WeightsScreen_errorState() = screenshotTest(
        uiState = WeightsUiState(
            weights = listOf(),
            isLoading = false,
            hasError = true,
        ),
    )

    private fun screenshotTest(uiState: WeightsUiState) {
        composeTestRule.setContent {
            UrgatTheme {
                WeightsScreen(
                    uiState = uiState,
                    onIntent = {},
                    onWeightClick = {},
                    onBack = {},
                    modifier = Modifier
                )
            }
        }

        screenshot()
    }

}