package com.ivzb.urgat.ui.wellbeing

import androidx.compose.ui.Modifier
import com.ivzb.urgat.annotations.Screenshot
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.navigation.FakeNavigator
import com.ivzb.urgat.suites.PlayStoreScreenshotTest
import com.ivzb.urgat.ui.base.ScreenshotTest
import com.ivzb.urgat.ui.theme.UrgatTheme
import com.ivzb.urgat.ui.weights.WeightsUiState
import org.junit.Test
import org.junit.experimental.categories.Category

@Screenshot
class WellbeingScreenshotTest: ScreenshotTest() {

    @Test
    @Category(PlayStoreScreenshotTest::class)
    fun screenshotTest_WellbeingScreen_sampleData() = screenshotTest(
        uiState = WellbeingUiState(
            weightsUiState = WeightsUiState(
                weights = SampleData.SAMPLE_WEIGHTS.take(itemsLimit),
            ),
            isLoading = false,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_WellbeingScreen_emptyState() = screenshotTest(
        uiState = WellbeingUiState(
            weightsUiState = WeightsUiState(),
            isLoading = false,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_WellbeingScreen_loadingState() = screenshotTest(
        uiState = WellbeingUiState(
            weightsUiState = WeightsUiState(),
            isLoading = true,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_WellbeingScreen_errorState() = screenshotTest(
        uiState = WellbeingUiState(
            weightsUiState = WeightsUiState(),
            isLoading = false,
            hasError = true,
        ),
    )

    private fun screenshotTest(uiState: WellbeingUiState) {
        composeTestRule.setContent {
            UrgatTheme {
                WellbeingScreen(
                    uiState = uiState,
                    onIntent = {},

                    onWeightClick = {},

                    navigator = FakeNavigator(),
                    modifier = Modifier,
                )
            }
        }

        screenshot()
    }

}