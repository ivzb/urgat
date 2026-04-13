package com.ivzb.urgat.ui.importData

import androidx.compose.ui.Modifier
import com.ivzb.urgat.R
import com.ivzb.urgat.annotations.Screenshot
import com.ivzb.urgat.ui.base.ScreenshotTest
import com.ivzb.urgat.ui.theme.UrgatTheme
import org.junit.Test

@Screenshot
class ImportDataScreenshotTest: ScreenshotTest() {

    @Test
    fun screenshotTest_ImportDataScreen_initialState() = screenshotTest(
        uiState = ImportDataUiState(
            isLoading = false,
            userMessage = null,
        ),
    )

    @Test
    fun screenshotTest_ImportDataScreen_manualMode_expenses() =
        screenshotTest_ImportDataScreen_manualMode(initialPage = 0)

    @Test
    fun screenshotTest_ImportDataScreen_manualMode_investments() =
        screenshotTest_ImportDataScreen_manualMode(initialPage = 1)

    @Test
    fun screenshotTest_ImportDataScreen_manualMode_savings() =
        screenshotTest_ImportDataScreen_manualMode(initialPage = 2)

    @Test
    fun screenshotTest_ImportDataScreen_manualMode_budgets() =
        screenshotTest_ImportDataScreen_manualMode(initialPage = 3)

    @Test
    fun screenshotTest_ImportDataScreen_manualMode_todos() =
        screenshotTest_ImportDataScreen_manualMode(initialPage = 5)

    @Test
    fun screenshotTest_ImportDataScreen_manualMode_notes() =
        screenshotTest_ImportDataScreen_manualMode(initialPage = 5)

    @Test
    fun screenshotTest_ImportDataScreen_manualMode_watchlist() =
        screenshotTest_ImportDataScreen_manualMode(initialPage = 6)

    @Test
    fun screenshotTest_ImportDataScreen_manualMode_fuelConsumption() =
        screenshotTest_ImportDataScreen_manualMode(initialPage = 7)

    @Test
    fun screenshotTest_ImportDataScreen_manualMode_weights() =
        screenshotTest_ImportDataScreen_manualMode(initialPage = 8)

    private fun screenshotTest_ImportDataScreen_manualMode(initialPage: Int) = screenshotTest(
        uiState = ImportDataUiState(
            isManualMode = true,
            initialPage = initialPage,
        ),
    )

    @Test
    fun screenshotTest_ImportDataScreen_loadingState() = screenshotTest(
        uiState = ImportDataUiState(
            isLoading = true,
            userMessage = R.string.import_data_done,
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