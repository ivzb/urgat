package com.ivzb.urgat.ui.exportData

import androidx.compose.ui.Modifier
import com.ivzb.urgat.R
import com.ivzb.urgat.annotations.Screenshot
import com.ivzb.urgat.suites.PlayStoreScreenshotTest
import com.ivzb.urgat.ui.base.ScreenshotTest
import com.ivzb.urgat.ui.theme.UrgatTheme
import org.junit.Test
import org.junit.experimental.categories.Category

@Screenshot
class ExportDataScreenshotTest: ScreenshotTest() {

    @Test
    @Category(PlayStoreScreenshotTest::class)
    fun screenshotTest_ExportDataScreen_initialState() = screenshotTest(
        uiState = ExportDataUiState(
            isLoading = false,
            userMessage = null,
            shouldNavigateBack = false,
        ),
    )

    @Test
    fun screenshotTest_ExportDataScreen_loadingState() = screenshotTest(
        uiState = ExportDataUiState(
            isLoading = true,
            userMessage = R.string.export_data_done,
            shouldNavigateBack = false,
        ),
    )

    private fun screenshotTest(uiState: ExportDataUiState) {
        composeTestRule.setContent {
            UrgatTheme {
                ExportDataScreen(
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