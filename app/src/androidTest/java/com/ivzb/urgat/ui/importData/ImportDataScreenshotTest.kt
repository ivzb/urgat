package com.ivzb.urgat.ui.importData

import androidx.compose.ui.Modifier
import com.ivzb.urgat.R
import com.ivzb.urgat.annotations.Screenshot
import com.ivzb.urgat.suites.PlayStoreScreenshotTest
import com.ivzb.urgat.ui.base.ScreenshotTest
import com.ivzb.urgat.ui.theme.UrgatTheme
import org.junit.Test
import org.junit.experimental.categories.Category

@Screenshot
class ImportDataScreenshotTest: ScreenshotTest() {

    @Test
    @Category(PlayStoreScreenshotTest::class)
    fun screenshotTest_ImportDataScreen_initialState() = screenshotTest(
        uiState = ImportDataUiState(
            isLoading = false,
            userMessage = null,
            shouldNavigateBack = false,
        ),
    )

    @Test
    fun screenshotTest_ImportDataScreen_loadingState() = screenshotTest(
        uiState = ImportDataUiState(
            isLoading = true,
            userMessage = R.string.import_data_done,
            shouldNavigateBack = false,
        ),
    )

    private fun screenshotTest(uiState: ImportDataUiState) {
        composeTestRule.setContent {
            UrgatTheme {
                ImportDataScreen(
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