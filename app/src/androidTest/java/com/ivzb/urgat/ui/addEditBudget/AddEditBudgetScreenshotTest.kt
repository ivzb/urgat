package com.ivzb.urgat.ui.addEditBudget

import androidx.compose.ui.Modifier
import androidx.test.espresso.Espresso
import com.ivzb.urgat.R
import com.ivzb.urgat.annotations.Screenshot
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.ui.base.ScreenshotTest
import com.ivzb.urgat.ui.theme.UrgatTheme
import org.junit.Test

@Screenshot
class AddEditBudgetScreenshotTest: ScreenshotTest() {

    @Test
    fun screenshotTest_AddEditBudgetScreen_addBudget() = screenshotTest(
        uiState = AddEditBudgetUiState(
            screenTitleResId = R.string.add_budget,
        ),
    )

    @Test
    fun screenshotTest_AddEditBudgetScreen_editBudget() = screenshotTest(
        uiState = SampleData.SAMPLE_BUDGETS.first().let { budget ->
            AddEditBudgetUiState(
                screenTitleResId = R.string.edit_budget,
                income = budget.income.toString(),
                bankStart = budget.bankStart.toString(),
                bankEnd = budget.bankEnd.toString(),
                date = budget.date,
                focusIndex = -1,
            )
        },
    )

    @Test
    fun screenshotTest_AddEditBudgetScreen_loading() = screenshotTest(
        uiState = AddEditBudgetUiState(
            screenTitleResId = R.string.edit_budget,
            isLoading = true,
        ),
    )

    private fun screenshotTest(uiState: AddEditBudgetUiState) {
        composeTestRule.setContent {
            UrgatTheme {
                AddEditBudgetScreen(
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