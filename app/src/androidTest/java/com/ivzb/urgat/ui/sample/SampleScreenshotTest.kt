package com.ivzb.urgat.ui.sample

import androidx.compose.ui.Modifier
import com.ivzb.urgat.R
import com.ivzb.urgat.annotations.Screenshot
import com.ivzb.urgat.suites.PlayStoreScreenshotTest
import com.ivzb.urgat.ui.base.ScreenshotTest
import com.ivzb.urgat.ui.theme.UrgatTheme
import org.junit.Test
import org.junit.experimental.categories.Category

@Screenshot
class SampleScreenshotTest: ScreenshotTest() {

    @Test
    @Category(PlayStoreScreenshotTest::class)
    fun screenshotTest_SampleScreen_initialState() = screenshotTest(
        uiState = SampleUiState(
            isLoading = false,
            userMessage = null,
            shouldNavigateBack = false,
        ),
    )

    @Test
    fun screenshotTest_SampleScreen_loadingState() = screenshotTest(
        uiState = SampleUiState(
            isLoading = true,
            userMessage = R.string.import_data_done,
            shouldNavigateBack = false,
        ),
    )

    private fun screenshotTest(uiState: SampleUiState) {
        composeTestRule.setContent {
            UrgatTheme {
                SampleScreen(
                    uiState = uiState,
                    onIntent = {},

                    onBack = {},

                    modifier = Modifier,
                )
            }
        }

        screenshot()
    }

}