package com.ivzb.urgat.ui.addEditWeight

import androidx.compose.ui.Modifier
import androidx.test.espresso.Espresso
import com.ivzb.urgat.R
import com.ivzb.urgat.annotations.Screenshot
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.ui.base.ScreenshotTest
import com.ivzb.urgat.ui.theme.UrgatTheme
import org.junit.Test

@Screenshot
class AddEditWeightScreenshotTest: ScreenshotTest() {

    @Test
    fun screenshotTest_AddEditWeightScreen_addWeight() = screenshotTest(
        uiState = AddEditWeightUiState(
            screenTitleResId = R.string.add_weight,
        ),
    )

    @Test
    fun screenshotTest_AddEditWeightScreen_editWeight() = screenshotTest(
        uiState = SampleData.SAMPLE_WEIGHTS.first().let { weight ->
            AddEditWeightUiState(
                screenTitleResId = R.string.edit_weight,
                value = weight.value.toString(),
                date = weight.date,
            )
        },
    )

    @Test
    fun screenshotTest_AddEditWeightScreen_loading() = screenshotTest(
        uiState = AddEditWeightUiState(
            screenTitleResId = R.string.edit_weight,
            isLoading = true,
        ),
    )

    private fun screenshotTest(uiState: AddEditWeightUiState) {
        composeTestRule.setContent {
            UrgatTheme {
                AddEditWeightScreen(
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