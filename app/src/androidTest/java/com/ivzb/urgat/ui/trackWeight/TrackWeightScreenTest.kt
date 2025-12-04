package com.ivzb.urgat.ui.trackWeight

import androidx.compose.ui.Modifier
import androidx.test.espresso.Espresso
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.base.ScreenshotTest
import com.ivzb.urgat.ui.theme.UrgatTheme
import org.junit.Test

class TrackWeightScreenTest: ScreenshotTest() {

    @Test
    fun screenshotTest_TrackWeightScreen_trackWeight() = screenshotTest(
        uiState = TrackWeightUiState(
            screenTitleResId = R.string.track_weight,
        ),
    )

    @Test
    fun screenshotTest_TrackWeightScreen_loading() = screenshotTest(
        uiState = TrackWeightUiState(
            screenTitleResId = R.string.track_weight,
            isLoading = true,
        ),
    )

    private fun screenshotTest(uiState: TrackWeightUiState) {
        composeTestRule.setContent {
            UrgatTheme {
                TrackWeightScreen(
                    uiState = uiState,
                    onIntent = {},
                    onBack = {},
                    modifier = Modifier,
                )
            }
        }

        Espresso.closeSoftKeyboard()

        screenshot()
    }

}