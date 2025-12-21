package com.ivzb.urgat.ui.addEditInvestment

import androidx.compose.ui.Modifier
import androidx.test.espresso.Espresso
import com.ivzb.urgat.R
import com.ivzb.urgat.annotations.Screenshot
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.ui.base.ScreenshotTest
import com.ivzb.urgat.ui.theme.UrgatTheme
import org.junit.Test

@Screenshot
class AddEditInvestmentScreenshotTest: ScreenshotTest() {

    @Test
    fun screenshotTest_AddEditInvestmentScreen_addInvestment() = screenshotTest(
        uiState = AddEditInvestmentUiState(
            screenTitleResId = R.string.add_investment,
        ),
    )

    @Test
    fun screenshotTest_AddEditInvestmentScreen_editInvestment() = screenshotTest(
        uiState = SampleData.SAMPLE_INVESTMENTS.first().let { investment ->
            AddEditInvestmentUiState(
                screenTitleResId = R.string.edit_investment,
                name = investment.name,
                shares = investment.shares.toString(),
                cost = investment.cost.toString(),
                category = investment.category,
                currency = investment.currency,
                date = investment.date,
                focusIndex = -1,
            )
        },
    )

    @Test
    fun screenshotTest_AddEditInvestmentScreen_loading() = screenshotTest(
        uiState = AddEditInvestmentUiState(
            screenTitleResId = R.string.edit_investment,
            isLoading = true,
        ),
    )

    private fun screenshotTest(uiState: AddEditInvestmentUiState) {
        composeTestRule.setContent {
            UrgatTheme {
                AddEditInvestmentScreen(
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