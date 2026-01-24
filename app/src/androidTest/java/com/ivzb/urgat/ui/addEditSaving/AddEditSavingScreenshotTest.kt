package com.ivzb.urgat.ui.addEditSaving

import androidx.compose.ui.Modifier
import androidx.test.espresso.Espresso
import com.ivzb.urgat.R
import com.ivzb.urgat.annotations.Screenshot
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.ui.base.ScreenshotTest
import com.ivzb.urgat.ui.theme.UrgatTheme
import org.junit.Test

@Screenshot
class AddEditSavingScreenshotTest: ScreenshotTest() {

    @Test
    fun screenshotTest_AddEditSavingScreen_addSaving() = screenshotTest(
        uiState = AddEditSavingUiState(
            screenTitleResId = R.string.add_saving,
        ),
    )

    @Test
    fun screenshotTest_AddEditSavingScreen_editSaving() = screenshotTest(
        uiState = SampleData.SAMPLE_SAVINGS.first().let { saving ->
            AddEditSavingUiState(
                screenTitleResId = R.string.edit_saving,
                name = saving.name,
                amount = saving.amount.toString(),
                date = saving.date,
                focusIndex = -1,
            )
        },
    )

    @Test
    fun screenshotTest_AddEditSavingScreen_loading() = screenshotTest(
        uiState = AddEditSavingUiState(
            screenTitleResId = R.string.edit_saving,
            isLoading = true,
        ),
    )

    private fun screenshotTest(uiState: AddEditSavingUiState) {
        composeTestRule.setContent {
            UrgatTheme {
                AddEditSavingScreen(
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