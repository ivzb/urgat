package com.ivzb.urgat.ui.addEditTrade

import androidx.compose.ui.Modifier
import androidx.test.espresso.Espresso
import com.ivzb.urgat.R
import com.ivzb.urgat.annotations.Screenshot
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.ui.base.ScreenshotTest
import com.ivzb.urgat.ui.theme.UrgatTheme
import org.junit.Test

@Screenshot
class AddEditTradeScreenshotTest: ScreenshotTest() {

    @Test
    fun screenshotTest_AddEditTradeScreen_addTrade() = screenshotTest(
        uiState = AddEditTradeUiState(
            screenTitleResId = R.string.add_trade,
        ),
    )

    @Test
    fun screenshotTest_AddEditTradeScreen_editTrade() = screenshotTest(
        uiState = SampleData.SAMPLE_TRADES.first().let { trade ->
            AddEditTradeUiState(
                screenTitleResId = R.string.edit_trade,
                name = trade.name,
                amount = trade.amount.toString(),
                currency = trade.currency,
                date = trade.date,
                focusIndex = -1,
            )
        },
    )

    @Test
    fun screenshotTest_AddEditTradeScreen_loading() = screenshotTest(
        uiState = AddEditTradeUiState(
            screenTitleResId = R.string.edit_trade,
            isLoading = true,
        ),
    )

    private fun screenshotTest(uiState: AddEditTradeUiState) {
        composeTestRule.setContent {
            UrgatTheme {
                AddEditTradeScreen(
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