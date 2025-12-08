package com.ivzb.urgat.ui.addEditTodo

import androidx.compose.ui.Modifier
import androidx.test.espresso.Espresso
import com.ivzb.urgat.R
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.suites.PlayStoreScreenshotTest
import com.ivzb.urgat.ui.base.ScreenshotTest
import com.ivzb.urgat.ui.theme.UrgatTheme
import org.junit.Test
import org.junit.experimental.categories.Category

class AddEditTodoScreenTest: ScreenshotTest() {

    @Test
    fun screenshotTest_AddEditTodoScreen_addTodo() = screenshotTest(
        uiState = AddEditTodoUiState(
            screenTitleResId = R.string.add_todo,
        ),
    )

    @Test
    @Category(PlayStoreScreenshotTest::class)
    fun screenshotTest_AddEditTodoScreen_editTodo() = screenshotTest(
        uiState = SampleData.SAMPLE_TODOS.first().let { todo ->
            AddEditTodoUiState(
                screenTitleResId = R.string.edit_todo,
                title = todo.title,
                description = todo.description,
                isTodoCompleted = todo.isCompleted,
            )
        },
    )

    @Test
    fun screenshotTest_AddEditTodoScreen_loading() = screenshotTest(
        uiState = AddEditTodoUiState(
            screenTitleResId = R.string.edit_todo,
            isLoading = true,
        ),
    )

    private fun screenshotTest(uiState: AddEditTodoUiState) {
        composeTestRule.setContent {
            UrgatTheme {
                AddEditTodoScreen(
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