package com.ivzb.urgat.ui.wellbeing

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.ui.theme.UrgatTheme
import org.junit.Rule
import org.junit.Test

class WellbeingScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

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

        Thread.sleep(2_000)

        // todo: screenshot
        // todo: do both light and dark mode
    }

}