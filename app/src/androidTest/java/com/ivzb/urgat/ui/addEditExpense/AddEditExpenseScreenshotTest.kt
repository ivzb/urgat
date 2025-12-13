package com.ivzb.urgat.ui.addEditExpense

import androidx.compose.ui.Modifier
import androidx.test.espresso.Espresso
import com.ivzb.urgat.R
import com.ivzb.urgat.annotations.Screenshot
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.ui.base.ScreenshotTest
import com.ivzb.urgat.ui.theme.UrgatTheme
import org.junit.Test

@Screenshot
class AddEditExpenseScreenshotTest: ScreenshotTest() {

    @Test
    fun screenshotTest_AddEditExpenseScreen_addExpense() = screenshotTest(
        uiState = AddEditExpenseUiState(
            screenTitleResId = R.string.add_expense,
        ),
    )

    @Test
    fun screenshotTest_AddEditExpenseScreen_editExpense() = screenshotTest(
        uiState = SampleData.SAMPLE_EXPENSES.first().let { expense ->
            AddEditExpenseUiState(
                screenTitleResId = R.string.edit_expense,
                name = expense.name,
                amount = expense.amount.toString(),
                category = expense.category,
                date = expense.date,
                focusIndex = -1,
            )
        },
    )

    @Test
    fun screenshotTest_AddEditExpenseScreen_loading() = screenshotTest(
        uiState = AddEditExpenseUiState(
            screenTitleResId = R.string.edit_expense,
            isLoading = true,
        ),
    )

    private fun screenshotTest(uiState: AddEditExpenseUiState) {
        composeTestRule.setContent {
            UrgatTheme {
                AddEditExpenseScreen(
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