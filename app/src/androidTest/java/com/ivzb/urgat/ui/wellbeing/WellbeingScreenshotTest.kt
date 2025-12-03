package com.ivzb.urgat.ui.wellbeing

import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.ui.base.ScreenshotTest
import com.ivzb.urgat.ui.theme.UrgatTheme
import org.junit.Test

class WellbeingScreenshotTest: ScreenshotTest() {

    @Test
    fun screenshotTest_WellbeingScreen_sampleData() = screenshotTest(
        uiState = WellbeingUiState(
            weights = SampleData.SAMPLE_WEIGHTS,
            isLoading = false,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_WellbeingScreen_emptyState() = screenshotTest(
        uiState = WellbeingUiState(
            weights = listOf(),
            isLoading = false,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_WellbeingScreen_loadingState() = screenshotTest(
        uiState = WellbeingUiState(
            weights = listOf(),
            isLoading = true,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_WellbeingScreen_errorState() = screenshotTest(
        uiState = WellbeingUiState(
            weights = listOf(),
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
                    navController = rememberNavController(),
                    modifier = Modifier,
                )
            }
        }

        screenshot()
    }

}